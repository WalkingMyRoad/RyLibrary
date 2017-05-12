package com.ist.rylibrary.base.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音
 * @author gh
 *
 */
public class PinYinUtil {
	private static HanyuPinyinOutputFormat format = null;

	static {
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	/**
	 * 汉字转拼音，非汉字，则保持原样
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinYin(String str) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i) {
			tempPinyin = getPinYin(str.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(str.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}

	/**
	 * 汉字转拼音，首字母大写，非汉字，则保持原样
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinYinFirstToUpperCase(String str) {
		try {
			StringBuilder sb = new StringBuilder();
			String tempPinyin = null;
			for (int i = 0; i < str.length(); ++i) {
				tempPinyin = getPinYin(str.charAt(i));
				if (tempPinyin == null) {
					// 如果str.charAt(i)非汉字，则保持原样
					sb.append(str.charAt(i));
				} else {
					sb.append(captureFirst(tempPinyin));
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	/**
	 * 转换单个字符
	 * 
	 * @param c
	 * @return
	 */
	public static String getPinYin(char c) {
		String[] pinyin = null;
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null)
			return null;

		// 只取一个发音，如果是多音字，仅取第一个发音
		return pinyin[0];
	}

	/**
	 * 首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String captureFirst(String name) {
		char[] cs = name.toCharArray();
		if (cs[0] >= 'a' && cs[0] <= 'z') {
			cs[0] -= 32;
		}
		return String.valueOf(cs);
	}
}