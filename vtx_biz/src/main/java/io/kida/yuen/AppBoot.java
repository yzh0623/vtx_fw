package io.kida.yuen;

import io.kida.yuen.components.configurations.BootstrapConfig;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_biz
 * @File: AppBoot.java
 * @ClassName: AppBoot
 * @Description:启动类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/16
 */
public class AppBoot {

    /**
     * 
     * @MethodName: main
     * @Description: 启动方法
     * @author yuanzhenhui
     * @param args
     *            void
     * @date 2023-10-17 09:42:12
     */
    public static void main(String[] args) {
        // 启动部署
        BootstrapConfig.setupAndDeploy();
    }

}
