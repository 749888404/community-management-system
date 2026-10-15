package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 车位情况统计 Excel 导入导出模型
 * 列顺序与《既有住宅小区车位情况统计表》表头一致
 */
@Data
public class ParkingExcel {

    @ExcelProperty("ID")
    @ColumnWidth(8)
    private Long id;

    @ExcelProperty("序号")
    @ColumnWidth(8)
    private String seqNo;

    @ExcelProperty("县（市、区）")
    @ColumnWidth(14)
    private String county;

    @ExcelProperty("街道办")
    @ColumnWidth(14)
    private String town;

    @ExcelProperty("社区")
    @ColumnWidth(16)
    private String communityOrg;

    @ExcelProperty("住宅小区名称")
    @ColumnWidth(22)
    private String communityName;

    @ExcelProperty("开发企业名称")
    @ColumnWidth(30)
    private String developer;

    @ExcelProperty("物业服务企业名称")
    @ColumnWidth(30)
    private String propertyCompany;

    @ExcelProperty("规划住宅户数")
    @ColumnWidth(14)
    private String plannedHouseholds;

    @ExcelProperty("交房时间")
    @ColumnWidth(18)
    private String deliveryTime;

    @ExcelProperty("规划车位总数")
    @ColumnWidth(18)
    private String plannedTotal;

    @ExcelProperty("已出售车位数")
    @ColumnWidth(14)
    private String soldCount;

    @ExcelProperty("已出租车位数")
    @ColumnWidth(14)
    private String rentedCount;

    @ExcelProperty("目前闲置车位数")
    @ColumnWidth(16)
    private String idleCount;

    @ExcelProperty("备注")
    @ColumnWidth(24)
    private String remark;
}
