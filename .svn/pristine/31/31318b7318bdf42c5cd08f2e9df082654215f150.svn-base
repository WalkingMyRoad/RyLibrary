package com.cmcc.nativepackage;

public class SimCard {

	static {
		System.loadLibrary("OPSLibReader");
		System.loadLibrary("OPSClient");
		// System.loadLibrary("RR_RK3288");
		// System.loadLibrary("FAKAJI_591_WEWINS");
		System.loadLibrary("RR");
	}

	/**
	 * <b><i>public static native int ConfigReader(int ReaderType, char[]
	 * DeviceID, char[] Password);</i></b>
	 * <p>
	 * 通过该函数连接读卡器<br>
	 * 
	 * @param ReaderType
	 *            1、USB口读卡器（CM-READER协议）；2、蓝牙读卡器；3、串口读卡器；4、内置读卡器</i>
	 * @param DeviceID
	 *            1.ReaderType为1时取值如下：WINDOWS: PCSC读卡器名称,Linux:
	 *            PCSC读卡器名称Android:USB读卡器VID+PID的16进制字符串，如VID为23D8，PID为0185，
	 *            则值为23D80185
	 *            2.ReaderType为2时为蓝牙读卡器MAC地址的16进制字符串,如读卡器MAC地址为11:22:33:44:55:66
	 *            ，则值为112233445566。 3.ReaderType为3时取值如下：WINDOWS: 串口名称，如COM1
	 *            Linux: 终端主机自带的串口，如/dev/ttyS0 USB卡(线)转换的串口，如/dev/ttyUSB0
	 *            Android: 终端主机自带的串口，如/dev/ttyS0
	 *            4.ReaderType为4时取值如下：可以设置为固定值，也可以不配置</i>
	 * @param Password
	 *            蓝牙读卡器连接密码，该字段为预留字段。
	 * @return <li>0->Success；状态码->Fail
	 */
	public static native int ConfigReader(int ReaderType, char[] DeviceID,
			char[] Password);

	/**
	 * <b><i>public static native int GetOPSVersion(byte[] version);</i></b>
	 * <p>
	 * 通过该函数获取统一写卡组件的版本信息
	 * 
	 * @param Version 方法返回，统一写卡组件版本信息</i>
	 * @return <li>0－>Success；非0状态码－>Fail
	 */
	public static native int GetOPSVersion(char[] Version);

	/**
	 * <b><i>public static native int GetCardSN(char[] CardSN);</i></b>
	 * <p>
	 * 该函数用于读取卡片空卡序列号，该函数支持本标准发布前和发布后的所有现场写卡系统空卡。
	 * 因此CRM客户端可通过调用GetCardSN判断是否为本标准发布后生产的空卡
	 * 
	 * @param CardSN
	 *            空卡序列号，如卡片符合中国移动《SIM卡远程写卡业务规范》v1.0.0版本，则长度为16位，
	 *            如卡片符合中国移动《现场写卡技术规范》，则长度为20位</i>
	 * @return <li>0－>Success；非0状态码－>Fail
	 */
	public static native int GetCardSN(char[] CardSN);

	/**
	 * <b><i>public static native int GetCardInfo(char[] CardInfo);</i></b>
	 * <p>
	 * 该函数用于读取卡片信息，卡片信息包含卡片ICCID、卡片空卡序列号
	 * 
	 * @param CardInfo
	 *            读取到的卡信息，包括空卡序列号+iccid号
	 * @return <li>0－>Success；非0状态码－>Fail
	 */
	public static native int GetCardInfo(char[] CardInfo);

	/**
	 * <b><i>public static native int WriteCard(char[] IssueData, char[]
	 * Result);</i></b>
	 * <p>
	 * 该函数用于实时写卡数据写入。函数返回值为0时表示统一写卡组件向卡片发送写卡数据成功并得到卡片响应。 写卡是否成功须根据Result判断.
	 * 
	 * @param IssueData
	 *            写卡数据
	 * @param Result
	 *            CRM向现场写卡系统回传写卡结果时须传带MAC值的完整结果
	 * @return <li>0－>Success；非0状态码－>Fail
	 */
	public static native int WriteCard(char[] IssueData, char[] Result);

	/**
	 * <b><i>public static native int GetOPSErrorMsg(int ErrorCode, char[]
	 * ErrorMsg);</i></b>
	 * <p>
	 * 该函数用于获取错误信息，统一写卡组件将返回最近一次函数调用的错误信息
	 * 
	 * @param ErrorCode
	 *            该参数为统一写卡组件最近一次接口调用的错误代码，如最近一次执行成功则该参数返回0
	 * @param ErrorMsg
	 *            CRM向现场写卡系统回传写卡结果时须传带MAC值的完整结果
	 * @return <li>0－>Success；非0状态码－>Fail
	 */
	public static native int GetOPSErrorMsg(int ErrorCode, char[] ErrorMsg);

	// 联通卡
	public static native int GetCardICCID(char[] CardInfo);

	public static native int WriteData(int nCardType, String IMSI,
			String SMSCNumber, String mobileNumber);

}
