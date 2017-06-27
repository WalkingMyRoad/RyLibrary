package com.wewins.facelibrary.api.facevisa;

import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.api.ApiConstants;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by yangwy on 2016/11/13.
 */

public class FacevisaApi extends ApiBase {
	final static String TAG = "FacevisaApi";
	private static final String appid = "42001890";
	private static final String appkey = "9992d863c44f9c40ba591c0fd2ad4e4008f57da2";

	// 接口
	private static final String A201 = "/v2/person/new";
	private static final String A202 = "/v2/person/delete";
	private static final String A203 = "/v2/person/add_face";
	private static final String A205 = "/v2/person/del_face";
	private static final String A207 = "/v2/person/match";
	private static final String A208 = "/v2/person/search";
	private static final String A215 = "/v2/base/prepare_target";
	private static final String A216 = "/v2/base/target_match";
	private static final String A217 = "/v2/base/match";
	private static final String A218 = "/v2/base/ocr";
	private static final String A219 = "/v2/base/ocr_both";
	private static final String A220 = "/v2/base/id_auth_with_img";


	public FacevisaApi () {
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
	public JSONObject getPersonList () {
		long globalMills;
		Log.i(TAG, "getPersonList, 开始");
		globalMills = System.currentTimeMillis();
		try {
			FacevisaApiServices apiServices = new FacevisaApiServices();
			apiServices.addParam("client_id", appid);
			apiServices.addParam("timestamp", Long.toString(System.currentTimeMillis() / 1000));
			apiServices.addParam("sign", Utils.sign(appkey, apiServices.getParamsMap()));

			apiServices.setContentType("multipart/form-data");
			apiServices.setApi(A208);
			JSONObject jsonResult = apiServices.postApi();
			if (jsonResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (jsonResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "getPersonList失败，请重新再试");
				jsonResult.put("result", false);
				return jsonResult;
			}
			jsonResult.put("result", true);
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "getPersonList, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
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

	@Override
	public JSONObject comparePicture (String imgFileName1, String imgFileName2) {
		byte[] imgArray1 = ImageUtil.getScaledBitmapByteArray(imgFileName1);
		byte[] imgArray2 = ImageUtil.getScaledBitmapByteArray(imgFileName2);
		return comparePicture(imgArray1, imgArray2);
	}

	@Override
	public JSONObject comparePicture (byte[] imgData1, byte[] imgData2) {
		long globalMills;
		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();

		try {
			FacevisaApiServices apiServices = new FacevisaApiServices();
			apiServices.addParam("algtype", "1");
			apiServices.addParam("client_id", appid);
			apiServices.addParam("timestamp", Long.toString(System.currentTimeMillis() / 1000));
			apiServices.addParam("orient_1", "3");
			apiServices.addParam("orient_2", "3");
			apiServices.addParam("sign", Utils.sign(appkey, apiServices.getParamsMap()));
			apiServices.addFile("image_1", new Multipart("image_1", "image/jpeg", imgData1));
			apiServices.addFile("image_2", new Multipart("image_2", "image/jpeg", imgData2));

			apiServices.setContentType("multipart/form-data");
			apiServices.setApi(A217);
			JSONObject compareResult = apiServices.postApi();
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
			compareResult.put("result", true);
			return compareResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "comparePicture, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
	}

}
