package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.Community;
import com.community.entity.DisabledPerson;
import com.community.entity.excel.DisabledExcel;
import com.community.entity.vo.DisabledVO;
import com.community.service.CommunityService;
import com.community.service.DisabledService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disabled")
public class DisabledController {

    @Autowired
    private DisabledService disabledService;

    @Autowired
    private CommunityService communityService;

    @GetMapping("/page")
    public Result<PageResult<DisabledVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String disabilityType,
            @RequestParam(required = false) Long communityId) {

        PageResult<DisabledVO> result = disabledService.pageVo(page, size, name, disabilityType, communityId);
        return Result.success(result);
    }

    @PostMapping
    public Result<?> save(@RequestBody DisabledPerson person) {
        disabledService.save(person);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody DisabledPerson person) {
        disabledService.updateById(person);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        disabledService.removeById(id);
        return Result.success("删除成功");
    }

    /** 批量删除 */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        disabledService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /** 导出 Excel（按查询条件导出全部） */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String disabilityType,
                       @RequestParam(required = false) Long communityId) throws IOException {
        List<DisabledVO> list = disabledService.listVo(name, disabilityType, communityId);
        List<DisabledExcel> data = list.stream().map(v -> {
            DisabledExcel e = new DisabledExcel();
            BeanUtils.copyProperties(v, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("残疾人管理", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), DisabledExcel.class).sheet("残疾人管理").doWrite(data);
    }

    /** 导入 Excel（按 ID 更新，无 ID 则新增；所属小区按名称匹配） */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<DisabledExcel> data = EasyExcel.read(file.getInputStream()).head(DisabledExcel.class).sheet().doReadSync();
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));

        int count = 0;
        for (DisabledExcel e : data) {
            DisabledPerson p = new DisabledPerson();
            BeanUtils.copyProperties(e, p);
            if (e.getCommunityName() != null && !e.getCommunityName().isEmpty()) {
                p.setCommunityId(communityMap.get(e.getCommunityName()));
            } else {
                p.setCommunityId(null);
            }
            if (e.getId() != null && disabledService.getById(e.getId()) != null) {
                disabledService.updateById(p);
            } else {
                p.setId(null);
                disabledService.save(p);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }
}
