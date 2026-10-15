-- ============================================================
-- TiDB Cloud (community_db) 建库 + 真实数据导入脚本
-- 结构来源：database/init.sql（与后端实体一致，权威）
-- 数据来源：本地 sjz_community 真实数据（22 小区 / 8 残疾人 / 1 用户）
-- 用法：mysql -h <tidb-host> -P 4000 -u <user> -p --ssl-mode=REQUIRED < tidb_import.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS `community_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `community_db`;

-- ----------------------------
-- 1. 用户管理表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(50)   NOT NULL COMMENT '登录账号',
  `password`    VARCHAR(100)  NOT NULL DEFAULT 'e10adc3949ba59abbe56e057f20f883e' COMMENT '密码',
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
  `disability_type` VARCHAR(30) NOT NULL COMMENT '残疾类型',
  `disability_level` VARCHAR(10) DEFAULT NULL COMMENT '残疾等级',
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
-- 4. 人员管理表
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
  `category`      VARCHAR(50)  DEFAULT NULL COMMENT '分类',
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

-- ============================================================
-- 真实数据导入（映射自本地 sjz_community）
-- ============================================================

-- 系统用户（原样照搬本地 admin）
INSERT INTO `sys_user` (`id`,`username`,`password`,`real_name`,`phone`,`email`,`role`,`status`) VALUES
(1,'admin','admin123','系统管理员',NULL,NULL,'admin',1);

-- 小区（22 个真实小区；manager/phone 来自 property 表，company 来自 property_company，
--       building_households<-total_households, resident_population<-population, remark="X栋Y单元"）
INSERT INTO `community` (`id`,`name`,`address`,`manager`,`company`,`phone`,`building_households`,`resident_households`,`resident_population`,`remark`) VALUES
(1,'家合园','天宁路89号','梁士魁','石家庄诺安物业服务有限公司','13731184718',309,260,689,'4栋14单元'),
(2,'大尚华庭','繁荣街6号','邵英军','石家庄僖庭物业服务有限公司','17367937380',248,198,876,'5栋11单元'),
(3,'大尚华府','红旗大街818号','邵英军','石家庄僖庭物业服务有限公司','17367937380',956,502,1300,'13栋27单元'),
(4,'万和华府','繁荣街19号','郑荣凤','恒辉物业服务集团有限公司','13147446786',588,240,560,'10栋31单元'),
(5,'祥宁佳苑','寺家庄镇寺家庄村天宁路6号','王彦波','石家庄泰荣物业服务有限公司','13930425988',546,298,936,'11栋30单元'),
(6,'高宏佳苑','寺家庄镇南降壁村兴中路12号','魏海军','石家庄耀博物业服务有限公司','15633833422',432,234,500,'5栋16单元'),
(7,'城南尚府','河北省石家庄市鹿泉区107国道169号','刘敏','河北小桔灯物业服务有限公司','15373909355',396,229,458,'5栋16单元'),
(8,'海兰嘉园','红旗南大街1099号','吴荣亮','石家庄海兰物业服务有限公司','13613285166',684,269,800,'8栋28单元'),
(9,'拾里洋房','石南路108号','刘福','恒辉物业服务集团有限公司','0311-69036989',1214,610,2213,'18栋53单元'),
(10,'柠檬小镇','兴中路14号','田立强','石家庄军强物业服务有限公司','15830633393',472,301,590,'4栋8单元'),
(11,'华硕苑','富强路25号','王彦辉','华硕苑物业','13180041959',95,95,281,'3栋9单元'),
(12,'四季阳光','红旗大街天宁路917号','刘腾','石家庄佳康物业服务有限公司','13333371252',960,530,1550,'19栋76单元'),
(13,'城市傲居','东营东街村内','王勇','石家庄市军存物业服务有限公司',NULL,459,150,380,'2栋5单元'),
(14,'泰荣园','寺家庄镇繁荣街20号','王彦波','石家庄泰荣物业服务有限公司','13930425988',143,96,298,'2栋6单元'),
(15,'怡美家园','高迁西街392省道','梁栋','无',NULL,98,70,170,'2栋7单元'),
(16,'富强南院','繁荣街8号','王亚林','石家庄仰山物业管理有限责任公司','13020861666',210,130,300,'2栋13单元'),
(17,'富强北院（3号楼）','繁荣街9号','刘少辉','无','13231156871',84,60,200,'1栋6单元'),
(18,'香堤小郡一期','香裕街1号','李鹏亮','无',NULL,170,110,270,'2栋12单元'),
(19,'香堤小郡二期','香裕街1号','王银刚','石家庄金果物业服务有限公司','17733898877',452,210,800,'3栋6单元'),
(20,'福润雅居','平南村村北','高亚海','石家庄致中物业服务有限公司','18032916352',246,82,153,'4栋18单元'),
(21,'长久广场','高迁北街 高尹路8号','刘益楠','河北深科物业服务有限公司','17798178635',486,256,730,'4栋5单元'),
(22,'城果小区','高迁东街','李燕','石家庄森太物业服务有限公司','18031178200',436,340,835,'6栋22单元');

-- 残疾人（8 人；community_id 由地址识别小区：拾里洋房9 / 四季阳光12 / 万合华府4 / 城果小区22 / 其余为空）
INSERT INTO `disabled_person` (`id`,`name`,`gender`,`id_card`,`disability_type`,`disability_level`,`community_id`,`phone`,`guardian`,`guardian_phone`,`guardian_relation`,`disability_cert_no`,`ethnicity`,`education`,`marital_status`,`household_type`,`address`,`remark`) VALUES
(1,'袁俊军','男','130821198511180017','肢体','四级',9,'13717564933',NULL,NULL,NULL,'13082119851118001744','汉族','初中','已婚','非农业','河北省石家庄市鹿泉区寺家庄镇寺家庄社区拾里洋房3-3-1102',NULL),
(2,'杨春双','女','130122195204014048','精神','二级',NULL,'13102839152','高亚龙','13102839152','子','13012219520401404862','汉族','小学','丧偶','农业','河北省鹿泉市获鹿镇南海山村富强路振兴里2号',NULL),
(3,'暴莲叶','女','130106195803210921','智力','四级',12,'15614172957','张娟','15614172957','女','13010619580321092154','汉族','小学','已婚','非农业','河北省石家庄市鹿泉区寺家庄镇四季阳光1-2-302',NULL),
(4,'冯缘','男','130982200505306574','视力','四级',4,'13102872286','郭连英','13103212286','父母','13098220050530657414','汉族','大专','未婚','非农业','河北省石家庄市鹿泉区寺家庄镇万合华府1-2-1202',NULL),
(5,'钟芙蓉','女','130110196311040929','精神','二级',NULL,'13785169047','高二明','13785169047','其他','13011019631104092962','汉族','文盲','未婚','农业','河北省石家庄市鹿泉区寺家庄镇寺家庄社区（石家庄庭瑞中医精神康复医院）',NULL),
(6,'魏文娜','女','130929198508158122','肢体','四级',9,'13582037882','李全胜','13473351387','配偶','13092919850815812244','汉族','大专','已婚','非农业','河北省石家庄市鹿泉区寺家庄镇寺家庄社区拾里洋房石南路108号2号楼1单元402室',NULL),
(7,'王梓轩','女','130108201008151222','肢体','四级',22,'18633875070','赵改霞','18633875070','父母','13010820100815122244','汉族','初中','未婚','非农业','河北省石家庄市鹿泉区寺家庄镇寺家庄社区成果小区3-2-502',NULL),
(8,'王功良','男','130534198006114110','肢体','四级',22,'13126155187','赵改霞','13126155187','夫妻','13053419800611411044','汉族','本科','已婚','非农业','河北省石家庄市鹿泉区寺家庄镇寺家庄社区成果小区3-2-502',NULL);

-- person / file_record 本地无数据，保持空表
