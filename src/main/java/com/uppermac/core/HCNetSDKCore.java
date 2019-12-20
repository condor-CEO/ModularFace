package com.uppermac.core;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.uppermac.config.HCNetSDK;
import com.uppermac.data.Constant;
import com.uppermac.utils.MyLog;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@Service
public class HCNetSDKCore {

	private static Logger log = Logger.getLogger(HCNetSDKCore.class);

	/**
	 * lUserID 用户句柄 dwEmployeeNo 用户工号 name 用户名字
	 *
	 *
	 */

	private MyLog logger = new MyLog(HCNetSDKCore.class);

	int lUserID;// 用户句柄


	HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

	NativeLong m_lUploadHandle;

	NativeLong m_UploadStatus;



	static {
		BasicConfigurator.configure();
		//System.load("/usr/apache-java-jar/lib/libHCCore.so");
		//System.load("/usr/apache-java-jar/lib/libhpr.so");
		//System.load("/usr/apache-java-jar/lib/libhcnetsdk.so");
		//System.load("H:/usr/HCCore.dll");
		//System.load("H:/usr/HCNetSDK.dll");
		System.load(Constant.HCPath+"/"+"HCCore.dll");
		System.load(Constant.HCPath+"/"+"HCNetSDK.dll");
		System.load(Constant.HCPath+"/"+"PlayCtrl.dll");
		System.load(Constant.HCPath+"/"+"SuperRender.dll");
		System.load(Constant.HCPath+"/"+"AudioRender.dll");
		System.load(Constant.HCPath+"/"+"ssleay32.dll");
		System.load(Constant.HCPath+"/"+"libeay32.dll");

		boolean isInit = false;
		//systemload();
		isInit = HCNetSDK.INSTANCE.NET_DVR_Init();
		if(!isInit) {
			log.error("海康SDK初始化失败");
		}else {
			log.info("海康SDK初始化成功");
		}
	}

	public boolean setCardInfo(String deviceIP, int dwEmployeeNo, String name, String strCardNo, String isdel,Map<String,String> map)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		lUserID = initAndLoginHK(deviceIP,map);
		if(lUserID < 0) {
			return false;
		}
		int iErr = 0;

		// 设置卡参数
		HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParamSet = new HCNetSDK.NET_DVR_CARD_CFG_COND();
		m_struCardInputParamSet.read();
		m_struCardInputParamSet.dwSize = m_struCardInputParamSet.size();
		m_struCardInputParamSet.dwCardNum = 1;
		m_struCardInputParamSet.byCheckCardNo = 1;

		Pointer lpInBuffer = m_struCardInputParamSet.getPointer();
		m_struCardInputParamSet.write();

		Pointer pUserData = null;
		FRemoteCfgCallBackCardSet fRemoteCfgCallBackCardSet = new FRemoteCfgCallBackCardSet();

		int lHandle = this.hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, lpInBuffer,
				m_struCardInputParamSet.size(), fRemoteCfgCallBackCardSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("建立长连接失败，错误号：" + iErr);
			return false;
		}

		HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50(); // 卡参数
		struCardInfo.read();
		struCardInfo.dwSize = struCardInfo.size();
		struCardInfo.dwModifyParamType = 0x6DAF;// 0x00000001 + 0x00000002 + 0x00000004 + 0x00000008 +
		// 0x00000010 + 0x00000020 + 0x00000080 + 0x00000100 + 0x00000200 + 0x00000400 +
		// 0x00000800;
		/***
		 * #define CARD_PARAM_CARD_VALID 0x00000001 //卡是否有效参数 #define CARD_PARAM_VALID
		 * 0x00000002 //有效期参数 #define CARD_PARAM_CARD_TYPE 0x00000004 //卡类型参数 #define
		 * CARD_PARAM_DOOR_RIGHT 0x00000008 //门权限参数 #define CARD_PARAM_LEADER_CARD
		 * 0x00000010 //首卡参数 #define CARD_PARAM_SWIPE_NUM 0x00000020 //最大刷卡次数参数 #define
		 * CARD_PARAM_GROUP 0x00000040 //所属群组参数 #define CARD_PARAM_PASSWORD 0x00000080
		 * //卡密码参数 #define CARD_PARAM_RIGHT_PLAN 0x00000100 //卡权限计划参数 #define
		 * CARD_PARAM_SWIPED_NUM 0x00000200 //已刷卡次数 #define CARD_PARAM_EMPLOYEE_NO
		 * 0x00000400 //工号 #define CARD_PARAM_NAME 0x00000800 //姓名
		 */
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struCardInfo.byCardNo[i] = 0;
		}
		for (int i = 0; i < strCardNo.length(); i++) {
			struCardInfo.byCardNo[i] = strCardNo.getBytes()[i];
		}
		if ("delete".equals(isdel)) {
			struCardInfo.byCardValid = 0;// 0-无效,1-有效
		} else {
			struCardInfo.byCardValid = 1;
		}
		struCardInfo.byCardType = 1;
		struCardInfo.byLeaderCard = 0;
		struCardInfo.byDoorRight[0] = 1; // 门1有权限
		struCardInfo.wCardRightPlan[0].wRightPlan[0] = 1; // 门1关联卡参数计划模板1

		// 卡有效期
		struCardInfo.struValid.byEnable = 1;
		struCardInfo.struValid.struBeginTime.wYear = 2010;
		struCardInfo.struValid.struBeginTime.byMonth = 12;
		struCardInfo.struValid.struBeginTime.byDay = 1;
		struCardInfo.struValid.struBeginTime.byHour = 0;
		struCardInfo.struValid.struBeginTime.byMinute = 0;
		struCardInfo.struValid.struBeginTime.bySecond = 0;
		struCardInfo.struValid.struEndTime.wYear = 2024;
		struCardInfo.struValid.struEndTime.byMonth = 12;
		struCardInfo.struValid.struEndTime.byDay = 1;
		struCardInfo.struValid.struEndTime.byHour = 0;
		struCardInfo.struValid.struEndTime.byMinute = 0;
		struCardInfo.struValid.struEndTime.bySecond = 0;

		struCardInfo.dwMaxSwipeTime = 0; // 无次数限制
		struCardInfo.dwSwipeTime = 0;
		struCardInfo.byCardPassword = "123456".getBytes();
		struCardInfo.dwEmployeeNo = dwEmployeeNo;
		struCardInfo.wSchedulePlanNo = 1;
		struCardInfo.bySchedulePlanType = 2;
		struCardInfo.wDepartmentNo = 1;

		byte[] strCardName = AccordingToName(name).getBytes("GBK");
		for (int i = 0; i < HCNetSDK.NAME_LEN; i++) {
			struCardInfo.byName[i] = 0;
		}
		for (int i = 0; i < strCardName.length; i++) {
			struCardInfo.byName[i] = strCardName[i];
		}

		struCardInfo.write();
		Pointer pSendBufSet = struCardInfo.getPointer();

		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x3, pSendBufSet, struCardInfo.size())) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("ENUM_ACS_SEND_DATA失败，错误号：" + iErr);
			return false;
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		logger.otherError("断开长连接成功!");
		return true;
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
	 * strCardNo 人脸关联的卡号
	 *
	 */
	public boolean setFace(String deviceIP, String strCardNo, Map<String,String> map)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP,map);
		if(lUserID<0) {
			return false;
		}
		int iErr = 0; // 错误号

		// 设置人脸参数
		HCNetSDK.NET_DVR_FACE_PARAM_COND m_struFaceSetParam = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
		m_struFaceSetParam.dwSize = m_struFaceSetParam.size();

		// String strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			m_struFaceSetParam.byCardNo[i] = 0;
		}
		System.arraycopy(strCardNo.getBytes(), 0, m_struFaceSetParam.byCardNo, 0, strCardNo.length());

		m_struFaceSetParam.byEnableCardReader[0] = 1;
		m_struFaceSetParam.dwFaceNum = 1;
		m_struFaceSetParam.byFaceID = 1;
		m_struFaceSetParam.write();

		Pointer lpInBuffer = m_struFaceSetParam.getPointer();

		Pointer pUserData = null;
		FRemoteCfgCallBackFaceSet fRemoteCfgCallBackFaceSet = new FRemoteCfgCallBackFaceSet();

		int lHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG, lpInBuffer,
				m_struFaceSetParam.size(), fRemoteCfgCallBackFaceSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("建立长连接失败，错误号：" + iErr);
			return false;
		}

		HCNetSDK.NET_DVR_FACE_PARAM_CFG struFaceInfo = new HCNetSDK.NET_DVR_FACE_PARAM_CFG(); // 卡参数
		struFaceInfo.read();
		struFaceInfo.dwSize = struFaceInfo.size();

		// strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struFaceInfo.byCardNo[i] = 0;
		}

		System.arraycopy(strCardNo.getBytes(), 0, struFaceInfo.byCardNo, 0, strCardNo.length());


		struFaceInfo.byEnableCardReader[0] = 1; // 需要下发人脸的读卡器，按数组表示，每位数组表示一个读卡器，数组取值：0-不下发该读卡器，1-下发到该读卡器
		struFaceInfo.byFaceID = 1; // 人脸ID编号，有效取值范围：1~2
		struFaceInfo.byFaceDataType = 1; // 人脸数据类型：0- 模板（默认），1- 图片

		/*****************************************
		 * 从本地文件里面读取JPEG图片二进制数据
		 *****************************************/
		FileInputStream picfile = null;
		int picdataLength = 0;
		try {

			File picture = new File(map.get(Constant.StaffPath));
			if (!picture.exists()) {
				return false;
			}

			picfile = new FileInputStream(picture);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}

		try {
			picdataLength = picfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (picdataLength < 0) {
			System.out.println("input file dataSize < 0");
			log.error("input file dataSize < 0");
			logger.otherError("照片数据错误");
			return false;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		try {
			picfile.read(ptrpicByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		ptrpicByte.write();
		/**************************/

		struFaceInfo.dwFaceLen = picdataLength;
		struFaceInfo.pFaceBuffer = ptrpicByte.getPointer();

		struFaceInfo.write();
		Pointer pSendBufSet = struFaceInfo.getPointer();
		log.info(lHandle + "*" + pSendBufSet + "*" + 0x9 + "*" + struFaceInfo.size());
		// ENUM_ACS_INTELLIGENT_IDENTITY_DATA = 9, //智能身份识别终端数据类型，下发人脸图片数据
		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x9, pSendBufSet, struFaceInfo.size())) {

			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("NET_DVR_SendRemoteConfig失败，错误号：" + iErr);
			return false;
		}


		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(fRemoteCfgCallBackFaceSet.sendFlag !=1 || fRemoteCfgCallBackFaceSet.faceFlag !=1) {
			logger.otherError("下发人脸参数失败");
			return false;
		}
		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		return true;
	}

	public boolean delFace(String idCardNo) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		int iErr = 0;
		// 删除人脸数据
		HCNetSDK.NET_DVR_FACE_PARAM_CTRL m_struFaceDel = new HCNetSDK.NET_DVR_FACE_PARAM_CTRL();
		m_struFaceDel.dwSize = m_struFaceDel.size();
		m_struFaceDel.byMode = 0; // 删除方式：0- 按卡号方式删除，1- 按读卡器删除

		m_struFaceDel.struProcessMode.setType(HCNetSDK.NET_DVR_FACE_PARAM_BYCARD.class);
		m_struFaceDel.struProcessMode.struByCard.byCardNo = idCardNo.getBytes();// 需要删除人脸关联的卡号
		m_struFaceDel.struProcessMode.struByCard.byEnableCardReader[0] = 1; // 读卡器
		m_struFaceDel.struProcessMode.struByCard.byFaceID[0] = 1; // 人脸ID
		m_struFaceDel.write();

		Pointer lpInBuffer = m_struFaceDel.getPointer();

		boolean lRemoteCtrl = hCNetSDK.NET_DVR_RemoteControl(lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, lpInBuffer,
				m_struFaceDel.size());
		if (!lRemoteCtrl) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("删除人脸图片失败，错误号：" + iErr);
			return false;
		} else {
			logger.info("删除人脸图片成功!");
			return true;
		}
	}

	class FRemoteCfgCallBackCardSet implements HCNetSDK.FRemoteConfigCallback {

		public int sendFlag = -1;		//卡状态下发返回标记（1成功，-1失败,0正在下发）

		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
			switch (dwType) {
			case 0:// NET_SDK_CALLBACK_TYPE_STATUS
				HCNetSDK.BYTE_ARRAY struCallbackStatus = new HCNetSDK.BYTE_ARRAY(40);
				struCallbackStatus.write();
				Pointer pStatus = struCallbackStatus.getPointer();
				pStatus.write(0, lpBuffer.getByteArray(0, struCallbackStatus.size()), 0, dwBufLen);
				struCallbackStatus.read();

				int iStatus = 0;
				byte[] byCardNo;
				for (int i = 0; i < 4; i++) {
					int ioffset = i * 8;
					int iByte = struCallbackStatus.byValue[i] & 0xff;
					iStatus = iStatus + (iByte << ioffset);
				}

				switch (iStatus) {
				case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
					logger.info("下发卡参数成功");
					sendFlag = 1;
					break;
				case 1001:
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 4, byCardNo, 0, 32);
					logger.info("正在下发卡参数中,卡号:" + new String(byCardNo).trim());
					sendFlag = 0;
					break;
				case 1002:
					int iErrorCode = 0;
					for (int i = 0; i < 4; i++) {
						int ioffset = i * 8;
						int iByte = struCallbackStatus.byValue[i + 4] & 0xff;
						iErrorCode = iErrorCode + (iByte << ioffset);
					}
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 8, byCardNo, 0, 32);
					logger.otherError("下发卡参数失败,卡号:"+ new String(byCardNo).trim() + ",错误号:" + iErrorCode);
					sendFlag = -1;
					break;
				}
				break;
			default:
				break;
			}
		}
	}
	/***
	 *
	 * 门禁设备人脸下发回调函数
	 * @author Admin
	 *
	 */
	class FRemoteCfgCallBackFaceSet implements HCNetSDK.FRemoteConfigCallback {

		public int faceFlag = -1; 	//人脸数据状态
		public int sendFlag = -1;	//人脸下发状态

		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
			switch (dwType) {
			case 0:// NET_SDK_CALLBACK_TYPE_STATUS
				HCNetSDK.BYTE_ARRAY struCallbackStatus = new HCNetSDK.BYTE_ARRAY(40);
				struCallbackStatus.write();
				Pointer pStatus = struCallbackStatus.getPointer();
				pStatus.write(0, lpBuffer.getByteArray(0, struCallbackStatus.size()), 0, dwBufLen);
				struCallbackStatus.read();

				int iStatus = 0;
				byte[] byCardNo;

				for (int i = 0; i < 4; i++) {
					int ioffset = i * 8;
					int iByte = struCallbackStatus.byValue[i] & 0xff;
					iStatus = iStatus + (iByte << ioffset);
				}

				switch (iStatus) {
				case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
					logger.info("下发人脸参数成功");
					sendFlag = 1;
					break;
				case 1001:
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 4, byCardNo, 0, 32);
					logger.info("正在下发人脸参数,卡号:" + new String(byCardNo).trim());
					sendFlag = 0;
					break;
				case 1002:
					int iErrorCode = 0;
					for (int i = 0; i < 4; i++) {
						int ioffset = i * 8;
						int iByte = struCallbackStatus.byValue[i + 4] & 0xff;
						iErrorCode = iErrorCode + (iByte << ioffset);
					}
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 8, byCardNo, 0, 32);
					logger.otherError("下发人脸参数失败,错误号:" + iErrorCode + ",卡号:"+ new String(byCardNo).trim());
					sendFlag = -1;
					break;
				}
				break;
			case 2:// 获取状态数据
				HCNetSDK.NET_DVR_FACE_PARAM_STATUS m_struFaceStatus = new HCNetSDK.NET_DVR_FACE_PARAM_STATUS();
				m_struFaceStatus.write();
				Pointer pStatusInfo = m_struFaceStatus.getPointer();
				pStatusInfo.write(0, lpBuffer.getByteArray(0, m_struFaceStatus.size()), 0, m_struFaceStatus.size());
				m_struFaceStatus.read();
				if(m_struFaceStatus.byCardReaderRecvStatus[0] == 1) {
					logger.info("人脸读卡器状态正常，照片可下发");
					faceFlag = 1;
				}else {
					logger.otherError("人脸读卡器状态错误，状态值：" + m_struFaceStatus.byCardReaderRecvStatus[0]);
					faceFlag = -1;
				}
				break;
			default:
				break;
			}
		}
	}

	public int initAndLoginHK(String sDeviceIP,Map<String,String>map) {
		// TODO Auto-generated method stub

		HCNetSDK.NET_DVR_USER_LOGIN_INFO struLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
		HCNetSDK.NET_DVR_DEVICEINFO_V40 struDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
		Pointer PointerstruDeviceInfoV40 = struDeviceInfo.getPointer();
		Pointer PointerstruLoginInfo = struLoginInfo.getPointer();
		String passwordFieldPwd = map.get(Constant.dpassword);
		String sUserName = map.get(Constant.dusername);
		int iPort = 8000;
		for (int i = 0; i < sDeviceIP.length(); i++) {
			struLoginInfo.sDeviceAddress[i] = (byte) sDeviceIP.charAt(i);
		}
		for (int i = 0; i < passwordFieldPwd.length(); i++) {
			struLoginInfo.sPassword[i] = (byte) passwordFieldPwd.charAt(i);
		}
		for (int i = 0; i < sUserName.length(); i++) {
			struLoginInfo.sUserName[i] = (byte) sUserName.charAt(i);
		}
		struLoginInfo.wPort = (short) iPort;
		struLoginInfo.write();
		lUserID = hCNetSDK.NET_DVR_Login_V40(PointerstruLoginInfo, PointerstruDeviceInfoV40);
		if(lUserID < 0) {
			logger.otherError("注册失败，失败号："+hCNetSDK.NET_DVR_GetLastError());
		}
		return lUserID;
	}

	public int initAndLogin(String sDeviceIP,Map<String,String> map) {
		// TODO Auto-generated method stub

		HCNetSDK.NET_DVR_USER_LOGIN_INFO struLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
		HCNetSDK.NET_DVR_DEVICEINFO_V40 struDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
		Pointer PointerstruDeviceInfoV40 = struDeviceInfo.getPointer();
		Pointer PointerstruLoginInfo = struLoginInfo.getPointer();
		String passwordFieldPwd = map.get(Constant.dpassword);
		String sUserName = map.get(Constant.dusername);
		int iPort = 8000;
		for (int i = 0; i < sDeviceIP.length(); i++) {
			struLoginInfo.sDeviceAddress[i] = (byte) sDeviceIP.charAt(i);
		}
		for (int i = 0; i < passwordFieldPwd.length(); i++) {
			struLoginInfo.sPassword[i] = (byte) passwordFieldPwd.charAt(i);
		}
		for (int i = 0; i < sUserName.length(); i++) {
			struLoginInfo.sUserName[i] = (byte) sUserName.charAt(i);
		}
		struLoginInfo.wPort = (short) iPort;
		struLoginInfo.write();
		lUserID = hCNetSDK.NET_DVR_Login_V40(PointerstruLoginInfo, PointerstruDeviceInfoV40);
		if(lUserID < 0) {
			logger.otherError("注册失败，失败号："+hCNetSDK.NET_DVR_GetLastError());
		}
		return lUserID;
	}

	public boolean sendToIPC(String hcDeviceIP, File picture, File picAppendData) {
		// TODO Auto-generated method stub

		m_lUploadHandle = UploadFile("1");

		if (m_lUploadHandle.longValue() != 0) {
			return false;
		}
		boolean result = UploadFaceLinData(picture, picAppendData);

		try {
			new Thread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 网络摄像头 与人脸库建立长连接
	 * 
	 * @param faceLib 人脸库ID
	 * @return
	 */
	public NativeLong UploadFile(String faceLib) {
		HCNetSDK.NET_DVR_FACELIB_COND struInput = new HCNetSDK.NET_DVR_FACELIB_COND();
		struInput.dwSize = struInput.size();
		struInput.szFDID = faceLib.getBytes();
		struInput.byConcurrent = 0;
		struInput.byCover = 1;
		struInput.byCustomFaceLibID = 0;
		struInput.write();
		Pointer lpInput = struInput.getPointer();
		NativeLong ret = hCNetSDK.NET_DVR_UploadFile_V40(lUserID, HCNetSDK.IMPORT_DATA_TO_FACELIB, lpInput,
				struInput.size(), null, null, 0);
		logger.info("m_lUploadHandle:" + ret.intValue());
		if (ret.longValue() == -1) {
			int code = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("上传图片文件失败: " + code);
			return ret;
		} else {
			return ret;
		}

	}

	/**
	 * 网络摄像头 获取文件上传进度
	 * 
	 * @return
	 */
	public NativeLong getUploadState() {
		IntByReference pInt = new IntByReference(0);
		m_UploadStatus = hCNetSDK.NET_DVR_GetUploadState(m_lUploadHandle, pInt);
		if (m_UploadStatus.longValue() == -1) {
			log.error("NET_DVR_GetUploadState fail,error=" + hCNetSDK.NET_DVR_GetLastError());
			logger.otherError("下发人脸及附加信息失败，错误号=" + hCNetSDK.NET_DVR_GetLastError());
		} else if (m_UploadStatus.longValue() == 2) {
			 log.info("is uploading!!!! progress = " + pInt.getValue());
		} else if (m_UploadStatus.longValue() == 1) {
			log.info("下发成功");
		} else {
			log.error("下发失败，失败号=" + hCNetSDK.NET_DVR_GetLastError());
		}

		return m_UploadStatus;
	}

	/**
	 * 
	 * @param picture       图片
	 * @param picAppendData 图片附加信息
	 */
	public boolean UploadFaceLinData(File picture, File picAppendData) {

		UploadSend(picture, picAppendData);
		while (true) {
			if (-1 == m_lUploadHandle.longValue()) {
				return false;
			}
			m_UploadStatus = getUploadState();
			if (m_UploadStatus.longValue() == 1) {
				HCNetSDK.NET_DVR_UPLOAD_FILE_RET struPicRet = new HCNetSDK.NET_DVR_UPLOAD_FILE_RET();
				struPicRet.write();
				Pointer lpPic = struPicRet.getPointer();

				boolean bRet = hCNetSDK.NET_DVR_GetUploadResult(m_lUploadHandle, lpPic, struPicRet.size());
				if (!bRet) {
					//System.out.println("NET_DVR_GetUploadResult failed with:" + hCNetSDK.NET_DVR_GetLastError());
					logger.info("NET_DVR_GetUploadResult failed with:" + hCNetSDK.NET_DVR_GetLastError());
					if (hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle)) {
						m_lUploadHandle.setValue(-1);
					}
					return false;
				} else {
					logger.info("NET_DVR_GetUploadResult succ");
					struPicRet.read();
					String m_picID = new String(struPicRet.sUrl);
					logger.info("图片上传成功 PID:" + m_picID);
					System.out.println("图片上传成功 PID:" + m_picID);

					if (hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle)) {
						m_lUploadHandle.setValue(-1);
					}
					return true;
				}

			} else if (m_UploadStatus.longValue() >= 3 || m_UploadStatus.longValue() == -1) {
				System.out.println("m_UploadStatus = " + m_UploadStatus);
				hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle);
				m_lUploadHandle.setValue(-1);
				return false;
			}
		}

	}

	/**
	 * 网络摄像头 上传图片及图片的附加信息
	 * 
	 * @param picture         jpg格式图片
	 * @param picAppendData   xml格式附加文件
	 * @param
	 */
	public void UploadSend(File picture, File picAppendData) {
		FileInputStream picfile = null;
		FileInputStream xmlfile = null;
		int picdataLength = 0;
		int xmldataLength = 0;

		try {
			picfile = new FileInputStream(picture);
			xmlfile = new FileInputStream(picAppendData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			picdataLength = picfile.available();
			xmldataLength = xmlfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (picdataLength < 0 || xmldataLength < 0) {
			System.out.println("input file/xml dataSize < 0");
			return;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		HCNetSDK.BYTE_ARRAY ptrxmlByte = new HCNetSDK.BYTE_ARRAY(xmldataLength);

		try {
			picfile.read(ptrpicByte.byValue);
			xmlfile.read(ptrxmlByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ptrpicByte.write();
		ptrxmlByte.write();

		HCNetSDK.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDK.NET_DVR_SEND_PARAM_IN();

		struSendParam.pSendData = ptrpicByte.getPointer();
		struSendParam.dwSendDataLen = picdataLength;
		struSendParam.pSendAppendData = ptrxmlByte.getPointer();
		struSendParam.dwSendAppendDataLen = xmldataLength;
		if (struSendParam.pSendData == null || struSendParam.pSendAppendData == null || struSendParam.dwSendDataLen == 0
				|| struSendParam.dwSendAppendDataLen == 0) {
			System.out.println("input file/xml data err");
			return;
		}

		struSendParam.byPicType = 1;
		struSendParam.dwPicMangeNo = 0;
		struSendParam.write();

		NativeLong iRet = hCNetSDK.NET_DVR_UploadSend(m_lUploadHandle, struSendParam.getPointer(), null);

		System.out.println("iRet=" + iRet);
		if (iRet.longValue() < 0) {
			System.out.println("NET_DVR_UploadSend fail,error=" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("NET_DVR_UploadSend success");
			System.out.println("dwSendDataLen =" + struSendParam.dwSendDataLen);
			System.out.println("dwSendAppendDataLen =" + struSendParam.dwSendAppendDataLen);
		}

		try {
			picfile.close();
			xmlfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/***
	 * 
	 * 	删除IPC摄像头里的人像照片
	 * 
	 * 
	 */
	public boolean delIPCpicture(String type, String picID,String deviceIp,Map<String,String> map) {
		// TODO Auto-generated method stub
		String str = "DELETE /ISAPI/Intelligent/FDLib/1/picture/" + picID;

		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		HCNetSDK.BYTE_ARRAY ptrDeleteFaceLibUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrDeleteFaceLibUrl.byValue, 0, str.length());
		ptrDeleteFaceLibUrl.write();
		struInput.lpRequestUrl = ptrDeleteFaceLibUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();
		struInput.lpInBuffer = null;
		struInput.dwInBufferSize = 0;
		struInput.write();

		HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();
		struOutput.lpOutBuffer = null;
		struOutput.dwOutBufferSize = 0;

		HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
		struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
		struOutput.write();
		lUserID = initAndLogin(deviceIp,map);
		if (!hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struInput, struOutput)) {
			logger.info("NET_DVR_STDXMLConfig DELETE failed with:" + " " + hCNetSDK.NET_DVR_GetLastError());
			return false;
		} else {
			logger.info("NET_DVR_STDXMLConfig DELETE Succ!!!!!!!!!!!!!!!");
			logger.info("图片删除成功 PID:" + picID);
			return true;
		}

	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

}
