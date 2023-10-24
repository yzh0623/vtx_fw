package io.kida.yuen.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: PropLoader.java
 * @ClassName: PropLoader
 * @Description:根据关键字获取配置信息注解
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropLoader {

    /**
     * 
     * @MethodName: key
     * @Description: 关键字
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 05:29:23
     */
    String key();
}
