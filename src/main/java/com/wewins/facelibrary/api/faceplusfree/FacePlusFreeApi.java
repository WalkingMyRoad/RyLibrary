package com.wewins.facelibrary.api.faceplusfree;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.api.ApiConstants;
import com.wewins.facelibrary.utils.FileSizeUtil;
import com.wewins.facelibrary.utils.FileUtil;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/*
 * Face++ 免费版Api接口
 * DNA 2015-11-10
 */
public class FacePlusFreeApi extends ApiBase {
	final static String TAG = "FacePlusFreeV3Api";
	final static String APP_KEY = "gE-zqP1hvuMO7JaoZo8rj7xKPupi_JQE";
	final static String APP_SEC = "KbmMqKw9_AmuIXCSmZk5jwjNqdHPbj0s";
	final static String GROUP_ID = "dce4cbbb7e22f0a5a62718ca9a165198";

	HttpRequests httpRequest = null;// 在线api

	public FacePlusFreeApi () {
		httpRequest = new HttpRequests(APP_KEY, APP_SEC);
		httpRequest.setWebSite(true, true);
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
		try {
			JSONObject objPersonList = httpRequest.infoGetPersonList();
			if (objPersonList == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objPersonList;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject personGetInfo (String person_id) {
		try {
			PostParameters personParams = new PostParameters();
			personParams.setPersonId(person_id);
			JSONObject objInfo = httpRequest.personGetInfo(personParams);
			if (objInfo == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public String personCreate (String person_name) {
		try {
			PostParameters createParams = new PostParameters();
			createParams.setPersonName(person_name);
			createParams.setGroupId(GROUP_ID);
			JSONObject objCreate = httpRequest.personCreate(createParams);
			if (objCreate == null) {
				return null;
			}
			if (objCreate.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personCreate失败");
				return null;
			}
			return objCreate.getString("person_id");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean personDelete (String person_id) {
		try {
			PostParameters deleteParams = new PostParameters();
			deleteParams.setPersonId(person_id);
			JSONObject objDelete = httpRequest.personDelete(deleteParams);
			if (objDelete == null) {
				return false;
			}
			if (objDelete.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personDelete失败");
				return false;
			}

			return objDelete.optString("success").equals("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean personAddFace (String person_id, String face_id) {
		try {
			PostParameters addParams = new PostParameters();
			addParams.setPersonId(person_id);
			addParams.setFaceToken(face_id);
			JSONObject objAdd = httpRequest.personAddFace(addParams);
			if (objAdd == null) {
				return false;
			}
			if (objAdd.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personAddFace失败");
				return false;
			}
			train();
			return objAdd.optString("success").equals("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean personRemoveFace (String person_id, String face_id) {
		try {
			PostParameters removeParams = new PostParameters();
			removeParams.setPersonId(person_id);
			removeParams.setFaceToken(face_id);
			JSONObject objRemove = httpRequest.personRemoveFace(removeParams);
			if (objRemove == null) {
				return false;
			}
			if (objRemove.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personRemoveFace失败");
				return false;
			}

			train();
			return objRemove.optString("success").equals("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String faceGetImgId (String face_id) {
		try {
			PostParameters faceParams = new PostParameters();
			faceParams.setFaceToken(face_id);
			JSONObject objFaces = httpRequest.infoGetFace(faceParams);
			if (objFaces == null) {
				return null;
			}
			if (objFaces.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "faceGetImgId失败");
				return null;
			}
			JSONArray faceinfoArray = objFaces.getJSONArray("face_info");
			return faceinfoArray.getJSONObject(0).optString("img_id");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean groupAddPerson (String person_id) {
		try {
			PostParameters joinParams = new PostParameters();
			joinParams.setGroupId(GROUP_ID);
			joinParams.setPersonId(person_id);
			JSONObject objJoin = httpRequest.groupAddPerson(joinParams);
			if (objJoin == null) {
				return false;
			}
			if (objJoin.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "faceGetImgId失败");
				return false;
			}
			return objJoin.optString("success").equals("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void train () {
		try {
			PostParameters trainParams = new PostParameters();
			trainParams.setGroupId(GROUP_ID);
			JSONObject objTrain = httpRequest.trainIdentify(trainParams);
			if (objTrain == null) {
				return;
			}
			//return objTrain.optString("apiResult").equals(HttpStatus.SC_OK) && objTrain.optString("session_id").length() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public JSONObject detect (byte[] imgData) {
		try {
			PostParameters detectParams = new PostParameters();
			detectParams.setImg(imgData);
			JSONObject objDetect = httpRequest.detectionDetect(detectParams);
			if (objDetect == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objDetect;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public String detectReturnFaceId (byte[] imgData) {
		try {
			PostParameters detectParams = new PostParameters();
			detectParams.setImg(imgData);
			JSONObject objDetect = httpRequest.detectionDetect(detectParams);
			JSONArray facesArray = objDetect.getJSONArray("faces");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return null;
			}
			int count = objDetect.getJSONArray("faces").length();
			if (count == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return null;
			}
			//取得人脸的数�?
			JSONObject faceJsonOjb = objDetect.getJSONArray("faces").getJSONObject(0);
			String face_id = faceJsonOjb.getString("face_token");
			Log.i(TAG, "face_Id = " + face_id);
			return face_id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject compare (String person_id, String face_id, byte[] imgData) {
		if (person_id != null && person_id.length() > 0)
			return compareByPersonId(person_id, imgData);
		else
			return compareByFaceId(face_id, imgData);
	}

	@Override
	public JSONObject compareByPersonId (String person_id, byte[] imgData) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("apiResult", HttpStatus.SC_NOT_FOUND);
			jsonResult.put("apiResultDesc", "接口不支持");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}

	@Override
	public JSONObject compareByFaceId (String src_face_id, byte[] imgData) {
		try {
			//第一步，调用detectionDetect接口把照片传上去并得到faceId
			JSONObject detectJsonResult = detect(imgData);
			if (detectJsonResult.getInt("apiResult") != HttpStatus.SC_OK)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detect出错");
			JSONArray facesArray = detectJsonResult.getJSONArray("face");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片中没有检测到人脸");
			}
			int count = detectJsonResult.getJSONArray("face").length();
			if (count == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detectionDetect没有识别结果");
			}
			JSONObject faceJsonOjb = detectJsonResult.getJSONArray("face").getJSONObject(0);
			String new_face_Id = faceJsonOjb.getString("face_id");
			Log.i(TAG, "new_face_Id = " + new_face_Id);

			PostParameters compareParams = new PostParameters();
			compareParams.setFaceToken1(src_face_id);
			compareParams.setFaceToken2(new_face_Id);
			JSONObject compareResult = httpRequest.recognitionCompare(compareParams);
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "recognitionCompare失败，请重新再试");
				return GenerateResultJSON(compareResult.getInt("apiResult"), "recognitionCompare失败");
			}
			return compareResult;
		} catch (Exception e) {
			Log.i(TAG, "compare出错");
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject identify (byte[] imgData) {
		try {
			PostParameters identifyParams = new PostParameters();
			identifyParams.setGroupId(GROUP_ID);
			identifyParams.setImg(imgData);
			identifyParams.setMode("oneface");
			JSONObject identifyResult = httpRequest.recognitionIdentify(identifyParams);
			if (identifyResult == null)
				return null;
			//return identifyResult.getJSONArray("face").getJSONObject(0).getJSONArray("candidate");
			return identifyResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject newPerson (String person_name) {
		try {
			//创建Person
			String person_id = personCreate(person_name);
			Log.i(TAG, "personCreate person_id = " + person_id);
			if (person_id == null || person_id.length() == 0) {
				Log.e(TAG, "创建Person失败，请重新再试");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "创建Person失败");
			}

			//加入到Group 此处省略，加入Group的动作在上一步personCreate中已经完成
			//groupAddPerson(person_id);

			JSONObject jsonResult = new JSONObject();
			jsonResult.put("apiResult", HttpStatus.SC_OK);
			jsonResult.put("apiResultDesc", "新增Person ok");
			jsonResult.put("person_id", person_id);
			//jsonResult.put("face_id", face_id);
			//jsonResult.put("img_id", img_id);
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "新增Person出错");
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject personNewFace (byte[] imgData, String person_id) {
		try {
			//第一步，调用detectionDetect接口把照片传上去并得到faceId
			JSONObject detectJsonResult = detect(imgData);
			if (detectJsonResult.getInt("apiResult") != HttpStatus.SC_OK)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detect出错");
			String img_id = detectJsonResult.getString("img_id");
			JSONArray facesArray = detectJsonResult.getJSONArray("face");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片中没有检测到人脸");
			}
			int count = detectJsonResult.getJSONArray("face").length();
			if (count == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detectionDetect没有识别结果");
			}
			//取得第一张人脸的数据
			JSONObject faceJsonOjb = detectJsonResult.getJSONArray("face").getJSONObject(0);
			String face_id = faceJsonOjb.getString("face_id");
			Log.i(TAG, "face_id = " + face_id);

			//将Face加入到Person
			boolean bAddResult = personAddFace(person_id, face_id);
			if (!bAddResult) {
				Log.e(TAG, "Face加入到Person失败，请重新再试");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "Face加入到Person失败");
			}

			//模型训练
			train();

			JSONObject jsonResult = new JSONObject();
			jsonResult.put("apiResult", HttpStatus.SC_OK);
			jsonResult.put("apiResultDesc", "新增Face ok");
			jsonResult.put("face_id", face_id);
			jsonResult.put("img_id", img_id);
			return jsonResult;
		} catch (Exception e) {
			Log.i(TAG, "新增Face出错");
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject comparePicture (byte[] imgData1, byte[] imgData2) {
		String face_id_1 = "";
		String face_id_2 = "";
		long globalMills;
		long startMills;

		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();

		try {
			//第一张照片的上传、识别
			face_id_1 = detectReturnFaceId(imgData1);
			if (face_id_1 == null || face_id_1.length() == 0) {
				Log.e(TAG, "图片1没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片1没有识别到人脸，比较失败");
			}

			//第二张照片上传、识别
			face_id_2 = detectReturnFaceId(imgData2);
			if (face_id_2 == null || face_id_2.length() == 0) {
				Log.e(TAG, "图片2没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片2没有识别到人脸，比较失败");
			}

			//2张图片的face_id都取到后，进行比对
			startMills = System.currentTimeMillis();
			PostParameters compareParams = new PostParameters();
			compareParams.setFaceToken1(face_id_1);
			compareParams.setFaceToken2(face_id_2);
			JSONObject compareResult = httpRequest.recognitionCompare(compareParams);
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "recognitionCompare失败，请重新再试");
				return GenerateResultJSON(compareResult.getInt("apiResult"), "recognitionCompare失败");
			}
			JSONObject newResult = new JSONObject(compareResult.toString());
			newResult.put("similarity", compareResult.getString("confidence"));
			newResult.put("apiResult", compareResult.getString("apiResult"));
			newResult.put("apiResultDesc", compareResult.getString("apiResultDesc"));
			newResult.put("result", true);
			Log.i(TAG, "recognitionCompare 耗时 = " + (System.currentTimeMillis() - startMills));
			return newResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "comparePicture, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
	}

	@Override
	public JSONObject comparePicture (String imgFileName1, String imgFileName2) {
		byte[] imgArray1 = ImageUtil.getScaledBitmapByteArray(imgFileName1);
		byte[] imgArray2 = ImageUtil.getScaledBitmapByteArray(imgFileName2);
		return comparePicture(imgArray1, imgArray2);
	}
}
