package com.wewins.facelibrary.api.njrobot;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NJApiServices {
	final static String TAG = "NJApiServices";
	//NJRobot 以DNA用户注册的，密码123456
	public final static String APP_KEY = "0b8e9ec7528a637cdd15caabf5d773f9";
	public final static String APP_SEC = "";
	public final static String API_SERVER = "http://121.41.122.32/v0/";
	public final static int CONNECTION_TIMEOUT = 10000; //连接请求的超时时间
	public final static int NJ_SOCKET_TIMEOUT = 30000; //读取远程数据的超时时间

	private static NJApiServices mNJApiServices;

	public NJApiServices() {
	}

	public static synchronized NJApiServices getInstance() {
		if (mNJApiServices == null) {
			mNJApiServices = new NJApiServices();
		}
		return mNJApiServices;
	}

	private JSONObject GenerateResultJSON(int apiResult, String apiResultDesc) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("apiResult", apiResult);
			jsonResult.put("apiResultDesc", apiResultDesc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	public JSONObject apiGet(String control, String action, Map<String, String> params) {
		JSONObject jsonResult = null; //返回结果；
		HttpClient httpClient = null;
		HttpParams httpParameters = null;
		long startMills = System.currentTimeMillis();

		String url = "";
		String paramStr = "";
		if (params != null) {
			Iterator iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				paramStr += paramStr = "&" + key + "=" + val;
			}
			if (!paramStr.equals("")) {
				paramStr = paramStr.replaceFirst("&", "?");
			}
		}
		url = API_SERVER + control + "/" + action + paramStr;
		Log.i(TAG, "apiGet, url = " + url);

		try {
			HttpGet get = new HttpGet(url);
			// 配置 HTTP 请求参数
			httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			// 设置 连接请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			// 设置 socket 读取超时时间
			HttpConnectionParams.setSoTimeout(httpParameters, NJ_SOCKET_TIMEOUT);

			get.addHeader("api-key", APP_KEY);

			// 开启一个客户端 HTTP 请求
			httpClient = new DefaultHttpClient(httpParameters);
			// 发送 HTTP 请求并获取服务端响应状态
			HttpResponse httpResponse = httpClient.execute(get);
			Log.i(TAG, "apiGet, httpClient.execute已经执行");

			String sResponseContent = "";
			// 获取请求返回的状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 请求成功
				sResponseContent = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			}

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}
			jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", HttpStatus.SC_OK);
			jsonResult.put("apiResultDesc", "查询成功");
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "apiGet发生错误：" + e.getMessage().toString());
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (httpClient != null) {
				//关闭连接管理器释放资源
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			Log.i(TAG, "apiGet, 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	public JSONObject apiPost(String control, String action, Map<String, String> params, byte[] imgData) {
		JSONObject jsonResult = null; //返回结果；
		HttpClient httpClient = null;
		HttpParams httpParameters = null;
		MultipartEntity multiPart = null;
		String boundary;
		long startMills = System.currentTimeMillis();

		String url = API_SERVER + control + "/" + action;
		Log.i(TAG, "apiPost, url = " + url);

		try {
			HttpPost post = new HttpPost(url);
			// 配置 HTTP 请求参数
			httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			// 设置 连接请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			// 设置 socket 读取超时时间
			HttpConnectionParams.setSoTimeout(httpParameters, NJ_SOCKET_TIMEOUT);

			post.addHeader("api-key", APP_KEY);

			boundary = getBoundary();
			multiPart = new MultipartEntity(HttpMultipartMode.STRICT, boundary, Charset.forName(HTTP.UTF_8));
			if (params != null) {
				Iterator iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					multiPart.addPart((String) key, new StringBody((String) val, Charset.forName(HTTP.UTF_8)));
				}
			}
			if (imgData != null) {
				multiPart.addPart("file", new ByteArrayBody(imgData, "file"));
			}
			/* 添加请求参数到请求对象 */
			post.setEntity(multiPart);

			// 开启一个客户端 HTTP 请求
			httpClient = new DefaultHttpClient(httpParameters);
			// 发送 HTTP 请求并获取服务端响应状态
			HttpResponse httpResponse = httpClient.execute(post);
			Log.i(TAG, "apiPost, httpClient.execute已经执行");

			String sResponseContent = "";
			// 获取请求返回的状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			sResponseContent = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			Log.i(TAG, "statusCode = " + statusCode + ", sResponseContent = " + sResponseContent);

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}
			jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", "查询成功");
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "apiPost发生错误：" + e.getMessage().toString());
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (httpClient != null) {
				//关闭连接管理器释放资源
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			Log.i(TAG, "apiPost, 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	public JSONObject apiPostJsonData(String control, String action, JSONObject jsonParams) {
		JSONObject jsonResult = null; //返回结果；
		HttpClient httpClient = null;
		HttpParams httpParameters = null;
		long startMills = System.currentTimeMillis();

		String url = API_SERVER + control + "/" + action;
		Log.i(TAG, "apiPostJsonData, url = " + url);
		Log.i(TAG, "apiPostJsonData, jsonParams = " + jsonParams.toString());
		try {
			HttpPost post = new HttpPost(url);
			// 配置 HTTP 请求参数
			httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			// 设置 连接请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			// 设置 socket 读取超时时间
			HttpConnectionParams.setSoTimeout(httpParameters, NJ_SOCKET_TIMEOUT);

			post.addHeader("Content-Type", "application/json");
			post.addHeader("api-key", APP_KEY);

			post.setEntity(new StringEntity(jsonParams.toString(), HTTP.UTF_8));

			// 开启一个客户端 HTTP 请求
			httpClient = new DefaultHttpClient(httpParameters);
			// 发送 HTTP 请求并获取服务端响应状态
			HttpResponse httpResponse = httpClient.execute(post);
			Log.i(TAG, "apiPostJsonData, httpClient.execute已经执行");

			String sResponseContent = "";
			// 获取请求返回的状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			sResponseContent = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			Log.i(TAG, "apiPostJsonData, statusCode = " + statusCode + ", sResponseContent = " + sResponseContent);

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}
			jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", "请求成功");
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "apiPostJsonData发生错误：" + e.getMessage().toString());
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (httpClient != null) {
				//关闭连接管理器释放资源
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			Log.i(TAG, "apiPostJsonData, 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	static String getBoundary() {
		final int boundaryLength = 32;
		final String boundaryAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < boundaryLength; ++i)
			sb.append(boundaryAlphabet.charAt(random.nextInt(boundaryAlphabet.length())));
		return sb.toString();
	}

}
