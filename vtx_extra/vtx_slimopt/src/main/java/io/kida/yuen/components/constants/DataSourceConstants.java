package io.kida.yuen.components.constants;

import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.ext.jdbc.JDBCClient;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DataSourceConstants.java
 * @ClassName: DataSourceConstants
 * @Description:数据源配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class DataSourceConstants {

    public static final String REMOTE = "remote";
    public static final String LOCAL = "local";

    public static final String CACHE_PREP_STMTS_PARAM = "cachePrepStmts";
    public static final String PREP_STMT_CACHE_SIZE_PARAM = "prepStmtCacheSize";
    public static final String PREP_STMT_CACHE_SQL_LIMIT_PARAM = "prepStmtCacheSqlLimit";

    public static final String DRIVER_CLASS_NAME_PARAM = "driver-class-name";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";
    public static final String URI_PARAM = "uri";
    public static final String NAME_PARAM = "name";

    // 数据源客户端
    public static final Map<String, JDBCClient> JDBC_CLIENT_MAP = new LinkedHashMap<>(15);

}
