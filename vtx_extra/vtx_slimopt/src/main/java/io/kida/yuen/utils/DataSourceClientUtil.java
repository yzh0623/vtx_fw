package io.kida.yuen.utils;

import java.lang.reflect.Method;
import java.util.Map;

import io.kida.yuen.components.annotions.DBLoader;
import io.kida.yuen.components.constants.DataSourceConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.vo.datasource.DataSourceExecParam;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DataSourceClientUtil.java
 * @ClassName: DataSourceClientUtil
 * @Description:数据源工具类（本类可能会出现性能瓶颈）
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class DataSourceClientUtil {

    /**
     * 
     * @MethodName: dbClient
     * @Description: 动态获取数据源 通过遍历堆栈并进行匹配找到方法上方带有DBLoader的方法 并根据DBLoader中定义的别名找到对应的jdbc客户端
     * @author yuanzhenhui
     * @param dbem
     * @return JDBCClient
     * @date 2023-10-19 03:08:19
     */
    public static JDBCClient dbClient(DataSourceExecParam dbem) {
        JDBCClient client = null;

        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            try {
                Method method = Class.forName(stackTrace.getClassName()).getDeclaredMethod(stackTrace.getMethodName(),
                    Message.class);
                if (null != method && method.isAnnotationPresent(DBLoader.class)) {
                    client = getClientByParams(dbem, method);
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return client;
    }

    /**
     * 
     * @MethodName: getClientByParams
     * @Description: 通过参数获取jdbc客户端
     * @author yuanzhenhui
     * @param dbem
     * @param method
     * @return JDBCClient
     * @date 2023-10-19 03:08:35
     */
    private static JDBCClient getClientByParams(DataSourceExecParam dbem, Method method) {
        JDBCClient client = null;
        DBLoader db = method.getDeclaredAnnotation(DBLoader.class);
        String key = db.key();

        // 获取实体类上的数据源名称
        Class<?> clazz = dbem.getTargetClass();
        Map<String, Object> classMap = EntityConstants.CLASS_MAP.get(clazz.getSimpleName());
        Object dync = classMap.get(EntityConstants.DB_LOADER);
        if (StringUtil.isNotEmpty(key)) {
            if (null != dync) {
                // 同时存在的情况下先尝试获取dyncField的，若获取没有再获取key的
                client = DataSourceConstants.JDBC_CLIENT_MAP.get(String.valueOf(dync));
                if (null == client) {
                    client = DataSourceConstants.JDBC_CLIENT_MAP.get(key);
                }
            } else {
                // key存在，dync不存在
                client = DataSourceConstants.JDBC_CLIENT_MAP.get(key);
            }
        } else {
            if (null != dync) {
                // key不存在，dync存在的情况获取dyncField数据源，若dyncField不存在则获取第一个数据源
                client = DataSourceConstants.JDBC_CLIENT_MAP.get(String.valueOf(dync));
                if (null == client) {
                    client = DataSourceConstants.JDBC_CLIENT_MAP.entrySet().iterator().next().getValue();
                }
            } else {
                // 同时不存在的情况获取第一个数据源
                client = DataSourceConstants.JDBC_CLIENT_MAP.entrySet().iterator().next().getValue();
            }
        }
        return client;
    }

}
