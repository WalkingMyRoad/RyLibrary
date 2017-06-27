package com.ist.rylibrary.face.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ist.rylibrary.R;
import com.ist.rylibrary.base.application.RyApplication;

import com.wewins.facelibrary.utils.ImageUtil;

public class FaceView extends ImageView {
	private static final String TAG = "FaceView";
	private final double validAreaScale = 0.5; //有效区域百分比

	private Context mContext;
	private RyApplication appData;

	private Face[] mFaces;
	private Matrix mMatrix = new Matrix();
	private RectF mRect = new RectF();
	private Drawable mFaceIndicator = null;

	private Paint mCenterRectPaint;
	private Paint mAroundPaint;
	private Rect mCenterRect;
	int iScreenWidth, iScreenHeight;

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) { return; }
		mContext = context;
		appData = (RyApplication) mContext.getApplicationContext();
		mFaceIndicator = getResources().getDrawable(R.drawable.ic_face_find_2);
		initPaint();
	}

	public void setFaces(Face[] faces) {
		this.mFaces = faces;
		invalidate();
	}

	public void clearFaces() {
		mFaces = null;
		invalidate();
	}

	private void initPaint() {
		//得到屏幕大小
		//Point p = DisplayUtil.getScreenMetrics(mContext);
		//iScreenWidth = p.x;
		//iScreenHeight = p.y;
		iScreenWidth = appData.getMywmParams().width;
		iScreenHeight = appData.getMywmParams().height;
		int iCenterRectWidth = (int) Math.floor(iScreenWidth * validAreaScale);
		int iCenterRectHeight = (int) Math.floor(iScreenHeight * validAreaScale);

		mCenterRect = new Rect();
		mCenterRect.left = (int) Math.floor((iScreenWidth - iCenterRectWidth) / 2);
		mCenterRect.top = (int) Math.floor((iScreenHeight - iCenterRectHeight) / 2);
		mCenterRect.right = mCenterRect.left + iCenterRectWidth;
		mCenterRect.bottom = mCenterRect.top + iCenterRectHeight;

		//绘制中间透明区域矩形边界的Paint  
		mCenterRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterRectPaint.setColor(Color.BLUE);
		mCenterRectPaint.setStyle(Style.STROKE);
		mCenterRectPaint.setStrokeWidth(5f);
		mCenterRectPaint.setAlpha(30);

		//绘制四周阴影区域  
		mAroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mAroundPaint.setColor(Color.GRAY);
		mAroundPaint.setStyle(Style.FILL);
		mAroundPaint.setAlpha(180);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*
		//以下画一个范围框
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.BLUE);// 颜色
		rectPaint.setStyle(Paint.Style.STROKE); //空心
		rectPaint.setAlpha(255); //透明度
		int iGrayRectWidth = (int) Math.floor(1080 * 0.8);
		int iGrayRectHeight = (int) Math.floor(1920 * 0.8);
		int iGrayRectLeft = (int) Math.floor((1080 - iGrayRectWidth) / 2);
		int iGrayRectTop = (int) Math.floor((1920 - iGrayRectHeight) / 2);
		int iGrayRectRight = iGrayRectLeft + iGrayRectWidth;
		int iGrayRectButtom = iGrayRectTop + iGrayRectHeight;
		canvas.drawRect(iGrayRectLeft, iGrayRectTop, iGrayRectRight, iGrayRectButtom, rectPaint);// 正方形 
		*/
		//绘制四周阴影区域  
		canvas.drawRect(0, 0, iScreenWidth, mCenterRect.top, mAroundPaint);
		canvas.drawRect(0, mCenterRect.bottom + 1, iScreenWidth, iScreenHeight, mAroundPaint);
		canvas.drawRect(0, mCenterRect.top, mCenterRect.left - 1, mCenterRect.bottom + 1, mAroundPaint);
		canvas.drawRect(mCenterRect.right + 1, mCenterRect.top, iScreenWidth, mCenterRect.bottom + 1, mAroundPaint);

		//绘制目标透明区域  
		canvas.drawRect(mCenterRect, mCenterRectPaint);

		if (mFaces != null && mFaces.length > 0) {
			boolean isMirror = false;
			if (appData.getDefaultCamera() == CameraInfo.CAMERA_FACING_BACK) {
				isMirror = false; //后置Camera无需mirror
			} else if (appData.getDefaultCamera() == CameraInfo.CAMERA_FACING_FRONT) {
				isMirror = true; //前置Camera需要mirror
			}
			ImageUtil.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
			canvas.save();
			Paint textPaint = new Paint();
			textPaint.setColor(Color.BLUE);
			textPaint.setTextSize(80);
			mMatrix.postRotate(0); //Matrix.postRotate默认是顺时针
			canvas.rotate(-0); //Canvas.rotate()默认是逆时针 
			for (int i = 0; i < mFaces.length; i++) {
				mRect.set(mFaces[i].rect);
				Log.i(TAG, "face rect = " + mFaces[i].rect.toString());
				mMatrix.mapRect(mRect);
				mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top), Math.round(mRect.right), Math.round(mRect.bottom));
				canvas.drawText(String.valueOf(Math.round(mRect.left)), Math.round(mRect.left), Math.round(mRect.top), textPaint);// 画文本
				mFaceIndicator.draw(canvas);
				//			canvas.drawRect(mRect, mLinePaint);
			}
			canvas.restore();
		} else return;
		super.onDraw(canvas);
	}

}
