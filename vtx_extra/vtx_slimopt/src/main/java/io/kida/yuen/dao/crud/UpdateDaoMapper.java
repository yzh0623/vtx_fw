package io.kida.yuen.dao.crud;

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
 * @File: UpdateDaoMapper.java
 * @ClassName: UpdateDaoMapper
 * @Description:更新操作类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class UpdateDaoMapper extends AbstractVerticle implements DaoSetUp {

    private static final String UPDATE = "UPDATE ";
    private static final String SET = " SET ";

    private static final String UPDATE_COMPLETE_TIP = "Update Complete";
    private static final String UPDATE_FALUSE_TIP = "Update Faluse";

    private static final String ACCEPT_FROM = "from";
    private static final String ACCEPT_TO = "to";

    @Override
    public void start() {
        eventBusLoader(UpdateDaoMapper.class, vertx);
    }

    /**
     * 
     * @MethodName: updateByPk
     * @Description: 根据主键进行更新
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:41:05
     */
    @DBLoader
    public <T> void updateByPk(Message<T> msg) {
        // 先判断传过来的信息是否为空
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            StringBuilder pkBuilder = DaoUtil.getAndPk(entityList, dsrcExec.getDataParam());
            StringBuilder updateBuilder = new StringBuilder(UPDATE)
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME))
                .append(SET);

            if (StringUtil.isNotEmpty(pkBuilder)) {
                JsonObject jsonObj = dsrcExec.getDataParam();
                for (Map<String, Object> map : entityList) {
                    String setVal = jsonObj.getString(String.valueOf(map.get(EntityConstants.ENTITY_FIELD)));
                    if ((!(Boolean)map.get(EntityConstants.FIELD_IS_PK)) && StringUtil.isNotEmpty(setVal)) {
                        updateBuilder.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                            .append(DaoConstants.BACKQUOTE).append(DaoConstants.EQUAL).append(DaoConstants.APOSTROPHE)
                            .append(setVal).append(DaoConstants.APOSTROPHE).append(DaoConstants.COMMA);
                    }
                }

                StringBuilder execBuilder = new StringBuilder(updateBuilder.substring(0, updateBuilder.length() - 1))
                    .append(DaoConstants.WHERE).append(pkBuilder);

                String sql = execBuilder.toString();
                dsrcExec.setExecSql(sql);
                log.info("[ function updateByPk ] - " + sql);

                DataSourceOperateUtil.opeate(dsrcExec, hdr -> {
                    if (hdr.intValue() > 0) {
                        msg.reply(Json.encode(new RouterValue(1, hdr, UPDATE_COMPLETE_TIP)));
                    } else {
                        msg.reply(Json.encode(new RouterValue(0, UPDATE_FALUSE_TIP)));
                    }
                });
            } else {
                msg.reply(Json.encode(new RouterValue(0, "Missing id value")));
            }
        });
    }

    /**
     * 
     * @MethodName: updateByCondition
     * @Description: 根据条件进行更新
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 11:41:46
     */
    @DBLoader
    public <T> void updateByCondition(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            JsonObject from = dsrcExec.getDataParam().getJsonObject(ACCEPT_FROM);
            JsonObject to = dsrcExec.getDataParam().getJsonObject(ACCEPT_TO);

            // from可以为空这是为了适配全更新的情况
            if (null != to && !to.isEmpty()) {
                StringBuilder updateBuilder = new StringBuilder(UPDATE)
                    .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME))
                    .append(SET);
                for (Map<String, Object> map : entityList) {
                    String setVal = to.getString(String.valueOf(map.get(EntityConstants.ENTITY_FIELD)));
                    if (StringUtil.isNotEmpty(setVal)) {
                        updateBuilder.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                            .append(DaoConstants.BACKQUOTE).append(DaoConstants.EQUAL).append(DaoConstants.APOSTROPHE)
                            .append(setVal).append(DaoConstants.APOSTROPHE).append(DaoConstants.COMMA);
                    }
                }

                StringBuilder execBuilder = new StringBuilder(updateBuilder.substring(0, updateBuilder.length() - 1));
                if (null != from && !from.isEmpty()) {
                    StringBuilder conditionBuilder = DaoUtil.getAndCondition(entityList, from);
                    execBuilder.append(DaoConstants.WHERE).append(conditionBuilder);
                }

                String sql = execBuilder.toString();
                dsrcExec.setExecSql(sql);
                log.info("[ function updateByCondition ] - " + sql);

                DataSourceOperateUtil.opeate(dsrcExec, hdr -> {
                    if (hdr.intValue() > 0) {
                        msg.reply(Json.encode(new RouterValue(1, hdr, UPDATE_COMPLETE_TIP)));
                    } else {
                        msg.reply(Json.encode(new RouterValue(0, UPDATE_FALUSE_TIP)));
                    }
                });
            } else {
                msg.reply(Json.encode(new RouterValue(0, "What to update？")));
            }
        });
    }
}
