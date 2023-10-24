package io.kida.yuen.components.annotions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DBLoader.java
 * @ClassName: DBLoader
 * @Description:获取数据源关键字
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBLoader {

    /**
     * 
     * @MethodName: key
     * @Description: 配置文件中数据源名称
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-16 05:14:47
     */
    String key() default "";
}
