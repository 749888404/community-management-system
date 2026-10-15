-- 既有住宅小区车位情况统计表（数据源：金山文档《既有住宅小区车位情况统计表》）
-- 只保留寺家庄镇数据
DROP TABLE IF EXISTS `parking`;
CREATE TABLE `parking` (
  `id`                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `community_id`       BIGINT       DEFAULT NULL COMMENT '关联本系统小区ID',
  `seq_no`             VARCHAR(20)  DEFAULT NULL COMMENT '序号',
  `county`             VARCHAR(50)  DEFAULT NULL COMMENT '县（市、区）',
  `town`               VARCHAR(50)  DEFAULT NULL COMMENT '街道办/乡镇',
  `community_org`      VARCHAR(100) DEFAULT NULL COMMENT '社区',
  `community_name`     VARCHAR(120) DEFAULT NULL COMMENT '住宅小区名称',
  `developer`          VARCHAR(200) DEFAULT NULL COMMENT '开发企业名称',
  `property_company`   VARCHAR(200) DEFAULT NULL COMMENT '物业服务企业名称',
  `planned_households` VARCHAR(50)  DEFAULT NULL COMMENT '规划住宅户数',
  `delivery_time`      VARCHAR(60)  DEFAULT NULL COMMENT '交房时间',
  `planned_total`      VARCHAR(80)  DEFAULT NULL COMMENT '规划车位总数',
  `sold_count`         VARCHAR(50)  DEFAULT NULL COMMENT '已出售车位数',
  `rented_count`       VARCHAR(50)  DEFAULT NULL COMMENT '已出租车位数',
  `idle_count`         VARCHAR(50)  DEFAULT NULL COMMENT '目前闲置车位数',
  `remark`             VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parking_community` (`community_id`),
  KEY `idx_parking_name` (`community_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '既有住宅小区车位情况统计表';
