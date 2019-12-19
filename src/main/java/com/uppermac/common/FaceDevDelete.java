package com.uppermac.common;

import java.util.Map;

public interface FaceDevDelete {

    /**
     * 人脸删除
     * @param deviceType 设备类型
       @param deviceIP   设备ip
       @param map
                    海景人脸删除条件参数:    设备IP地址、删除的用户姓名、
                    海康门禁删除条件参数:   设备ip、用户的工号、用户的姓名
                    海康摄像头删除条件参数: 人脸照片id
       @return
     * @throws Exception
     */
    Boolean faceDel(String deviceType, String deviceIP, Map<String,String> map) throws Exception;

}
