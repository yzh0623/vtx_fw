package io.kida.yuen.slimopt.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_biz
 * @File: VtxSlimopt.java
 * @ClassName: VtxSlimopt
 * @Description:测试slimopt组件的样例表
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/17
 */
@Data
@Table(name = "vtx_slimopt")
public class VtxSlimopt {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "IP")
    private String ip;

    @Column(name = "URI")
    private String uri;

    @Column(name = "BUZZ_ID")
    private Long buzzId;

    @Column(name = "ACCESS_DATE")
    private LocalDateTime accessDate;

    @Column(name = "DURATION_TIME")
    private Long durationTime;

    @Column(name = "OPER_TYPE")
    private String operType;

    @Column(name = "ANALYSIS_LABEL")
    private String analysisLabel;

}
