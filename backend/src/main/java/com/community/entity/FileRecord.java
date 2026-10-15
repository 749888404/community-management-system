package com.community.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_record")
public class FileRecord {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID

    private String fileName;         // 文件名（展示名，含扩展名）
    private String category;         // 分类: 政策文件/通知公告/居民档案/社区文档/其他
    private String originalName;     // 原始文件名
    private String storageName;      // 存储文件名（UUID.扩展名）
    private String filePath;         // 存储路径
    private Long fileSize;           // 文件大小(字节)
    private String ext;              // 扩展名
    private String uploader;         // 上传人
    private String remark;           // 备注

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;   // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;   // 更新时间
}
