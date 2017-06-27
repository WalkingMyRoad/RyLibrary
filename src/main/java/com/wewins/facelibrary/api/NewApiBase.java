package com.wewins.facelibrary.api;

import org.json.JSONObject;

/*
 * 人脸识别接口抽象(新版本)
 * DNA 2016-12-13
 */
public abstract class NewApiBase {
	//为一个已经创建的FaceSet添加人脸标识face_token
	public abstract JSONObject facesetAddface (String faceset_token, String face_tokens);

	//移除一个FaceSet中的某些或者全部face_token
	public abstract JSONObject facesetRemoveface (String faceset_token, String face_tokens);

	//获取一个FaceSet的所有信息
	public abstract JSONObject facesetGetdetail (String faceset_token);

	//图片人脸检测
	public abstract JSONObject detect (byte[] imgData);

	//图片人脸检测 返回FaceID
	public abstract JSONObject detectReturnFaceId (byte[] imgData);

	//在Faceset中找出与目标人脸最相似的一张或多张人脸。
	public abstract JSONObject search (String faceset_token, byte[] imgData);

	//为检测出的某一个人脸添加标识信息
	public abstract JSONObject setUserId (String face_token, String user_id);

	//通过传入在Detect API检测出的人脸标识face_token，获取一个人脸的关联信息，包括源图片ID、归属的FaceSet。
	public abstract JSONObject faceGetDetail(String face_token);

	public abstract JSONObject faceAnalyze(String face_token);

	public abstract JSONObject compareFace(byte[] imgData1,byte[] imgData2);

	public abstract JSONObject createFaceset(String ace_tokens);
}
