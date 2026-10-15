package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonVO {
    private Long id;              // 主键ID
    private String name;          // 姓名
    private String gender;        // 性别: 男/女
    private String idCard;        // 身份证号
    private String phone;         // 联系电话
    private Long communityId;     // 所属小区ID
    private String communityName; // 所属小区名称
    private String building;      // 楼栋
    private String unit;          // 单元
    private String room;          // 房号
    private String householdType; // 户籍类型: 常住/暂住
    private String nativePlace;   // 籍贯
    private String remark;        // 备注
    private LocalDateTime createTime; // 创建时间
}
