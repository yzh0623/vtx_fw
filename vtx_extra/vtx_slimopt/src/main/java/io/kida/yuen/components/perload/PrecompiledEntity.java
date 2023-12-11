package io.kida.yuen.components.perload;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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
 * @Description:预获取类和对应实体
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class PrecompiledEntity extends AbstractVerticle {

    @Override
    public void start() {

        try {
            // 扫描所有实体类
            CopyOnWriteArraySet<Class<?>> clazzSet = ReflectUtil.getClasses(RouterConstants.SERVER_MODEL_LOADED, true);

            // 获取所有 class 集合
            clazzSet.stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName(), clazz -> {
                // 获得类集合
                Map<String, Object> classMap = new HashMap<>(4);
                // 获取实体对应的数据源名称
                classMap.put(EntityConstants.DB_LOADER, clazz.isAnnotationPresent(DBLoader.class)
                    ? clazz.getDeclaredAnnotation(DBLoader.class).key() : null);
                // 获取表名
                classMap.put(EntityConstants.TABLE_NAME,
                    clazz.isAnnotationPresent(Table.class) ? clazz.getDeclaredAnnotation(Table.class).name() : null);
                // 获取实体对应的反射访问方法对象
                classMap.put(EntityConstants.METHOD_ACCESS, MethodAccess.get(clazz));
                // 获取实体对应的类
                classMap.put(EntityConstants.ENTITY_NAME, clazz);
                return classMap;
            })).forEach(EntityConstants.CLASS_MAP::put);

            // 获取所有实体集合
            clazzSet.stream().collect(Collectors.toMap(clazz -> clazz, clazz -> {
                return Arrays.asList(clazz.getDeclaredFields()).stream()
                    .filter(field -> field.isAnnotationPresent(Column.class)).map(field -> {
                        Map<String, Object> fieldMap = new HashMap<>(4);
                        // 保存字段名称
                        fieldMap.put(EntityConstants.ENTITY_FIELD, field.getName());
                        // 保存sql字段名称
                        fieldMap.put(EntityConstants.SQL_FIELD,
                            StringUtil.camel2Underline(field.getName()).toUpperCase());
                        // 保存字段类型
                        fieldMap.put(EntityConstants.FIELD_TYPE, field.getType());
                        // 是否主键
                        fieldMap.put(EntityConstants.FIELD_IS_PK, field.isAnnotationPresent(Id.class));
                        return fieldMap;
                    }).collect(Collectors.toList());
            })).forEach(EntityConstants.ENTITY_INFO_MAP::put);
        } catch (ClassNotFoundException | IOException e) {
            log.error("func[PrecompiledEntity.start] Main method error Exception [{} - {}]", e.getCause(),
                e.fillInStackTrace());
        }
    }
}
