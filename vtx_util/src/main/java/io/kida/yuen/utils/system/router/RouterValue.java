package io.kida.yuen.utils.system.router;

import lombok.Data;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: RouterValue.java
 * @ClassName: RouterValue
 * @Description:返回数据
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Data
public class RouterValue {

    // 返回代码 0：失败，1：成功
    private Integer retCode;

    // 返回文本信息
    private String retMsg;

    // 返回数据
    private Object retData;

    public RouterValue(Integer retCode) {
        this.retCode = retCode;
    }

    public RouterValue(Integer retCode, Object retData) {
        this.retCode = retCode;
        this.retData = retData;
    }

    public RouterValue(Integer retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public RouterValue(Integer retCode, Object retData, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.retData = retData;
    }

}
