package io.kida.yuen.utils.selfdev.encrypt.ascii;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: AsciiUtil.java
 * @ClassName: AsciiUtil
 * @Description:ascii 工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
public class AsciiUtil {

    /**
     * 
     * @MethodName: str2Ascii
     * @Description: 字符串转ascii
     * @author yuanzhenhui
     * @param value
     * @return int[]
     * @date 2023-10-11 05:08:25
     */
    public static int[] str2Ascii(String value) {
        int[] asciiArray = new int[value.length()];
        for (int i = 0; i < value.length(); i++) {
            asciiArray[i] = value.charAt(i);
        }
        return asciiArray;
    }

    /**
     * 
     * @MethodName: ascii2Str
     * @Description: ascii转换到字符串
     * @author yuanzhenhui
     * @param chars
     * @return String
     * @date 2023-10-11 05:08:35
     */
    public static String ascii2Str(int... chars) {
        StringBuilder sb = new StringBuilder();
        for (int c : chars) {
            sb.append((char)c);
        }
        return sb.toString();
    }
}
