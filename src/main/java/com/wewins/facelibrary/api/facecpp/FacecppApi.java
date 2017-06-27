package com.wewins.facelibrary.api.facecpp;

import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.api.facevisa.Multipart;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by yangwy on 2016/11/13.
 */

public class FacecppApi extends ApiBase {
	final static String TAG = "FacecppApi";

	// 接口
	private static final String A201 = "/v2/person/new";
	private static final String A202 = "/v2/person/delete";
	private static final String A203 = "/v2/person/add_face";
	private static final String A205 = "/v2/person/del_face";
	private static final String A207 = "/v2/person/match";
	private static final String A215 = "/v2/base/prepare_target";
	private static final String A216 = "/v2/base/target_match";
	private static final String A217 = "/v2/base/match";
	private static final String A218 = "/v2/base/ocr";
	private static final String A219 = "/v2/base/ocr_both";
	private static final String A220 = "/v2/base/id_auth_with_img";


	public FacecppApi () {
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
		String face_token1, face_token2;
		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();

		try {
			face_token1 = detectReturnFaceId(imgData1);
			if (face_token1 == null || face_token1.length() == 0) {
				Log.e(TAG, "图片1没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片1没有识别到人脸，比较失败");
			}

			face_token2 = detectReturnFaceId(imgData2);
			if (face_token2 == null || face_token2.length() == 0) {
				Log.e(TAG, "图片2没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片2没有识别到人脸，比较失败");
			}

			FacecppApiServices apiServices = new FacecppApiServices();
			apiServices.addParam("face_token1", face_token1);
			apiServices.addParam("face_token2", face_token2);

			apiServices.setContentType("multipart/form-data");
			apiServices.setApi("compare");
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
		long globalMills;
		String faceId = "";
		Log.i(TAG, "detectReturnFaceId, 开始");
		globalMills = System.currentTimeMillis();
		try {
			FacecppApiServices apiServices = new FacecppApiServices();
			apiServices.addFile("image_file", new Multipart("image_file", "image/jpeg", imgData));
			apiServices.setContentType("multipart/form-data");
			apiServices.setApi("detect");
			JSONObject detectResult = apiServices.postApi();
			if (detectResult == null) {
				return faceId; //GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (detectResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "detectReturnFaceId失败，请重新再试");
				//detectResult.put("result", false);
				return faceId;
			}
			//DecimalFormat df = new DecimalFormat("#.00");
			//detectResult.put("similarity", df.format(detectResult.getDouble("confidence") * 100));
			//detectResult.put("result", true);
			return faceId;
		} catch (Exception e) {
			e.printStackTrace();
			return faceId; //GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "detectReturnFaceId, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
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
}
