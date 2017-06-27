package com.ist.rylibrary.base.util;

/**
 * Created by maxy
 * on 2017/5/10.
 */
public class Utils {
    /**
     * 随机3位数
     *
     * @return
     */
    public static int getRandom() {
        return getRandom(100, 999);
    }

    /**
     * 生成规定上下限的整型随机数
     *
     * @param start
     * @param end
     * @return
     */

    public static int getRandom(double start, double end) {
        return (int) (Math.random() * (start - end) + end);
    }


}
