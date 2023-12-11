package io.kida.yuen.router;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.HttpConstants;
import io.kida.yuen.components.constants.RouterConstants;
import io.kida.yuen.utils.RouterUtil;
import io.kida.yuen.utils.selfdev.base.ReflectUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.system.router.RouterSet;
import io.kida.yuen.utils.system.router.RouterValue;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: InsertRouter.java
 * @ClassName: InsertRouter
 * @Description:新增路由
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class InsertRouter extends AbstractVerticle implements RouterSet {

    /**
     * eventbus访问路径
     */
    private static final String EVENTBUS_INSERT =
        DaoConstants.INSERT_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.INSERT;

    /**
     * vertx实例
     */
    private static Vertx evbVtx;

    @Override
    public void start() {
        setVertxStatic(vertx);
    }

    public static void setVertxStatic(Vertx vertx) {
        evbVtx = vertx;
    }

    /**
     * 
     * @MethodName: postInsert
     * @Description: 通过post方式进行插入
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 11:57:53
     */
    public void postInsert(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String jsonStr = ctx.body().asString();
            JsonObject jsonObj = StringUtil.isNotEmpty(jsonStr) ? new JsonObject(jsonStr) : new JsonObject();
            jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());
            evbVtx.eventBus().request(EVENTBUS_INSERT, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                reMsg -> hsr.end(reMsg.result().body().toString()));
        } else {
            hsr.end(Json.encode(new RouterValue(0, "Unable to find entity class by name")));
        }
    }

    /**
     * 
     * @MethodName: restRouter
     * @Description: 批量注册插入路由
     * @author yuanzhenhui
     * @param router
     * @throws ClassNotFoundException
     * @throws IOException
     * @see io.kida.yuen.utils.system.router.RouterSet#restRouter(io.vertx.ext.web.Router)
     * @date 2023-10-19 11:58:57
     */
    @Override
    public void restRouter(Router router) throws ClassNotFoundException, IOException {
        CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(RouterConstants.SERVER_MODEL_LOADED, true);
        if (null != clazzSet && !clazzSet.isEmpty()) {
            String insertUri = RouterConstants.BASE_URI + DaoConstants.INSERT_PARAM + HttpConstants.HTTP_SLASH;
            clazzSet.forEach(clazz -> {
                String entityName = StringUtil.lowerFirstCase(clazz.getSimpleName());
                // 新增
                router.post(insertUri + entityName).handler(this::postInsert);
            });
        }
    }

}
