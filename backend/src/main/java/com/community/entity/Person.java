package com.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("person")
public class Person {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID

    private String name;            // 姓名
    private String gender;          // 性别: 男/女
    private String idCard;          // 身份证号
    private String phone;           // 联系电话
    private Long communityId;       // 所属小区ID
    private String building;        // 楼栋
    private String unit;            // 单元
    private String room;            // 房号
    private String householdType;   // 户籍类型: 常住/暂住
    private String nativePlace;     // 籍贯
    private String remark;          // 备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;   // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   // 更新时间
}
