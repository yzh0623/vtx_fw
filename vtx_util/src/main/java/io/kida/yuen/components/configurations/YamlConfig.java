package io.kida.yuen.components.configurations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import io.kida.yuen.components.constants.GlobalConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: YamlConfig.java
 * @ClassName: YamlConfig
 * @Description:获取本地yml配置文件
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/17
 */
@Slf4j
public class YamlConfig extends AbstractVerticle {

    // 指定配置目录和启动文件（类似springboot的约定大于配置）
    private static final String CONFIG_FOLDER = "configs";
    private static final String BOOTSTRAP_PATH = CONFIG_FOLDER + "/bootstrap.yml";

    /**
     * 
     * @MethodName: start
     * @Description: 将配置信息加载到内存
     * @author yuanzhenhui
     * @see io.vertx.core.AbstractVerticle#start()
     * @date 2023-10-17 09:31:16
     */
    @Override
    public void start() {

        // 若配置信息已经加载过了就不需要重新加载了，这里做了个判断
        if (GlobalConstants.YAML_MAP == null || GlobalConstants.YAML_MAP.isEmpty()) {

            // 第一次获取bootstrap文件的基本路径
            Buffer bootBfr = vertx.fileSystem().readFileBlocking(BOOTSTRAP_PATH);
            try {
                if (null != bootBfr) {
                    // 读取bootstrap文件中的配置内容
                    Map<?, ?> baseMap = (Map<?, ?>)new Yaml().load(bootBfr.toString());
                    // 读取之后数据以Map<Object,Object>返回，这个时候需要转换成Map<String,Object>格式
                    Map<String, Object> bootMap = baseMap.entrySet().stream()
                        .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Entry::getValue));
                    GlobalConstants.YAML_MAP.putAll(bootMap);

                    // 获取bootstrap文件中有关当前环境的信息server.active这个跟springboot很相似
                    String envStr = new JsonObject(bootMap).getJsonObject("server").getString("active");
                    // 根据配置环境名字找到对应目录下的所有文件
                    vertx.fileSystem().readDir(CONFIG_FOLDER + "/" + envStr, dirHeader -> {
                        if (dirHeader.succeeded()) {
                            // 获取到所有目录下的文件路径集合
                            List<String> fileList = dirHeader.result();
                            if (null != fileList && !fileList.isEmpty()) {
                                for (String pathName : fileList) {
                                    Buffer pluginBfr = vertx.fileSystem().readFileBlocking(pathName);
                                    if (Objects.nonNull(pluginBfr)) {
                                        Map<?, ?> pluginMap = (Map<?, ?>)new Yaml().load(pluginBfr.toString());
                                        Map<String, Object> appMap = pluginMap.entrySet().stream().collect(
                                            Collectors.toMap(entry -> String.valueOf(entry.getKey()), Entry::getValue));
                                        GlobalConstants.YAML_MAP.putAll(appMap);
                                    }
                                }
                                GlobalConstants.CONFIG_LOAD_COMPLETE = true;
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("func[YamlConfig.start] Exception [{} - {}]", e.getCause(), e.getMessage());
            }
        }
    }
}
