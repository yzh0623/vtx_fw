package io.kida.yuen.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.vo.pagehelper.Pages;
import io.kida.yuen.vo.pagehelper.limit.LimitInterface;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DaoUtil.java
 * @ClassName: DaoUtil
 * @Description:DAO工具
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Slf4j
public class DaoUtil {

    private static final String RE_VALUE_DATA_FIELD = "retData";
    private static final String DYNC_DATA_ROWS_FIELD = "rows";
    private static final String DYNC_DATA_NUM_ROWS_FIELD = "numRows";

    /**
     * 
     * @MethodName: getAndPk
     * @Description: 动态获得主键内容
     * @author yuanzhenhui
     * @param entityList
     * @param dataParam
     * @return StringBuilder
     * @date 2023-10-19 02:52:23
     */
    public static StringBuilder getAndPk(List<Map<String, Object>> entityList, JsonObject dataParam) {
        StringBuilder whereBuilder = null;

        Map<String, Object> pkMap =
            entityList.stream().filter(map -> (Boolean)map.get(EntityConstants.FIELD_IS_PK)).findFirst().get();
        if (null != pkMap && !pkMap.isEmpty()) {
            String pkValue = dataParam.getString(String.valueOf(pkMap.get(EntityConstants.ENTITY_FIELD)));
            if (StringUtil.isNotEmpty(pkValue)) {
                whereBuilder =
                    new StringBuilder().append(DaoConstants.BACKQUOTE).append(pkMap.get(EntityConstants.SQL_FIELD))
                        .append(DaoConstants.BACKQUOTE).append(DaoConstants.EQUAL).append(DaoConstants.APOSTROPHE)
                        .append(pkValue).append(DaoConstants.APOSTROPHE);
            }
        }

        return whereBuilder;
    }

    /**
     * 
     * @MethodName: getAndCondition
     * @Description: 动态获得选择性内容
     * @author yuanzhenhui
     * @param entityList
     * @param dataParam
     * @return StringBuilder
     * @date 2023-10-19 03:00:33
     */
    public static StringBuilder getAndCondition(List<Map<String, Object>> entityList, JsonObject dataParam) {
        StringBuilder whereBuilder = null;
        StringBuilder conditionBuilder = new StringBuilder();

        entityList.stream().forEach(map -> {
            String conditionString = dataParam.getString(String.valueOf(map.get(EntityConstants.ENTITY_FIELD)));
            if (null != conditionString) {
                conditionBuilder.append(DaoConstants.BACKQUOTE).append(map.get(EntityConstants.SQL_FIELD))
                    .append(DaoConstants.BACKQUOTE);
                if (StringUtil.isNotEmpty(conditionString)) {
                    if (map.get(EntityConstants.FIELD_TYPE).equals(String.class)) {
                        // 若字段类型是String的情况下将采用Like的关键字进行模糊搜索
                        conditionBuilder.append(DaoConstants.LIKE).append(DaoConstants.APOSTROPHE)
                            .append(DaoConstants.PERCENT_SIGN).append(conditionString).append(DaoConstants.PERCENT_SIGN)
                            .append(DaoConstants.APOSTROPHE).append(DaoConstants.AND);
                    } else if (map.get(EntityConstants.FIELD_TYPE).equals(Date.class)) {
                        // 若字段是Date类型则采用‘’进行精确搜索
                        conditionBuilder.append(DaoConstants.EQUAL).append(DaoConstants.APOSTROPHE)
                            .append(conditionString).append(DaoConstants.APOSTROPHE).append(DaoConstants.AND);
                    } else {
                        // 其他的情况下都是不带单引号的搜索
                        conditionBuilder.append(DaoConstants.EQUAL).append(conditionString).append(DaoConstants.AND);
                    }
                } else {
                    conditionBuilder.append(DaoConstants.ISNULL).append(DaoConstants.AND);
                }
            }
        });
        if (conditionBuilder.length() > 0) {
            conditionBuilder.setLength(conditionBuilder.length() - 4);
            whereBuilder = conditionBuilder;
        }
        return whereBuilder;
    }

    /**
     * 
     * @MethodName: setupPaginationParam
     * @Description: 通过反射设定分页参数
     * @author yuanzhenhui
     * @param start
     * @param pageSize
     * @param type
     * @return String
     * @date 2023-10-19 03:00:44
     */
    public static String setupPaginationParam(int start, int pageSize, String type) {
        String limitReturn = null;
        try {
            Class<?> limitClazz = Class.forName(
                LimitInterface.class.getPackage().getName() + ".impl." + StringUtil.getFirstUpper(type) + "Limit");
            Constructor<?> constructor = limitClazz.getConstructor(int.class, int.class);
            LimitInterface li = (LimitInterface)constructor.newInstance(start, pageSize);
            limitReturn = li.limitRetuen();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
            | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("func[DaoUtil.setupPaginationParam] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return limitReturn;
    }

    /**
     * 
     * @MethodName: transToPage
     * @Description: 将List集合转换成Page分页查询
     * @author yuanzhenhui
     * @param <T>
     * @param jsonObj
     * @param pageNo
     * @param pageSize
     * @return Pages<T>
     * @date 2023-10-19 03:00:54
     */
    @SuppressWarnings("unchecked")
    public static <T> Pages<T> transToPage(JsonObject jsonObj, int pageNo, int pageSize) {
        Pages<T> page = null;
        if (null == jsonObj || jsonObj.isEmpty()) {
            page = new Pages<>(0);
            page.setStart(0);
            page.setPageSize(0);
        } else {
            JsonObject retDataData = jsonObj.getJsonObject(RE_VALUE_DATA_FIELD);
            JsonArray rowsArray = retDataData.getJsonArray(DYNC_DATA_ROWS_FIELD);
            int totalRows = retDataData.getInteger(DYNC_DATA_NUM_ROWS_FIELD);
            page = new Pages<>(totalRows);
            page.setStart(page.getStart(pageNo));
            page.setPageSize(pageSize);
            if (totalRows > 0) {
                page.setPageList(rowsArray.getList());
            }
        }
        return page;
    }

}
