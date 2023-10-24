package io.kida.yuen.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import io.kida.yuen.components.annotions.DBLoader;
import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.components.constants.RouterConstants;
import io.kida.yuen.dao.callback.DaoCallback;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.system.router.RouterValue;
import io.kida.yuen.vo.datasource.DataSourceExecParam;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DaoSetUp.java
 * @ClassName: DaoSetUp
 * @Description:DAO设置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public interface DaoSetUp {

    static final Logger LOGGER = LoggerFactory.getLogger(DaoSetUp.class);
    static final Marker MARKER = MarkerFactory.getMarker(DaoSetUp.class.getName());

    static final String REGISTER_FALUE_MSG = "Failed to register eventbus, the exception is:";

    /**
     * 
     * @MethodName: eventBusLoader
     * @Description: 事件总线映射加载
     * @author yuanzhenhui
     * @param <T>
     * @param clazz
     * @param vertx
     *            void
     * @date 2023-10-16 05:12:45
     */
    @SuppressWarnings("unchecked")
    default <T> void eventBusLoader(Class<?> clazz, Vertx vertx) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            T t = (T)constructor.newInstance();
            Method[] methods = clazz.getDeclaredMethods();

            // 原来是使用Arrays.asList先将Method[]转换为list再进行stream处理的，但使用了Arrays.asList方法将方法数组转换为List，然后使用stream操作。
            // 这样做会创建一个新的List对象，并且在流操作中使用了Lambda表达式，可能会导致性能下降。于是还是用传统的for方法来遍历吧
            for (Method method : methods) {
                if (method.isAnnotationPresent(DBLoader.class)) {
                    vertx.eventBus()
                        .consumer(method.getDeclaringClass().getName() + RouterConstants.DOT + method.getName())
                        .handler(hdl -> {
                            try {
                                method.invoke(t, hdl);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                LOGGER.error(MARKER,
                                    "func[DaoSetUp.eventBusLoader] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                                    e.getMessage(), e.getStackTrace());
                            }
                        });
                }
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | SecurityException
            | IllegalArgumentException | InvocationTargetException e1) {
            LOGGER.error(MARKER, "func[DaoSetUp.eventBusLoader] Exception [{} - {}] stackTrace[{}] ", e1.getCause(),
                e1.getMessage(), e1.getStackTrace());
        }
    }

    /**
     * 
     * @MethodName: setupBeforeOperate
     * @Description: 整理操作所需
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     * @param daoCallback
     *            void
     * @date 2023-10-16 05:12:57
     */
    default <T> void setupBeforeOperate(Message<T> msg, DaoCallback daoCallback) {
        DataSourceExecParam dsrcExec = null;
        // 先判断传过来的信息是否为空
        if (null != msg.body()) {
            JsonObject jsonObj = new JsonObject(msg.body().toString());

            dsrcExec = new DataSourceExecParam();
            dsrcExec.setTargetClass((Class<?>)EntityConstants.CLASS_MAP.get(jsonObj.getString(DaoConstants.CLAZZ_PATH))
                .get(EntityConstants.ENTITY_NAME));
            dsrcExec.setDataParam(jsonObj);

            // 尾部sql整理
            String tailSql = "";

            // 获取order by关键字
            String dbOrder = StringUtil.isNotEmpty(jsonObj.getString(DaoConstants.DB_ORDER))
                ? jsonObj.getString(DaoConstants.DB_ORDER) : null;
            if (StringUtil.isNotEmpty(dbOrder)) {
                tailSql += dbOrder;
            }

            // 获取limit关键字
            String dbLimit = StringUtil.isNotEmpty(jsonObj.getString(DaoConstants.DB_LIMIT))
                ? jsonObj.getString(DaoConstants.DB_LIMIT) : "";
            if (StringUtil.isNotEmpty(dbLimit)) {
                tailSql += dbLimit;
            }
            dsrcExec.setTailSql(tailSql);
            daoCallback.process(dsrcExec);
        } else {
            msg.reply(Json.encode(new RouterValue(0, "Basic data has not been collected")));
        }
    }
}
