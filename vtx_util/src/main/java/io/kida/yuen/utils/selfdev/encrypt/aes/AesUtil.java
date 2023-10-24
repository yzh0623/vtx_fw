/**
 * @@Copyright by kida yuan All right reserved.
 * @ @FilePath: AESUtil.java
 * @Description: aes 工具类
 * @Author: yzh0623@outlook.com
 * @Date: 2022-04-28 12:00:33
 * @LastEditTime: 2023-08-31 16:38:43
 */

package io.kida.yuen.utils.selfdev.encrypt.aes;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: AesUtil.java
 * @ClassName: AesUtil
 * @Description: aes工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class AesUtil {

    private static final String CRYPTO_AES = "AES";
    private static final String CRYPTO_SLASH = "/";
    private static final String CRYPTO_PADDING = "PKCS7Padding";
    private static final String CRYPTO_ENCRYPTION = "ECB";
    private static final int CRYPTO_KEYSIZE = 256;

    private static KeyGenerator aesGener;
    private static Cipher cipher;

    /**
     * 初始化生成了密钥生成器
     */
    static {

        try {
            Security.addProvider(new BouncyCastleProvider());
            aesGener = KeyGenerator.getInstance(CRYPTO_AES);
            aesGener.init(CRYPTO_KEYSIZE);
            cipher = Cipher.getInstance(CRYPTO_AES + CRYPTO_SLASH + CRYPTO_ENCRYPTION + CRYPTO_SLASH + CRYPTO_PADDING);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("func[AesUtil.static] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
    }

    private AesUtil() {}

    /**
     * 
     * @MethodName: encrypt
     * @Description: aes加密
     * @author yuanzhenhui
     * @param str
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:06:20
     */
    public static byte[] encrypt(String str, String key) {
        byte[] reEncryptByte = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), CRYPTO_AES));
            byte[] doFinal = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            reEncryptByte = Base64.getEncoder().encode(doFinal);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[AesUtil.encrypt] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }

        return reEncryptByte;
    }

    /**
     * 
     * @MethodName: decrypt
     * @Description: aes 解密
     * @author yuanzhenhui
     * @param str
     * @param key
     * @return String
     * @date 2023-10-11 05:06:53
     */
    public static String decrypt(String str, String key) {
        String reDecryptStr = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), CRYPTO_AES));
            reDecryptStr = new String(cipher.doFinal(Base64.getDecoder().decode(str)), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[AesUtil.decrypt] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return reDecryptStr;
    }

}
