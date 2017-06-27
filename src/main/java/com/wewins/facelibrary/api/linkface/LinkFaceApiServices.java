package com.wewins.facelibrary.api.linkface;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Random;

public class LinkFaceApiServices {
	final static String TAG = "LinkFaceApiServices";
	private static final Logger logger = LoggerFactory.getLogger(LinkFaceApiServices.class);

	public final static String API_ID = "1829e8963d744205a40a643874cfb00d";
	public final static String API_SECRET = "d0761534bf784edba9fde2105126ff8d";
	public final static String API_SERVER = "https://v1-auth-api.visioncloudapi.com/";
	public final static int CONNECTION_TIMEOUT = 10000; //连接请求的超时时间
	public final static int NJ_SOCKET_TIMEOUT = 10000; //读取远程数据的超时时间

	private static LinkFaceApiServices mLinkFaceApiServices;
	private String boundary;
	private MultipartEntity multiPart = null;

	public LinkFaceApiServices () {
		boundary = getBoundary();
		multiPart = new MultipartEntity(HttpMultipartMode.STRICT, boundary, Charset.forName(HTTP.UTF_8));
		addParam("api_id", API_ID);
		addParam("api_secret", API_SECRET);
	}

	public void clearParams () {
		multiPart = null;
		multiPart = new MultipartEntity(HttpMultipartMode.STRICT, boundary, Charset.forName(HTTP.UTF_8));
		addParam("api_id", API_ID);
		addParam("api_secret", API_SECRET);
	}

	public static synchronized LinkFaceApiServices getInstance () {
		if (mLinkFaceApiServices == null) {
			mLinkFaceApiServices = new LinkFaceApiServices();
		}
		return mLinkFaceApiServices;
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

	public void addParam (String paramName, String paramValue) {
		try {
			multiPart.addPart(paramName, new StringBody(paramValue, Charset.forName(HTTP.UTF_8)));
		} catch (Exception e) {

		}
	}

	public void addImage (String paramName, byte[] imgData) {
		try {
			multiPart.addPart(paramName, new ByteArrayBody(imgData, paramName));
		} catch (Exception e) {

		}
	}

	public void addFile (String paramName, String fileName) {
		try {
			multiPart.addPart(paramName, new FileBody(new File(fileName)));
		} catch (Exception e) {

		}
	}

	public JSONObject apiPost (String control, String action) {
		JSONObject jsonResult = null; //返回结果；
		HttpClient httpClient = null;
		HttpParams httpParameters = null;

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
			logger.info("statusCode = " + statusCode + ", sResponseContent = " + sResponseContent);

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}

			jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", jsonResult.get("status"));
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

	static String getBoundary () {
		final int boundaryLength = 32;
		final String boundaryAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < boundaryLength; ++i)
			sb.append(boundaryAlphabet.charAt(random.nextInt(boundaryAlphabet.length())));
		return sb.toString();
	}

}
