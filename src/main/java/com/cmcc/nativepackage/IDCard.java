package com.cmcc.nativepackage;

public class IDCard
{

	//连接二代证身份识别设备
	public static native int openIDCard(int idCardType,String deviceId,String password);
	
	//关闭与二代证身份识别设备的连接
	public static native int closeIDCard();
	
	//获取各厂商二代证身份识别设备组件的版本信息。
	public static native int getIDCardVersion(byte[] version);
	
	//初始化二代证身份识别设备，清除二代证身份识别设备内缓存数据。
	public static native int initialIDCard();
	
	//获得二代证信息
	public static native int getIdCardInfo(String[] idCardInfo,byte[] img);
	
	static
	{
		System.loadLibrary("RR");
	}	
}
