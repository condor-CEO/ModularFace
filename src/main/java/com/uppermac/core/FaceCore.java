package com.uppermac.core;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.data.Constant;
import com.uppermac.data.FaceDevResponse;
import com.uppermac.utils.HttpUtil;
import com.uppermac.utils.ThirdResponseObj;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class FaceCore {

    private static Logger logger = LoggerFactory.getLogger(FaceCore.class);

    HCNetSDKCore hkCore = new HCNetSDKCore();

    /**
     * @param deviceIp 设备Ip
     * @param map     公司员工
     * @param photo    员工照片
     * @return
     */
    public boolean sendWhiteList(String deviceIp, Map map, String photo) {
        // TODO Auto-generated method stub

        JSONObject paramsJson = new JSONObject();
        String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
//        String URL = "http://www.baidu.com";
        //String option = user.getCurrentStatus().equals("normal") ? "save" : "delete";
        //paramsJson.put("name", user.getUserName());
        //paramsJson.put("idCard", user.getIdNO());
        String option = map.get(Constant.currentStatus).equals("normal") ? "save" : "delete";
        paramsJson.put("name", map.get(Constant.userName));
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
     *
     * @param map   用户
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean setUser(Map<String,String> map) throws UnsupportedEncodingException {
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

        return hkCore.sendToIPC(hcDeviceIP, picture, picAppendData);
    }

}
