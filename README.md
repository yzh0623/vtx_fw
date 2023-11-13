![](https://cdn.discordapp.com/attachments/1081156783227273268/1166261004234137670/kida1905_Please_design_a_novel_banner_image_with_the_name_VTX_F_1b4d79e7-55ff-4f48-9d0c-49244522018c.png?ex=6549d833&is=65376333&hm=e45b43186f95b979cfb2d05386d748c572d1e26d0622248f857a905c6416e15b&)

# 1. VTX framework 简介
这是一个基于 Vert.x 的轻量级框架。

可能有的小伙伴会说“与其选 Vert.x 还不如选 Quarkus 啦”。

是的，Quarkus 是将 Spring 和 Vert.x 的优势结合起来了，这对于 Spring 转型的小伙伴来说真的非常友好。但本人之所以选择在 Vert.x 上进行构建，主要是因为 Vert.x 具备“高度定制”的特性。“自由”是对于曾经作为程序员的我是一种快乐。除此之外，本框架的构建宗旨是：

1. 要满足“低配高产”的目标；
2. 要贴近 Spring 的开发风格；
3. 封装工具要满足日常使用需要且可以 copy 迁移；
4. 可兼容信创产品线（软件国产化）基本要求；

今天是 2023 年 10 月 24 日程序员节，借此机会将这套框架开源，与各位同道好友共勉之。

# 2. 技术描述 （语雀）
[1. 本地构建 Docker 镜像（细说如何直接通过 Maven 打包成 Docker 镜像并上传到私有镜像库以及验证过程）](https://www.yuque.com/kidayuan/pa6ygl/bggfcb6eu41xwa2c)

[2. Yaml 文件读取机制（细说 Yaml 文件的获取过程）](https://www.yuque.com/kidayuan/pa6ygl/ecy32imuegehslfi)