package com.uppermac.common.impl;


import com.dhnetsdk.DHNetSDKCore;
import com.uppermac.common.FaceDevDelete;
import com.uppermac.core.FaceCore;
import com.uppermac.core.HCNetSDKCore;
import com.uppermac.data.Constant;
import org.apache.log4j.Logger;

import java.util.Map;


public class FaceDevDeleteImpl implements FaceDevDelete {

    private static Logger logger = Logger.getLogger(FaceDevDeleteImpl.class);

    /**
     *
     * @param deviceType 设备类型
     * @param deviceIP   设备ip
     * @param map
                海景人脸删除条件参数:    设备IP地址、删除的用户姓名、下发员工的照片
                海康门禁删除条件参数:   设备ip、用户的工号、用户的姓名
                海康摄像头删除条件参数: 人脸照片id
     * @return
     * @throws Exception
     */
    @Override
    public Boolean faceDel(String deviceType, String deviceIP, Map<String,String> map) throws Exception {

        if (null == deviceType || "".equals(deviceType)) {
            logger.error("设备类型不能为空");
            return false;
        }
        if (null == deviceIP || "".equals(deviceIP)) {
            logger.error("设备ip不能为空");
            return false;
        }
        if (null == map) {
            logger.error("参数不能为空");
            return false;
        }

        boolean isSuccess = true;
        if (Constant.HJKey.equals(deviceType)) {
            FaceCore faceCore = new FaceCore();
            //user.setCurrentStatus("normal");
            isSuccess = faceCore.sendWhiteListDelete(deviceIP, map, map.get(Constant.photo));
        }
        else if(Constant.HKGuardKey.equals(deviceType)){
            HCNetSDKCore hcNetSDKCore = new HCNetSDKCore();
            //String strCardNo = "S"+user.getCompanyUserId();
            String strCardNo = "S"+map.get(Constant.companyUserId);
            isSuccess = hcNetSDKCore.setCardInfo(deviceIP, Integer.parseInt(map.get(Constant.companyUserId)),map.get(Constant.userName),strCardNo,"delete",map);
        }
        else if(Constant.HKCameraKey.equals(deviceType)){
            HCNetSDKCore hcNetSDKCore = new HCNetSDKCore();
            isSuccess = hcNetSDKCore.delIPCpicture("staff", map.get(Constant.IdFrontImgUrl),map.get(Constant.deviceIp),map);
        }else if(Constant.DHKey.equals(deviceType)){
            isSuccess = DHNetSDKCore.deleteCard(map);
        }
        return isSuccess;
    }
}