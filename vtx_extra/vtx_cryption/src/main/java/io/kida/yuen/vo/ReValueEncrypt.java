package io.kida.yuen.vo;

import io.kida.yuen.utils.system.router.RouterValue;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_cryption
 * @File: ReValueEncrypt.java
 * @ClassName: ReValueEncrypt
 * @Description:在原来返回信息的基础上加上加盐钥匙
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class ReValueEncrypt extends RouterValue {

    private String encryptKey;

    public ReValueEncrypt(Integer retCode, Object retData, String retMsg) {
        super(retCode, retData, retMsg);
    }

    public ReValueEncrypt(Integer retCode, Object retData, String retMsg, String encryptKey) {
        super(retCode, retData, retMsg);
        this.encryptKey = encryptKey;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

}
