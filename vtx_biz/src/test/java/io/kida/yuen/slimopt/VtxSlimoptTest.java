package io.kida.yuen.slimopt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.kida.yuen.components.constants.HttpConstants;
import io.kida.yuen.example.model.VtxSlimopt;
import io.kida.yuen.utils.selfdev.base.DateUtil;
import io.kida.yuen.utils.selfdev.base.StringUtil;
import io.kida.yuen.utils.selfdev.encrypt.uuid.UuidUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_biz
 * @File: VtxSlimoptTest.java
 * @ClassName: VtxSlimoptTest
 * @Description: 单元测试样例
 *
 * @Author: yuanzhenhui
 * @Date: 2023/12/07
 */
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(VertxExtension.class)
public class VtxSlimoptTest {

    // 远程访问的 ip 地址
    private static final String IP = "127.0.0.1";

    // 项目的上下文根
    private static final String CONTEXT = "/biz";

    // 测试模板的实体类
    private static final String EXAMPLE_ENTITY = StringUtil.lowerFirstCase(VtxSlimopt.class.getSimpleName());

    // 访问端口
    private static final int PORT = 8197;

    // 超时时间（ms）
    private static final int TIMEOUT = 1000;

    // 需要客户端特殊配置的都写在这里
    private static final HttpClientOptions OPTIONS = new HttpClientOptions().setKeepAlive(false);

    // 随机数
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // 数据集容器
    private static final List<VtxSlimopt> VS_LIST = new ArrayList<>();

    // 随机 ip 地址
    private String randomIpAddress;

    // 查询条件
    private String searchBodyContext = "{\"ip\": \"192.5\"}";

    // http 客户端
    private static HttpClient client;

    /**
     * 
     * @MethodName: setupHttpClient
     * @Description: 初始化 httpclient
     * @author yuanzhenhui
     * @param vertx
     *            void
     * @date 2023-12-07 02:44:43
     */
    @BeforeAll
    static void setupHttpClient(Vertx vertx) {
        client = vertx.createHttpClient(OPTIONS);
    }

    /**
     * 
     * @MethodName: setupRandomIpAddress
     * @Description: 每次都初始化一个随机的 ip 地址
     * @author yuanzhenhui void
     * @date 2023-12-07 10:51:50
     */
    @BeforeEach
    void setupRandomIpAddress() {
        StringBuilder ipStrBuilder = new StringBuilder();
        ipStrBuilder.append("192").append(".");
        ipStrBuilder.append(SECURE_RANDOM.nextInt(10)).append(".");
        ipStrBuilder.append(SECURE_RANDOM.nextInt(10)).append(".");
        ipStrBuilder.append(SECURE_RANDOM.nextInt(255));
        randomIpAddress = ipStrBuilder.toString();
    }

    /**
     * 
     * @MethodName: insertOneRecord
     * @Description: 单条记录插入
     * @author yuanzhenhui
     * @param vertx
     * @param testContext
     *            void
     * @date 2023-12-07 09:08:54
     */
    @Order(1)
    @RepeatedTest(500)
    @DisplayName("Unit test for insertOneRecord method")
    void insertOneRecord(VertxTestContext testContext) {
        String bodyContext = "{\"ip\": \"" + randomIpAddress + "\",\"uri\": \"http://" + UuidUtil.getUuid()
            + ".io\",\"buzzId\": 1,\"accessDate\": \"" + DateUtil.getCurDate(DateUtil.yyyy_MM_dd_HH_mm_ss_EN)
            + "\",\"durationTime\": " + SECURE_RANDOM.nextInt(100)
            + ",\"operType\": \"insert\",\"analysisLabel\": \"单元测试插入数据\"}";

        client.request(HttpMethod.POST, PORT, IP, CONTEXT + "/insert/" + EXAMPLE_ENTITY)
            .compose(req -> req.putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE)
                .setTimeout(TIMEOUT).send(bodyContext).compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
                JsonObject jsonObj = buffer.toJsonObject();
                // 断言判断接口是否正常，若 retCode 字段返回 1 则代表正常，否则不正常
                assertEquals(jsonObj.getInteger("retCode"), 1);
                testContext.completeNow();
            })));
    }

    /**
     * 
     * @MethodName: updateDataByCondition
     * @Description: 根据条件更新数据集
     * @author yuanzhenhui
     * @param vertx
     * @param testContext
     *            void
     * @date 2023-12-07 02:26:31
     */
    @Test
    @Order(2)
    @DisplayName("Unit test for updateDataByCondition method")
    void updateDataByCondition(VertxTestContext testContext) {
        String bodyContext =
            "{\"from\": {\"ip\": \"192.6\"},\"to\": {\"uri\": \"http://www.kida.io\",\"durationTime\": "
                + SECURE_RANDOM.nextInt(100) + ",\"operType\": \"update\",\"analysisLabel\": \"单元测试条件更新数据\"}}";

        client.request(HttpMethod.PUT, PORT, IP, CONTEXT + "/update/" + EXAMPLE_ENTITY + "/byCondition")
            .compose(req -> req.putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE)
                .setTimeout(TIMEOUT).send(bodyContext).compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
                JsonObject jsonObj = buffer.toJsonObject();
                assertEquals(jsonObj.getInteger("retCode"), 1);
                testContext.completeNow();
            })));
    }

    /**
     * 
     * @MethodName: searchDataByCondition
     * @Description: 根据条件查询数据集，验证查询功能顺便为后面删除工作的数据集作准备
     * @author yuanzhenhui
     * @param vertx
     * @param testContext
     *            void
     * @date 2023-12-07 02:25:50
     */
    @Test
    @Order(3)
    @DisplayName("Unit test for searchDataByCondition method")
    void searchDataByCondition(VertxTestContext testContext) {
        client.request(HttpMethod.POST, PORT, IP, CONTEXT + "/query/" + EXAMPLE_ENTITY + "/byCondition")
            .compose(req -> req.putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE)
                .setTimeout(TIMEOUT).send(searchBodyContext).compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
                JsonObject jsonObj = buffer.toJsonObject();

                // 若 retCode 字段返回 1 则代表正常，否则不正常
                int retCode = jsonObj.getInteger("retCode");
                if (retCode == 1) {
                    // 由于无法直接通过 jsonArray 提取数据，所以先转换为 map 再提取
                    Map<String, Object> reMap = jsonObj.getMap();

                    // 获取制定的返回数据
                    LinkedHashMap<String, Map<String, Object>> dataMap =
                        (LinkedHashMap<String, Map<String, Object>>)reMap.get("retData");

                    // 查询返回是一个 List，因此直接转换即可
                    List<Map<String, Object>> objList = (List<Map<String, Object>>)dataMap.get("rows");

                    // 通过反射将 Map 转换成指定格式
                    objList.stream().map(objMap -> {
                        VtxSlimopt vs = new VtxSlimopt();
                        vs.setId(Long.valueOf(String.valueOf(objMap.get("id"))));
                        vs.setIp(String.valueOf(objMap.get("ip")));
                        vs.setUri(String.valueOf(objMap.get("uri")));
                        vs.setDurationTime(Long.valueOf(String.valueOf(objMap.get("durationTime"))));
                        return vs;
                    }).forEach(VS_LIST::add);
                }
                testContext.completeNow();
            })));
    }

    /**
     * 
     * @MethodName: deleteDataByPk
     * @Description: 由于更新和查询都是用 condition 的，删除就用主键删除试试吧
     * @author yuanzhenhui
     * @param vertx
     * @param testContext
     *            void
     * @date 2023-12-07 02:14:13
     */
    @Test
    @Order(4)
    @DisplayName("Unit test for deleteDataByPk method")
    void deleteDataByPk(VertxTestContext testContext) {
        if (!VS_LIST.isEmpty()) {
            for (VtxSlimopt vs : VS_LIST) {
                String bodyContext = "{\"id\": " + vs.getId() + "}";
                client.request(HttpMethod.DELETE, PORT, IP, CONTEXT + "/delete/" + EXAMPLE_ENTITY + "/byPk")
                    .compose(
                        req -> req.putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE)
                            .setTimeout(TIMEOUT).send(bodyContext).compose(HttpClientResponse::body))
                    .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> testContext.completeNow())));
            }
            VS_LIST.removeAll(VS_LIST);
        }
    }

    /**
     * 
     * @MethodName: totallyCheck
     * @Description: 所有单元测试完成后的检查和重置
     * @author yuanzhenhui
     * @param vertx
     * @param testContext
     *            void
     * @date 2023-12-07 02:25:26
     */
    @Test
    @Order(5)
    @DisplayName("Unit test for totallyCheck method")
    void totallyCheck(VertxTestContext testContext) {
        // 执行查询查看是否还存在 ip 为 “192.5”开头的记录,按道理应该为 0，因为 delete 的时候已经删除了这批数据集
        searchDataByCondition(testContext);
        assertEquals(VS_LIST.size(), 0);

        // 最后执行一次数据删除动作保证单元测试可重复执行
        String bodyContext = "{}";
        client.request(HttpMethod.DELETE, PORT, IP, CONTEXT + "/delete/" + EXAMPLE_ENTITY + "/byCondition")
            .compose(req -> req.putHeader(HttpConstants.HTTP_CONTENT_TYPE_PARAMNAME, HttpConstants.HTTP_CONTENT_TYPE)
                .setTimeout(TIMEOUT).send(bodyContext).compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
                testContext.completeNow();
            })));
    }
}
