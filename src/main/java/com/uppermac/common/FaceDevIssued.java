package com.uppermac.common;


import java.util.Map;

public interface FaceDevIssued {

    /**
     *
     * @param devicesType 设备型号
     * @param devicesIp   设备ip
     * @param map
     *            海景设备下发参数:设备IP地址、下发员工的姓名.状态和身份证号码、下发员工的照片
     *            海康门禁下发参数:设备ip、用户工号、用户姓名、用户id,用户公司id,设备登录名,设备登录密码
     *            海康摄像头下发参数:设备ip、用户姓名、公司id、照片
     *            大华设备下发参数:设备ip、照片,卡号,用户ID,卡名,有效开始时间,有效结束时间,设备登录名,设备登录密码
     * @return
     */
    //Boolean faceSend(String devicesType, String devicesIp, CompanyUser user) throws Exception;
    Boolean faceSend(String devicesType, String devicesIp, Map<String,String> map) throws Exception;

}
