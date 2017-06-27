package com.wewins.facelibrary.api.facevisa;

import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FacevisaApiServices {

	// 接口前缀
	public static final String HOST = "http://open.facevisa.com";

	private String TAG = "FacevisaApiServices";
	static final private int TIMEOUT = 10000;
	private static final Logger logger = LoggerFactory.getLogger(FacevisaApiServices.class);
	private String tips;

	private String api;
	private byte[] data;
	private Map<String, Multipart> files;
	private Map<String, String> params;
	private String contentType;

	private static FacevisaApiServices mFacevisaApiServices;

	public FacevisaApiServices () {
	}

	public static synchronized FacevisaApiServices getInstance () {
		if (mFacevisaApiServices == null) {
			mFacevisaApiServices = new FacevisaApiServices();
		}
		return mFacevisaApiServices;
	}

	private JSONObject GenerateResultJSON (int apiResult, String apiResultDesc) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("apiResult", apiResult);
			jsonResult.put("apiResultDesc", apiResultDesc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	public void setApi (String api) {
		this.api = api;
	}

	public void setData (byte[] data) {
		this.data = data;
	}

	public Map<String, String> getParamsMap () {
		return params;
	}

	public void setContentType (String type) {
		this.contentType = type;
	}

	public void addParam (String key, String value) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		if (value.startsWith("请填写")) {
			tips = value;
		}
		params.put(key, value);
	}

	public void addFile (String key, Multipart file) {
		if (files == null) {
			files = new HashMap<String, Multipart>();
		}
		files.put(key, file);
	}

	public JSONObject postApi () {
		JSONObject jsonResult = null; //返回结果；
		HttpURLConnection con = null;
		DataOutputStream ds = null;
		long startMills = System.currentTimeMillis();
		try {
			Log.i(TAG, "postApi, start");
			String enterNewline = "\r\n";
			String fix = "--";
			String boundary = "######";
			String MULTIPART_FORM_DATA = contentType;

			// 0. connection
			URL url = new URL(getUrl());

			con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(TIMEOUT);
			con.setReadTimeout(TIMEOUT);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + boundary);

			ds = new DataOutputStream(con.getOutputStream());
			Set<String> keySet = params.keySet();
			Iterator<String> it = keySet.iterator();

			if (data == null) {
				// 1. write params
				while (it.hasNext()) {
					String key = it.next();
					String value = params.get(key);
					Log.i(TAG, "postApi, key = " + key + ", value = " + value);
					ds.writeBytes(fix + boundary + enterNewline);
					ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewline);
					ds.writeBytes(enterNewline);
					// 参数有中文,要转成UTF-8
					ds.write(value.getBytes("UTF-8"));
					//ds.writeBytes(value);// 如果有中文乱码，保存改用上面的ds.writeBytes(enterNewline);那句代码
					ds.writeBytes(enterNewline);
				}
			} else {
				// 2. write data
				ds.write(data);
			}

			// 3. write file
			if (files != null) {
				keySet = files.keySet();
				it = keySet.iterator();

				while (it.hasNext()) {
					String key = it.next();
					Multipart value = files.get(key);
					ds.writeBytes(fix + boundary + enterNewline);
					ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + "; filename=\"" + key + "\"" + enterNewline);
					ds.writeBytes("Content-Type: " + value.type + "\r\n");
					ds.writeBytes(enterNewline);
					ds.write(value.data);
					ds.writeBytes(enterNewline);
					Log.i(TAG, "postApi, filekey = " + key + ", data = " + value.data.length);
				}
			}

			ds.writeBytes(fix + boundary + fix + enterNewline);
			ds.flush();

			// read data
			String line = "";
			StringBuffer b = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			while (null != (line = br.readLine())) {
				b.append(line);
			}
			String respStr = b.toString();
			Log.i(TAG, "postApi, resp = " + respStr);
			logger.info("postApi, resp = " + respStr);
			if (respStr == null || respStr.length() == 0) {
				return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询无返回数据");
			} else {
				jsonResult = new JSONObject(respStr);
				jsonResult.put("apiResult", HttpStatus.SC_OK);
				jsonResult.put("apiResultDesc", jsonResult.opt("message"));
			}
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "postApi, 发生错误：" + e.getMessage().toString());
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			try {
				if (ds != null)
					ds.close();
				if (con != null)
					con.disconnect();
			} catch (Exception e) {

			}
			Log.i(TAG, "apiPost, 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	private String getUrl () {
		StringBuilder builder = new StringBuilder(HOST);
		if (data == null) {
			builder.append(api);
		} else {
			builder.append(api).append("?");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
			}
		}
		Log.i(TAG, "postApi url = " + builder.toString());
		return builder.toString();
	}

	public static String encode (String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return url;
		}
	}
}