package com.ist.nativepackage;

public class RR {

	static {
		System.loadLibrary("RR");
	}

	/** 红外 0-没人；1-有人 */
	public static native int GetIrStatus();

	/** 充电开关 1开 0关 */
	public static native int SwitchIdcPower(int i);

	/** 打开爪子，一次开、一次关 */
	public static native int SwitchHelthCheakPower();
	

	/**
	 * 设置体检设备开关 led_id 1-4 power 0 关闭 1开 返回值 0 成功 其他：失败
	 */
	public static native int SetLedPower(int led_id, int power);

	/**
	 * 设置体检设备开关 led_id 1-4 返回值 0 成功 其他：失败
	 */
	public static native int GetLedPower(int led_id);
	//初始化线程锁
	public static native int InitLock();

}
