package com.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community")
public class Community {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID

    private String name;            // 小区名称
    private String address;         // 详细地址
    private String manager;         // 物业负责人
    private String company;         // 物业公司
    private String phone;           // 联系电话
    private Integer buildingHouseholds;  // 建筑户数
    private Integer residentHouseholds;   // 常住户数
    private Integer residentPopulation;   // 常住人口
    private String remark;          // 备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;   // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   // 更新时间
}
