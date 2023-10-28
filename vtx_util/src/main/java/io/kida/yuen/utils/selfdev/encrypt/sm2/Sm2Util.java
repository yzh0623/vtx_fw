package io.kida.yuen.utils.selfdev.encrypt.sm2;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: Sm2Util.java
 * @ClassName: Sm2Util
 * @Description:sm2 工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class Sm2Util {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String ELLIPTIC_CURVE_ALGORITHM = "sm2p256v1";
    private static final String ENCRYPTION_TYPE = "EC";
    private static final String PUBLIC_KEY = "Sm2PublicKey";
    private static final String PRIVATE_KEY = "Sm2PrivateKey";

    private static KeyPairGenerator keyPairGenerator;
    private static ECGenParameterSpec sm2Spec;

    /**
     * 初始化生成了密钥生成器
     */
    static {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.addProvider(bcp);
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_TYPE, bcp);
            sm2Spec = new ECGenParameterSpec(ELLIPTIC_CURVE_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("func[Sm2Util.static] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
    }

    private Sm2Util() {}

    /**
     * 
     * @MethodName: encrypt
     * @Description: 根据publicKey对原始数据data，使用SM2加密
     * @author yuanzhenhui
     * @param data
     * @param publicKey
     * @return byte[]
     * @date 2023-10-11 05:18:32
     */
    public static byte[] encrypt(byte[] data, String publicKey) {
        byte[] arrayOfByte2 = null;
        ECPublicKeyParameters localEcPublicKeyParameters = null;
        try {
            BCECPublicKey localEcPublicKey = (BCECPublicKey)createPublicKey(publicKey);
            ECParameterSpec localEcParameterSpec = localEcPublicKey.getParameters();
            ECDomainParameters localEcDomainParameters = new ECDomainParameters(localEcParameterSpec.getCurve(),
                localEcParameterSpec.getG(), localEcParameterSpec.getN());
            localEcPublicKeyParameters = new ECPublicKeyParameters(localEcPublicKey.getQ(), localEcDomainParameters);
            SM2Engine localSm2Engine = new SM2Engine();
            localSm2Engine.init(true, new ParametersWithRandom(localEcPublicKeyParameters, SECURE_RANDOM));
            arrayOfByte2 = localSm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("func[Sm2Util.encrypt] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return arrayOfByte2;
    }

    /**
     * 
     * @MethodName: decrypt
     * @Description: 根据privateKey对加密数据encodeData，使用SM2解密
     * @author yuanzhenhui
     * @param encodeData
     * @param privateKey
     * @return byte[]
     * @date 2023-10-11 05:18:53
     */
    public static byte[] decrypt(byte[] encodeData, String privateKey) {
        byte[] arrayOfByte3 = null;
        try {
            BCECPrivateKey sm2PriK = (BCECPrivateKey)createPrivateKey(privateKey);
            ECParameterSpec localEcParameterSpec = sm2PriK.getParameters();
            ECDomainParameters localEcDomainParameters = new ECDomainParameters(localEcParameterSpec.getCurve(),
                localEcParameterSpec.getG(), localEcParameterSpec.getN());
            ECPrivateKeyParameters localEcPrivateKeyParameters =
                new ECPrivateKeyParameters(sm2PriK.getD(), localEcDomainParameters);
            SM2Engine localSm2Engine = new SM2Engine();
            localSm2Engine.init(false, localEcPrivateKeyParameters);
            arrayOfByte3 = localSm2Engine.processBlock(encodeData, 0, encodeData.length);
        } catch (InvalidCipherTextException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("func[Sm2Util.decrypt] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return arrayOfByte3;
    }

    /**
     * 
     * @MethodName: signByPrivateKey
     * @Description: 私钥签名
     * @author yuanzhenhui
     * @param data
     * @param privateKey
     * @return byte[]
     * @date 2023-10-11 05:19:33
     */
    public static byte[] signByPrivateKey(byte[] data, PrivateKey privateKey) {
        byte[] ret = null;
        try {
            Signature sig = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(),
                BouncyCastleProvider.PROVIDER_NAME);
            sig.initSign(privateKey);
            sig.update(data);
            ret = sig.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("func[Sm2Util.signByPrivateKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return ret;
    }

    /**
     * 
     * @MethodName: verifyByPublicKey
     * @Description: 公钥验签
     * @author yuanzhenhui
     * @param data
     * @param publicKey
     * @param signature
     * @return boolean
     * @date 2023-10-11 05:19:42
     */
    public static boolean verifyByPublicKey(byte[] data, PublicKey publicKey, byte[] signature) {
        boolean ret = false;
        try {
            Signature sig = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(),
                BouncyCastleProvider.PROVIDER_NAME);
            sig.initVerify(publicKey);
            sig.update(data);
            ret = sig.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            log.error("func[Sm2Util.verifyByPublicKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return ret;
    }

    /**
     * 
     * @MethodName: generateSmKey
     * @Description: 生成 sm2 密钥
     * @author yuanzhenhui
     * @return Map<String,Object>
     * @date 2023-10-11 05:20:15
     */
    public static Map<String, Object> generateSmKey() {
        Map<String, Object> keyMap = new HashMap<>(2);
        try {
            keyPairGenerator.initialize(sm2Spec, SECURE_RANDOM);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            keyMap.put(PUBLIC_KEY, keyPair.getPublic());
            keyMap.put(PRIVATE_KEY, keyPair.getPrivate());
        } catch (InvalidAlgorithmParameterException e) {
            log.error("func[Sm2Util.generateSmKey] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return keyMap;
    }

    /**
     * 
     * @MethodName: createPublicKey
     * @Description: 将Base64转码的公钥串，转化为公钥对象
     * @author yuanzhenhui
     * @param publicKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     *             PublicKey
     * @date 2023-10-11 05:20:31
     */
    private static PublicKey createPublicKey(String publicKeyStr)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_TYPE, new BouncyCastleProvider());
        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * 
     * @MethodName: createPrivateKey
     * @Description: 将Base64转码的私钥串，转化为私钥对象
     * @author yuanzhenhui
     * @param privateKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     *             PrivateKey
     * @date 2023-10-11 05:20:40
     */
    private static PrivateKey createPrivateKey(String privateKeyStr)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_TYPE, new BouncyCastleProvider());
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 
     * @MethodName: getPrivateKey
     * @Description: 取得私钥
     * @author yuanzhenhui
     * @param keyMap
     * @return String
     * @date 2023-10-11 05:20:48
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
     * @date 2023-10-11 05:20:56
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key)keyMap.get(PUBLIC_KEY);
        return encryptBase64(key.getEncoded());
    }

    /**
     * 
     * @MethodName: encryptBase64
     * @Description: base64加密
     * @author yuanzhenhui
     * @param key
     * @return String
     * @date 2023-10-11 05:21:04
     */
    private static String encryptBase64(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }
}
