package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 小区管理 Excel 导入导出模型
 */
@Data
public class CommunityExcel {
    @ExcelProperty("ID")
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty("小区名称")
    @ColumnWidth(18)
    private String name;

    @ExcelProperty("详细地址")
    @ColumnWidth(25)
    private String address;

    @ExcelProperty("物业负责人")
    @ColumnWidth(12)
    private String manager;

    @ExcelProperty("物业公司")
    @ColumnWidth(20)
    private String company;

    @ExcelProperty("联系电话")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("建筑户数")
    @ColumnWidth(10)
    private Integer buildingHouseholds;

    @ExcelProperty("常住户数")
    @ColumnWidth(10)
    private Integer residentHouseholds;

    @ExcelProperty("常住人口")
    @ColumnWidth(10)
    private Integer residentPopulation;

    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
}
