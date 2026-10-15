-- 社区管理系统数据库初始化脚本
-- 数据库：community_db
-- 字符集：utf8mb4

CREATE DATABASE IF NOT EXISTS `community_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `community_db`;

-- ----------------------------
-- 1. 用户管理表（系统用户）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(50)   NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(100)  NOT NULL DEFAULT 'e10adc3949ba59abbe56e057f20f883e' COMMENT '密码(MD5: 123456)',
  `real_name`   VARCHAR(50)   DEFAULT NULL COMMENT '真实姓名',
  `phone`       VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
  `email`       VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
  `role`        VARCHAR(20)   NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER',
  `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 2. 小区管理表
-- ----------------------------
DROP TABLE IF EXISTS `community`;
CREATE TABLE `community` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`           VARCHAR(100) NOT NULL COMMENT '小区名称',
  `address`        VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
  `manager`        VARCHAR(50)  DEFAULT NULL COMMENT '物业负责人',
  `company`        VARCHAR(100) DEFAULT NULL COMMENT '物业公司',
  `phone`          VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `building_households` INT     DEFAULT 0 COMMENT '建筑户数',
  `resident_households` INT     DEFAULT 0 COMMENT '常住户数',
  `resident_population` INT     DEFAULT 0 COMMENT '常住人口',
  `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小区表';

-- ----------------------------
-- 3. 残疾人管理表
-- ----------------------------
DROP TABLE IF EXISTS `disabled_person`;
CREATE TABLE `disabled_person` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`           VARCHAR(50)  NOT NULL COMMENT '姓名',
  `gender`         VARCHAR(10)  DEFAULT NULL COMMENT '性别: 男/女',
  `id_card`        VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
  `disability_type` VARCHAR(30) NOT NULL COMMENT '残疾类型: 视力/听力/言语/肢体/智力/精神/多重',
  `disability_level` VARCHAR(10) DEFAULT NULL COMMENT '残疾等级: 一级/二级/三级/四级',
  `community_id`   BIGINT       DEFAULT NULL COMMENT '所属小区ID',
  `phone`          VARCHAR(20)  DEFAULT NULL COMMENT '本人电话',
  `guardian`         VARCHAR(50)  DEFAULT NULL COMMENT '监护人姓名',
  `guardian_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '监护人电话',
  `guardian_relation` VARCHAR(30) DEFAULT NULL COMMENT '监护人与残疾人关系',
  `disability_cert_no` VARCHAR(50) DEFAULT NULL COMMENT '残疾证号',
  `ethnicity`        VARCHAR(20)  DEFAULT NULL COMMENT '民族',
  `education`        VARCHAR(50)  DEFAULT NULL COMMENT '文化程度',
  `marital_status`   VARCHAR(20)  DEFAULT NULL COMMENT '婚姻状况',
  `household_type`   VARCHAR(20)  DEFAULT NULL COMMENT '户口类型',
  `address`          VARCHAR(200) DEFAULT NULL COMMENT '家庭住址',
  `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_community` (`community_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='残疾人信息表';

-- ----------------------------
-- 4. 人员管理表（社区常住/暂住人员）
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`           VARCHAR(50)  NOT NULL COMMENT '姓名',
  `gender`         VARCHAR(10)  DEFAULT NULL COMMENT '性别: 男/女',
  `id_card`        VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
  `phone`          VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `community_id`   BIGINT       DEFAULT NULL COMMENT '所属小区ID',
  `building`      VARCHAR(20)  DEFAULT NULL COMMENT '楼栋',
  `unit`          VARCHAR(20)  DEFAULT NULL COMMENT '单元',
  `room`          VARCHAR(20)  DEFAULT NULL COMMENT '房号',
  `household_type` VARCHAR(10) DEFAULT NULL COMMENT '户籍类型: 常住/暂住',
  `native_place`  VARCHAR(100) DEFAULT NULL COMMENT '籍贯',
  `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_community` (`community_id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息表';

-- ----------------------------
-- 5. 文件管理表
-- ----------------------------
DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_name`     VARCHAR(255) NOT NULL COMMENT '文件名',
  `category`      VARCHAR(50)  DEFAULT NULL COMMENT '分类: 政策文件/通知公告/居民档案/社区文档/其他',
  `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `storage_name`  VARCHAR(255) DEFAULT NULL COMMENT '存储文件名',
  `file_path`     VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
  `file_size`     BIGINT       DEFAULT NULL COMMENT '文件大小(字节)',
  `ext`           VARCHAR(20)  DEFAULT NULL COMMENT '扩展名',
  `uploader`      VARCHAR(50)  DEFAULT NULL COMMENT '上传人',
  `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件管理表';

-- ----------------------------
-- 6. 通讯基站统计表
-- ----------------------------
DROP TABLE IF EXISTS `base_station`;
CREATE TABLE `base_station` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `community_id`     BIGINT       DEFAULT NULL COMMENT '关联本系统小区ID（按小区名称自动匹配，可为空）',
  `community_name`   VARCHAR(100) DEFAULT NULL COMMENT '小区名称',
  `town_community`   VARCHAR(100) DEFAULT NULL COMMENT '所属乡镇/社区',
  `build_time`       VARCHAR(100) DEFAULT NULL COMMENT '基站建成时间',
  `operator_name`    VARCHAR(200) DEFAULT NULL COMMENT '运营商/铁塔(对方主体)',
  `device_type`      VARCHAR(100) DEFAULT NULL COMMENT '设备类型: 室内分布系统/信号放大器/宏站/微站等',
  `occupy_position`  VARCHAR(200) DEFAULT NULL COMMENT '占用位置: 楼顶/楼道/地库/弱电井等',
  `party_a`          VARCHAR(300) DEFAULT NULL COMMENT '合同甲方(签约主体)',
  `party_b`          VARCHAR(500) DEFAULT NULL COMMENT '合同乙方(运营主体单位)',
  `sign_time`        VARCHAR(200) DEFAULT NULL COMMENT '合同签订时间',
  `contract_period`  VARCHAR(500) DEFAULT NULL COMMENT '合同约定期限',
  `rent_agreement`   VARCHAR(100) DEFAULT NULL COMMENT '租金收入约定',
  `rent_fee`         VARCHAR(200) DEFAULT NULL COMMENT '租金/场地费(元/年)',
  `payee`            VARCHAR(300) DEFAULT NULL COMMENT '实际收款方',
  `power_account`    VARCHAR(100) DEFAULT NULL COMMENT '基站用电户头: 独立电表/转供表/小区总表转供等',
  `power_price`      VARCHAR(200) DEFAULT NULL COMMENT '物业向基站收取电价(元/度)',
  `annual_power`     VARCHAR(100) DEFAULT NULL COMMENT '年用电量(度)',
  `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_bs_community` (`community_id`),
  KEY `idx_bs_name` (`community_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通讯基站统计表';

-- ----------------------------
-- 7. 既有住宅小区车位情况统计表
-- ----------------------------
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='既有住宅小区车位情况统计表';

-- ----------------------------
-- 初始化示例数据
-- ----------------------------
INSERT INTO `sys_user` (`username`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '系统管理员', '13800000000', 'ADMIN', 1),
('zhangsan', '张三', '13800000001', 'USER', 1),
('lisi', '李四', '13800000002', 'USER', 0);

INSERT INTO `community` (`name`, `address`, `manager`, `company`, `phone`, `building_households`, `resident_households`, `resident_population`, `remark`) VALUES
('家合园', '河北省石家庄市鹿泉区寺家庄镇天宁路89号', '梁士魁', '石家庄诺安物业服务有限公司', '13731184718', 309, 260, 689, '4栋14单元'),
('大尚华府', '河北省石家庄市鹿泉区寺家庄镇红旗大街818号', '邵英军', '石家庄僖庭物业服务有限公司', '13785160680', 956, 502, 1300, '13栋27单元'),
('大尚华庭', '河北省石家庄市鹿泉区寺家庄镇繁荣街6号', '邵英军', '石家庄僖庭物业服务有限公司', '13785160680', 248, 198, 876, '5栋11单元');

INSERT INTO `disabled_person` (`name`, `gender`, `id_card`, `disability_type`, `disability_level`, `community_id`, `phone`, `guardian`, `guardian_phone`, `guardian_relation`, `disability_cert_no`, `ethnicity`, `education`, `marital_status`, `household_type`, `address`, `remark`) VALUES
('陈明亮', '男', '110101198003071234', '视力', '一级', 1, '13700000001', '陈父', '13700000002', '父子', '11010119800307X01', '汉族', '初中', '已婚', '农业户口', '家合园3栋2单元201室', '需定期上门探访'),
('周小雨', '女', '110101199105152345', '肢体', '二级', 1, '13700000003', '周母', '13700000004', '母女', '11010119910515X02', '汉族', '高中', '未婚', '非农业户口', '家合园5栋1单元302室', NULL),
('吴强', '男', '110108197812034567', '听力', '三级', 2, '13700000005', '吴妻', '13700000006', '夫妻', '11010819781203X03', '汉族', '中专', '已婚', '农业户口', '大尚华府2栋3单元101室', '配备助听器'),
('郑芳', '女', '110102199203056789', '精神', '二级', 3, '13700000007', '郑夫', '13700000008', '夫妻', '11010219920305X04', '汉族', '大专', '已婚', '非农业户口', '大尚华庭7栋1单元501室', '需药物监护'),
('孙磊', '男', '110105198807089012', '智力', '四级', 2, '13700000009', '孙姐', '13700000010', '姐弟', '11010519880708X05', '汉族', '初中', '未婚', '农业户口', '大尚华府9栋2单元203室', NULL);

INSERT INTO `person` (`name`, `gender`, `id_card`, `phone`, `community_id`, `building`, `unit`, `room`, `household_type`, `native_place`, `remark`) VALUES
('赵建国', '男', '110101197506123456', '13600001101', 1, '3栋', '2单元', '201', '常住', '北京', '退休职工'),
('钱小梅', '女', '110101198209204567', '13600001102', 1, '5栋', '1单元', '302', '常住', '河北', '全职妈妈'),
('孙志远', '男', '110108199003155678', '13600001103', 2, '2栋', '3单元', '101', '暂住', '山东', '外来务工人员'),
('李秀英', '女', '110102197811166789', '13600001104', 2, '9栋', '2单元', '203', '常住', '北京', NULL),
('周浩然', '男', '110105200012177890', '13600001105', 3, '7栋', '1单元', '501', '常住', '北京', '大学生');
