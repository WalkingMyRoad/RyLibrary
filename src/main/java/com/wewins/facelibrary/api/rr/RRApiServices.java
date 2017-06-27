package com.wewins.facelibrary.api.rr;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import org.json.JSONObject;

import android.util.Log;

/*
 * 机器人后台服务接口
 */
public class RRApiServices {
	final static String TAG = "RRApiServices";

	public final static String RR_API_SERVER = "http://rrmobile.wewins.cn/";
	public final static String RR_IMAGE_SERVER = "http://ns801.gicp.net:8880/rr/face/";
	public final static int CONNECTION_TIMEOUT = 10000; //连接请求的超时时间
	public final static int SOCKET_TIMEOUT = 30000; //读取远程数据的超时时间

	private static RRApiServices mRRApiServices;

	public RRApiServices() {
	}

	public static synchronized RRApiServices getInstance() {
		if (mRRApiServices == null) {
			mRRApiServices = new RRApiServices();
		}
		return mRRApiServices;
	}

	public JSONObject http_post(String action, Map<String, String> params, byte[] imgData) {
		JSONObject jsonResult = null; //返回结果；
		HttpClient httpClient = null;
		HttpParams httpParameters = null;
		MultipartEntity multiPart = null;
		String boundary;
		long startMills = System.currentTimeMillis();

		String url = RR_API_SERVER + action;
		Log.i(TAG, "http_post, url = " + url);

		try {
			HttpPost post = new HttpPost(url);
			// 配置 HTTP 请求参数
			httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			// 设置 连接请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			// 设置 socket 读取超时时间
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

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
			Log.i(TAG, "http_post, httpClient.execute已经执行");

			String sResponseContent = "";
			// 获取请求返回的状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			sResponseContent = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			Log.i(TAG, "http_post, statusCode = " + statusCode + ", sResponseContent = " + sResponseContent);

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				jsonResult = new JSONObject();
				jsonResult.put("apiResult", HttpStatus.SC_NO_CONTENT);
				jsonResult.put("apiResultDesc", "无数据");
				return jsonResult;
			}
			jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", "成功");
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "http_post发生错误：" + e.getMessage().toString());
			e.printStackTrace();
			try {
				jsonResult = new JSONObject();
				jsonResult.put("apiResult", HttpStatus.SC_BAD_REQUEST);
				jsonResult.put("apiResultDesc", "查询出错，错误消息：" + e.getMessage().toString());
				return jsonResult;
			} catch (Exception ex) {
				return new JSONObject();
			}
		} finally {
			if (httpClient != null) {
				//关闭连接管理器释放资源
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			if (jsonResult != null)
				Log.i(TAG, "http_post, 耗时 = " + (System.currentTimeMillis() - startMills) + ", 返回结果 = " + jsonResult.toString());
			else
				Log.i(TAG, "http_post, 耗时 = " + (System.currentTimeMillis() - startMills) + ", 返回结果 = null");
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
