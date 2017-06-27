package com.wewins.facelibrary.api.faceplusbusiness;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * DNA 2015-11-10
 */

public class HttpRequests {
	final static String TAG = "HttpRequests";
	//private static final Logger logger = LoggerFactory.getLogger(HttpRequests.class);
	static final private String WEBSITE = "http://apicn-dev.faceplusplus.com/";

	static final private int BUFFERSIZE = 1048576;
	static final private int TIMEOUT = 10000;

	private String webSite;
	private String apiKey, apiSecret;
	private PostParameters params;
	private int httpTimeOut = TIMEOUT;

	public HttpRequests (String apiKey, String apiSecret) {
		super();
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.webSite = WEBSITE;
	}

	public HttpRequests () {
		super();
	}

	String getAuthString () {
		String authString = apiKey + ":" + apiSecret;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		return new String(authEncBytes);
	}

	/**
	 * default is 30 sec
	 * set http timeout limit (million second)
	 *
	 * @param timeOut
	 */
	public void setHttpTimeOut (int timeOut) {
		this.httpTimeOut = timeOut;
	}

	/**
	 * (million second)
	 *
	 * @return http timeout limit
	 */
	public int getHttpTimeOut () {
		return this.httpTimeOut;
	}

	/**
	 * @return api_key
	 */
	public String getApiKey () {
		return apiKey;
	}

	/**
	 * @param apiKey
	 */
	public void setApiKey (String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return api_secret
	 */
	public String getApiSecret () {
		return apiSecret;
	}

	/**
	 * @param apiSecret
	 */
	public void setApiSecret (String apiSecret) {
		this.apiSecret = apiSecret;
	}

	/**
	 * @return a webSite clone
	 */
	public String getWebSite () {
		return new String(webSite);
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

	public JSONObject http_get (String control, String action) {
		return http_get(control, action, getParams());
	}

	public JSONObject http_post (String control, String action) {
		return http_post("POST", control, action, getParams());
	}

	public JSONObject http_post (String control, String action, PostParameters params) {
		return http_post("POST", control, action, params);
	}

	public JSONObject http_post (String requestMethod, String control, String action, PostParameters params) {
		URL url;
		HttpURLConnection urlConn = null;
		long startMills = System.currentTimeMillis();

		try {
			String sTmp = webSite + control + "/" + action;
			Log.d(TAG, "http_post, action = " + action + ", url = " + sTmp);
			url = new URL(sTmp);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod(requestMethod);
			urlConn.setConnectTimeout(httpTimeOut);
			urlConn.setReadTimeout(httpTimeOut);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);

			urlConn.setRequestProperty("connection", "keep-alive");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());
			urlConn.setRequestProperty("Authorization", "Basic " + getAuthString()); //携带验证信息

			MultipartEntity reqEntity = params.getMultiPart();
			Log.d(TAG, "http_post, action = " + action + ", Param List" + "\r\n" + params.getParamMapStr());

			reqEntity.writeTo(urlConn.getOutputStream());

			int statusCode = urlConn.getResponseCode();
			Log.d(TAG, "http_post, action = " + action + ", ResponseCode = " + statusCode);
			switch (statusCode) {
				case 201:
					statusCode = 200;
					break;
				case 202:
					statusCode = 200;
					break;
				case 203:
					statusCode = 200;
					break;
				case 204:
					statusCode = 200;
					break;
				case 205:
					statusCode = 200;
					break;
			}

			String resultString = null;
			if (statusCode == 200)
				resultString = getResultString(urlConn.getInputStream(), "UTF-8");
			else
				resultString = "";

			if (resultString == null || resultString.length() == 0 || resultString.equals("[]")) {
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}

			Log.d(TAG, "http_post, action = " + action + ", resultString = " + resultString);
			//logger.info("http_post, action = " + action + ", resultString = " + resultString);
			JSONObject jsonResult = new JSONObject(resultString);
			jsonResult.put("apiResult", HttpStatus.SC_OK);
			jsonResult.put("apiResultDesc", "查询成功");
			urlConn.getInputStream().close();
			return jsonResult;
		} catch (Exception e) {
			Log.d(TAG, "http_post, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (urlConn != null)
				urlConn.disconnect();
			Log.d(TAG, "http_post, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	public JSONObject http_delete (String requestMethod, String control, String action, PostParameters params) {
		long startMills = System.currentTimeMillis();

		try {

			String sTmp = webSite + control + "/" + action;
			Log.d(TAG, "http_delete, action = " + action + ", url = " + sTmp);
			HttpParams httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(httpParameters, httpTimeOut);
			HttpConnectionParams.setSoTimeout(httpParameters, httpTimeOut);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpDelete httpDelete = new HttpDelete(sTmp);
			httpDelete.addHeader("connection", "keep-alive");
			//httpDelete.addHeader("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());
			httpDelete.addHeader("Authorization", "Basic " + getAuthString()); //携带验证信息

			HttpResponse response = client.execute(httpDelete);

			int statusCode = response.getStatusLine().getStatusCode();
			Log.d(TAG, "http_delete, action = " + action + ", ResponseCode = " + statusCode);

			switch (statusCode) {
				case 201:
					statusCode = 200;
					break;
				case 202:
					statusCode = 200;
					break;
				case 203:
					statusCode = 200;
					break;
				case 204:
					statusCode = 200;
					break;
				case 205:
					statusCode = 200;
					break;
			}
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", "执行成功");
			return jsonResult;
		} catch (Exception e) {
			Log.d(TAG, "http_delete, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			Log.d(TAG, "http_delete, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	public JSONObject http_get (String control, String action, PostParameters params) {
		HttpClient httpClient = null;
		HttpParams httpParameters = null;
		long startMills = System.currentTimeMillis();

		String url = "";
		String paramStr = "";
		if (params.getParamMap() != null) {
			Iterator iter = params.getParamMap().entrySet().iterator();
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
		url = webSite + control + "/" + action + paramStr;
		Log.d(TAG, "http_get, action = " + action + ", url = " + url);

		try {
			HttpGet get = new HttpGet(url);
			// 配置 HTTP 请求参数
			httpParameters = new BasicHttpParams();
			httpParameters.setParameter("charset", HTTP.UTF_8);
			// 设置 连接请求超时时间
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			// 设置 socket 读取超时时间
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			get.setHeader("Authorization", "Basic " + getAuthString());

			//get.addHeader("api_key", username);
			//get.addHeader("api_secret", password);

			// �?启一个客户端 HTTP 请求
			httpClient = new DefaultHttpClient(httpParameters);
			// 发�?? HTTP 请求并获取服务端响应状�??
			HttpResponse httpResponse = httpClient.execute(get);
			Log.d(TAG, "http_get, action = " + action + ", httpClient.execute已经执行");

			String sResponseContent = "";
			// 获取请求返回的状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.d(TAG, "http_get, action = " + action + ", statusCode = " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				// 请求成功
				sResponseContent = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			}
			Log.d(TAG, "http_get, action = " + action + ", sResponseContent = " + sResponseContent);

			if (sResponseContent == null || sResponseContent.length() == 0 || sResponseContent.equals("[]")) {
				Log.d(TAG, "http_get, action = " + action + ", 查询无返回数据");
				return GenerateResultJSON(statusCode, "查询无返回数据");
			}

			JSONObject jsonResult = new JSONObject(sResponseContent);
			jsonResult.put("apiResult", statusCode);
			jsonResult.put("apiResultDesc", "查询成功");
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "http_get, action = " + action + ", exception = " + e.toString());
			return GenerateResultJSON(HttpStatus.SC_BAD_REQUEST, "查询出错，错误消息：" + e.getMessage().toString());
		} finally {
			if (httpClient != null) {
				//关闭连接管理器释放资�?
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			Log.d(TAG, "http_get, action = " + action + ", 耗时 = " + (System.currentTimeMillis() - startMills));
		}
	}

	private static String getResultString (InputStream inputStream, String encode) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), encode);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
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

	/**
	 * @return {@link PostParameters} object
	 */
	public PostParameters getParams () {
		if (params == null)
			params = new PostParameters();
		return params;
	}

	/**
	 * set default PostParameters
	 *
	 * @param params
	 */
	public void setParams (PostParameters params) {
		this.params = params;
	}

}
