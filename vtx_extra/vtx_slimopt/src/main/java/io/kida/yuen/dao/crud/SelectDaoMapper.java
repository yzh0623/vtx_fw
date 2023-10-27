package io.kida.yuen.dao.crud;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.kida.yuen.components.annotions.DBLoader;
import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.dao.DaoSetUp;
import io.kida.yuen.utils.DaoUtil;
import io.kida.yuen.utils.DataSourceOperateUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.system.router.RouterValue;
import io.kida.yuen.vo.datasource.DataSourceExecParam;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: SelectDaoMapper.java
 * @ClassName: SelectDaoMapper
 * @Description:查询操作类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class SelectDaoMapper extends AbstractVerticle implements DaoSetUp {

    private static final String SELECT_COMPLETE_TIP = "Search Complete";
    private static final String SELECT_FALUSE_TIP = "Search Faluse";

    @Override
    public void start() {
        eventBusLoader(SelectDaoMapper.class, vertx);
    }

    /**
     * 
     * @MethodName: selectByPk
     * @Description: 根据主键进行查询
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:32:10
     */
    @DBLoader
    public <T> void selectByPk(Message<T> msg) {
        try {
            log.info("mapper ip is : " + Inet4Address.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);
            StringBuilder pkBuilder = DaoUtil.getAndPk(entityList, dsrcExec.getDataParam());

            StringBuilder headerBuilder = new StringBuilder("");
            entityList.stream()
                .forEach(map -> headerBuilder.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                    .append(DaoConstants.BACKQUOTE).append(" AS ").append(DaoConstants.BACKQUOTE)
                    .append(map.get(EntityConstants.ENTITY_FIELD)).append(DaoConstants.BACKQUOTE).append(","));

            StringBuilder selectBuilder = new StringBuilder("SELECT ")
                .append(headerBuilder.substring(0, headerBuilder.length() - 1)).append(" FROM ")
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME));

            if (StringUtil.isNotEmpty(pkBuilder)) {
                selectBuilder.append(DaoConstants.WHERE).append(pkBuilder);

                String sql = selectBuilder.toString();
                dsrcExec.setExecSql(sql);
                log.info("[ function selectByPk ] - " + sql);
                DataSourceOperateUtil.query(dsrcExec, hdr -> {
                    if (null != hdr) {
                        msg.reply(Json.encode(new RouterValue(1, hdr, SELECT_COMPLETE_TIP)));
                    } else {
                        msg.reply(Json.encode(new RouterValue(0, SELECT_FALUSE_TIP)));
                    }
                });
            } else {
                msg.reply(Json.encode(new RouterValue(0, "Missing id value")));
            }
        });
    }

    /**
     * 
     * @MethodName: selectByCondition
     * @Description: 根据条件进行查询（兼容分页查询）
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:32:26
     */
    @DBLoader
    public <T> void selectByCondition(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            StringBuilder conditionBuilder = DaoUtil.getAndCondition(entityList, dsrcExec.getDataParam());

            StringBuilder headerBuilder = new StringBuilder("");
            entityList.stream()
                .forEach(map -> headerBuilder.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                    .append(DaoConstants.BACKQUOTE).append(" AS ").append(DaoConstants.BACKQUOTE)
                    .append(map.get(EntityConstants.ENTITY_FIELD)).append(DaoConstants.BACKQUOTE).append(","));

            StringBuilder selectBuilder = new StringBuilder("SELECT ")
                .append(headerBuilder.substring(0, headerBuilder.length() - 1)).append(" FROM ")
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME));
            if (StringUtil.isNotEmpty(conditionBuilder)) {
                selectBuilder.append(DaoConstants.WHERE).append(conditionBuilder);
            }
            if (StringUtil.isNotEmpty(dsrcExec.getTailSql())) {
                selectBuilder.append(dsrcExec.getTailSql());
            }

            String sql = selectBuilder.toString();
            dsrcExec.setExecSql(sql);
            log.info("[ function selectByCondition ] - " + sql);

            DataSourceOperateUtil.query(dsrcExec, hdr -> {
                if (null != hdr) {
                    msg.reply(Json.encode(new RouterValue(1, hdr, SELECT_COMPLETE_TIP)));
                } else {
                    msg.reply(Json.encode(new RouterValue(0, SELECT_FALUSE_TIP)));
                }
            });
        });
    }

    /**
     * 
     * @MethodName: selectByConditionCounter
     * @Description: 分页查询获取总数
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:32:56
     */
    @DBLoader
    public <T> void selectByConditionCounter(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            StringBuilder conditionBuilder = DaoUtil.getAndCondition(entityList, dsrcExec.getDataParam());

            // 计算条件总数
            StringBuilder selectCountBuilder = new StringBuilder("SELECT COUNT(1) AS COUNTER FROM ").append(
                String.valueOf(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME)));
            if (StringUtil.isNotEmpty(conditionBuilder)) {
                selectCountBuilder.append(DaoConstants.WHERE).append(conditionBuilder);
            }

            String sql = selectCountBuilder.toString();
            dsrcExec.setExecSql(sql);
            log.info("[ function selectByConditionCounter ] - " + sql);

            DataSourceOperateUtil.query(dsrcExec, hdrCount -> {
                if (null != hdrCount) {
                    int totalCount = hdrCount.getRows().get(0).getInteger("COUNTER");
                    if (totalCount > 0) {
                        hdrCount.setNumRows(totalCount);
                        hdrCount.setRows(new ArrayList<JsonObject>());
                        msg.reply(Json.encode(new RouterValue(1, hdrCount, SELECT_COMPLETE_TIP)));
                    } else {
                        hdrCount.setNumRows(0);
                        hdrCount.setRows(new ArrayList<JsonObject>());
                        msg.reply(Json.encode(new RouterValue(0, hdrCount, SELECT_COMPLETE_TIP)));
                    }
                } else {
                    msg.reply(Json.encode(new RouterValue(0, SELECT_FALUSE_TIP)));
                }
            });
        });
    }

}
