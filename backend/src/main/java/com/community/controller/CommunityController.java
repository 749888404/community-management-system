package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.Community;
import com.community.entity.excel.CommunityExcel;
import com.community.service.CommunityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @GetMapping("/page")
    public Result<PageResult<Community>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String manager) {

        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), Community::getName, name)
                .like(manager != null && !manager.isEmpty(), Community::getManager, manager)
                .orderByAsc(Community::getId);

        Page<Community> result = communityService.page(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 下拉列表（供残疾人模块选择小区）
     */
    @GetMapping("/list")
    public Result<List<Community>> list() {
        return Result.success(communityService.list());
    }

    @PostMapping
    public Result<?> save(@RequestBody Community community) {
        communityService.save(community);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody Community community) {
        communityService.updateById(community);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        communityService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的小区");
        }
        communityService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /**
     * 导出 Excel（按查询条件导出全部）
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String manager) throws IOException {
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), Community::getName, name)
                .like(manager != null && !manager.isEmpty(), Community::getManager, manager)
                .orderByAsc(Community::getId);

        List<Community> list = communityService.list(wrapper);
        List<CommunityExcel> data = list.stream().map(c -> {
            CommunityExcel e = new CommunityExcel();
            BeanUtils.copyProperties(c, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("小区管理", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), CommunityExcel.class).sheet("小区管理").doWrite(data);
    }

    /**
     * 导入 Excel（按 ID 更新，无 ID 则新增）
     */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<CommunityExcel> data = EasyExcel.read(file.getInputStream()).head(CommunityExcel.class).sheet().doReadSync();
        int count = 0;
        for (CommunityExcel e : data) {
            Community c = new Community();
            BeanUtils.copyProperties(e, c);
            if (e.getId() != null && communityService.getById(e.getId()) != null) {
                communityService.updateById(c);
            } else {
                c.setId(null);
                communityService.save(c);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }
}
