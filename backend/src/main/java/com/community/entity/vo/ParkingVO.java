package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车位情况统计 视图对象
 */
@Data
public class ParkingVO {

    private Long id;                  // 主键ID
    private Long communityId;         // 关联本系统小区ID
    private String seqNo;             // 序号
    private String county;            // 县（市、区）
    private String town;              // 街道办/乡镇
    private String communityOrg;      // 社区
    private String communityName;     // 住宅小区名称
    private String developer;         // 开发企业名称
    private String propertyCompany;   // 物业服务企业名称
    private String plannedHouseholds; // 规划住宅户数
    private String deliveryTime;      // 交房时间
    private String plannedTotal;      // 规划车位总数
    private String soldCount;         // 已出售车位数
    private String rentedCount;       // 已出租车位数
    private String idleCount;         // 目前闲置车位数
    private String remark;            // 备注
    private LocalDateTime createTime; // 创建时间
}
