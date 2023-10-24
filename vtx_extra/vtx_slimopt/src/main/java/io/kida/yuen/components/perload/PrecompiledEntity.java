package io.kida.yuen.components.perload;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.esotericsoftware.reflectasm.MethodAccess;

import io.kida.yuen.components.annotions.DBLoader;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.components.constants.RouterConstants;
import io.kida.yuen.utils.selfdev.base.ReflectUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: PrecompiledEntity.java
 * @ClassName: PrecompiledEntity
 * @Description:预编译实体
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class PrecompiledEntity extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        // 扫描所有实体类
        CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(RouterConstants.SERVER_MODEL_LOADED, true);
        clazzSet.parallelStream().forEach(clazz -> {
            Long start = System.currentTimeMillis();
            // 获得类集合
            Map<String, Object> classMap = new HashMap<>();
            // 获取实体对应的数据源名称
            classMap.put(EntityConstants.DB_LOADER,
                clazz.isAnnotationPresent(DBLoader.class) ? clazz.getDeclaredAnnotation(DBLoader.class).key() : null);
            // 获取表名
            classMap.put(EntityConstants.TABLE_NAME,
                clazz.isAnnotationPresent(Table.class) ? clazz.getDeclaredAnnotation(Table.class).name() : null);
            // 获取实体对应的反射访问方法对象
            classMap.put(EntityConstants.METHOD_ACCESS, MethodAccess.get(clazz));
            // 获取实体对应的类
            classMap.put(EntityConstants.ENTITY_NAME, clazz);

            EntityConstants.CLASS_MAP.put(clazz.getSimpleName(), classMap);

            // 获得类信息集合
            // 由于 stream 创建有一定 overhead,会产生对象和调用stack,比直接 for 循环稍慢，因此这里从stream模式改为传统的for循环模式
            List<Map<String, Object>> entityList = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    Map<String, Object> fieldMap = new HashMap<>();
                    // 保存字段名称
                    fieldMap.put(EntityConstants.ENTITY_FIELD, field.getName());
                    // 保存sql字段名称
                    fieldMap.put(EntityConstants.SQL_FIELD, StringUtil.camel2Underline(field.getName()).toUpperCase());
                    // 保存字段类型
                    fieldMap.put(EntityConstants.FIELD_TYPE, field.getType());
                    // 是否主键
                    fieldMap.put(EntityConstants.FIELD_IS_PK, field.isAnnotationPresent(Id.class));
                    entityList.add(fieldMap);
                }
            }

            EntityConstants.ENTITY_INFO_MAP.put(clazz, entityList);

            Long total = System.currentTimeMillis() - start;
            log.info("Precompiled entity class:" + clazz.getSimpleName() + " complete :" + total + "ms");
        });
    }
}
