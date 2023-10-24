package io.kida.yuen.utils;

import io.kida.yuen.components.constants.DaoConstants;
import io.kida.yuen.components.constants.EntityConstants;
import io.kida.yuen.components.constants.HttpConstants;
import io.kida.yuen.utils.selfdev.base.StringUtil;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: RouterUtil.java
 * @ClassName: RouterUtil
 * @Description:路由工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class RouterUtil {

    private static final String[] OPERATE_ARRAY = new String[] {DaoConstants.INSERT_PARAM, DaoConstants.DELETE_PARAM,
        DaoConstants.QUERY_PARAM, DaoConstants.UPDATE_PARAM};

    /**
     * 
     * @MethodName: extractEntity
     * @Description: 根据名称获取实体
     * @author yuanzhenhui
     * @param uri
     * @return Class<?>
     * @date 2023-10-19 03:19:33
     */
    public static Class<?> extractEntity(String uri) {
        Class<?> clazz = null;
        StringBuilder entityNameBuf = new StringBuilder();

        for (String operate : OPERATE_ARRAY) {
            if (uri.indexOf(operate) > -1) {
                String tmpStr = uri.substring(uri.indexOf(operate) + operate.length() + 1);
                if (tmpStr.indexOf(HttpConstants.HTTP_SLASH) > -1) {
                    entityNameBuf.append(tmpStr.substring(0, tmpStr.indexOf(HttpConstants.HTTP_SLASH)));
                } else {
                    entityNameBuf.append(tmpStr);
                }
            }
        }

        if (StringUtil.isNotEmpty(entityNameBuf)) {
            clazz = (Class<?>)EntityConstants.CLASS_MAP.get(StringUtil.getFirstUpper(entityNameBuf.toString()))
                .get(EntityConstants.ENTITY_NAME);
        }
        return clazz;
    }

}
