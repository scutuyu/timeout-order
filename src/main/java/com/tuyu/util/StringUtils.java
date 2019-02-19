package com.tuyu.util;

import java.util.UUID;

/**
 * String 工具类
 *
 * @author tuyu
 * @date 2/19/19
 * Talk is cheap, show me the code.
 */
public class StringUtils {

    private StringUtils() {
        throw new AssertionError("no StringUtils instance for you!");
    }

    /**
     * 获取uuid
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
