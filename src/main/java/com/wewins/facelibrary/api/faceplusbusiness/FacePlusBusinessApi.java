package com.wewins.facelibrary.api.faceplusbusiness;

import android.util.Log;

import com.wewins.facelibrary.api.ApiBase;
import com.wewins.facelibrary.api.ApiConstants;
import com.wewins.facelibrary.utils.ImageUtil;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Face++ 商业版Api接口
 * DNA 2015-11-10
 */
public class FacePlusBusinessApi extends ApiBase {
	final static String TAG = "FacePlusBusinessApi";
	final static String APP_KEY = "zkteco.uK3PmZlW80KELLISPgccnRxQmrbnevv0";
	final static String APP_SEC = "UT6tcUWiyVy2zNC8IhFd4r33Jb3cpZrS";
	final static String GROUP_ID = "Znp9doB50Px9_O4lHjtrLEea9ca7aj-K";

	HttpRequests httpRequest = null;// 在线api

	public FacePlusBusinessApi () {
		httpRequest = new HttpRequests(APP_KEY, APP_SEC);
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
			JSONObject objPeopleList = httpRequest.http_get("v3", "groups/" + GROUP_ID);
			if (objPeopleList == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			//返回的JSON字段不同，需要转换 将People转换成Person
			if (objPeopleList.getJSONArray("people").length() > 0) {
				JSONObject newResult = new JSONObject();
				JSONArray personArray = new JSONArray();

				JSONArray peopleArray = objPeopleList.getJSONArray("people");
				for (int iPeople = 0; iPeople < peopleArray.length(); iPeople++) {
					String id = peopleArray.getJSONObject(iPeople).getString("id");
					String tag = peopleArray.getJSONObject(iPeople).getString("tag");
					JSONObject personObj = new JSONObject();
					personObj.put("person_id", id);
					personObj.put("person_name", tag);
					personArray.put(personObj);
				}
				newResult.put("person", personArray);
				newResult.put("apiResult", objPeopleList.getInt("apiResult"));
				newResult.put("apiResultDesc", objPeopleList.getString("apiResultDesc"));
				return newResult;
			} else
				return objPeopleList;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject personGetInfo (String person_id) {
		try {
			JSONObject objInfo = httpRequest.http_get("v3", "people/" + person_id);
			if (objInfo == null)
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			//返回的JSON字段不同，需要转换将faces转换成face
			if (objInfo.getJSONArray("faces").length() > 0) {
				JSONObject newResult = new JSONObject();
				JSONArray newFaceArray = new JSONArray();

				JSONArray facesArray = objInfo.getJSONArray("faces");
				for (int iFace = 0; iFace < facesArray.length(); iFace++) {
					String face_id = facesArray.getString(iFace);
					JSONObject newFaceObj = new JSONObject();
					newFaceObj.put("face_id", face_id);
					newFaceArray.put(newFaceObj);
				}
				newResult.put("face", newFaceArray);
				newResult.put("apiResult", objInfo.getInt("apiResult"));
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
	public String personCreate (String person_name) {
		try {
			PostParameters createParams = new PostParameters();
			createParams.setTag(person_name);
			JSONObject objCreate = httpRequest.http_post("POST", "v3", "people/", createParams);
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
			JSONObject objDelete = httpRequest.http_delete("DELETE", "v3", "people/" + person_id, new PostParameters());
			if (objDelete == null) {
				return false;
			}
			return objDelete.getInt("apiResult") == HttpStatus.SC_OK;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean personAddFace (String person_id, String face_id) {
		try {
			PostParameters addParams = new PostParameters();
			addParams.setFaceId(face_id);
			JSONObject objAdd = httpRequest.http_post("POST", "v3", "people/" + person_id + "/", addParams);
			if (objAdd == null) {
				return false;
			}
			if (objAdd.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "personAddFace失败");
				return false;
			}
			train();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean personRemoveFace (String person_id, String face_id) {
		try {
			JSONObject objRemove = httpRequest.http_delete("DELETE", "v3", "people/" + person_id + "/" + face_id, new PostParameters());
			if (objRemove == null) {
				return false;
			}
			//if (objRemove.getInt("apiResult") != HttpStatus.SC_OK) {
			//	Log.e(TAG, "personRemoveFace失败");
			//	return false;
			//}

			train();
			return objRemove.getInt("apiResult") == HttpStatus.SC_OK;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String faceGetImgId (String face_id) {
		return face_id; //商业版，没有返回img_id，用face_id代替
	}

	@Override
	public boolean groupAddPerson (String person_id) {
		try {
			PostParameters joinParams = new PostParameters();
			joinParams.setPersonId(person_id);
			JSONObject objJoin = httpRequest.http_post("POST", "v3", "groups/" + GROUP_ID + "/", joinParams);
			if (objJoin == null) {
				return false;
			}
			return objJoin.getInt("apiResult") == HttpStatus.SC_OK;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void train () {
		//这个版本不需要Train
	}

	@Override
	public JSONObject detect (byte[] imgData) {
		try {
			PostParameters detectParams = new PostParameters();
			detectParams.setImg(imgData);
			JSONObject objDetect = httpRequest.http_post("POST", "v3", "detect", detectParams);
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
			JSONObject objDetect = httpRequest.http_post("POST", "v3", "detect", detectParams);
			if (objDetect == null)
				return null;
			JSONArray facesArray = objDetect.getJSONArray("faces");
			if (facesArray == null || facesArray.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return null;
			}
			int count = facesArray.length();
			if (count == 0) {
				Log.i(TAG, "detect没有识别结果");
				return null;
			}
			//取得第一张人脸的数据
			JSONObject faceJsonOjb = facesArray.getJSONObject(0);
			String face_Id = faceJsonOjb.getString("id");
			Log.i(TAG, "face_Id = " + face_Id);
			return face_Id;
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
		try {
			String new_face_id = detectReturnFaceId(imgData);
			Log.i(TAG, "new_face_Id = " + new_face_id);
			if (new_face_id == null || new_face_id.length() == 0) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}

			PostParameters compareParams = new PostParameters();
			compareParams.setPersonId(person_id);
			compareParams.setFaceId(new_face_id);
			JSONObject verifyResult = httpRequest.http_post("POST", "v3", "verify", compareParams);
			if (verifyResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (verifyResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "verify失败，请重新再试");
				return GenerateResultJSON(verifyResult.getInt("apiResult"), "verify失败");
			}
			JSONObject newResult = new JSONObject(verifyResult.toString());
			newResult.put("similarity", verifyResult.getString("confidence"));
			return newResult;
		} catch (Exception e) {
			Log.i(TAG, "compare出错");
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		}
	}

	@Override
	public JSONObject compareByFaceId (String src_face_id, byte[] imgData) {
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
	public JSONObject identify (byte[] imgData) {
		try {
			PostParameters recognizeParams = new PostParameters();
			recognizeParams.setGroupId(GROUP_ID);
			recognizeParams.setImg(imgData);
			JSONObject recognizeResult = httpRequest.http_post("POST", "v3", "recognize", recognizeParams);
			if (recognizeResult == null || recognizeResult.optJSONObject("person") == null)
				return null;

			JSONObject candidateObj = new JSONObject();
			candidateObj.put("confidence", recognizeResult.getString("confidence"));
			candidateObj.put("person_id", recognizeResult.getJSONObject("person").getString("id"));
			candidateObj.put("person_name", recognizeResult.getJSONObject("person").getString("tag"));
			candidateObj.put("tag", "");
			JSONArray candidateArray = new JSONArray();
			candidateArray.put(candidateObj);

			JSONObject newResult = new JSONObject().putOpt("face", new JSONArray().put(new JSONObject().put("candidate", candidateArray)));
			newResult.put("apiResult", recognizeResult.getInt("apiResult"));
			newResult.put("apiResultDesc", recognizeResult.getString("apiResultDesc"));
			return newResult;
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

			//加入到Group
			groupAddPerson(person_id);

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
			//得到face_id
			String face_id = detectReturnFaceId(imgData);
			if (face_id == null || face_id.length() == 0) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
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
	public JSONObject comparePicture (byte[] imgData1, byte[] imgData2) {
		long globalMills;
		long startMills;

		Log.i(TAG, "comparePicture, 开始");
		globalMills = System.currentTimeMillis();
		try {
			//第一张照片的上传、识别
			startMills = System.currentTimeMillis();
			String feature1 = faceExctract(imgData1);
			if (feature1 == null || feature1.length() == 0) {
				Log.e(TAG, "图片1没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片1没有识别到人脸，比较失败");
			}
			Log.i(TAG, "faceExctract1, 耗时 = " + (System.currentTimeMillis() - startMills));

			//第二张照片上传、识别
			startMills = System.currentTimeMillis();
			String feature2 = faceExctract(imgData2);
			if (feature2 == null || feature2.length() == 0) {
				Log.e(TAG, "图片2没有识别到人脸，比较失败");
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, "图片2没有识别到人脸，比较失败");
			}
			Log.i(TAG, "faceExctract2, 耗时 = " + (System.currentTimeMillis() - startMills));

			//2张图片的feature都取到后，进行比对
			startMills = System.currentTimeMillis();
			PostParameters compareParams = new PostParameters();
			compareParams.addAttribute("feature1", feature1);
			compareParams.addAttribute("feature2", feature2);
			JSONObject compareResult = httpRequest.http_post("POST", "face", "compare", compareParams);
			Log.i(TAG, "face_compare 耗时 = " + (System.currentTimeMillis() - startMills));
			if (compareResult == null) {
				return GenerateResultJSON(HttpStatus.SC_NOT_FOUND);
			}
			if (compareResult.getInt("apiResult") != HttpStatus.SC_OK) {
				Log.e(TAG, "face_compare失败，请重新再试");
				compareResult.put("result", false);
				return compareResult;
			}
			compareResult.put("similarity", compareResult.getDouble("confidence"));
			compareResult.put("result", true);
			return compareResult;
		} catch (Exception e) {
			e.printStackTrace();
			return GenerateResultJSON(HttpStatus.SC_NOT_FOUND, e.getMessage().toString());
		} finally {
			Log.i(TAG, "comparePicture, 结束" + ", 耗时 = " + (System.currentTimeMillis() - globalMills));
		}
	}

	//抽取图片的特征值
	public String faceExctract (byte[] imgData) {
		try {
			PostParameters extractParams = new PostParameters();
			extractParams.setImg(imgData);
			JSONObject objExtract = httpRequest.http_post("POST", "face", "extract", extractParams);
			if (objExtract == null)
				return null;
			String sSuccess = objExtract.optString("success");
			if (sSuccess == null || sSuccess.length() == 0) {
				Log.e(TAG, "图片中没有检测到人脸");
				return null;
			}
			return objExtract.optString("feature");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JSONObject comparePicture (String imgFileName1, String imgFileName2) {
		byte[] imgArray1 = ImageUtil.getScaledBitmapByteArray(imgFileName1);
		byte[] imgArray2 = ImageUtil.getScaledBitmapByteArray(imgFileName2);
		return comparePicture(imgArray1, imgArray2);
	}
}
