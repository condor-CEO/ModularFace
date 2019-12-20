package com.uppermac.common.impl;


import com.dhnetsdk.DHNetSDKCore;
import com.uppermac.common.FaceDevIssued;
import com.uppermac.core.FaceCore;
import com.uppermac.data.Constant;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.FilesUtils;
import org.apache.log4j.Logger;


import java.io.*;
import java.util.Map;

public class FaceDevIssuedImpl implements FaceDevIssued {

    private static Logger logger = Logger.getLogger(FaceDevIssuedImpl.class);

    /**
     * @param devicesType 设备型号
     * @param devicesIp   设备ip
     * @param map
     *            海景设备下发参数:设备IP地址、下发员工的姓名.状态和身份证号码、下发员工的照片
     *            海康门禁下发参数:设备ip、用户工号、用户姓名、用户id,用户公司id,设备登录名,设备登录密码
     *            海康摄像头下发参数:设备ip、用户姓名、公司id、照片
     *            大华设备下发参数:设备ip、照片,卡号,用户ID,卡名,有效开始时间,有效结束时间,设备登录名,设备登录密码
     * @return
     * @throws Exception
     */
    @Override
    public Boolean faceSend(String devicesType, String devicesIp, Map<String, String> map) throws Exception {

        if (null == devicesType || "".equals(devicesType)) {
            logger.error("设备类型不能为空");
            return false;
        }
        if (null == devicesIp || "".equals(devicesIp)) {
            logger.error("设备ip不能为空");
            return false;
        }
        if (null == map) {
            logger.error("参数不能为空");
            return false;
        }

        boolean isSuccess = true;
        if (Constant.HJKey.equals(devicesType)) {
            FaceCore faceCore = new FaceCore();
            String photo = null;
            File file = new File(map.get(Constant.photo));
            photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
            isSuccess = faceCore.sendWhiteList(devicesIp, map,photo);

        } else if (Constant.HKGuardKey.equals(devicesType)) {
            FaceCore faceCore = new FaceCore();
            isSuccess = faceCore.setUser(map);

        } else if (Constant.HKCameraKey.equals(devicesType)) {
            FaceCore faceCore = new FaceCore();
            File picAppendData = IPCxmlFile(map);
            String filePath = map.get(Constant.StaffPath);
            File picture = new File(filePath);
            if (!picAppendData.exists() || !picture.exists()) {
                logger.error("下发网络摄像头失败，找不到附加信息或图片的数据");
                isSuccess = false;
            } else {
                isSuccess = faceCore.sendToIPC(devicesIp, picture, picAppendData);
            }
        } else if (Constant.DHKey.equals(devicesType)) {
             isSuccess = DHNetSDKCore.insertCard(map);
        }
        return isSuccess;
    }


    /**
     * 下发IPC人像时所需照片附加信息文件
     *
     * @param map
     * @return
     */
    public File IPCxmlFile(Map<String, String> map) {
        // TODO Auto-generated method stub
        String filePath = Constant.StaffPath + "/" + map.get(Constant.userName) + map.get(Constant.companyId) + ".xml";
        File filepath = new File(Constant.StaffPath);
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        File file = new File(filePath);

        StringBuilder builder = new StringBuilder();
        builder.append("<FaceAppendData><name>S");
        builder.append(map.get(Constant.userName));
        builder.append("</name><certificateType>ID</certificateType><certificateNumber>");
        builder.append(map.get(Constant.companyId));
        builder.append("</certificateNumber></FaceAppendData>");

        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8");
            StringBuilder outputString = new StringBuilder();
            outputString.append(builder.toString());
            out.write(outputString.toString());

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return file;
    }
}