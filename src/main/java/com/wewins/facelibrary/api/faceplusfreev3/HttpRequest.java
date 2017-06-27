package com.wewins.facelibrary.api.faceplusfreev3;


import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpRequest {
	final static String TAG = "HttpRequest";
//	final static String API_KEY = "gE-zqP1hvuMO7JaoZo8rj7xKPupi_JQE";
//	final static String API_SECRET = "KbmMqKw9_AmuIXCSmZk5jwjNqdHPbj0s";

	final static String API_KEY = "ZEdacrznZeA7EvRc2n8_X6xlHBR76xg4";
	final static String API_SECRET = "L5DTTGrEhsguHBDVpltKqeh7mp-dzF-w";

	private final static int CONNECT_TIME_OUT = 30000;
	private final static int READ_OUT_TIME = 50000;
	static final private int BUFFERSIZE = 1048576;
	private static String boundaryString = getBoundary();

	protected static JSONObject simplePost (String control, String action, HashMap<String, String> map) {
		HttpURLConnection urlConn = null;
		long startMills = System.currentTimeMillis();
		try {
			String sTmp = "";
			if (control != null && control.length() > 0)
				sTmp = Key.WEB_BASE + "/" + control + "/" + action;
			else
				sTmp = Key.WEB_BASE + "/" + action;
			Log.d(TAG, "post, action = " + action + ", url = " + sTmp);
			URL url = new URL(sTmp);
			//忽略 https 证书验证
			if (url.getProtocol().toUpperCase().equals("HTTPS")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				urlConn = https;
			} else {
				urlConn = (HttpURLConnection) url.openConnection();
			}
			//urlConn = (HttpURLConnection) url1.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("accept", "*/*");
			urlConn.setRequestProperty("connection", "Keep-Alive");
			PrintWriter obos = new PrintWriter(urlConn.getOutputStream());
			//加入Key、Sec
			map.put(Key.KEY_FOR_APIKEY, API_KEY);
			map.put(Key.KEY_FOR_APISECRET, API_SECRET);

			Iterator iter = map.entrySet().iterator();
			StringBuffer sb = new StringBuffer();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry) iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key);
				sb.append("=");
				sb.append(value);
				sb.append("&");
			}
			if (map.size() > 0) {
				sb = sb.deleteCharAt(sb.length() - 1);
			}
			obos.print(sb.toString());
			obos.flush();

			String resultString = null;
			int responseCode = urlConn.getResponseCode();
			Log.d(TAG, "post, action = " + action + ", ResponseCode = " + responseCode);
			Log.i(TAG,"post, action = " + action + ", ResponseCode = " + responseCode);
			/*
			if (responseCode == HttpStatus.SC_OK) {
				resultString = readString(urlConn.getInputStream());
			} else {
				resultString = "";
			}
			*/
			try{
				resultString = readString(urlConn.getInputStream());
				if (resultString == null || resultString.length() == 0 || resultString.equals("[]")) {
					return GenerateResultJSON(responseCode, "查询无返回数据");
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			Log.d(TAG, "post, action = " + action + ", resultString = " + resultString);
			JSONObject jsonResult = new JSONObject(resultString);
			jsonResult.put("apiResult", HttpStatus.SC_OK);
			jsonResult.put("apiResultDesc", "查询成功");
			urlConn.getInputStream().close();
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "post, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (urlConn != null)
				urlConn.disconnect();
			Log.d(TAG, "post, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	protected static JSONObject post (String control, String action, HashMap<String, String> map, byte[] fileByte, File file) {
		HttpURLConnection urlConn = null;
		long startMills = System.currentTimeMillis();
		try {
			String sTmp = "";
			if (control != null && control.length() > 0)
				sTmp = Key.WEB_BASE + "/" + control + "/" + action;
			else
				sTmp = Key.WEB_BASE + "/" + action;
			Log.d(TAG, "post, action = " + action + ", url = " + sTmp);
			Log.i(TAG, "post, action = " + action + ", url = " + sTmp);
			URL url = new URL(sTmp);
			//忽略 https 证书验证
			if (url.getProtocol().toUpperCase().equals("HTTPS")) {
				Log.i(TAG,"忽略证书！");
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				urlConn = https;
			} else {
				Log.i(TAG,"不忽略证书！");
				urlConn = (HttpURLConnection) url.openConnection();
			}
			//urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(CONNECT_TIME_OUT);
			urlConn.setReadTimeout(READ_OUT_TIME);
			urlConn.setRequestProperty("accept", "*/*");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
			urlConn.setRequestProperty("connection", "Keep-Alive");
			urlConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
			DataOutputStream obos = new DataOutputStream(urlConn.getOutputStream());
			//加入Key、Sec
			map.put(Key.KEY_FOR_APIKEY, API_KEY);
			map.put(Key.KEY_FOR_APISECRET, API_SECRET);

			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry) iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
				obos.writeBytes("\r\n");
				obos.writeBytes(encode(value) + "\r\n");
			}
			if (fileByte != null) {
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + Key.KEY_FOR_IMAGE_FILE + "\"; filename=\"image_file\"" + "\r\n");
				obos.writeBytes("\r\n");
				obos.write(fileByte);
				obos.writeBytes("\r\n");
			}

			if (file != null) {
				byte[] buf = getBytesFromFile(file);
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + Key.KEY_FOR_IMAGE_FILE + "\"; filename=\"" + encode(file.getName()) + "\"\r\n");
				obos.writeBytes("\r\n");
				obos.write(buf);
				obos.writeBytes("\r\n");
			}

			obos.writeBytes("--" + boundaryString + "--" + "\r\n");
			obos.writeBytes("\r\n");
			obos.flush();

			String resultString = null;
			int responseCode = urlConn.getResponseCode();
			Log.d(TAG, "post, action = " + action + ", ResponseCode = " + responseCode);
			/*
			if (responseCode == HttpStatus.SC_OK) {
				resultString = readString(urlConn.getInputStream());
			} else {
				resultString = "";
			}
			*/
			resultString = readString(urlConn.getInputStream());
			if (resultString == null || resultString.length() == 0 || resultString.equals("[]")) {
				return GenerateResultJSON(responseCode, "查询无返回数据");
			}
			Log.d(TAG, "post, action = " + action + ", resultString = " + resultString);
			JSONObject jsonResult = new JSONObject(resultString);
			jsonResult.put("apiResult", responseCode);
			if (responseCode != HttpStatus.SC_OK) {
				jsonResult.put("apiResultDesc", jsonResult.optString("error_message"));
			}
			urlConn.getInputStream().close();
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "post, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (urlConn != null)
				urlConn.disconnect();
			Log.d(TAG, "post, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	protected static JSONObject postCompare(String control, String action, HashMap<String, String> map, byte[] fileByte1, byte[] fileByte2) {
		HttpURLConnection urlConn = null;
		long startMills = System.currentTimeMillis();
		try {
			String sTmp = "";
			if (control != null && control.length() > 0)
				sTmp = Key.WEB_BASE + "/" + control + "/" + action;
			else
				sTmp = Key.WEB_BASE + "/" + action;
			Log.d(TAG, "post, action = " + action + ", url = " + sTmp);
			Log.i(TAG, "post, action = " + action + ", url = " + sTmp);
			URL url = new URL(sTmp);
			//忽略 https 证书验证
			if (url.getProtocol().toUpperCase().equals("HTTPS")) {
				Log.i(TAG,"忽略证书！");
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				urlConn = https;
			} else {
				Log.i(TAG,"不忽略证书！");
				urlConn = (HttpURLConnection) url.openConnection();
			}
			//urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(CONNECT_TIME_OUT);
			urlConn.setReadTimeout(READ_OUT_TIME);
			urlConn.setRequestProperty("accept", "*/*");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
			urlConn.setRequestProperty("connection", "Keep-Alive");
			urlConn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
			DataOutputStream obos = new DataOutputStream(urlConn.getOutputStream());
			//加入Key、Sec
			map.put(Key.KEY_FOR_APIKEY, API_KEY);
			map.put(Key.KEY_FOR_APISECRET, API_SECRET);

			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry) iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
				obos.writeBytes("\r\n");
				obos.writeBytes(encode(value) + "\r\n");
			}
			if (fileByte1 != null) {
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + Key.KEY_FOR_IMAGE_FILE1 + "\"; filename=\"image_file1\"" + "\r\n");
				obos.writeBytes("\r\n");
				obos.write(fileByte1);
				obos.writeBytes("\r\n");
			}if (fileByte2 != null) {
				obos.writeBytes("--" + boundaryString + "\r\n");
				obos.writeBytes("Content-Disposition: form-data; name=\"" + Key.KEY_FOR_IMAGE_FILE2 + "\"; filename=\"image_file2\"" + "\r\n");
				obos.writeBytes("\r\n");
				obos.write(fileByte2);
				obos.writeBytes("\r\n");
			}

			obos.writeBytes("--" + boundaryString + "--" + "\r\n");
			obos.writeBytes("\r\n");
			obos.flush();

			String resultString = null;
			int responseCode = urlConn.getResponseCode();
			Log.d(TAG, "post, action = " + action + ", ResponseCode = " + responseCode);
			/*
			if (responseCode == HttpStatus.SC_OK) {
				resultString = readString(urlConn.getInputStream());
			} else {
				resultString = "";
			}
			*/
			resultString = readString(urlConn.getInputStream());
			if (resultString == null || resultString.length() == 0 || resultString.equals("[]")) {
				return GenerateResultJSON(responseCode, "查询无返回数据");
			}
			Log.d(TAG, "post, action = " + action + ", resultString = " + resultString);
			JSONObject jsonResult = new JSONObject(resultString);
			jsonResult.put("apiResult", responseCode);
			if (responseCode != HttpStatus.SC_OK) {
				jsonResult.put("apiResultDesc", jsonResult.optString("error_message"));
			}
			urlConn.getInputStream().close();
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "post, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (urlConn != null)
				urlConn.disconnect();
			Log.d(TAG, "post, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	private static JSONObject GenerateResultJSON (int apiResult, String apiResultDesc) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("apiResult", apiResult);
			jsonResult.put("apiResultDesc", apiResultDesc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	private static String readString (InputStream is) {
		StringBuffer rst = new StringBuffer();

		byte[] buffer = new byte[BUFFERSIZE];
		int len = 0;
		try {
			while ((len = is.read(buffer)) > 0)
				for (int i = 0; i < len; ++i)
					rst.append((char) buffer[i]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rst.toString();
	}

	private static String getBoundary () {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 32; ++i) {
			sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
		}

		return sb.toString();
	}

	public static byte[] getBytesFromFile (File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {

		}
		return null;
	}


	private static String encode (String value) throws Exception {
		return URLEncoder.encode(value, "UTF-8");
	}

	static void trustAllHosts () {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers () {
				return new java.security.cert.X509Certificate[]{};
			}

			public void checkClientTrusted (X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted (X509Certificate[] chain, String authType) throws CertificateException {
			}
		}};
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify (String hostname, SSLSession session) {
			return true;
		}
	};
}
