package com.wewins.facelibrary.api.rr;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.util.Log;

import com.ist.rylibrary.base.application.RyApplication;
import com.ist.rylibrary.base.entity.PersonInformationBean;


/*
 * 机器人业务接口封装
 */
public class RRBusinessApi {
	final static String TAG = "RRBusinessApi";

	private static RRBusinessApi mRRBusinessApi;

	public RRBusinessApi() {
	}

	public static synchronized RRBusinessApi getInstance() {
		if (mRRBusinessApi == null) {
			mRRBusinessApi = new RRBusinessApi();
		}
		return mRRBusinessApi;
	}

	public boolean deleteFaceImgFromRRSvr(String personId, String faceId) {
		try {
			final Map<String, String> deleteParams = new HashMap<String, String>();
			deleteParams.put("personId", personId);
			deleteParams.put("faceId", faceId);

			JSONObject regJsonResult = RRApiServices.getInstance().http_post("deleteFaceImgFromRRSvr", deleteParams, null);
			if (regJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
				return false;
			}
			int retcode = regJsonResult.getInt("retcode");
			return retcode == 0;
		} catch (Exception e) {
			Log.i(TAG, "deleteFaceImgFromRRSvr出错，错误消息：" + e.getMessage().toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveFaceToRRSvr(String personId, String faceId, String imgId, byte[] imgData,String name,String sex,String age) {
		Log.i(TAG,"上传照片到服务端!");
		try {
			if(sex.equals("Male")){
				sex = "1";
			}else if(sex.equals("Female")){
				sex = "0";
			}
			final Map<String, String> saveParams = new HashMap<String, String>();
			saveParams.put("personId", personId);
			saveParams.put("faceId", faceId);
			saveParams.put("imgId", imgId);
			saveParams.put("name",name);
			saveParams.put("sex",sex);
			saveParams.put("age",age);
			JSONObject regJsonResult = RRApiServices.getInstance().http_post("/face/saveFaceToRRSvr", saveParams, imgData);
			Log.d(TAG,"上传用户的信息 "+regJsonResult.toString());
			if (regJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
				return false;
			}
			int retcode = regJsonResult.getInt("retcode");
			return retcode == 0;
		} catch (Exception e) {
			Log.i(TAG, "saveFaceToRRSvr出错，错误消息：" + e.getMessage().toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean updatePerson(String faceId,String name) {
		Log.i(TAG,"跟新信息到服务器!");
		try {
			final Map<String, String> saveParams = new HashMap<String, String>();
			saveParams.put("personId", faceId);
			saveParams.put("name",name);
			JSONObject regJsonResult = RRApiServices.getInstance().http_post("/face/updatePerson", saveParams,null);
			Log.d(TAG,"获得人物信息的接口数据 "+regJsonResult);
			if (regJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
				return false;
			}
			int retcode = regJsonResult.getInt("retcode");
			return retcode == 0;
		} catch (Exception e) {
			Log.i(TAG, "saveFaceToRRSvr出错，错误消息：" + e.getMessage().toString());
			e.printStackTrace();
			return false;
		}
	}
	public boolean getPerson(String faceId, PersonInformationBean bean) {
		Log.i(TAG,"获取服务器上的人物信息!");
		try {
			final Map<String, String> saveParams = new HashMap<String, String>();
			saveParams.put("personId", faceId);
			JSONObject regJsonResult = RRApiServices.getInstance().http_post("/face/getPerson", saveParams,null);
			if (regJsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
				return false;
			}
			int retcode = regJsonResult.getInt("retcode");
			Log.d(TAG,"获得人物信息的接口数据 "+regJsonResult);
			try{
				if(regJsonResult.has("person")){
					JSONObject person = regJsonResult.getJSONObject("person");
					if(person.has("name")){
						bean.setName(person.getString("name"));
					}
					if(person.has("sex")){
						bean.setGender(person.getString("sex"));
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			return retcode == 0;
		} catch (Exception e) {
			Log.i(TAG, "saveFaceToRRSvr出错，错误消息：" + e.getMessage().toString());
			e.printStackTrace();
			return false;
		}
	}
}
