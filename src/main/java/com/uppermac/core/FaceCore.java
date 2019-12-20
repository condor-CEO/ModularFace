package com.uppermac.core;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.data.Constant;
import com.uppermac.data.FaceDevResponse;
import com.uppermac.utils.HttpUtil;
import com.uppermac.utils.ThirdResponseObj;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class FaceCore {

    private static Logger logger = Logger.getLogger(FaceCore.class);

    /**
     * @param deviceIp 设备Ip
     * @param map     公司员工
     * @param photo    员工照片
     * @return
     */
    public boolean sendWhiteList(String deviceIp, Map map, String photo) {
        // TODO Auto-generated method stub
        BasicConfigurator.configure();
        JSONObject paramsJson = new JSONObject();
        String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
//        String URL = "http://www.baidu.com";
        //String option = user.getCurrentStatus().equals("normal") ? "save" : "delete";
        //paramsJson.put("name", user.getUserName());
        //paramsJson.put("idCard", user.getIdNO());
        String option = map.get(Constant.currentStatus).equals("normal") ? "save" : "delete";
        paramsJson.put("name", AccordingToName((String) map.get(Constant.userName)));
        paramsJson.put("idCard", map.get(Constant.idNo));
        paramsJson.put("op", option);
        if ("save".equals(option)) {
            paramsJson.put("type", "staff");
            paramsJson.put("imageFile", photo);
        }

        StringEntity entity = new StringEntity(paramsJson.toJSONString(), "UTF-8");
        ThirdResponseObj thirdResponseObj = null;
        entity.setContentType("aaplication/json");
        try {
            thirdResponseObj = HttpUtil.http2Se(URL, entity, "UTF-8");
        } catch (Exception e) {
            logger.error(URL);
            return false;
        }
        if (thirdResponseObj == null) {
            logger.error("人脸识别仪器" + deviceIp + "接收"+map.get(Constant.userName)+"失败,");
            return false;
        }
        FaceDevResponse faceResponse = JSON.parseObject(thirdResponseObj.getResponseEntity(),FaceDevResponse.class);

        if ("success".equals(thirdResponseObj.getCode())) {
            logger.info(map.get(Constant.userName)+"下发"+deviceIp+"成功");
        } else {
            logger.error(map.get(Constant.userName)+"下发"+deviceIp+"失败");
            return false;
        }
        if("001".equals(faceResponse.getResult())) {
            logger.info("人脸设备接收"+map.get(Constant.userName)+"成功");
            return true;
        }else {
            logger.error("人脸设备接收"+map.get(Constant.userName)+"失败，失败原因："+faceResponse.getMessage());
            return false;
        }
    }
    /**
     * @param deviceIp 设备Ip
     * @param map     公司员工
     * @param photo    员工照片
     * @return
     */
    public boolean sendWhiteListDelete(String deviceIp, Map map, String photo) {
        // TODO Auto-generated method stub
        BasicConfigurator.configure();
        JSONObject paramsJson = new JSONObject();
        String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
        String option = map.get(Constant.currentStatus).equals("normal") ? "save" : "delete";
        paramsJson.put("name", AccordingToName((String) map.get(Constant.userName)));
        paramsJson.put("idCard", map.get(Constant.idNo));
        paramsJson.put("op", option);
        if ("save".equals(option)) {
            paramsJson.put("type", "staff");
            paramsJson.put("imageFile", photo);
        }

        StringEntity entity = new StringEntity(paramsJson.toJSONString(), "UTF-8");
        ThirdResponseObj thirdResponseObj = null;
        entity.setContentType("aaplication/json");
        try {
            thirdResponseObj = HttpUtil.http2Se(URL, entity, "UTF-8");
        } catch (Exception e) {
            logger.error(URL);
            return false;
        }
        FaceDevResponse faceResponse = JSON.parseObject(thirdResponseObj.getResponseEntity(),FaceDevResponse.class);

        if("001".equals(faceResponse.getResult())) {
            logger.info(map.get(Constant.userName)+"人脸删除成功");
            return true;
        }else {
            logger.error(map.get(Constant.userName)+"人脸删除失败，失败原因："+faceResponse.getMessage());
            return false;
        }
    }
    //姓名隐藏
    private static String AccordingToName(String username) {
        StringBuffer name = null;
        if (username.length() >= 2 || username.length() <= 3) {
            name = new StringBuffer(username);
            //创建StringBuffer对象strb
            name.setCharAt(1, '*');    //修改指定位置的字符
            //输出strb 的长度
            name.setLength(6);      //设置字符串长度，超出部分会被裁剪
        }
        if (username.length() == 4) {
            name = new StringBuffer(username);
            //创建StringBuffer对象strb
            name.setCharAt(1, '*');    //修改指定位置的字符
            name.setCharAt(2, '*');    //修改指定位置的字符
            //输出strb 的长度
            name.setLength(6);
        }
        String str = new String(name);
        return str;
    }
    /**
     *
     * @param map   用户
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean setUser(Map<String,String> map) throws UnsupportedEncodingException {
        HCNetSDKCore hkCore = new HCNetSDKCore();
        //String strCardNo = "S" + companyUser.getCompanyUserId();
        String strCardNo = "S" + map.get(Constant.companyUserId);
        boolean setCard = hkCore.setCardInfo(map.get(Constant.deviceIp), Integer.parseInt(map.get(Constant.companyUserId)),
                map.get(Constant.userName), strCardNo, "normal",map);
        if (!setCard) {
            return false;
        }

        return hkCore.setFace(map.get(Constant.deviceIp), strCardNo, map);
    }


    /**
     * 对IPC网络摄像头下发数据
     *
     */
    public boolean sendToIPC(String hcDeviceIP, File picture, File picAppendData) {
        HCNetSDKCore hkCore = new HCNetSDKCore();

        return hkCore.sendToIPC(hcDeviceIP, picture, picAppendData);
    }

}
