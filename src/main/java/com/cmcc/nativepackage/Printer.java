
package com.cmcc.nativepackage;

public class Printer {
	
	//连接打印机
	public static native int openPrinter(int printerType,String deviceId,String passWord);	
	
	//关闭与打印机的连接
	public static native int closePrinter();
	
	//获取各厂商打印机组件的版本信息。
	public static native int getPrinterVersion(byte[] version);
	
	//初始化打印机，清除打印缓冲区中的数据，复位打印机打印参数到打印机缺省参数。
	public static native int initialPrinter();
	
	//设置打印机字符串的字符宽高缩放比例。
	public static native int setZoonIn(int widthZoonIn,int heightZoonIn);
	
	//设置打印机字符串的对齐方式。(0 左对齐  1居中 2右对齐)
	public static native int setAlignType(int alignType);
	
	//设置打印机每行字符左边距为n 个点距
	public static native int setLeftMargin(int n);
	
	//设置打印机每行字符右边距为n 个点距
	public static native int setRightMargin(int n);
	
	//设置打印机字符串的字符行间距为 n 个垂直点距
	public static native int setLineSpacingByDotPitch (int n);
	
	//设置打印机字符串的字符间距为 n个水平点距
	public static native int setWordSpacingByDotPitch(int n);
	
	//设置打印机字符串的打印方向
	public static native int setPrintOrientation(int printOrientation);
	
	//设置打印机字符串是否粗体打印
	public static native int setBold(int n);
	
	//设置打印机字符串是否下划线打印
	public static native int setUnderLine(int n);
	
	//设置打印机字符串是否反白打印
	public static native int setInverse(int n);
	//设置打印纸张宽度0:58mm；1：80mm或76mm
	public static native int setPaperWidth(int n);
	//打印字符串
	public static native int print(String content);
	
	//打印HTML格式数据
	public static native int printHTML(String content);
		

	static
	{
//		System.loadLibrary("CMCC_PRINTER_WEWINS_Box");
		System.loadLibrary("CMCC_PRINTER_WEWINS_Box");
		//System.loadLibrary("CMCC_UNITDEVICE_WEWINS_A8");
		System.out.println("loadLibrary CMCC_PRINTER_WEWINS_Box.so");
	}
	
}