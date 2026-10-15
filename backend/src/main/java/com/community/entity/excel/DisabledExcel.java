package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 残疾人管理 Excel 导入导出模型
 */
@Data
public class DisabledExcel {
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

    @ExcelProperty("残疾类型")
    @ColumnWidth(12)
    private String disabilityType;

    @ExcelProperty("残疾等级")
    @ColumnWidth(10)
    private String disabilityLevel;

    @ExcelProperty("民族")
    @ColumnWidth(10)
    private String ethnicity;

    @ExcelProperty("文化程度")
    @ColumnWidth(12)
    private String education;

    @ExcelProperty("婚姻状况")
    @ColumnWidth(12)
    private String maritalStatus;

    @ExcelProperty("户口类型")
    @ColumnWidth(14)
    private String householdType;

    @ExcelProperty("残疾证号")
    @ColumnWidth(18)
    private String disabilityCertNo;

    @ExcelProperty("所属小区")
    @ColumnWidth(15)
    private String communityName;

    @ExcelProperty("本人电话")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("监护人")
    @ColumnWidth(12)
    private String guardian;

    @ExcelProperty("与残疾人关系")
    @ColumnWidth(15)
    private String guardianRelation;

    @ExcelProperty("监护人电话")
    @ColumnWidth(15)
    private String guardianPhone;

    @ExcelProperty("家庭住址")
    @ColumnWidth(25)
    private String address;

    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
}
