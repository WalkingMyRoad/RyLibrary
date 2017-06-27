package com.wewins.facelibrary.api.facevisa;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import android.util.Base64;


public class Utils {
	private static String TAG="http";
	
	/**
	 * 接口签名
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static String sign(String appkey, Map<String, String> map) throws Exception {
        // 键名排序
        Set<String> set = map.keySet();
        String[] keys = new String[set.size()];
        set.toArray(keys);
        Arrays.sort(keys);

        // 拼接
        StringBuilder builder = new StringBuilder();
        boolean firstTime = true;
        for (String k : keys) {
            if (firstTime) {
                firstTime = false;
            } else {
                builder.append('&');
            }
            builder.append(k).append('=').append(map.get(k));
        }
        builder.append(appkey);
        String str = builder.toString();
        Log.d(TAG, "md="+str);
        String md5 = encrypt(str);
        // String md5 = DigestUtils.md5Hex(str.getBytes("UTF-8"));
        // String md5 = Md5Utils.md5(str);
        // System.out.println("lib md5:" + DigestUtils.md5Hex(str));
        // System.out.println(" my md5:" + Md5Utils.md5(str));

        return md5;
    }

    static String encrypt(String str) {
        byte[] bytes = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            bytes = messageDigest.digest();
        } catch (Exception e) {
            return "";
        }

        return hex_encode(bytes);
    }

    static String encryptFile(File file) {
        byte[] bytes = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();

            FileInputStream in = new FileInputStream(file);
            byte buffer[] = new byte[1024];
            while (true) {
                int read = in.read(buffer);
                if (read < 1) {
                    break;
                }
                messageDigest.update(buffer, 0, read);
            }
            in.close();
            bytes = messageDigest.digest();
        } catch (Exception e) {
            return "";
        }
        return hex_encode(bytes);
    }

    static String hex_encode(byte bytes[]) {
        final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(hex[(b >> 4) & 0x0F]);
            builder.append(hex[b & 0x0F]);
        }
        return builder.toString();
    }

    static byte[] hex_decode(String str) {
        byte result[] = new byte[str.length() / 2];
        for (int i = 0; i < result.length; i++) {
            String sub = str.substring(2 * i, 2 * i + 2);
            result[i] = Integer.valueOf(sub, 16).byteValue();
        }
        return result;
    }

    static String base64_encode(byte bytes[]) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    static byte[] base64_decode(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

}