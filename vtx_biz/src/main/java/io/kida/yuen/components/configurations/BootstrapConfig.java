package io.kida.yuen.components.configurations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArraySet;

import io.kida.yuen.utils.selfdev.base.ReflectUtil;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_biz
 * @File: BootstrapConfig.java
 * @ClassName: BootstrapConfig
 * @Description: 启动配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class BootstrapConfig {

    // 线程配置参数
    private static final String THREAD_WORKER = "thread.worker";
    private static final String THREAD_INIT_POOL_SIZE = "thread.init-pool-size";
    private static final String THREAD_EVENTLOOP_POOL_SIZE = "thread.eventloop-pool-size";
    private static final String THREAD_DEPLOY_INIT = "thread.deploy.init";
    private static final String THREAD_DEPLOY_MAX_SIZE = "thread.deploy.max-size";
    private static final String THREAD_DEPLOY_POOL_NAME = "thread.deploy.pool-name";

    private static final String SERVER_ROOT_PATH = "server.root-path";

    // zookeeper配置信息
    private static final String ZK_HAS_OPEN = "zookeeper.has-open";
    private static final String ZK_HOST = "zookeeper.zookeeper-hosts";
    private static final String ZK_RETRY_INITIAL_SLEEP_TIME = "zookeeper.retry.initial-sleep-time";
    private static final String ZK_RETRY_MAX_TIMES = "zookeeper.retry.max-times";
    private static final String ZK_HOST_PARAM = "zookeeperHosts";
    private static final String ZK_ROOT_PATH_PARAM = "rootPath";
    private static final String ZK_RETRY_PARAM = "retry";
    private static final String ZK_INITIAL_SLEEP_TIME_PARAM = "initialSleepTime";
    private static final String ZK_MAX_TIMES_PARAM = "maxTimes";

    // vtx Verticle常量
    private static final String VTX_VERTICLE = "Verticle";

    /**
     * 
     * @MethodName: setupAndDeploy
     * @Description: 启动部署(通过配置参数判断是否需要启动集群模式)
     * @author yuanzhenhui void
     * @date 2023-10-19 10:54:58
     */
    public static void setupAndDeploy() {
        log.debug("In order to get the startup configuration first initialize the YamlConfig class...");
        Vertx.vertx().deployVerticle(new YamlConfig(), handler -> {
            VertxOptions options = setupOptions();
            if (Boolean.TRUE.equals(YamlUtil.getBooleanValue(ZK_HAS_OPEN))) {
                try {
                    options.setClusterManager(setupCluster());
                    options.setEventBusOptions(setupEbOptions(InetAddress.getLocalHost().getHostAddress()));

                    Vertx.clusteredVertx(options, clusterHandler -> {
                        if (clusterHandler.succeeded()) {
                            setupDeploy(clusterHandler.result());
                        } else {
                            log.error("Unable to use cluster mode");
                            setupDeploy(Vertx.vertx(options));
                        }
                    });
                } catch (UnknownHostException e) {
                    log.error("func[BootstrapConfig.setupAndDeploy] Main method error Exception [{} - {}]",
                        e.getCause(), e.fillInStackTrace());
                }
            } else {
                setupDeploy(Vertx.vertx(options));
            }
        });
    }

    /**
     * 
     * @MethodName: setupOptions
     * @Description: 线程池配置
     * @author yuanzhenhui
     * @return VertxOptions
     * @date 2023-10-19 10:55:34
     */
    private static VertxOptions setupOptions() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(YamlUtil.getIntegerValue(THREAD_WORKER));
        options.setInternalBlockingPoolSize(YamlUtil.getIntegerValue(THREAD_INIT_POOL_SIZE));
        options.setEventLoopPoolSize(YamlUtil.getIntegerValue(THREAD_EVENTLOOP_POOL_SIZE));
        return options;
    }

    /**
     * 
     * @MethodName: setupCluster
     * @Description: 集群配置（集群采用的是Zookeeper接入方式，因为Hazelcast、Infinispan和Ignite都暂时没有接触过）
     * @author yuanzhenhui
     * @return ClusterManager
     * @date 2023-10-19 10:55:45
     */
    private static ClusterManager setupCluster() {
        JsonObject zkConfig = new JsonObject();
        zkConfig.put(ZK_HOST_PARAM, YamlUtil.getStringValue(ZK_HOST));
        zkConfig.put(ZK_ROOT_PATH_PARAM, YamlUtil.getStringValue(SERVER_ROOT_PATH));

        JsonObject jsonObj = new JsonObject();
        jsonObj.put(ZK_INITIAL_SLEEP_TIME_PARAM, YamlUtil.getIntegerValue(ZK_RETRY_INITIAL_SLEEP_TIME));
        jsonObj.put(ZK_MAX_TIMES_PARAM, YamlUtil.getIntegerValue(ZK_RETRY_MAX_TIMES));

        zkConfig.put(ZK_RETRY_PARAM, jsonObj);
        return new ZookeeperClusterManager(zkConfig);
    }

    /**
     * 
     * @MethodName: setupEbOptions
     * @Description: eventbus集群配置
     * @author yuanzhenhui
     * @param ipv4
     * @return EventBusOptions
     * @date 2023-10-19 10:56:12
     */
    private static EventBusOptions setupEbOptions(String ipv4) {
        EventBusOptions ebOpt = new EventBusOptions();
        ebOpt.setHost(ipv4);
        ebOpt.setClusterPublicHost(ipv4);
        return ebOpt;
    }

    /**
     * 
     * @MethodName: setupDeploy
     * @Description: 遍历所有类，对继承AbstractVerticle的类进行注册部署
     * @author yuanzhenhui
     * @param vtx
     *            void
     * @date 2023-10-19 10:56:23
     */
    private static void setupDeploy(Vertx vtx) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true);
        deploymentOptions.setInstances(YamlUtil.getIntegerValue(THREAD_DEPLOY_INIT));
        deploymentOptions.setWorkerPoolSize(YamlUtil.getIntegerValue(THREAD_DEPLOY_MAX_SIZE));
        deploymentOptions.setWorkerPoolName(YamlUtil.getStringValue(THREAD_DEPLOY_POOL_NAME));

        if (null != vtx) {
            try {
                CopyOnWriteArraySet<Class<Verticle>> clazzSet =
                    ReflectUtil.getVerticleClasses(YamlUtil.getStringValue(SERVER_ROOT_PATH), true);
                // 这里采用了stream代替了原来的parallelStream是考虑到有某些特殊情况下或许需要顺序部署
                clazzSet.stream().filter(clazz -> !clazz.getSimpleName().contains(VTX_VERTICLE))
                    .forEach(clazz -> vtx.deployVerticle(clazz, deploymentOptions));
            } catch (ClassNotFoundException | IOException e) {
                log.error("func[BootstrapConfig.setupDeploy] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                    e.getMessage(), e.getStackTrace());
            }
        }
    }
}
