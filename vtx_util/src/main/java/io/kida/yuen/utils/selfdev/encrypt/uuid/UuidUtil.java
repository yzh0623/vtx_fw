package io.kida.yuen.utils.selfdev.encrypt.uuid;

import java.security.SecureRandom;
import java.util.stream.IntStream;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: UuidUtil.java
 * @ClassName: UuidUtil
 * @Description:uuid生成器
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
public class UuidUtil {

    private static final char[] DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private UuidUtil() {}

    /**
     * 
     * @MethodName: getCustomUuid
     * @Description: 自定义位UUID 计算公式为 1-e^(-(10^10)^2/62^size)
     * @author yuanzhenhui
     * @param size
     * @return String
     * @date 2023-10-11 05:22:16
     */
    public static String getCustomUuid(int size) {
        // 获取随机实例
        char[] cArray = new char[size];
        IntStream.range(0, size).forEach(i -> cArray[i] = DIGITS[SECURE_RANDOM.nextInt(DIGITS.length)]);
        return new String(cArray);
    }

    /**
     * 
     * @MethodName: getUuid
     * @Description: 最后封装一次
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 05:22:24
     */
    public static String getUuid() {
        return getCustomUuid(16);
    }

}
