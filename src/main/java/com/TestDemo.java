package com;

import com.uppermac.common.impl.FaceDevIssuedImpl;

import java.util.HashMap;
import java.util.Map;

public class TestDemo {

    //海景设备测试
//    public static void main(String[] args) throws Exception {
//        FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put(Constant.userName, "林福");
//        map.put(Constant.currentStatus, "normal");
//        map.put(Constant.idNo, "D565C47F4D8B5FF25FC11BF0EEB70E1508E2FC738BB00566");
//        String photo = "E:\\sts-space\\photoCache\\staff\\林福1.jpg";
//        File file = new File(photo);
//        photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
//        map.put(Constant.photo, photo);
//        Boolean issend = faceDevIssued.faceSend("TPS980", "192.168.0.46", map);
//        if (issend) {
//            System.out.println("下发成功");
//        } else {
//            System.out.println("下发失败");
//        }

//        FaceDevDeleteImpl faceDevDelete = new FaceDevDeleteImpl();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put(Constant.currentStatus, "");
//        map.put(Constant.userName, "e");
//        map.put(Constant.idNo, "D565C47F4D8B5FF25FC11BF0EEB70E1508E2FC738BB00566");
//        String photo = "E:\\sts-space\\photoCache\\staff\\林福1.jpg";
//        File file = new File(photo);
//        photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
//        map.put(Constant.photo, photo);
//        Boolean del = faceDevDelete.faceDel("TPS980", "192.168.4.12", map);
//
//        if (del) {
//            System.out.println("删除成功");
//        } else {
//            System.out.println("删除失败");
//        }
//    }

    //海康门禁测试
//    public static void main(String[] args) throws Exception {
//
//    FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
//    Map<String, String> map = new HashMap<>();
//    map.put(Constant.userName, "林福");
//    map.put(Constant.StaffPath, "E:\\sts-space\\photoCache\\staff\\林福1.jpg");
//    map.put(Constant.companyUserId, "999999");
//    map.put(Constant.companyId, "11");
//    map.put(Constant.deviceIp, "192.168.4.222");
//    map.put(Constant.dusername, "admin");
//    map.put(Constant.dpassword, "xs123456");
//
//
//    Boolean issend = faceDevIssued.faceSend("DS-K5671", "192.168.4.222", map);
//        if (issend) {
//            System.out.println("下发成功");
//        } else {
//            System.out.println("下发失败");
//        }
//
//       FaceDevDeleteImpl faceDevDelete = new FaceDevDeleteImpl();
//       Map<String, String> delMap = new HashMap<>();
//       delMap.put(Constant.deviceIp, "192.168.0.111");
//       delMap.put(Constant.companyUserId, "4");
//       delMap.put(Constant.userName, "林福");
//       Boolean del = faceDevDelete.faceDel("DS-K5671", "192.168.0.111", delMap);
//       if(del){
//           System.out.println("人脸删除成功");
//       }else{
//           System.out.println("人脸删除失败");
//       }
//    }


    //海康摄像头 测试
//    public static void main(String[] args) throws Exception {
//
//        FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
//        Map<String, String> map = new HashMap<>();
//        map.put(Constant.userName, "雷磊");
//        map.put(Constant.companyId, "1");
//        map.put(Constant.deviceIp, "192.168.0.105");
//        map.put(Constant.StaffPath, "E:\\sts-space\\photoCache\\staff\\雷磊1.jpg");
//
//        Boolean issend = faceDevIssued.faceSend("DS-2CD8627FWD", "192.168.0.105", map);
//        if (issend) {
//            System.out.println("下发成功");
//        } else {
//            System.out.println("下发失败");
//        }
//
//        FaceDevDeleteImpl faceDevDelete = new FaceDevDeleteImpl();
//        Map<String, String> delMap = new HashMap<>();
//        delMap.put(Constant.deviceIp, "192.168.0.105");
//        delMap.put(Constant.IdFrontImgUrl, "58");
//        Boolean del = faceDevDelete.faceDel("DS-2CD8627FWD", "192.168.0.105", delMap);
//        if (del) {
//            System.out.println("人脸删除成功");
//        } else {
//            System.out.println("人脸删除失败");
//        }
//    }

    //大华设备
    public static void main(String[] args) throws Exception {
        FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
        Map<String, String> map = new HashMap<String,String>();
        map.put(com.dhnetsdk.date.Constant.deviceIp, "192.168.0.222");
        map.put(com.dhnetsdk.date.Constant.username, "admin");
        map.put(com.dhnetsdk.date.Constant.password, "wgmhao123");
        map.put(com.dhnetsdk.date.Constant.photoPath, "E:\\sts-space\\photoCache\\staff\\吴桂民1.jpg");
        map.put(com.dhnetsdk.date.Constant.cardNo, "3");                                  //卡号
        map.put(com.dhnetsdk.date.Constant.userId, "3");                                  //用户ID
        map.put(com.dhnetsdk.date.Constant.cardName, "林福");                           //卡名
        map.put(com.dhnetsdk.date.Constant.startValidTime, "2019-12-09 13:00:00");        //有效开始时间
        map.put(com.dhnetsdk.date.Constant.endValidTime, "2020-01-01 13:00:00");          //有效结束时间


        faceDevIssued.faceSend("DH-ASI728","192.168.0.222",map);
    }
}
