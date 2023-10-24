package io.kida.yuen.components.constants;

import io.kida.yuen.utils.system.yaml.YamlUtil;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: RouterConstants.java
 * @ClassName: RouterConstants
 * @Description:RESTful配置
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class RouterConstants {

    public static final String BY_PK_PARAM = "byPk";
    public static final String BY_CONDITION_PARAM = "byCondition";
    public static final String BY_CONDITION_PAGE_PARAM = "byConditionPage";
    public static final String DOT = ".";
    public static final String COLON = ":";

    public static final String SELECT_BY_PK = "selectByPk";
    public static final String SELECT_BY_CONDITION = "selectByCondition";
    public static final String SELECT_BY_CONDITION_COUNT = "selectByConditionCounter";
    public static final String INSERT = "insert";
    public static final String UPDATE_BY_CONDITION = "updateByCondition";
    public static final String UPDATE_BY_PK = "updateByPk";
    public static final String DELETE_BY_CONDITION = "deleteByCondition";
    public static final String DELETE_BY_PK = "deleteByPk";

    private static final Integer THOUSAND_MILLISECOND = 1000;
    private static final Integer EVENTBUS_TIMEOUT = YamlUtil.getIntegerValue("server.event-bus.timeout");
    public static final DeliveryOptions DELIVERY_OPTIONS =
        new DeliveryOptions().setSendTimeout(EVENTBUS_TIMEOUT * THOUSAND_MILLISECOND);

    public static final String SERVER_MODEL_LOADED = YamlUtil.getStringValue("slimopt.scan.path");
    public static final String SERVER_CONTEXT = YamlUtil.getStringValue("server.context");
    public static final String SERVER_TIMEOUT = YamlUtil.getStringValue("server.timeout");
    public static final String BASE_URI = HttpConstants.HTTP_SLASH + SERVER_CONTEXT + HttpConstants.HTTP_SLASH;

    public static final String CAN_NOT_FIND_ENTITY = "Unable to find entity class by name";
}
