package io.kida.yuen.vo.pagehelper.limit.impl;

import io.kida.yuen.vo.pagehelper.limit.LimitInterface;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: MysqlLimit.java
 * @ClassName: MysqlLimit
 * @Description:mysql分页
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class MysqlLimit implements LimitInterface {

    // 起始页数从1开始
    private int pageNo;

    // 每页行数
    private int pageSize;

    public MysqlLimit() {

    }

    public MysqlLimit(int pageNo, int pageSize) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    public String limitRetuen() {
        if (pageNo < 1) {
            pageNo = 1;
        }
        return " LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
    }

}
