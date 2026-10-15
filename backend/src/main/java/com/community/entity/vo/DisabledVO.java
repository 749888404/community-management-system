package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DisabledVO {
    private Long id;              // 主键ID
    private String name;          // 姓名
    private String gender;        // 性别: 男/女
    private String idCard;        // 身份证号
    private String disabilityType;  // 残疾类型: 视力/听力/言语/肢体/智力/精神/多重
    private String disabilityLevel; // 残疾等级: 一级/二级/三级/四级
    private Long communityId;     // 所属小区ID
    private String communityName; // 所属小区名称
    private String phone;         // 本人电话
    private String guardian;         // 监护人姓名
    private String guardianPhone;    // 监护人电话
    private String guardianRelation; // 监护人与残疾人关系
    private String disabilityCertNo; // 残疾证号
    private String ethnicity;        // 民族
    private String education;        // 文化程度
    private String maritalStatus;    // 婚姻状况: 已婚/未婚/离异/丧偶
    private String householdType;    // 户口类型: 农业户口/非农业户口
    private String address;          // 家庭住址
    private String remark;        // 备注
    private LocalDateTime createTime; // 创建时间
}
