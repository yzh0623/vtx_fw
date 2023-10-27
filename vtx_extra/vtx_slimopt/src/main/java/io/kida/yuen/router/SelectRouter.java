package io.kida.yuen.router;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.Id;

import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.HttpConstants;
import io.kida.yuen.components.constants.RouterConstants;
import io.kida.yuen.utils.DaoUtil;
import io.kida.yuen.utils.RouterUtil;
import io.kida.yuen.utils.selfdev.base.ReflectUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.system.router.RouterSet;
import io.kida.yuen.utils.system.router.RouterValue;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.kida.yuen.vo.pagehelper.Pages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: SelectRouter.java
 * @ClassName: SelectRouter
 * @Description:查询路由
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class SelectRouter extends AbstractVerticle implements RouterSet {

    /**
     * eventbus访问路径
     */
    private static final String EVENTBUS_GET_QUERY_WITH_PK =
        DaoConstants.SELECT_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.SELECT_BY_PK;
    private static final String EVENTBUS_POST_QUERY_WITH_LIST =
        DaoConstants.SELECT_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.SELECT_BY_CONDITION;
    private static final String EVENTBUS_POST_QUERY_WITH_COUNT =
        DaoConstants.SELECT_DAO_FULL_NAME + RouterConstants.DOT + RouterConstants.SELECT_BY_CONDITION_COUNT;

    private static final String PK = "pk";
    private static final String RE_VALUE_RETCODE_FIELD = "retCode";
    private static final String PAGES_PAGE_NO_FIELD = "pageNo";
    private static final String PAGES_PAGE_SIZE_FIELD = "pageSize";
    private static final String PAGES_ORDER_BY_FIELD = "orderBy";
    private static final String DB_TYPE = YamlUtil.getStringValue("databases.db-type");

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
     * @MethodName: getQueryWithPk
     * @Description: 通过get方式进行主键查询
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 12:00:07
     */
    public void getQueryWithPk(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String pk = ctx.request().getParam(PK);
            Optional<Field> fieldOptional = Arrays.asList(clazz.getDeclaredFields()).stream()
                .filter(fieldTmp -> fieldTmp.isAnnotationPresent(Id.class)).findFirst();
            if (fieldOptional.isPresent()) {
                JsonObject jsonObj = new JsonObject();
                jsonObj.put(fieldOptional.get().getName(), pk);
                jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());
                try {
                    log.info("router ip is : " + Inet4Address.getLocalHost());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                evbVtx.eventBus().request(EVENTBUS_GET_QUERY_WITH_PK, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                    reMsg -> hsr.end(reMsg.result().body().toString()));
            } else {
                hsr.end(Json.encode(new RouterValue(0, "The primary key is not set in the entity")));
            }
        } else {
            hsr.end(Json.encode(new RouterValue(0, RouterConstants.CAN_NOT_FIND_ENTITY)));
        }
    }

    /**
     * 
     * @MethodName: postQueryWithList
     * @Description: 通过post方式进行条件查询
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 12:00:26
     */
    public void postQueryWithList(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String jsonStr = ctx.body().asString();
            JsonObject jsonObj = StringUtil.isNotEmpty(jsonStr) ? new JsonObject(jsonStr) : new JsonObject();
            jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());

            String orderBy = jsonObj.getString(PAGES_ORDER_BY_FIELD);
            jsonObj.put(DaoConstants.DB_ORDER, DaoConstants.ORDERBY + orderBy);
            evbVtx.eventBus().request(EVENTBUS_POST_QUERY_WITH_LIST, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                reMsg -> hsr.end(reMsg.result().body().toString()));
        } else {
            hsr.end(Json.encode(new RouterValue(0, RouterConstants.CAN_NOT_FIND_ENTITY)));
        }
    }

    /**
     * 
     * @MethodName: postQueryWithPage
     * @Description: 通过post方式进行条件分页查询
     * @author yuanzhenhui
     * @param ctx
     *            void
     * @date 2023-10-19 12:01:11
     */
    public void postQueryWithPage(RoutingContext ctx) {
        HttpServerResponse hsr =
            ctx.response().putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE);
        Class<?> clazz = RouterUtil.extractEntity(ctx.normalizedPath());
        if (null != clazz) {
            String jsonStr = ctx.body().asString();
            if (StringUtil.isNotEmpty(jsonStr)) {
                JsonObject jsonObj = new JsonObject(jsonStr);
                jsonObj.put(DaoConstants.CLAZZ_PATH, clazz.getSimpleName());

                // 获取分页参数
                int pageNo = jsonObj.getInteger(PAGES_PAGE_NO_FIELD);
                int pageSize = jsonObj.getInteger(PAGES_PAGE_SIZE_FIELD);
                String limitParam = DaoUtil.setupPaginationParam(pageNo, pageSize, DB_TYPE);
                jsonObj.put(DaoConstants.DB_LIMIT, limitParam);

                String orderBy = jsonObj.getString(PAGES_ORDER_BY_FIELD);
                jsonObj.put(DaoConstants.DB_ORDER, DaoConstants.ORDERBY + orderBy);

                // 先查询数据总数
                evbVtx.eventBus().request(EVENTBUS_POST_QUERY_WITH_COUNT, jsonObj, RouterConstants.DELIVERY_OPTIONS,
                    reMsg -> {
                        JsonObject jsonRe = new JsonObject(reMsg.result().body().toString());

                        // 当返回结果是数据大于0的时候继续进行数据的分页查询
                        if (jsonRe.getInteger(RE_VALUE_RETCODE_FIELD) == 1) {

                            // 分页查询
                            evbVtx.eventBus().request(EVENTBUS_POST_QUERY_WITH_LIST, jsonObj,
                                RouterConstants.DELIVERY_OPTIONS, reMsgDetail -> {
                                    JsonObject jsonReDetail = new JsonObject(reMsgDetail.result().body().toString());
                                    if (jsonReDetail.getInteger(RE_VALUE_RETCODE_FIELD) == 1) {

                                        // 将数据的总数重新设置到totalcount里面
                                        Pages<JsonObject> page = DaoUtil.transToPage(jsonReDetail, pageNo, pageSize);
                                        page.setTotalCount(jsonRe.getJsonObject("retData").getInteger("numRows"));
                                        hsr.end(Json
                                            .encode(new RouterValue(1, JsonObject.mapFrom(page), "Search Complete")));
                                    } else {
                                        hsr.end(jsonRe.toString());
                                    }
                                });
                        } else {
                            hsr.end(jsonRe.toString());
                        }
                    });
            } else {
                hsr.end(Json.encode(new RouterValue(0, "wrong parameter")));
            }
        } else {
            hsr.end(Json.encode(new RouterValue(0, RouterConstants.CAN_NOT_FIND_ENTITY)));
        }
    }

    /**
     * 
     * @MethodName: restRouter
     * @Description: 批量注册查询路由
     * @author yuanzhenhui
     * @param router
     * @throws ClassNotFoundException
     * @throws IOException
     * @see io.kida.yuen.utils.system.router.RouterSet#restRouter(io.vertx.ext.web.Router)
     * @date 2023-10-19 12:01:56
     */
    @Override
    public void restRouter(Router router) throws ClassNotFoundException, IOException {
        CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(RouterConstants.SERVER_MODEL_LOADED, true);
        if (null != clazzSet && !clazzSet.isEmpty()) {
            String queryUri = RouterConstants.BASE_URI + DaoConstants.QUERY_PARAM + HttpConstants.HTTP_SLASH;
            clazzSet.parallelStream().forEach(clazz -> {
                String entityName = StringUtil.lowerFirstCase(clazz.getSimpleName());
                String pkParamUri = entityName + HttpConstants.HTTP_SLASH + RouterConstants.BY_PK_PARAM
                    + HttpConstants.HTTP_SLASH + RouterConstants.COLON + PK;
                String conditionParamUri = entityName + HttpConstants.HTTP_SLASH + RouterConstants.BY_CONDITION_PARAM;
                String conditionPageParamUri =
                    entityName + HttpConstants.HTTP_SLASH + RouterConstants.BY_CONDITION_PAGE_PARAM;

                // 主键查询
                router.get(queryUri + pkParamUri).handler(this::getQueryWithPk);
                // 条件查询
                router.post(queryUri + conditionParamUri).handler(this::postQueryWithList);
                // 条件查询（分页）
                router.post(queryUri + conditionPageParamUri).handler(this::postQueryWithPage);
            });
        }
    }
}
