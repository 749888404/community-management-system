package com.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community")
public class Community {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID

    // ===== 基础信息 =====
    private String name;            // 小区名称
    private String address;         // 详细地址
    private String manager;         // 项目负责人
    private String company;         // 物业公司
    private String phone;           // 负责人联系方式
    private Integer buildingHouseholds;  // 建筑户数
    private Integer residentHouseholds;   // 常住户数
    private Integer residentPopulation;   // 常住人口
    private String remark;          // 备注

    // ===== 物业企业信息（来自小区台账）=====
    private String legalPerson;         // 法人代表
    private String companyPhone;        // 企业联系电话
    private String communityOrg;        // 所属乡镇（区）、社区居委会
    private String buildTime;           // 建设时间
    private String propertyType;        // 物业性质（住宅/商住/园区等）

    // ===== 建筑与户数 =====
    private BigDecimal areaResidential;    // 建筑面积-住宅（万平方米）
    private BigDecimal areaCommercial;     // 建筑面积-商业（万平方米）
    private String buildingMulti;          // 多层楼栋数/层数
    private String buildingHigh;           // 高层楼栋数/层数
    private Integer householdsResidential; // 住宅户数
    private Integer householdsCommercial;  // 商业户数

    // ===== 车位与充电 =====
    private Integer parkingGround;        // 机动车位-地上
    private Integer parkingUnderground;   // 机动车位-地下
    private Integer chargingGround;       // 机动车充电桩-地上
    private Integer chargingUnderground;  // 机动车充电桩-地下
    private Integer nonmotorGround;       // 非机动车位-地上
    private Integer nonmotorUnderground;  // 非机动车位-地下
    private String ebikeShed;             // 电动车充电设备-车棚
    private Integer ebikePort;            // 电动车充电设备-充电头

    // ===== 安全与配套 =====
    private Integer elevatorCount;   // 电梯（部）
    private Integer fireStation;     // 微型消防站（个）
    private Integer smokeDetector;   // 独立烟感（个）
    private Integer fireHydrant;     // 消火栓（个）

    // ===== 水电暖与承接方式 =====
    private String waterManaged;     // 水是否一管到户
    private String powerManaged;     // 电是否一管到户
    private String heatManaged;      // 暖是否一管到户
    private String takeoverMode;     // 承接方式
    private String ledgerRemark;     // 台账备注（未交房注明）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;   // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   // 更新时间
}
