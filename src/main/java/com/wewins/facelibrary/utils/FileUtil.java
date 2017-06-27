package com.wewins.facelibrary.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.wewins.facelibrary.api.ApiConstants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String root_storage = "";
	private static String idcard_folder = "";
	private static String src_folder = "";
	private static final String ROOT_FOLDER_NAME = "FaceDetectV2";
	private static final String IDCARD_FOLDER_NAME = "IDCard";
	private static final String SRC_FOLDER_NAME = "SRC";
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "FaceDetect";

	/**
	 * 初始化保存路径
	 *
	 * @return
	 */
	private static String initRootPath () {
		if (root_storage.equals("")) {
			root_storage = parentPath.getAbsolutePath() + "/" + ROOT_FOLDER_NAME;
			File f = new File(root_storage);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return root_storage;
	}

	/**
	 * 退出时删除
	 */
	public static void deleteResouce () {
		String path = parentPath.getAbsolutePath() + "/" + ROOT_FOLDER_NAME;
		if (path != null && !path.equals("")) {
			File file = new File(path);
			if (file.exists()) {
				Log.i(TAG, "图片目录存在");
				File[] f = file.listFiles();
				if (f.length > 0) {
					for (int i = 0; i < f.length; i++) {
						f[i].delete();
					}
				}
			}
		}
		Log.i(TAG, "图片删除成功");
	}

	private static String initIDCardPath () {
		if (idcard_folder.equals("")) {
			idcard_folder = parentPath.getAbsolutePath() + "/" + ROOT_FOLDER_NAME + "/" + IDCARD_FOLDER_NAME;
			File f = new File(idcard_folder);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return idcard_folder;
	}

	private static String initSrcPath () {
		if (src_folder.equals("")) {
			src_folder = parentPath.getAbsolutePath() + "/" + ROOT_FOLDER_NAME + "/" + SRC_FOLDER_NAME;
			File f = new File(src_folder);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return src_folder;
	}

	public static String getIDCardFolder () {
		return initIDCardPath();
	}

	public static String getSRCFolder () {
		return initSrcPath();
	}

	/**
	 * 保存Bitmap到sdcard
	 *
	 * @param b
	 */
	public static String saveBitmap (Bitmap b) {
		String path = initRootPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake + ".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, ApiConstants.PIC_COMPRESS, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap成功");
			return jpegName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "saveBitmap:失败");
			e.printStackTrace();
			return null;
		}
	}

	public static String getJpegFileName () {
		String path = initRootPath();
		long dataTake = System.currentTimeMillis();
		return path + "/" + dataTake + ".jpg";
	}

	public static String getIDJpegFileName (String name) {
		String path = initSrcPath();
		//long dataTake = System.currentTimeMillis();
		return path + "/" + name + ".jpg";
	}

	public static boolean fileExists (String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static String getFileName (String pathAndName) {

		int start = pathAndName.lastIndexOf("/");
		int end = pathAndName.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathAndName.substring(start + 1, end);
		} else {
			return null;
		}
	}

	//读取身份证文件列表
	public static List getIDCardFiles () {
		List fileList = new ArrayList();
		String picPath = getIDCardFolder();
		File file = new File(picPath);
		File[] files = file.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			final File f = files[i];
			if (f.isFile()) {
				try {
					int idx = f.getPath().lastIndexOf(".");
					if (idx == 0) {
						continue;
					}
					String suffix = f.getPath().substring(idx);
					if (suffix.toLowerCase().equals(".jpg")) {
						fileList.add(f.getPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(fileList);
		return fileList;
	}

	//读取SRC文件列表
	public static List getSrcFiles () {
		List fileList = new ArrayList();
		String picPath = getSRCFolder();
		File file = new File(picPath);
		File[] files = file.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			final File f = files[i];
			if (f.isFile()) {
				try {
					int idx = f.getPath().lastIndexOf(".");
					if (idx == 0) {
						continue;
					}
					String suffix = f.getPath().substring(idx);
					if (suffix.toLowerCase().equals(".jpg")) {
						fileList.add(f.getPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(fileList);
		return fileList;
	}

	public static void deleteFile (String fileName) {
		File file = new File(fileName);
		try {
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void fileDelete(String serialNo, String fileName) {
		File file = new File(fileName);
		boolean bResult = file.delete();
		Log.i(TAG, "SerialNo = " + serialNo + ", 文件删除，Result = " + bResult);
	}
	/**
	 * 保存Bitmap到sdcard
	 *
	 * @param b
	 */
	public static String saveBitmap(String serialNo, Bitmap b) {
		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake + ".jpg";
		Log.i(TAG, "SerialNo = " + serialNo + ", saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "SerialNo = " + serialNo + ", saveBitmap成功");
			return jpegName;
		} catch (IOException e) {
			Log.i(TAG, "SerialNo = " + serialNo + ", saveBitmap:失败");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 初始化保存路径
	 *
	 * @return
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}
}
