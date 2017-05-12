package com.ist.rylibrary.base.entity;

/**
 * User: guansheng Date: 2013/12/25
 * 
 */
public class Version {

	// 版本号
	private int versionCode;
	// apk文件
	private String apkFile;
	// 差异文件
	private String diffFile;
	private boolean enforce;
	// 版本更新内容描述
	private String description;
	// md5校验数值
	private String md5Code;
	// 匹配服务端版本
	private String versionCodeName;
	// 是否是增量更新
	private boolean isDiff;
	private  String apkFileName;
	private  String pkg;

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getApkFileName() {
		return apkFileName;
	}

	public void setApkFileName(String apkFileName) {
		this.apkFileName = apkFileName;
	}

	public boolean isEnforce() {
		return enforce;
	}

	public void setEnforce(boolean enforce) {
		this.enforce = enforce;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getApkFile() {
		return apkFile;
	}

	public void setApkFile(String apkFile) {
		this.apkFile = apkFile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMd5Code() {
		return md5Code;
	}

	public void setMd5Code(String md5Code) {
		this.md5Code = md5Code;
	}

	public String getVersionCodeName() {
		return versionCodeName;
	}

	public void setVersionCodeName(String versionCodeName) {
		this.versionCodeName = versionCodeName;
	}

	public String getDiffFile() {
		return diffFile;
	}

	public void setDiffFile(String diffFile) {
		this.diffFile = diffFile;
	}

	public boolean isDiff() {
		return isDiff;
	}

	public void setDiff(boolean isDiff) {
		this.isDiff = isDiff;
	}

}
