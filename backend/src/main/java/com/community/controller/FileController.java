package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.FileRecord;
import com.community.service.FileRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileRecordService fileRecordService;

    /** 文件存储目录（相对启动目录） */
    @Value("${community.upload-dir:./uploads}")
    private String uploadDir;

    @GetMapping("/page")
    public Result<PageResult<FileRecord>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String uploader) {
        return Result.success(fileRecordService.pageVo(page, size, fileName, category, uploader));
    }

    /** 上传人列表（去重，供筛选下拉使用） */
    @GetMapping("/uploaders")
    public Result<java.util.List<String>> uploaders() {
        return Result.success(fileRecordService.listUploaders());
    }

    /** 上传文件（支持多选） */
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("files") MultipartFile[] files,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String remark,
                            @RequestParam(required = false) String uploader) {
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            List<String> names = new ArrayList<>();
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;
                String original = f.getOriginalFilename() == null ? "未命名" : f.getOriginalFilename();
                String ext = "";
                int dot = original.lastIndexOf('.');
                if (dot >= 0 && dot < original.length() - 1) {
                    ext = original.substring(dot + 1).toLowerCase();
                }
                // 用 UUID 作为存储文件名，避免重名覆盖与路径穿越
                String storageName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

                Path target = dir.resolve(storageName).normalize();
                f.transferTo(target.toFile());

                FileRecord record = new FileRecord();
                record.setFileName(original);
                record.setCategory(category);
                record.setOriginalName(original);
                record.setStorageName(storageName);
                record.setFilePath(target.toString());
                record.setFileSize(f.getSize());
                record.setExt(ext);
                record.setUploader(uploader);
                record.setRemark(remark);
                fileRecordService.save(record);
                names.add(original);
            }
            return Result.success("上传成功：" + names.size() + " 个文件");
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /** 预览文件（浏览器内联显示，支持 pdf/图片/文本类） */
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileRecord record = fileRecordService.getById(id);
        if (record == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(record.getFilePath());
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        String ext = record.getExt() == null ? "" : record.getExt().toLowerCase();
        response.setContentType(previewContentType(ext));
        String fileName = URLEncoder.encode(record.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "inline;filename*=utf-8''" + fileName);
        response.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }

    /** 根据扩展名返回可内联预览的 Content-Type */
    private String previewContentType(String ext) {
        switch (ext) {
            case "pdf": return "application/pdf";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "doc": return "application/msword";
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "webp": return "image/webp";
            case "bmp": return "image/bmp";
            case "svg": return "image/svg+xml";
            case "txt":
            case "md":
            case "log": return "text/plain;charset=UTF-8";
            case "json": return "application/json;charset=UTF-8";
            case "xml": return "text/xml;charset=UTF-8";
            case "html":
            case "htm": return "text/html;charset=UTF-8";
            default: return "application/octet-stream";
        }
    }

    /** 下载文件 */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        FileRecord record = fileRecordService.getById(id);
        if (record == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(record.getFilePath());
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        String fileName = URLEncoder.encode(record.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName);
        response.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }
    }

    /** 编辑（分类/文件名/备注） */
    @PutMapping
    public Result<?> update(@RequestBody FileRecord record) {
        FileRecord db = fileRecordService.getById(record.getId());
        if (db == null) {
            return Result.error("文件不存在");
        }
        if (record.getFileName() != null && !record.getFileName().isEmpty()) {
            db.setFileName(record.getFileName());
        }
        db.setCategory(record.getCategory());
        db.setRemark(record.getRemark());
        fileRecordService.updateById(db);
        return Result.success("修改成功");
    }

    /** 删除（同时删除磁盘文件） */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        FileRecord record = fileRecordService.getById(id);
        if (record == null) {
            return Result.error("文件不存在");
        }
        fileRecordService.removeById(id);
        try {
            Files.deleteIfExists(Paths.get(record.getFilePath()));
        } catch (IOException ignored) {
            // 磁盘文件删除失败不影响记录删除
        }
        return Result.success("删除成功");
    }

    /** 批量删除（同时删除磁盘文件） */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的文件");
        }
        fileRecordService.batchRemove(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }
}
