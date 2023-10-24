package io.kida.yuen.dao.callback;

import io.kida.yuen.vo.datasource.DataSourceExecParam;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_slimopt
 * @File: DaoCallback.java
 * @ClassName: DaoCallback
 * @Description:DAO回调函数
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
@FunctionalInterface
public interface DaoCallback {

    /**
     * 
     * @MethodName: process
     * @Description: dao回调操作
     * @author yuanzhenhui
     * @param dsrcExec
     *            void
     * @date 2023-10-19 11:27:08
     */
    void process(DataSourceExecParam dsrcExec);

}
