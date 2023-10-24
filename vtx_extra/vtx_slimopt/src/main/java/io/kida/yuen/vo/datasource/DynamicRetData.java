package io.kida.yuen.vo.datasource;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DynamicRetData.java
 * @ClassName: DynamicRetData
 * @Description:返回的动态数据
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Data
public class DynamicRetData {

    /**
     * 列表数据（key-value表示）
     */
    private List<JsonObject> rows = new ArrayList<>();

    /**
     * 查询行数
     */
    private Integer numRows = 0;

}
