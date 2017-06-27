package com.wewins.facelibrary.api.linkface;

import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by yangwy on 2016/11/13.
 */

public class LinkFaceApi extends ApiBase {
	final static String TAG = "LinkFaceApi";

	public LinkFaceApi () {
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
	public JSONObject comparePicture (byte[] imgData1, byte[] imgData2) {
		long globalMills;

		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();

		try {
			LinkFaceApiServices apiServices = new LinkFaceApiServices();
			apiServices.addImage("selfie_file", imgData1);
			apiServices.addImage("historical_selfie_file", imgData2);
			JSONObject compareResult = apiServices.apiPost("identity", "historical_selfie_verification");
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "comparePicture失败，请重新再试");
				compareResult.put("result", false);
				return compareResult;
			}
			DecimalFormat df = new DecimalFormat("#.00");
			compareResult.put("similarity", df.format(compareResult.getDouble("confidence") * 100));
			//compareResult.put("similarity", compareResult.getDouble("confidence"));
			compareResult.put("result", true);
			return compareResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "comparePicture, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
	}

	@Override
	public JSONObject comparePicture (String imgFileName1, String imgFileName2) {
		long globalMills;

		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();

		try {
			LinkFaceApiServices apiServices = new LinkFaceApiServices();
			apiServices.addFile("selfie_file", imgFileName1);
			apiServices.addFile("historical_selfie_file", imgFileName2);
			JSONObject compareResult = apiServices.apiPost("identity", "historical_selfie_verification");
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "comparePicture失败，请重新再试");
				compareResult.put("result", false);
				return compareResult;
			}
			DecimalFormat df = new DecimalFormat("#.00");
			compareResult.put("similarity", df.format(compareResult.getDouble("confidence") * 100));
			//compareResult.put("similarity", compareResult.getDouble("confidence"));
			compareResult.put("result", true);
			return compareResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "comparePicture, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
	}

	@Override
	public JSONObject getPersonList () {
		return null;
	}

	@Override
	public JSONObject personGetInfo (String person_id) {
		return null;
	}

	@Override
	public String personCreate (String person_name) {
		return null;
	}

	@Override
	public boolean personDelete (String person_id) {
		return false;
	}

	@Override
	public boolean personAddFace (String person_id, String face_id) {
		return false;
	}

	@Override
	public boolean personRemoveFace (String person_id, String face_id) {
		return false;
	}

	@Override
	public String faceGetImgId (String face_id) {
		return null;
	}

	@Override
	public boolean groupAddPerson (String person_id) {
		return false;
	}

	@Override
	public void train () {

	}

	@Override
	public JSONObject detect (byte[] imgData) {
		return null;
	}

	@Override
	public String detectReturnFaceId (byte[] imgData) {
		return null;
	}

	@Override
	public JSONObject compare (String person_id, String face_id, byte[] imgData) {
		return null;
	}

	@Override
	public JSONObject compareByPersonId (String person_id, byte[] imgData) {
		return null;
	}

	@Override
	public JSONObject compareByFaceId (String src_face_id, byte[] imgData) {
		return null;
	}

	@Override
	public JSONObject identify (byte[] imgData) {
		return null;
	}

	@Override
	public JSONObject newPerson (String person_name) {
		return null;
	}

	@Override
	public JSONObject personNewFace (byte[] imgData, String person_id) {
		return null;
	}

}
