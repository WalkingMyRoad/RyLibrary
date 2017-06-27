package com.wewins.facelibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangwy on 2016-07-12.
 */
public class SequenceUtil {
    private static int sequence = 0;

    public static synchronized String makeSerialNo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sTimeSwap = format.format(new Date());
        int increment = ++sequence;
        String incrementStr = String.valueOf(increment);
        if (incrementStr.length() == 1) {
            incrementStr = "00" + incrementStr;
        } else if (incrementStr.length() == 2) {
            incrementStr = "0" + incrementStr;
        } else if (incrementStr.length() == 3 && incrementStr.equals("999")) {
            sequence = 0;
        }
        return sTimeSwap + incrementStr;
    }
}
