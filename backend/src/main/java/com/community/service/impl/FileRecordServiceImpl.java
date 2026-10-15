package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.PageResult;
import com.community.entity.FileRecord;
import com.community.mapper.FileRecordMapper;
import com.community.service.FileRecordService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileRecordServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileRecordService {

    @Override
    public PageResult<FileRecord> pageVo(int page, int size, String fileName, String category, String uploader) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(fileName != null && !fileName.isEmpty(), FileRecord::getFileName, fileName)
               .eq(category != null && !category.isEmpty(), FileRecord::getCategory, category)
               .eq(uploader != null && !uploader.isEmpty(), FileRecord::getUploader, uploader)
               .orderByDesc(FileRecord::getId);

        Page<FileRecord> pageResult = page(new Page<>(page, size), wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
    }

    @Override
    public List<String> listUploaders() {
        List<FileRecord> records = list(new LambdaQueryWrapper<FileRecord>()
                .select(FileRecord::getUploader)
                .isNotNull(FileRecord::getUploader)
                .ne(FileRecord::getUploader, "")
                .groupBy(FileRecord::getUploader)
                .orderByAsc(FileRecord::getUploader));
        List<String> uploaders = new ArrayList<>();
        for (FileRecord r : records) {
            if (r.getUploader() != null && !r.getUploader().isEmpty()) {
                uploaders.add(r.getUploader());
            }
        }
        return uploaders;
    }

    @Override
    public void batchRemove(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<FileRecord> records = listByIds(ids);
        removeByIds(ids);
        for (FileRecord r : records) {
            try {
                if (r.getFilePath() != null) {
                    Files.deleteIfExists(Paths.get(r.getFilePath()));
                }
            } catch (IOException ignored) {
                // 磁盘文件删除失败不影响记录删除
            }
        }
    }
}
