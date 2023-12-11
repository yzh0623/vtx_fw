package io.kida.yuen.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import io.kida.yuen.components.annotations.PropLoader;
import io.kida.yuen.components.constants.DataSourceConstants;
import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.kida.yuen.vo.datasource.DataSourceExecParam;
import io.kida.yuen.vo.datasource.DynamicRetData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DataSourceOperateUtil.java
 * @ClassName: DataSourceOperateUtil
 * @Description:数据库操作工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class DataSourceOperateUtil extends AbstractVerticle {

    private static final String QUERY_EXCEPTION_OUTPUT =
        "There is a problem in the process of data query, the exception is:";
    private static final String CREATE_CLIENT_EXCEPTION_OUTPUT = "Failed to establish data channel, the exception is:";
    private static final String CREATE_NO_PREPARE_OUTPUT = "System data source is being prepared, please wait...";
    private static final String OPERATE_FALISE_OUTPUT =
        "The ddl operation is abnormal, and now the data rollback begins. The abnormal content is:";
    private static final String BATCH_FALISE_OUTPUT =
        "An exception occurs in the batch operation, and the exception content is:";
    private static final String EXECUTE_FALISE_OUTPUT =
        "An exception occurs in the create operation, and the exception content is:";

    // 尝试获取数据源次数
    @PropLoader(key = "databases.retry.get-counter")
    private int getCounter;

    // 重试间隔时间（毫秒）
    @PropLoader(key = "databases.retry.interval")
    private int interval;

    @Override
    public void start() {

        // 这里需要注意的是@PropLoader注解对应的变量只需要注册服务时使用一次，因此YamlUtil.propLoadSetter方法放在start()中执行一次即可。
        // 若变量需要多次引用的话YamlUtil.propLoadSetter就必须在构造函数中声明
        YamlUtil.propLoadSetter(this);

    }

    /**
     * 
     * @MethodName: query
     * @Description: 查询方法通过sql查询，返回DyncData
     * @author yuanzhenhui
     * @param dbem
     * @param resultHandler
     *            void
     * @date 2023-04-17 04:58:56
     */
    public static void query(DataSourceExecParam dbem, Handler<DynamicRetData> resultHandler) {
        if (!DataSourceConstants.JDBC_CLIENT_MAP.isEmpty()) {
            DataSourceClientUtil.dbClient(dbem).getConnection(ar -> {
                if (ar.succeeded()) {
                    SQLConnection connection = ar.result();
                    connection.query(dbem.getExecSql(), reHandler -> {
                        if (reHandler.succeeded()) {
                            ResultSet rs = reHandler.result();
                            DynamicRetData add = new DynamicRetData();

                            // 将返回的resultset分解并存入AjaxDyncData中方便后续使用
                            if (null != rs) {
                                // 返回的行数有多少条
                                add.setNumRows(rs.getNumRows());
                                // 获取返回数据集，格式是jsonobject能够与columnName字段做映射 这里设定返回字段大小写不敏感，这样方便统一处理
                                List<JsonObject> list = rs.getRows(true);
                                list.forEach(jsonObj -> {
                                    jsonObj.stream().filter(entry -> entry.getValue() instanceof LocalDateTime
                                        || entry.getValue() instanceof Date).forEach(entry -> {
                                            LocalDateTime ldt = LocalDateTime.parse(jsonObj.getString(entry.getKey()));
                                            jsonObj.put(entry.getKey(),
                                                ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " "));
                                        });
                                });
                                add.setRows(list);
                            }
                            resultHandler.handle(add);
                        } else {
                            log.error(QUERY_EXCEPTION_OUTPUT + reHandler.cause());
                            resultHandler.handle(null);
                        }
                    }).close();
                } else {
                    log.error(CREATE_CLIENT_EXCEPTION_OUTPUT + ar.cause());
                    resultHandler.handle(null);
                }
            });
        } else {
            log.error(CREATE_NO_PREPARE_OUTPUT);
            resultHandler.handle(null);
        }
    }

    /**
     * 
     * @MethodName: opeate
     * @Description: 增、删、改的通用方法
     * @author yuanzhenhui
     * @param dbem
     * @param resultHandler
     *            void
     * @date 2023-04-17 04:59:22
     */
    public static void opeate(DataSourceExecParam dbem, Handler<Integer> resultHandler) {
        if (!DataSourceConstants.JDBC_CLIENT_MAP.isEmpty()) {
            DataSourceClientUtil.dbClient(dbem).getConnection(ar -> {
                if (ar.succeeded()) {
                    SQLConnection connection = ar.result();
                    connection.update(dbem.getExecSql(), reHandler -> {
                        if (reHandler.succeeded()) {
                            resultHandler.handle(reHandler.result().getUpdated());
                        } else {
                            log.error(OPERATE_FALISE_OUTPUT + reHandler.cause());
                            resultHandler.handle(0);
                        }
                        connection.close();
                    });
                } else {
                    log.error(CREATE_CLIENT_EXCEPTION_OUTPUT + ar.cause());
                    resultHandler.handle(0);
                }
            });
        } else {
            log.error(CREATE_NO_PREPARE_OUTPUT);
            resultHandler.handle(0);
        }
    }

    /**
     * 
     * @MethodName: batch
     * @Description: 批量执行sql语句，执行sql存放在list集合中，一般来说建议一个list集合中全部都是同一类sql，不要掺杂其他的操作(此方法只开放给手撸代码时使用，slimopt组件不使用这个方法)
     * @author yuanzhenhui
     * @param dbem
     * @param resultHandler
     *            void
     * @date 2023-04-17 04:59:53
     */
    public static void batch(DataSourceExecParam dbem, Handler<Integer> resultHandler) {
        if (!DataSourceConstants.JDBC_CLIENT_MAP.isEmpty()) {
            DataSourceClientUtil.dbClient(dbem).getConnection(ar -> {
                if (ar.succeeded()) {
                    SQLConnection connection = ar.result();
                    connection.batch(dbem.getBatchSql(), batchHandler -> {
                        if (batchHandler.succeeded()) {
                            int execSum = batchHandler.result().stream().mapToInt(x -> x).sum();
                            resultHandler.handle(execSum);
                        } else {
                            log.error(BATCH_FALISE_OUTPUT + batchHandler.cause());
                            resultHandler.handle(0);
                        }
                        connection.close();
                    });
                } else {
                    log.error(CREATE_CLIENT_EXCEPTION_OUTPUT + ar.cause());
                    resultHandler.handle(0);
                }
            });
        } else {
            log.error(CREATE_NO_PREPARE_OUTPUT);
            resultHandler.handle(0);
        }
    }

    /**
     * 
     * @MethodName: execute
     * @Description: 数据库dml操作
     * @author yuanzhenhui
     * @param dbem
     * @param flagHandler
     *            void
     * @date 2023-04-17 05:00:14
     */
    public static void execute(DataSourceExecParam dbem, Handler<Boolean> flagHandler) {
        if (!DataSourceConstants.JDBC_CLIENT_MAP.isEmpty()) {
            DataSourceClientUtil.dbClient(dbem).getConnection(ar -> {
                if (ar.succeeded()) {
                    SQLConnection connection = ar.result();
                    connection.execute(dbem.getExecSql(), resultHandler -> {
                        if (resultHandler.succeeded()) {
                            flagHandler.handle(true);
                        } else {
                            log.error(EXECUTE_FALISE_OUTPUT + resultHandler.cause());
                            flagHandler.handle(false);
                        }
                        connection.close();
                    });
                } else {
                    log.error(CREATE_CLIENT_EXCEPTION_OUTPUT + ar.cause());
                    flagHandler.handle(false);
                }
            });
        } else {
            log.error(CREATE_NO_PREPARE_OUTPUT);
            flagHandler.handle(false);
        }
    }

}
