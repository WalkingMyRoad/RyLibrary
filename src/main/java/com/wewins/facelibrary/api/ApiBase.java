package com.wewins.facelibrary.api;

import org.json.JSONObject;

/*
 * 人脸识别接口抽象
 * DNA 2015-11-10
 */
public abstract class ApiBase {
	//取得Person列表 无输入参数
	public abstract JSONObject getPersonList ();

	//取得一个Person信息
	public abstract JSONObject personGetInfo (String person_id);

	//新建一个Person
	public abstract String personCreate (String person_name);

	//Person删除
	public abstract boolean personDelete (String person_id);

	//Person增加Face
	public abstract boolean personAddFace (String person_id, String face_id);

	//Person删除Face
	public abstract boolean personRemoveFace (String person_id, String face_id);

	//取得Face相关的img_id
	public abstract String faceGetImgId (String face_id);

	//将Person加入到群组中
	public abstract boolean groupAddPerson (String person_id);

	//模型训练
	public abstract void train ();

	//图片人脸检测
	public abstract JSONObject detect (byte[] imgData);

	//图片人脸检测 返回FaceID
	public abstract String detectReturnFaceId (byte[] imgData);

	//人脸比较
	public abstract JSONObject compare (String person_id, String face_id, byte[] imgData);

	//人脸比较，根据PersonID进行识别
	public abstract JSONObject compareByPersonId (String person_id, byte[] imgData);

	//人脸比较，根据FaceID进行识别
	public abstract JSONObject compareByFaceId (String src_face_id, byte[] imgData);

	//图片比较
	public abstract JSONObject comparePicture (byte[] imgData1, byte[] imgData2);

	public abstract JSONObject comparePicture (String imgFileName1, String imgFileName2);

	//人脸识别
	public abstract JSONObject identify (byte[] imgData);

	//新增一个Person，将多个接口整合在一起
	public abstract JSONObject newPerson (String person_name);

	//Person新增一张Face，将多个接口整合在一起
	public abstract JSONObject personNewFace (byte[] imgData, String person_id);
}
