package com.wewins.facelibrary.api.njrobot;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;

/*
 * 南江机器人Api接口
 * DNA 2015-11-17
 */
public class NJRobotApi extends ApiBase {
	final static String TAG = "FacePlusBusinessApi";
	public final static String GROUP_ID = "2366d4053cc22c4813f3cc964bcf1953";

	public NJRobotApi() {
	}

	public JSONObject GenerateResultJSON(int apiResult) {
		return GenerateResultJSON(apiResult, "请求失败");
	}

	public JSONObject GenerateResultJSON(int apiResult, String apiResultDesc) {
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
	public JSONObject getPersonList() {
		try {
			JSONObject objPersonList = NJApiServices.getInstance().apiGet("info", "get_person_list", null);
			if (objPersonList == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			return objPersonList;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject personGetInfo(String person_id) {
		try {
			//JSONObject objInfo = httpRequest.personGetInfo(personParams);
			JSONObject objInfo = NJApiServices.getInstance().apiGet("person", "get_info" + "/" + person_id, null);
			if (objInfo == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			//格式转换
			if (objInfo.getJSONArray("face_id").length() > 0) {
				JSONObject newResult = new JSONObject();
				JSONArray newFaceArray = new JSONArray();

				JSONArray facesArray = objInfo.getJSONArray("face_id");
				for (int iFace = 0; iFace < facesArray.length(); iFace++) {
					String face_id = facesArray.getString(iFace);
					JSONObject newFaceObj = new JSONObject();
					newFaceObj.put("face_id", face_id);
					newFaceArray.put(newFaceObj);
				}
				newResult.put("face", newFaceArray);
				newResult.put("apiResult", objInfo.getString("apiResult"));
				newResult.put("apiResultDesc", objInfo.getString("apiResultDesc"));
				return newResult;
			} else
				return objInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public String personCreate(String person_name) {
		try {
			JSONObject createParams = new JSONObject();
			createParams.put("person_name", person_name);
			createParams.put("group_id", new JSONArray().put(GROUP_ID));
			JSONObject objCreate = NJApiServices.getInstance().apiPostJsonData("person", "create", createParams);
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
	public boolean personDelete(String person_id) {
		try {
			JSONObject objDelete = NJApiServices.getInstance().apiPostJsonData("person", "delete",
					new JSONObject().put("person_id", new JSONArray().put(person_id)));
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
	public boolean personAddFace(String person_id, String face_id) {
		try {
			JSONObject addParams = new JSONObject();
			JSONArray faceIdArray = new JSONArray();
			faceIdArray.put(face_id);
			addParams.put("face_id", faceIdArray);
			addParams.put("person_id", person_id);

			JSONObject objAdd = NJApiServices.getInstance().apiPostJsonData("person", "add_face", addParams);
			if (objAdd == null) {
				return false;
			}
			if (objAdd.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personAddFace失败");
				return false;
			}
			return objAdd.optString("success").equals("true");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean personRemoveFace(String person_id, String face_id) {
		try {
			JSONObject removeParams = new JSONObject();
			JSONArray faceIdArray = new JSONArray();
			faceIdArray.put(face_id);
			removeParams.put("face_id", faceIdArray);
			removeParams.put("person_id", person_id);

			JSONObject objRemove = NJApiServices.getInstance().apiPostJsonData("person", "remove_face", removeParams);
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
	public String faceGetImgId(String face_id) {
		return face_id;
		/*
		try {
			JSONObject objFaces = NJApiServices.getInstance().apiPostJsonData("info", "get_face/",
					new JSONObject().put("face_id", new JSONArray().put(face_id)));
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
		*/
	}

	@Override
	public boolean groupAddPerson(String person_id) {
		try {
			JSONObject joinParams = new JSONObject();
			JSONArray personIdArray = new JSONArray();
			personIdArray.put(person_id);
			joinParams.put("person_id", personIdArray);
			joinParams.put("group_id", GROUP_ID);
			JSONObject objJoin = NJApiServices.getInstance().apiPostJsonData("group", "add_person", joinParams);
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
	public void train() {
	}

	@Override
	public JSONObject detect(byte[] imgData) {
		return NJApiServices.getInstance().apiPost("detection", "detect", null, imgData);
	}

	@Override
	public String detectReturnFaceId(byte[] imgData) {
		try {
			JSONObject objDetect = NJApiServices.getInstance().apiPost("detection", "detect", null, imgData);
			JSONArray facesArray = objDetect.getJSONArray("face");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return null;
			}
			int count = objDetect.getJSONArray("face").length();
			if (count == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return null;
			}
			JSONObject faceJsonOjb = objDetect.getJSONArray("face").getJSONObject(0);
			String face_id = faceJsonOjb.getString("face_id");
			Log.i(TAG, "face_Id = " + face_id);
			return face_id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject compare(String person_id, String face_id, byte[] imgData) {
		if (person_id != null && person_id.length() > 0)
			return compareByPersonId(person_id, imgData);
		else
			return compareByFaceId(face_id, imgData);
	}

	@Override
	public JSONObject compareByPersonId(String person_id, byte[] imgData) {
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
	public JSONObject compareByFaceId(String src_face_id, byte[] imgData) {
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

			JSONObject compareParams = new JSONObject();
			compareParams.put("face_id1", src_face_id);
			compareParams.put("face_id2", new_face_Id);
			JSONObject compareResult = NJApiServices.getInstance().apiPostJsonData("recognition", "compare", compareParams);
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
	public JSONObject identify(byte[] imgData) {
		try {
			String face_id = detectReturnFaceId(imgData);
			if (face_id == null || face_id.length() == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detectionDetect没有识别结果");
			}

			JSONObject identifyParams = new JSONObject();
			identifyParams.put("group_id", GROUP_ID);
			identifyParams.put("face_id", face_id);
			JSONObject identifyResult = NJApiServices.getInstance().apiPostJsonData("recognition", "identify", identifyParams);
			if (identifyResult == null)
				return null;

			JSONObject candidateObj = new JSONObject();
			candidateObj.put("confidence", identifyResult.getDouble("confidence") * 100);
			candidateObj.put("person_id", identifyResult.getString("person_id"));
			candidateObj.put("person_name", identifyResult.getString("person_name"));
			candidateObj.put("face_id", identifyResult.getString("face_id"));
			candidateObj.put("tag", "");
			JSONArray candidateArray = new JSONArray();
			candidateArray.put(candidateObj);

			JSONObject newResult = new JSONObject().putOpt("face", new JSONArray().put(new JSONObject().put("candidate", candidateArray)));
			newResult.put("apiResult", identifyResult.getInt("apiResult"));
			newResult.put("apiResultDesc", identifyResult.getString("apiResultDesc"));
			return newResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject newPerson(String person_name) {
		try {
			//创建Person
			String person_id = personCreate(person_name);
			Log.i(TAG, "personCreate person_id = " + person_id);
			if (person_id == null || person_id.length() == 0) {
				Log.e(TAG, "创建Person失败，请重新再试");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "创建Person失败");
			}

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
	public JSONObject personNewFace(byte[] imgData, String person_id) {
		try {
			String face_id = detectReturnFaceId(imgData);
			if (face_id == null || face_id.length() == 0) {
				Log.i(TAG, "detectionDetect没有识别结果");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "detectionDetect没有识别结果");
			}
			String img_id = face_id;

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
	public JSONObject comparePicture(byte[] imgData1, byte[] imgData2) {
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
			JSONObject compareParams = new JSONObject();
			compareParams.put("face_id1", face_id_1);
			compareParams.put("face_id2", face_id_2);
			JSONObject compareResult = NJApiServices.getInstance().apiPostJsonData("recognition", "compare", compareParams);
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "recognitionCompare失败，请重新再试");
				compareResult.put("result", false);
				return compareResult;
			}
			compareResult.put("result", true);
			Log.i(TAG, "recognitionCompare 耗时 = " + (System.currentTimeMillis() - startMills));
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
		return null;
	}
}
