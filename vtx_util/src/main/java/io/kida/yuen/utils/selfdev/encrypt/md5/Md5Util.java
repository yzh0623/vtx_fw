package io.kida.yuen.utils.selfdev.encrypt.md5;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: Md5Util.java
 * @ClassName: Md5Util
 * @Description:md5 工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class Md5Util {

    private static final char[] DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final String MESSAGE_DIGEST = "MD5";
    private static MessageDigest digest;

    /**
     * 初始化生成了密钥生成器
     */
    static {
        try {
            digest = MessageDigest.getInstance(MESSAGE_DIGEST);
        } catch (NoSuchAlgorithmException e) {
            log.error("func[Md5Util.static] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
    }

    private Md5Util() {}

    /**
     * 
     * @MethodName: encrypt
     * @Description: MD5加密
     * @author yuanzhenhui
     * @param plainText
     * @return String
     * @date 2023-10-11 05:10:20
     */
    public static String encrypt(String plainText) {
        digest.update(plainText.getBytes(StandardCharsets.UTF_8));
        return new String(encodeHex(digest.digest()));
    }

    /**
     * 
     * @MethodName: encodeHex
     * @Description: md5加密转换16进制
     * @author yuanzhenhui
     * @param data
     * @return char[]
     * @date 2023-10-11 05:10:29
     */
    private static char[] encodeHex(byte[] data) {
        char[] out = new char[data.length * 2];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS[(data[i] & 0xF0) >>> 4];
            out[j++] = DIGITS[data[i] & 0x0F];
        }
        return out;
    }
}
