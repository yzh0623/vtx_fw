package io.kida.yuen.dao.crud;

import java.util.List;
import java.util.Map;

import io.kida.yuen.components.annotions.DBLoader;
import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.dao.DaoSetUp;
import io.kida.yuen.utils.DataSourceOperateUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.system.router.RouterValue;
import io.kida.yuen.vo.datasource.DataSourceExecParam;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: InsertDaoMapper.java
 * @ClassName: InsertDaoMapper
 * @Description:插入操作类（基于安全考虑不提供批量插入操作）
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class InsertDaoMapper extends AbstractVerticle implements DaoSetUp {

    private static final String INSERT_INTO = " INSERT INTO ";
    private static final String VALUES = " VALUES";

    private static final String INSERT_COMPLETE_TIP = "Insert Complete";
    private static final String INSERT_FALUSE_TIP = "Insert Faluse";

    @Override
    public void start() {
        eventBusLoader(InsertDaoMapper.class, vertx);
    }

    /**
     * 
     * @MethodName: insert
     * @Description: 插入数据操作
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:29:52
     */
    @DBLoader
    public <T> void insert(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();

            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            StringBuilder insertBuilder = new StringBuilder(INSERT_INTO)
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME));
            StringBuilder fieldBuffer = new StringBuilder(DaoConstants.LEFT_PARENTHESIS);
            StringBuilder valueBuffer = new StringBuilder(VALUES).append(DaoConstants.LEFT_PARENTHESIS);
            entityList.forEach(map -> {
                String fieldValue =
                    dsrcExec.getDataParam().getString(String.valueOf(map.get(EntityConstants.ENTITY_FIELD)));
                if (StringUtil.isNotEmpty(fieldValue)) {
                    fieldBuffer.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                        .append(DaoConstants.BACKQUOTE).append(DaoConstants.COMMA);
                    valueBuffer.append(DaoConstants.APOSTROPHE).append(fieldValue).append(DaoConstants.APOSTROPHE)
                        .append(DaoConstants.COMMA);
                }
            });
            String fieldTotalStr = fieldBuffer.substring(0, fieldBuffer.length() - 1) + DaoConstants.RIGHT_PARENTHESIS;
            String valueTotalStr = valueBuffer.substring(0, valueBuffer.length() - 1) + DaoConstants.RIGHT_PARENTHESIS;
            insertBuilder.append(fieldTotalStr).append(valueTotalStr);
            String sql = insertBuilder.toString();
            dsrcExec.setExecSql(sql);
            log.info("[ function insert ] - " + sql);
            DataSourceOperateUtil.opeate(dsrcExec, hdr -> {
                if (hdr.intValue() > 0) {
                    msg.reply(Json.encode(new RouterValue(1, hdr, INSERT_COMPLETE_TIP)));
                } else {
                    msg.reply(Json.encode(new RouterValue(0, INSERT_FALUSE_TIP)));
                }
            });
        });
    }
}
