package io.kida.yuen.utils.system.router;

import java.io.IOException;

import io.vertx.ext.web.Router;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: RouterSet.java
 * @ClassName: RouterSet
 * @Description:路由代理接口
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
public interface RouterSet {

    static final String ERROR_RESPONSE_MSG = "There is an error in the request, please contact the system developer";
    static final String ERROR_EXCEPTION_MSG = "Operation failed due to network abnormality";
    static final String SUCCESS_RESPONSE_MSG = "The request is successful and has been returned to the front end";

    /**
     * 
     * @MethodName: restRouter
     * @Description: restful路由
     * @author yuanzhenhui
     * @param router
     * @throws ClassNotFoundException
     * @throws IOException
     *             void
     * @date 2023-10-11 05:28:44
     */
    void restRouter(Router router) throws ClassNotFoundException, IOException;

}
