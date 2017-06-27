package com.wewins.facelibrary.api.faceplusfreev3;

import android.util.Log;

import com.ist.rylibrary.base.module.BaseActivity;
import com.wewins.facelibrary.api.NewApiBase;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/*
 * Face++ 免费版Api接口V3
 * DNA 2016-12-13
 */
public class FacePlusFreeV3Api extends NewApiBase {
	final static String TAG = "FacePlusFreeV3Api";

	public FacePlusFreeV3Api () {
	}

	public JSONObject GenerateResultJSON (int apiResult) {
		return GenerateResultJSON(apiResult, "请求失败");
	}

	public JSONObject GenerateResultJSON (int apiResult, String apiResultDesc) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("apiResult", apiResult);
			jsonResult.put("apiResultDesc", apiResultDesc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	@Override
	public JSONObject facesetAddface (String faceset_token, String face_tokens) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACESET_TOKEN, faceset_token);
			map.put(Key.KEY_FOR_FACE_TOKENS, face_tokens);
			JSONObject objAddResult = HttpRequest.simplePost(Key.FACESET, Key.ADD_FACE, map);
			if (objAddResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objAddResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject facesetRemoveface (String faceset_token, String face_tokens) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACESET_TOKEN, faceset_token);
			map.put(Key.KEY_FOR_FACE_TOKENS, face_tokens);
			JSONObject objRemoveResult = HttpRequest.simplePost(Key.FACESET, Key.REMOVE_FACE, map);
			if (objRemoveResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objRemoveResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject facesetGetdetail (String faceset_token) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACESET_TOKEN, faceset_token);
			JSONObject objPersonList = HttpRequest.simplePost(Key.FACESET, Key.GET_DETAIL, map);
			if (objPersonList == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objPersonList;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject detect (byte[] imgData) {
		try {
			HashMap<String, String> map = new HashMap<>();
//			map.put("return_landmark","1");
			map.put("return_attributes","gender");
			JSONObject objDetectResult = HttpRequest.post("", "detect", map, imgData, null);
			if (objDetectResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objDetectResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject detectReturnFaceId (byte[] imgData) {
		try {
			JSONObject objDetect = detect(imgData);
			JSONArray facesArray = objDetect.getJSONArray("faces");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
//				BaseActivity.setFaceText("人脸上传失败：图片中没有检测到人脸");
				return null;
			}
			int count = objDetect.getJSONArray("faces").length();
			if (count == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
//				BaseActivity.setFaceText("人脸上传失败：detectionDetect没有识别结果");
				return null;
			}
			//取得人脸数组
			JSONObject faceJsonOjb = objDetect.getJSONArray("faces").getJSONObject(0);
			String face_id = faceJsonOjb.getString("face_token");
			Log.i(TAG, "face_Id = " + face_id);
			return objDetect;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject search (String faceset_token, byte[] imgData) {
		Log.i(TAG,"utl===https://api.megvii.com/facepp/v3/search");
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACESET_TOKEN, faceset_token);
			//返回比对置信度最高的n个结果，范围[1,5]。默认值为1
			map.put(Key.KEY_FOR_RETURN_RESULT_COUNT, "3");
			JSONObject objSearchResult = HttpRequest.post("", "search", map, imgData, null);
			if (objSearchResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objSearchResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject setUserId (String face_token, String user_id) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACE_TOKEN, face_token);
			map.put(Key.KEY_FOR_USER_ID, user_id);
			JSONObject objSetResult = HttpRequest.simplePost("face", "setuserid", map);
			if (objSetResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objSetResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject faceGetDetail (String face_token) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACE_TOKEN, face_token);
			JSONObject objFaceResult = HttpRequest.simplePost("face", "getdetail", map);
			if (objFaceResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objFaceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject faceAnalyze(String face_token) {
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put(Key.KEY_FOR_FACE_TOKEN, face_token);
			JSONObject objFaceResult = HttpRequest.simplePost("face", "analyze", map);
			if (objFaceResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objFaceResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}

	}

	@Override
	public JSONObject compareFace(byte[] imgData1, byte[] imgData2) {
		Log.i(TAG,"utl===https://api.megvii.com/facepp/v3/search");
		try {
			HashMap<String, String> map = new HashMap<>();
			//返回比对置信度最高的n个结果，范围[1,5]。默认值为1
//			map.put(Key.KEY_FOR_RETURN_RESULT_COUNT, "3");
			JSONObject objSearchResult = HttpRequest.postCompare("", "compare", map, imgData1, imgData2);
			if (objSearchResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objSearchResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject createFaceset(String ace_tokens) {
		Log.i(TAG,"场景Faceset");
		try{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("face_tokens",ace_tokens+"");
			JSONObject getfacesetsResult = HttpRequest.simplePost(Key.FACESET,"create", map);
			Log.i(TAG,"createFaceset=="+getfacesetsResult.toString());
			if (getfacesetsResult == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return getfacesetsResult;
		}catch(Exception e){
			e.printStackTrace();
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}
}
