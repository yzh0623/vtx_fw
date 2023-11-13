package io.kida.yuen.utils.system.yaml;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.kida.yuen.components.annotations.PropLoader;
import io.kida.yuen.components.constants.GlobalConstants;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: YamlUtil.java
 * @ClassName: YamlUtil
 * @Description:Yaml读取工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class YamlUtil {

    private static JsonObject JSON_OBJECT = null;

    static {
        // 等待configJson静态变量加载完成
        while (true) {
            if (GlobalConstants.CONFIG_LOAD_COMPLETE) {
                JSON_OBJECT = new JsonObject(GlobalConstants.YAML_MAP);
                break;
            } else {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    log.error("func[YamlUtil.static] Exception [{} - {}]", e.getCause(), e.getStackTrace());
                }
            }
        }
    }

    /**
     * 
     * @MethodName: getIntegerValue
     * @Description: 返回Integer类型配置
     * @author yuanzhenhui
     * @param key
     * @return Integer
     * @date 2023-10-11 05:30:24
     */
    public static Integer getIntegerValue(String key) {
        return Integer.valueOf(getStringValue(key));
    }

    /**
     * 
     * @MethodName: getBooleanValue
     * @Description: 返回Boolean类型配置
     * @author yuanzhenhui
     * @param key
     * @return Boolean
     * @date 2023-10-11 05:30:33
     */
    public static Boolean getBooleanValue(String key) {
        return Boolean.valueOf(getStringValue(key));
    }

    /**
     * 
     * @MethodName: getStringValue
     * @Description: 返回String类型配置
     * @author yuanzhenhui
     * @param key
     * @return String
     * @date 2023-10-11 05:31:39
     */
    public static String getStringValue(String key) {
        return String.valueOf(getValue(key));
    }

    /**
     * 
     * @MethodName: getListValue
     * @Description: 返回List类型配置
     * @author yuanzhenhui
     * @param key
     * @return List<?>
     * @date 2023-10-11 05:31:58
     */
    public static List<?> getListValue(String key) {
        return new JsonArray(getStringValue(key)).getList();
    }

    /**
     * 
     * @MethodName: getMapValue
     * @Description: 返回Map类型配置
     * @author yuanzhenhui
     * @param key
     * @return Map<?,?>
     * @date 2023-10-11 05:32:06
     */
    public static Map<?, ?> getMapValue(String key) {
        return new JsonObject(getStringValue(key)).getMap();
    }

    /**
     * 
     * @MethodName: getValue
     * @Description: 获取Object值
     * @author yuanzhenhui
     * @param value
     * @return Object
     * @date 2023-10-11 05:40:14
     */
    public static Object getValue(String value) {
        String[] values = value.split("\\.");
        int len = values.length - 1;
        JsonObject json = JSON_OBJECT;

        // 使用递归遍历获取到配置信息
        for (int i = 0; i < len; i++) {
            if (json.containsKey(values[i])) {
                json = json.getJsonObject(values[i]);
            } else {
                return null;
            }
        }
        return json.getValue(values[len]);
    }

    /**
     * 
     * @MethodName: propLoadSetter
     * @Description: 使用自定义注解获取到变量内容（不需要用到reflectasm）
     * @author yuanzhenhui
     * @param <T>
     * @param t
     *            void
     * @date 2023-10-11 05:40:25
     */
    public static <T> void propLoadSetter(T t) {
        Arrays.asList(t.getClass().getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(PropLoader.class))
            .forEach(f -> {
                f.setAccessible(true);
                PropLoader pl = f.getDeclaredAnnotation(PropLoader.class);
                try {
                    Object obj = getValue(pl.key());
                    if (obj instanceof JsonArray) {
                        obj = ((JsonArray)obj).getList();
                    } else if (obj instanceof JsonObject) {
                        obj = ((JsonObject)obj).getMap();
                    }
                    f.set(t, obj);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("func[YamlUtil.propLoadSetter] Exception [{} - {}]", e.getCause(), e.getStackTrace());
                }
            });
    }
}
