package com.ist.rylibrary.face.services;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ist.rylibrary.face.util.Constants;


public class GoogleFaceDetect implements FaceDetectionListener {
	private static final String TAG = "GoogleFaceDetect";
	private Context mContext;
	private Handler mHander;

	public GoogleFaceDetect(Context c, Handler handler) {
		mContext = c;
		mHander = handler;
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		Log.i(TAG, "onFaceDetection..."+faces);

		if (faces != null) {
			Message m = mHander.obtainMessage();
			m.what = Constants.FACE_DETECTED;
			m.obj = faces;
			m.sendToTarget();
		}
	}
}
