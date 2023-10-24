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
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DeleteDaoMapper.java
 * @ClassName: DeleteDaoMapper
 * @Description:删除操作类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class DeleteDaoMapper extends AbstractVerticle implements DaoSetUp {

    private static final String DELETE_FROM = "DELETE FROM ";

    // 提示信息
    private static final String DELETE_COMPLETE_TIP = "Delete Complete";
    private static final String DELETE_FALUSE_TIP = "Delete Faluse";

    @Override
    public void start() {
        eventBusLoader(DeleteDaoMapper.class, vertx);
    }

    /**
     * 
     * @MethodName: deleteByPk
     * @Description: 根据主键进行删除
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 10:50:29
     */
    @DBLoader
    public <T> void deleteByPk(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            // 获取需要执行的类
            Class<?> clazz = dsrcExec.getTargetClass();
            // 通过类找到对应的字段信息
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            // 根据字段找到主键
            StringBuilder pkBuilder = DaoUtil.getAndPk(entityList, dsrcExec.getDataParam());
            // 组装delete sql
            StringBuilder deleteBuilder = new StringBuilder(DELETE_FROM)
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME));
            if (StringUtil.isNotEmpty(pkBuilder)) {
                // 当存在主键条件的时候就拼接到字符串
                deleteBuilder.append(DaoConstants.WHERE).append(pkBuilder);
                String sql = deleteBuilder.toString();
                dsrcExec.setExecSql(sql);
                log.info("[ function deleteByPk ] - " + sql);
                DataSourceOperateUtil.opeate(dsrcExec, hdr -> {
                    if (hdr.intValue() > 0) {
                        msg.reply(Json.encode(new RouterValue(1, hdr, DELETE_COMPLETE_TIP)));
                    } else {
                        msg.reply(Json.encode(new RouterValue(0, DELETE_FALUSE_TIP)));
                    }
                });
            } else {
                msg.reply(Json.encode(new RouterValue(0, "Missing id value")));
            }
        });
    }

    /**
     * 
     * @MethodName: deleteByCondition
     * @Description: 根据条件进行删除，如果没有条件输入的话将会全部删除
     * @author yuanzhenhui
     * @param <T>
     * @param msg
     *            void
     * @date 2023-10-19 10:50:42
     */
    @DBLoader
    public <T> void deleteByCondition(Message<T> msg) {
        setupBeforeOperate(msg, (DataSourceExecParam dsrcExec) -> {
            Class<?> clazz = dsrcExec.getTargetClass();
            List<Map<String, Object>> entityList = EntityConstants.ENTITY_INFO_MAP.get(clazz);

            StringBuilder conditionBuilder = DaoUtil.getAndCondition(entityList, dsrcExec.getDataParam());
            StringBuilder deleteBuilder = new StringBuilder(DELETE_FROM)
                .append(EntityConstants.CLASS_MAP.get(clazz.getSimpleName()).get(EntityConstants.TABLE_NAME));
            if (StringUtil.isNotEmpty(conditionBuilder)) {
                deleteBuilder.append(DaoConstants.WHERE).append(conditionBuilder);
            }
            String sql = deleteBuilder.toString();
            dsrcExec.setExecSql(sql);
            log.info("[ function deleteByCondition ] - " + sql);
            DataSourceOperateUtil.opeate(dsrcExec, hdr -> {
                if (hdr.intValue() > 0) {
                    msg.reply(Json.encode(new RouterValue(1, hdr, DELETE_COMPLETE_TIP)));
                } else {
                    msg.reply(Json.encode(new RouterValue(0, DELETE_FALUSE_TIP)));
                }
            });
        });
    }
}
