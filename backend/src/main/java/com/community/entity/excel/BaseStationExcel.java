package com.community.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 通讯基站统计 Excel 导入导出模型
 * 列顺序与《住宅小区通信基站信息统计表》表头一致
 */
@Data
public class BaseStationExcel {

    @ExcelProperty("ID")
    @ColumnWidth(8)
    private Long id;

    @ExcelProperty("小区名称")
    @ColumnWidth(18)
    private String communityName;

    @ExcelProperty("所属乡镇/社区")
    @ColumnWidth(16)
    private String townCommunity;

    @ExcelProperty("基站建成时间")
    @ColumnWidth(18)
    private String buildTime;

    @ExcelProperty("运营商/铁塔(对方主体)")
    @ColumnWidth(20)
    private String operatorName;

    @ExcelProperty("设备类型")
    @ColumnWidth(16)
    private String deviceType;

    @ExcelProperty("占用位置")
    @ColumnWidth(18)
    private String occupyPosition;

    @ExcelProperty("合同甲方(签约主体)")
    @ColumnWidth(24)
    private String partyA;

    @ExcelProperty("合同乙方(运营主体单位)")
    @ColumnWidth(28)
    private String partyB;

    @ExcelProperty("合同签订时间")
    @ColumnWidth(18)
    private String signTime;

    @ExcelProperty("合同约定期限")
    @ColumnWidth(28)
    private String contractPeriod;

    @ExcelProperty("租金收入约定")
    @ColumnWidth(14)
    private String rentAgreement;

    @ExcelProperty("租金/场地费(元/年)")
    @ColumnWidth(20)
    private String rentFee;

    @ExcelProperty("实际收款方")
    @ColumnWidth(26)
    private String payee;

    @ExcelProperty("基站用电户头")
    @ColumnWidth(16)
    private String powerAccount;

    @ExcelProperty("物业向基站收取电价(元/度)")
    @ColumnWidth(22)
    private String powerPrice;

    @ExcelProperty("年用电量(度)")
    @ColumnWidth(14)
    private String annualPower;

    @ExcelProperty("备注")
    @ColumnWidth(24)
    private String remark;
}
