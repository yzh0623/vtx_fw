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
 * @File: DataSourceExecParam.java
 * @ClassName: DataSourceExecParam
 * @Description:数据源执行参数
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@Data
public class DataSourceExecParam {

    /**
     * 执行sql语句
     */
    private String execSql;

    /**
     * 目标类
     */
    private Class<?> targetClass;

    /**
     * 批量执行sql语句
     */
    private List<String> batchSql = new ArrayList<>();

    /**
     * 数据参数
     */
    private JsonObject dataParam;

    /**
     * where条件后的自定义sql语句（limit、order by等） 因为标准的sql语句可以通过代码通用处理，但是where后的语句不同类型的数据库不尽相同，这部分扩展是给有需要的编码设计的
     */
    private String tailSql;

}
