package com.wewins.facelibrary.api.facevisa;

/**
 * MultipartJob
 * @author   WuString
 * @version  1.1.201608241717
 */
public class Multipart {
	public String type;
	public String name;
	public byte[] data;
	
	public Multipart(String name, String type, byte[] data) {
		this.name = name;
		this.type = type;
		this.data = data;
	}
}