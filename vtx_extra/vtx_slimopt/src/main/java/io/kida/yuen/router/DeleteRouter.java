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
 * @File: DeleteRouter.java
 * @ClassName: DeleteRouter
 * @Description:删除路由
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class DeleteRouter extends AbstractVerticle implements RouterSet {

    /**
     * eventbus访问路径
     */
    private static final String EVENTBUS_DELETE_DELETE =
        DaoConstants.DELETE_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.DELETE_BY_CONDITION;
    private static final String EVENTBUS_DELETE_DELETE_WITH_PK =
        DaoConstants.DELETE_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.DELETE_BY_PK;

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
     * @MethodName: deleteDelete
     * @Description: 通过delete方式进行删除
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 11:53:54
     */
    public void deleteDelete(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String jsonStr = ctx.body().asString();
            JsonObject jsonObj = StringUtil.isNotEmpty(jsonStr) ? new JsonObject(jsonStr) : new JsonObject();
            jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());
            evbVtx.eventBus().request(EVENTBUS_DELETE_DELETE, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                reMsg -> hsr.end(reMsg.result().body().toString()));
        } else {
            hsr.end(Json.encode(new RouterValue(0, "Unable to find entity class by name")));
        }
    }

    /**
     * 
     * @MethodName: deleteDeleteWithPk
     * @Description: 通过delete方式进行主键删除
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 11:56:39
     */
    public void deleteDeleteWithPk(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String jsonStr = ctx.body().asString();
            JsonObject jsonObj = new JsonObject();
            if (StringUtil.isNotEmpty(jsonStr)) {
                jsonObj = new JsonObject(jsonStr);
            } else {
                hsr.end(Json.encode(new RouterValue(0, "The incoming parameter does not meet the standard")));
            }
            jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());
            evbVtx.eventBus().request(EVENTBUS_DELETE_DELETE_WITH_PK, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                reMsg -> hsr.end(reMsg.result().body().toString()));
        } else {
            hsr.end(Json.encode(new RouterValue(0, "Unable to find entity class by name")));
        }
    }

    /**
     * 
     * @MethodName: restRouter
     * @Description: 批量注册删除路由
     * @author yuanzhenhui
     * @param router
     * @throws ClassNotFoundException
     * @throws IOException
     * @see io.kida.yuen.utils.system.router.RouterSet#restRouter(io.vertx.ext.web.Router)
     * @date 2023-10-19 11:56:54
     */
    @Override
    public void restRouter(Router router) throws ClassNotFoundException, IOException {
        CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(RouterConstants.SERVER_MODEL_LOADED, true);
        if (null != clazzSet && !clazzSet.isEmpty()) {
            String deleteUri = RouterConstants.BASE_URI + DaoConstants.DELETE_PARAM + HttpConstants.HTTP_SLASH;
            clazzSet.forEach(clazz -> {
                String entityName = StringUtil.lowerFirstCase(clazz.getSimpleName());
                String pkParamUri = entityName + HttpConstants.HTTP_SLASH + RouterConstants.BY_PK_PARAM;
                String conditionParamUri = entityName + HttpConstants.HTTP_SLASH + RouterConstants.BY_CONDITION_PARAM;
                // 根据主键删除
                router.delete(deleteUri + pkParamUri).handler(this::deleteDeleteWithPk);
                // 根据条件删除
                router.delete(deleteUri + conditionParamUri).handler(this::deleteDelete);
            });
        }
    }
}
