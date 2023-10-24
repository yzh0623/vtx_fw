package io.kida.yuen.utils.thirdparty.redis;

import io.kida.yuen.components.annotations.PropLoader;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.RedisOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: RedisClient.java
 * @ClassName: RedisClient
 * @Description:获取redis客户端
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/17
 */
@Slf4j
public class RedisClient extends AbstractVerticle {

    // 连接池最大大小
    @PropLoader(key = "redis.max-pool-size")
    private int maxPoolSize;

    // 连接池最大等待
    @PropLoader(key = "redis.max-pool-waiting")
    private int maxPoolWaiting;

    // 连接池超时
    @PropLoader(key = "redis.pool-recycle-timeout")
    private int poolRecycleTimeout;

    // 最大等待用户
    @PropLoader(key = "redis.max-waiting-handlers")
    private int maxWaitingHandlers;

    // 连接字符串
    @PropLoader(key = "redis.connection-string")
    private String connectionString;

    public static RedisAPI redis;

    @Override
    public void start() {
        YamlUtil.propLoadSetter(this);

        // 创建redis客户端连接
        Redis.createClient(vertx, getConfiguration()).connect(onConnect -> {
            if (onConnect.succeeded()) {
                RedisConnection redisClient = onConnect.result();
                redis = RedisAPI.api(redisClient);
            } else {
                Throwable e = onConnect.cause();
                log.error("func[RedisUtil.start] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                    e.getStackTrace());
            }
        });
    }

    /**
     * 
     * @MethodName: getConfiguration
     * @Description: 获取配置信息
     * @author yuanzhenhui
     * @return RedisOptions
     * @date 2023-10-16 05:09:21
     */
    private RedisOptions getConfiguration() {
        RedisOptions options = new RedisOptions();
        options.setMaxPoolSize(maxPoolSize);
        options.setMaxPoolWaiting(maxPoolWaiting);
        options.setConnectionString(connectionString);
        options.setPoolRecycleTimeout(poolRecycleTimeout);
        options.setMaxWaitingHandlers(maxWaitingHandlers);
        return options;
    }

}
