package io.kida.yuen.components.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.jackson.DatabindCodec;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: JacksonConfig.java
 * @ClassName: JacksonConfig
 * @Description: jackson转换配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/18
 */
public class JacksonConfig extends AbstractVerticle {

    @Override
    public void start() {
        ObjectMapper mapper = DatabindCodec.mapper();
        mapper.registerModule(new JavaTimeModule());
    }
}
