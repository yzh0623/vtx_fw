package io.kida.yuen.components.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: EntityConstants.java
 * @ClassName: EntityConstants
 * @Description:实体常量
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class EntityConstants {

    // 实体字段
    public static final String ENTITY_FIELD = "entity_field";

    // sql字段
    public static final String SQL_FIELD = "sql_field";

    // 字段类型
    public static final String FIELD_TYPE = "field_type";

    // 数据源
    public static final String DB_LOADER = "db_loader";

    // 表名称
    public static final String TABLE_NAME = "table_name";

    // 方法访问
    public static final String METHOD_ACCESS = "method_access";

    // 实体名称
    public static final String ENTITY_NAME = "entity_name";

    // 实体主键
    public static final String FIELD_IS_PK = "field_is_pk";

    // 类集合
    public static final Map<String, Map<String, Object>> CLASS_MAP = new HashMap<>(150);

    // 类信息
    public static final Map<Class<?>, List<Map<String, Object>>> ENTITY_INFO_MAP = new HashMap<>(150);

    // 转换信息
    public static final Map<Class<?>, Map<String, String>> SQL_TRANS_MAP = new HashMap<>(150);

}
