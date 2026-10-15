package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 居民管理 Excel 导入导出模型
 */
@Data
public class PersonExcel {
    @ExcelProperty("ID")
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("性别")
    @ColumnWidth(8)
    private String gender;

    @ExcelProperty("身份证号")
    @ColumnWidth(20)
    private String idCard;

    @ExcelProperty("联系电话")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("所属小区")
    @ColumnWidth(15)
    private String communityName;

    @ExcelProperty("楼栋")
    @ColumnWidth(8)
    private String building;

    @ExcelProperty("单元")
    @ColumnWidth(8)
    private String unit;

    @ExcelProperty("房号")
    @ColumnWidth(8)
    private String room;

    @ExcelProperty("户籍类型")
    @ColumnWidth(10)
    private String householdType;

    @ExcelProperty("籍贯")
    @ColumnWidth(15)
    private String nativePlace;

    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
}
