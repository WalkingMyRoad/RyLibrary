package com.ist.rylibrary.face.util;

public class Constants {

	//广播ActionName
	public final static String BROADCAST_ACTION_NAME = "com.wewins.facedetect.notify";

	//NJRobot 以DNA用户注册的，密码123456
	public final static String NJ_APP_KEY = "0b8e9ec7528a637cdd15caabf5d773f9";
	public final static String NJ_APP_SEC = "";
	public final static String NJ_GROUP_ID = "2366d4053cc22c4813f3cc964bcf1953";
	public final static String NJ_API_SERVER = "http://121.41.122.32/v0/";
	public final static int NJ_CONNECTION_TIMEOUT = 10000; //连接请求的超时时间
	public final static int NJ_SOCKET_TIMEOUT = 30000; //读取远程数据的超时时间
	//开始进入识别状态
	public static final int CAMERA_HAS_STARTED_PREVIEW = 0;
	//检测到人脸
	public static final int FACE_DETECTED = 1;
	//人脸识别成功
	public static final int FACE_DETECT_SUCC = 2;
	//图片比较成功
	public static final int PICTURE_COMPARE_SUCC = 4;
	//图片比较进行中
	public static final int PICTURE_COMPARE_PROCESS = 5;

	public static final int STOP_SERVICE = -1; //关闭服务
	public static final int START_SERVICE = 3; //开启服务


	public final static String RR_API_SERVER = "http://ns801.gicp.net:8880/rr/face/";
	public final static String RR_IMAGE_SERVER = "http://ns801.gicp.net:8880/rr/face/";
}
