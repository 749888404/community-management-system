package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小区管理 Excel 导入导出模型（字段与《寺家庄镇-小区台账》对齐）
 */
@Data
public class CommunityExcel {
    @ExcelProperty("ID")
    @ColumnWidth(10)
    private Long id;

    // ---- 基础信息 ----
    @ExcelProperty("小区名称")
    @ColumnWidth(18)
    private String name;

    @ExcelProperty("详细地址")
    @ColumnWidth(28)
    private String address;

    @ExcelProperty("物业公司")
    @ColumnWidth(28)
    private String company;

    @ExcelProperty("法人代表")
    @ColumnWidth(12)
    private String legalPerson;

    @ExcelProperty("企业联系电话")
    @ColumnWidth(16)
    private String companyPhone;

    @ExcelProperty("项目负责人")
    @ColumnWidth(12)
    private String manager;

    @ExcelProperty("负责人联系方式")
    @ColumnWidth(16)
    private String phone;

    @ExcelProperty("所属乡镇/社区居委会")
    @ColumnWidth(28)
    private String communityOrg;

    @ExcelProperty("建设时间")
    @ColumnWidth(14)
    private String buildTime;

    @ExcelProperty("物业性质")
    @ColumnWidth(12)
    private String propertyType;

    // ---- 建筑与户数 ----
    @ExcelProperty("建筑面积-住宅(万㎡)")
    @ColumnWidth(18)
    private BigDecimal areaResidential;

    @ExcelProperty("建筑面积-商业(万㎡)")
    @ColumnWidth(18)
    private BigDecimal areaCommercial;

    @ExcelProperty("多层楼栋数/层数")
    @ColumnWidth(20)
    private String buildingMulti;

    @ExcelProperty("高层楼栋数/层数")
    @ColumnWidth(20)
    private String buildingHigh;

    @ExcelProperty("住宅户数")
    @ColumnWidth(10)
    private Integer householdsResidential;

    @ExcelProperty("商业户数")
    @ColumnWidth(10)
    private Integer householdsCommercial;

    @ExcelProperty("建筑户数")
    @ColumnWidth(10)
    private Integer buildingHouseholds;

    @ExcelProperty("常住户数")
    @ColumnWidth(10)
    private Integer residentHouseholds;

    @ExcelProperty("常住人口")
    @ColumnWidth(10)
    private Integer residentPopulation;

    // ---- 车位与充电 ----
    @ExcelProperty("机动车位-地上")
    @ColumnWidth(14)
    private Integer parkingGround;

    @ExcelProperty("机动车位-地下")
    @ColumnWidth(14)
    private Integer parkingUnderground;

    @ExcelProperty("机动车充电桩-地上")
    @ColumnWidth(18)
    private Integer chargingGround;

    @ExcelProperty("机动车充电桩-地下")
    @ColumnWidth(18)
    private Integer chargingUnderground;

    @ExcelProperty("非机动车位-地上")
    @ColumnWidth(16)
    private Integer nonmotorGround;

    @ExcelProperty("非机动车位-地下")
    @ColumnWidth(16)
    private Integer nonmotorUnderground;

    @ExcelProperty("电动车充电-车棚")
    @ColumnWidth(16)
    private String ebikeShed;

    @ExcelProperty("电动车充电-充电头")
    @ColumnWidth(18)
    private Integer ebikePort;

    // ---- 安全与配套 ----
    @ExcelProperty("电梯(部)")
    @ColumnWidth(10)
    private Integer elevatorCount;

    @ExcelProperty("微型消防站(个)")
    @ColumnWidth(16)
    private Integer fireStation;

    @ExcelProperty("独立烟感(个)")
    @ColumnWidth(14)
    private Integer smokeDetector;

    @ExcelProperty("消火栓(个)")
    @ColumnWidth(12)
    private Integer fireHydrant;

    // ---- 水电暖与承接方式 ----
    @ExcelProperty("水是否一管到户")
    @ColumnWidth(16)
    private String waterManaged;

    @ExcelProperty("电是否一管到户")
    @ColumnWidth(16)
    private String powerManaged;

    @ExcelProperty("暖是否一管到户")
    @ColumnWidth(16)
    private String heatManaged;

    @ExcelProperty("承接方式")
    @ColumnWidth(14)
    private String takeoverMode;

    @ExcelProperty("备注")
    @ColumnWidth(24)
    private String remark;

    @ExcelProperty("台账备注")
    @ColumnWidth(24)
    private String ledgerRemark;
}
