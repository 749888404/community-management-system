package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseStationVO {
    private Long id;                  // 主键ID
    private Long communityId;         // 关联本系统小区ID
    private String communityName;     // 小区名称
    private String townCommunity;     // 所属乡镇/社区
    private String buildTime;         // 基站建成时间
    private String operatorName;      // 运营商/铁塔(对方主体)
    private String deviceType;        // 设备类型
    private String occupyPosition;    // 占用位置
    private String partyA;            // 合同甲方(签约主体)
    private String partyB;            // 合同乙方（运营主体单位）
    private String signTime;          // 合同签订时间
    private String contractPeriod;    // 合同约定期限
    private String rentAgreement;     // 租金收入约定
    private String rentFee;           // 租金/场地费(元/年)
    private String payee;             // 实际收款方
    private String powerAccount;      // 基站用电户头
    private String powerPrice;        // 物业向基站收取电价(元/度)
    private String annualPower;       // 年用电量(度)
    private String remark;            // 备注
    private LocalDateTime createTime; // 创建时间
}
