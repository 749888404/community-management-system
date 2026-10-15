package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.Community;
import com.community.entity.Person;
import com.community.entity.excel.PersonExcel;
import com.community.entity.vo.PersonVO;
import com.community.service.CommunityService;
import com.community.service.PersonService;
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
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private CommunityService communityService;

    @GetMapping("/page")
    public Result<PageResult<PersonVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long communityId) {

        return Result.success(personService.pageVo(page, size, name, communityId));
    }

    @PostMapping
    public Result<?> save(@RequestBody Person person) {
        personService.save(person);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<?> update(@RequestBody Person person) {
        personService.updateById(person);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        personService.removeById(id);
        return Result.success("删除成功");
    }

    /** 批量删除 */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的居民");
        }
        personService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /** 导出 Excel（按查询条件导出全部） */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Long communityId) throws IOException {
        List<PersonVO> list = personService.listVo(name, communityId);
        List<PersonExcel> data = list.stream().map(v -> {
            PersonExcel e = new PersonExcel();
            BeanUtils.copyProperties(v, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("居民管理", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PersonExcel.class).sheet("居民管理").doWrite(data);
    }

    /** 导入 Excel（按 ID 更新，无 ID 则新增；所属小区按名称匹配） */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<PersonExcel> data = EasyExcel.read(file.getInputStream()).head(PersonExcel.class).sheet().doReadSync();
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));

        int count = 0;
        for (PersonExcel e : data) {
            Person p = new Person();
            BeanUtils.copyProperties(e, p);
            if (e.getCommunityName() != null && !e.getCommunityName().isEmpty()) {
                p.setCommunityId(communityMap.get(e.getCommunityName()));
            } else {
                p.setCommunityId(null);
            }
            if (e.getId() != null && personService.getById(e.getId()) != null) {
                personService.updateById(p);
            } else {
                p.setId(null);
                personService.save(p);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }
}
