package io.kida.yuen.components.configurations;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.kida.yuen.components.annotations.PropLoader;
import io.kida.yuen.components.constants.HttpConstants;
import io.kida.yuen.utils.selfdev.base.ReflectUtil;
import io.kida.yuen.utils.system.router.RouterSet;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: RouterConfig.java
 * @ClassName: RouterConfig
 * @Description:restful路由配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/17
 */
@Slf4j
public class RouterConfig extends AbstractVerticle {

    // 路由配置信息
    @PropLoader(key = "server.port")
    private int port;
    @PropLoader(key = "server.http.header")
    private List<String> httpHeader;
    @PropLoader(key = "server.root-path")
    private String rootPath;

    @Override
    public void start() {

        // 注册yaml自定义注解
        YamlUtil.propLoadSetter(this);

        // 获取路由实例
        Router router = setupRouter(vertx);

        // 路由实例进行定义
        setupRouterDefinition(router);
        setupRouterRestful(router);
    }

    /**
     * 
     * @MethodName: setupRouter
     * @Description: 创建路由对象
     * @author yuanzhenhui
     * @param vertx
     * @return Router
     * @date 2023-10-17 09:28:05
     */
    private Router setupRouter(Vertx vertx) {

        Router router = Router.router(vertx);
        Route route = router.route();

        CorsHandler corsHandler = CorsHandler.create();
        corsHandler.allowedHeaders(new HashSet<>(httpHeader));
        corsHandler.allowedMethods(setupRouterHeader());
        route.handler(corsHandler);

        route.handler(BodyHandler.create());

        route.consumes(HttpConstants.HTTP_CONTENT_TYPE);
        route.produces(HttpConstants.HTTP_CONTENT_TYPE);
        return router;
    }

    /**
     * 
     * @MethodName: setupRouterHeader
     * @Description: 设定支持的http方法
     * @author yuanzhenhui
     * @return Set<HttpMethod>
     * @date 2023-10-17 09:29:03
     */
    private Set<HttpMethod> setupRouterHeader() {
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.PUT);
        return allowedMethods;
    }

    /**
     * 
     * @MethodName: setupRouterDefinition
     * @Description: 扫描定义路由
     * @author yuanzhenhui
     * @param router
     *            void
     * @date 2023-10-17 09:29:34
     */
    private void setupRouterDefinition(Router router) {
        // 通过反射获取到顶层目录下所有的类
        try {
            CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(rootPath, true);
            clazzSet.parallelStream().forEach(clazz -> {
                Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
                if (interfaces.contains(RouterSet.class)) {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        RouterSet rs = (RouterSet)constructor.newInstance();
                        rs.restRouter(router);
                    } catch (InvocationTargetException | NoSuchMethodException | SecurityException
                        | InstantiationException | IllegalAccessException | IllegalArgumentException
                        | ClassNotFoundException | IOException e) {
                        log.error("func[RouterConfig.setupRouterDefinition] Exception [{} - {}] stackTrace[{}] ",
                            e.getCause(), e.getMessage(), e.getStackTrace());
                    }
                }
            });
        } catch (Exception e) {
            log.error("func[RouterConfig.setupRouterDefinition] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
    }

    /**
     * 
     * @MethodName: setupRouterRestful
     * @Description: 对系统中所有的RESTful接口进行监听设定和链路设定
     * @author yuanzhenhui
     * @param router
     *            void
     * @date 2023-10-17 09:30:04
     */
    private void setupRouterRestful(Router router) {
        vertx.createHttpServer(new HttpServerOptions()).requestHandler(router).listen(port);
    }

}
