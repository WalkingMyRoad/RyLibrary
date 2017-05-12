package com.ist.nativepackage;

public class Motor {

	public static native int Init();
	public static native int Init2(int pos);
	public static native int action(int position,int times);
	public static native int ExecAction(int data);
	public static native int setSpeed(int speed);
	public static native int setPosition(int pos);
	public static native int getSpeed();
	public static native int getPosition();
	public static native int leftaction(int position);
	public static native int rightaction(int position);
	public static native int kan(String id);
	static{
		System.loadLibrary("RR");
	}
}
