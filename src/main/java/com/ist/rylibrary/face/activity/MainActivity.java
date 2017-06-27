package com.ist.rylibrary.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//StandOutWindow.closeAll(this, CameraActivity.class);
		//StandOutWindow.show(this, CameraActivity.class, CameraActivity.DEFAULT_ID);
		//finish();

		Intent mIntent = new Intent("startFaceDetect");
		mIntent.setClass(getApplicationContext(), FloatCamera.class);
		getApplicationContext().startService(mIntent);
		finish();
	}
}
