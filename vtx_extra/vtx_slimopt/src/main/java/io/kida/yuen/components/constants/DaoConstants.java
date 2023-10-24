package io.kida.yuen.components.constants;

import java.util.HashMap;

import io.kida.yuen.dao.crud.DeleteDaoMapper;
import io.kida.yuen.dao.crud.InsertDaoMapper;
import io.kida.yuen.dao.crud.SelectDaoMapper;
import io.kida.yuen.dao.crud.UpdateDaoMapper;
import io.kida.yuen.utils.system.yaml.YamlUtil;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DaoConstants.java
 * @ClassName: DaoConstants
 * @Description:dao常量
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class DaoConstants {

    // dao操作
    public static final String INSERT_PARAM = "insert";
    public static final String DELETE_PARAM = "delete";
    public static final String QUERY_PARAM = "query";
    public static final String UPDATE_PARAM = "update";

    // 获取dao全称
    public static final String INSERT_DAO_FULL_NAME = InsertDaoMapper.class.getName();
    public static final String DELETE_DAO_FULL_NAME = DeleteDaoMapper.class.getName();
    public static final String SELECT_DAO_FULL_NAME = SelectDaoMapper.class.getName();
    public static final String UPDATE_DAO_FULL_NAME = UpdateDaoMapper.class.getName();

    public static final String CLAZZ_PATH = "clazzPath";
    public static final String DB_LIMIT = "dbLimit";
    public static final String DB_ORDER = "dbOrder";

    // sql常用常量
    private static final HashMap<String, String> BACKQUOTE_MAP = new HashMap<String, String>(10) {
        private static final long serialVersionUID = 1L;
        {
            put("mysql", "`");
            put("postgresql", "\"");
        }
    };
    public static final String LEFT_PARENTHESIS = " (";
    public static final String RIGHT_PARENTHESIS = ")";
    public static final String BACKQUOTE = BACKQUOTE_MAP.get(YamlUtil.getStringValue("databases.db-type"));
    public static final String COMMA = ",";
    public static final String APOSTROPHE = "'";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String EQUAL = " = ";
    public static final String PERCENT_SIGN = "%";
    public static final String LIKE = " LIKE ";
    public static final String ISNULL = " IS NULL ";
    public static final String ORDERBY = " ORDER BY ";

}
