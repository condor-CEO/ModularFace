package com;

import com.dhnetsdk.DHNetSDKCore;
import com.uppermac.common.impl.FaceDevDeleteImpl;
import com.uppermac.common.impl.FaceDevIssuedImpl;
import com.uppermac.data.Constant;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.FilesUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestDemo {

    //海景设备测试
//    public static void main(String[] args) throws Exception {
//        FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put(Constant.userName, "雷磊");
//        map.put(Constant.currentStatus, "normal");
//        map.put(Constant.idNo, "420621199708271257");
//        String photo = "E:\\sts-space\\photoCache\\staff\\雷磊1.jpg";
//        map.put(Constant.photo, photo);
//        Boolean issend = faceDevIssued.faceSend("TPS980", "192.168.4.12", map);
//        if (issend) {
//            System.out.println("下发成功");
//        } else {
//            System.out.println("下发失败");
//        }
//
////        FaceDevDeleteImpl faceDevDelete = new FaceDevDeleteImpl();
////        Map<String, String> map = new HashMap<String, String>();
////        map.put(Constant.currentStatus, "");
////        map.put(Constant.userName, "林福");
////        map.put(Constant.idNo, "420621199708271257");
////        Boolean del = faceDevDelete.faceDel("TPS980", "192.168.4.12", map);
////
////        if (del) {
////            System.out.println("删除成功");
////        } else {
////            System.out.println("删除失败");
////        }
//    }

    //海康门禁测试
    public static void main(String[] args) throws Exception {

        FaceDevIssuedImpl faceDevIssued = new FaceDevIssuedImpl();
        Map<String, String> map = new HashMap<>();
        map.put(Constant.userName, "叶志博");
        map.put(Constant.StaffPath, "E:\\sts-space\\photoCache\\staff\\叶志博1.jpg");
        map.put(Constant.companyUserId, "9");
        map.put(Constant.deviceIp, "192.168.4.111");
        map.put(Constant.dusername, "admin");
        map.put(Constant.dpassword, "wgmhao123");

        Boolean issend = faceDevIssued.faceSend("DS-K5671", "192.168.4.111", map);
        if (issend) {
            System.out.println("下发成功");
        } else {
            System.out.println("下发失败");
        }

//        FaceDevDeleteImpl faceDevDelete = new FaceDevDeleteImpl();
//        Map<String, String> delMap = new HashMap<>();
//        delMap.put(Constant.dusername, "admin");
//        delMap.put(Constant.dpassword, "wgmhao123");
//        delMap.put(Constant.deviceIp, "192.168.4.111");
//        delMap.put(Constant.companyUserId, "99999");
//        delMap.put(Constant.userName, "雷磊");
//        Boolean del = faceDevDelete.faceDel("DS-K5671", "192.168.4.111", delMap);
//        if (del) {
//            System.out.println("人脸删除成功");
//        } else {
//            System.out.println("人脸删除失败");
//        }
    }


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

}
