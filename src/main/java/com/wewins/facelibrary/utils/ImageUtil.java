package com.wewins.facelibrary.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.wewins.facelibrary.api.ApiConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

	private static final String TAG = "ImageUtil";

	public static void prepareMatrix (Matrix matrix, boolean mirror, int displayOrientation, int viewWidth, int viewHeight) {
		// Need mirror for front camera.
		matrix.setScale(mirror ? -1 : 1, 1);
		// This is the value for android.hardware.Camera.setDisplayOrientation.
		matrix.postRotate(displayOrientation);
		// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
		// UI coordinates range from (0, 0) to (width, height).
		matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
		matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
	}

	/**
	 * 旋转Bitmap
	 *
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap (Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	// 从文件中读取bitmap
	public static Bitmap getScaledBitmap (String fileName) {
		return getScaledBitmap(fileName, ApiConstants.PIC_FILE_SIZE, false);
	}

	private static Bitmap getScaledBitmap (String fileName, int dstWidth) {
		try {
			BitmapFactory.Options localOptions = new BitmapFactory.Options();
			localOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, localOptions);
			int originWidth = localOptions.outWidth;
			int originHeight = localOptions.outHeight;

			localOptions.inSampleSize = originWidth > originHeight ? originWidth / dstWidth : originHeight / dstWidth;
			localOptions.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(fileName, localOptions);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 从文件中读取bitmap并自动调整方向
	private  static Bitmap getScaledBitmap (String fileName, int dstWidth, boolean adjustOritation) {
		try {
			Bitmap bitmap = getScaledBitmap(fileName, dstWidth);
			if (!adjustOritation) {
				return bitmap;
			}
			int digree = 0;
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(fileName);
			} catch (IOException e) {
				e.printStackTrace();
				exif = null;
			}
			if (exif != null) { //读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						digree = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						digree = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						digree = 270;
						break;
					default:
						digree = 0;
						break;
				}
			}
			if (digree != 0) { //旋转图片
				Matrix m = new Matrix();
				m.postRotate(digree);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 从文件中读取arrayImg
	public static byte[] getScaledBitmapByteArray (String fileName) {
		return getScaledBitmapByteArray(fileName, ApiConstants.PIC_FILE_SIZE);
	}

	public static byte[] getScaledBitmapByteArray(String fileName, int dstWidth) {
		Log.i(TAG, "src file size = " + FileSizeUtil.getAutoFileOrFilesSize(fileName));
		Bitmap bitmap = getScaledBitmap(fileName, ApiConstants.PIC_FILE_SIZE, true);
		if (bitmap == null) {
			Log.e(TAG, fileName + "图片加载");
			return null;
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, ApiConstants.PIC_COMPRESS, outStream);
		//String new_file = FileUtil.saveBitmap(bitmap);
		//Log.i(TAG, "new file = " + new_file + ", dst file size = " + FileSizeUtil.getAutoFileOrFilesSize(new_file));
		return outStream.toByteArray();
	}

	//得到Bitmap的大小

	public static long getBitmapByte (Bitmap b) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.JPEG, ApiConstants.PIC_COMPRESS, baos);//这里100的话表示不压缩质量
		return baos.toByteArray().length / 1024;//读出图片的kb大小
	}

	public static Bitmap StringtoBitmap (String string) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap convertBmp(Bitmap bmp) {
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1); // 镜像水平翻转
		Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

		return convertBmp;
	}

}
