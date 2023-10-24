package io.kida.yuen.vo.pagehelper.limit.impl;

import io.kida.yuen.vo.pagehelper.limit.LimitInterface;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: PostgresqlLimit.java
 * @ClassName: PostgresqlLimit
 * @Description:Postgresql分页
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class PostgresqlLimit implements LimitInterface {

    // 起始页数从1开始
    private int pageNo;

    // 每页行数
    private int pageSize;

    public PostgresqlLimit() {

    }

    public PostgresqlLimit(int pageNo, int pageSize) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    public String limitRetuen() {
        if (pageNo < 1) {
            pageNo = 1;
        }
        return " LIMIT " + pageSize + " OFFSET " + (pageNo - 1) * pageSize;
    }

}
