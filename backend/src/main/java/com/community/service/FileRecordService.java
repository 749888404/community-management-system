package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.PageResult;
import com.community.entity.FileRecord;

import java.util.List;

public interface FileRecordService extends IService<FileRecord> {

    PageResult<FileRecord> pageVo(int page, int size, String fileName, String category, String uploader);

    /** 获取所有上传人列表（去重） */
    List<String> listUploaders();

    /** 批量删除（同时删除磁盘文件） */
    void batchRemove(List<Long> ids);
}
