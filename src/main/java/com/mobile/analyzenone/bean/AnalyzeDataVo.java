package com.mobile.analyzenone.bean;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;
import lombok.ToString;

/**
 * AnalyzeDataVo
 *
 * @author xuyang
 * @version 1.0
 * @description 日志分析VO
 * @date 2020/8/5 15:14
 */
@Data
@ToString
@ExcelTarget("analyzeDataVo")
public class AnalyzeDataVo {
    @Excel(name = "请求链接",orderNum="1",width = 100)
    private String api;
    @Excel(name = "请求方式",orderNum="2",width = 30)
    private String method;
    @Excel(name = "功能说明",orderNum="3",width = 30)
    private String desc;
    @Excel(name = "请求次数",orderNum="4",width = 30)
    private Double count;
    @Excel(name = "百分比",orderNum="5",width = 30)
    private String percent;
}
 