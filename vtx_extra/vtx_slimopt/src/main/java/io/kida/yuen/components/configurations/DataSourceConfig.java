package io.kida.yuen.components.configurations;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.kida.yuen.components.annotations.PropLoader;
import io.kida.yuen.components.constants.DataSourceConstants;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DataSourceConfig.java
 * @ClassName: DataSourceConfig
 * @Description:数据源配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class DataSourceConfig extends AbstractVerticle {

    // 是否启用数据库
    @PropLoader(key = "databases.has-open")
    private Boolean hasOpen;

    // 数据库节点配置
    @PropLoader(key = "databases.nodes")
    private List<HashMap<?, ?>> nodes;

    // 自动提交从池中返回的连接
    @PropLoader(key = "databases.hikari.is-auto-commit")
    private Boolean isAutoCommit;

    // 控制池是否可以通过JMX暂停和恢复
    @PropLoader(key = "databases.hikari.allow-pool-suspension")
    private Boolean allowPoolSuspension;

    // 如果您的驱动程序支持JDBC4，我们强烈建议您不要设置此属性
    @PropLoader(key = "databases.hikari.connection-test-query")
    private String connectionTestQuery;

    // 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置
    @PropLoader(key = "databases.hikari.pool-name")
    private String poolName;

    // 池中维护的最小空闲连接数
    @PropLoader(key = "databases.hikari.minimum-idle")
    private int minimumIdle;

    // 池中最大连接数，包括闲置和使用中的连接
    @PropLoader(key = "databases.hikari.maximum-pool-size")
    private int maximumPoolSize;

    // 连接允许在池中闲置的最长时间
    @PropLoader(key = "databases.hikari.idle-timeout")
    private int idleTimeout;

    // 等待来自池的连接的最大毫秒数
    @PropLoader(key = "databases.hikari.connection-timeout")
    private int connectionTimeout;

    // 缓存PerperaStatment
    @PropLoader(key = "databases.hikari.cache-prep-stmts")
    private Boolean cachePrepStmts;

    // PerperaStatment缓存大小
    @PropLoader(key = "databases.hikari.prep-stmt-cache-size")
    private int prepStmtCacheSize;

    // PerperaStatment SQL脚本缓存大小
    @PropLoader(key = "databases.hikari.prep-stmt-cache-sql-limit")
    private int prepStmtCacheSqlLimit;

    // 连接被占用的超时时间
    @PropLoader(key = "databases.hikari.leak-detection-threshold")
    private int leakDetectionThreshold;

    /**
     * 
     * @MethodName: getHikariCPConfig
     * @Description: 获取数据库连接池配置
     *               若要使用hikariCP数据库连接池就必须采用这种方式创建HikariConfig，之后在创建jdbcclient的时候使用create来new一个HikariDataSource来创建不共享的数据库客户端
     *               若使用createShared来创建客户端，由于数据库连接池配置参数采用的是jsonobject的方式传入，没有指定使用hikariCP，所以vertx将创建一个c3p0的连接池，这样就没意义了
     * @author yuanzhenhui
     * @return HikariConfig
     * @date 2023-10-19 11:01:57
     */
    public HikariConfig getHikariCpConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setAutoCommit(isAutoCommit);
        hikariConfig.setAllowPoolSuspension(allowPoolSuspension);
        hikariConfig.setConnectionTestQuery(connectionTestQuery);
        hikariConfig.setPoolName(poolName);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setLeakDetectionThreshold(leakDetectionThreshold);
        hikariConfig.addDataSourceProperty(DataSourceConstants.CACHE_PREP_STMTS_PARAM, cachePrepStmts);
        hikariConfig.addDataSourceProperty(DataSourceConstants.PREP_STMT_CACHE_SIZE_PARAM, prepStmtCacheSize);
        hikariConfig.addDataSourceProperty(DataSourceConstants.PREP_STMT_CACHE_SQL_LIMIT_PARAM, prepStmtCacheSqlLimit);
        return hikariConfig;
    }

    /**
     * 
     * @MethodName: start
     * @Description: 初始化加载
     * @author yuanzhenhui
     * @see io.vertx.core.AbstractVerticle#start()
     * @date 2023-10-19 11:02:30
     */
    @Override
    public void start() {
        // 这里需要注意的是@PropLoader注解对应的变量只需要注册服务时使用一次，因此YamlUtil.propLoadSetter方法放在start()中执行一次即可。
        YamlUtil.propLoadSetter(this);
        // 判断是否需要使用数据库
        if (Boolean.TRUE.equals(hasOpen)) {
            // 获取数据源参数
            HikariConfig hc = getHikariCpConfig();
            // 遍历并创建多数据源
            if (!nodes.isEmpty()) {
                nodes.stream().collect(
                    Collectors.toMap(lhMap -> String.valueOf(lhMap.get(DataSourceConstants.NAME_PARAM)), lhMap -> {
                        // 对应驱动名称
                        hc.setDriverClassName(String.valueOf(lhMap.get(DataSourceConstants.DRIVER_CLASS_NAME_PARAM)));
                        // 对应用户名
                        hc.setUsername(String.valueOf(lhMap.get(DataSourceConstants.USERNAME_PARAM)));
                        // 对应密码
                        hc.setPassword(String.valueOf(lhMap.get(DataSourceConstants.PASSWORD_PARAM)));
                        // 对应连接字符串
                        hc.setJdbcUrl(String.valueOf(lhMap.get(DataSourceConstants.URI_PARAM)));
                        // 创建不共享的jdbc客户端（使用了hikariCP数据库连接池）
                        return JDBCClient.create(vertx, new HikariDataSource(hc));
                    })).forEach(DataSourceConstants.JDBC_CLIENT_MAP::put);
            }
        }
    }
}
