package io.kida.yuen.utils.selfdev.encrypt.rsa;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: RsaUtil.java
 * @ClassName: RsaUtil
 * @Description:rsa 工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class RsaUtil {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String RSA_ENCRYPTION_PADDING = "/ECB/PKCS1Padding";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static KeyFactory keyFactory;
    private static Cipher cipher;
    private static Signature signature;
    private static KeyPairGenerator keyPairGen;

    /**
     * 初始化生成了密钥生成器
     */
    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            cipher = Cipher.getInstance(KEY_ALGORITHM + RSA_ENCRYPTION_PADDING);
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(4096);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("func[RsaUtil.static] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
    }

    private RsaUtil() {}

    /**
     * 
     * @MethodName: decryptByPrivateKey
     * @Description: 用私钥解密
     * @author yuanzhenhui
     * @param data
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:12:49
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) {
        byte[] reDecryptByte = null;
        try {
            byte[] keyBytes = decryptBase64(key);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            reDecryptByte = cipher.doFinal(data);
        } catch (InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[RsaUtil.decryptByPrivateKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return reDecryptByte;
    }

    /**
     * 
     * @MethodName: encryptByPublicKey
     * @Description: 使用公钥加密
     * @author yuanzhenhui
     * @param data
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:13:28
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) {
        byte[] reEcryptByte = null;
        try {
            byte[] keyBytes = decryptBase64(key);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            reEcryptByte = cipher.doFinal(data);
        } catch (InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[RsaUtil.encryptByPublicKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return reEcryptByte;
    }

    /**
     * 
     * @MethodName: decryptByPublicKey
     * @Description: 用公钥解密
     * @author yuanzhenhui
     * @param data
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:13:54
     */
    public static byte[] decryptByPublicKey(byte[] data, String key) {
        byte[] reDecryptByte = null;
        try {
            byte[] keyBytes = decryptBase64(key);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            reDecryptByte = cipher.doFinal(data);
        } catch (InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[RsaUtil.decryptByPublicKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return reDecryptByte;
    }

    /**
     * 
     * @MethodName: encryptByPrivateKey
     * @Description: 使用私钥加密
     * @author yuanzhenhui
     * @param data
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:14:18
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) {
        byte[] reEcryptByte = null;
        try {
            byte[] keyBytes = decryptBase64(key);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            reEcryptByte = cipher.doFinal(data);
        } catch (InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("func[RsaUtil.encryptByPrivateKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return reEcryptByte;
    }

    /**
     * 
     * @MethodName: sign
     * @Description: 用私钥对信息生成数字签名
     * @author yuanzhenhui
     * @param data
     * @param privateKey
     * @return String
     * @date 2023-10-11 05:14:44
     */
    public static String sign(byte[] data, String privateKey) {
        String reSignStr = null;
        try {
            byte[] keyBytes = decryptBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            signature.initSign(priKey);
            signature.update(data);
            reSignStr = encryptBase64(signature.sign());
        } catch (InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            log.error("func[RsaUtil.sign] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return reSignStr;
    }

    /**
     * 
     * @MethodName: verify
     * @Description: 校验数字签名(验证通过 true，验证不通过 false)
     * @author yuanzhenhui
     * @param data
     * @param publicKey
     * @param sign
     * @return boolean
     * @date 2023-10-11 05:15:08
     */
    public static boolean verify(byte[] data, String publicKey, String sign) {
        boolean flag = false;
        try {
            byte[] keyBytes = decryptBase64(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            signature.initVerify(pubKey);
            signature.update(data);
            flag = signature.verify(decryptBase64(sign));
        } catch (InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            log.error("func[RsaUtil.verify] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return flag;
    }

    /**
     * 
     * @MethodName: getPrivateKey
     * @Description: 取得私钥
     * @author yuanzhenhui
     * @param keyMap
     * @return String
     * @date 2023-10-11 05:15:31
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key)keyMap.get(PRIVATE_KEY);
        return encryptBase64(key.getEncoded());
    }

    /**
     * 
     * @MethodName: getPublicKey
     * @Description: 取得公钥
     * @author yuanzhenhui
     * @param keyMap
     * @return String
     * @date 2023-10-11 05:15:39
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key)keyMap.get(PUBLIC_KEY);
        return encryptBase64(key.getEncoded());
    }

    /**
     * 
     * @MethodName: initKey
     * @Description: 初始化密钥
     * @author yuanzhenhui
     * @return Map<String,Object>
     * @date 2023-10-11 05:15:47
     */
    public static Map<String, Object> initKey() {
        KeyPair keyPair = keyPairGen.generateKeyPair();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, (RSAPublicKey)keyPair.getPublic());
        keyMap.put(PRIVATE_KEY, (RSAPrivateKey)keyPair.getPrivate());
        return keyMap;
    }

    /**
     * 
     * @MethodName: decryptBase64
     * @Description: base64解密
     * @author yuanzhenhui
     * @param key
     * @return byte[]
     * @date 2023-10-11 05:15:56
     */
    private static byte[] decryptBase64(String key) {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        return decoder.decode(key);
    }

    /**
     * 
     * @MethodName: encryptBase64
     * @Description: base64加密
     * @author yuanzhenhui
     * @param key
     * @return String
     * @date 2023-10-11 05:16:03
     */
    private static String encryptBase64(byte[] key) {
        Base64.Encoder encoder = Base64.getMimeEncoder();
        return encoder.encodeToString(key);
    }
}
