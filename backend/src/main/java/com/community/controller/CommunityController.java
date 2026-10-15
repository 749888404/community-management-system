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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    /** 构造查询条件 */
    private LambdaQueryWrapper<Community> buildWrapper(String name, String manager, String company,
                                                       String communityOrg, String propertyType,
                                                       String takeoverMode) {
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(notBlank(name), Community::getName, name)
                .like(notBlank(manager), Community::getManager, manager)
                .like(notBlank(company), Community::getCompany, company)
                .like(notBlank(communityOrg), Community::getCommunityOrg, communityOrg)
                .like(notBlank(propertyType), Community::getPropertyType, propertyType)
                .like(notBlank(takeoverMode), Community::getTakeoverMode, takeoverMode)
                .orderByAsc(Community::getId);
        return wrapper;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @GetMapping("/page")
    public Result<PageResult<Community>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String manager,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String communityOrg,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String takeoverMode) {

        Page<Community> result = communityService.page(new Page<>(page, size),
                buildWrapper(name, manager, company, communityOrg, propertyType, takeoverMode));
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 下拉列表（供残疾人 / 基站模块选择小区）
     */
    @GetMapping("/list")
    public Result<List<Community>> list() {
        return Result.success(communityService.list());
    }

    /**
     * 汇总统计（小区管理页顶部卡片）
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        List<Community> all = communityService.list();
        Map<String, Object> vo = new HashMap<>();
        vo.put("communityCount", all.size());
        vo.put("householdsResidential", sumInt(all, Community::getHouseholdsResidential));
        vo.put("householdsCommercial", sumInt(all, Community::getHouseholdsCommercial));
        vo.put("residentPopulation", sumInt(all, Community::getResidentPopulation));
        vo.put("areaResidential", sumDecimal(all, Community::getAreaResidential));
        vo.put("areaCommercial", sumDecimal(all, Community::getAreaCommercial));
        vo.put("parkingTotal", sumInt(all, c -> nz(c.getParkingGround()) + nz(c.getParkingUnderground())));
        vo.put("chargingTotal", sumInt(all, c -> nz(c.getChargingGround()) + nz(c.getChargingUnderground())
                + nz(c.getEbikePort())));
        vo.put("elevatorCount", sumInt(all, Community::getElevatorCount));
        vo.put("fireStation", sumInt(all, Community::getFireStation));
        vo.put("smokeDetector", sumInt(all, Community::getSmokeDetector));
        vo.put("fireHydrant", sumInt(all, Community::getFireHydrant));
        return Result.success(vo);
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private static int sumInt(List<Community> list, java.util.function.Function<Community, Integer> f) {
        int s = 0;
        for (Community c : list) {
            s += nz(f.apply(c));
        }
        return s;
    }

    private static BigDecimal sumDecimal(List<Community> list,
                                         java.util.function.Function<Community, BigDecimal> f) {
        BigDecimal s = BigDecimal.ZERO;
        for (Community c : list) {
            BigDecimal v = f.apply(c);
            if (v != null) {
                s = s.add(v);
            }
        }
        return s;
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
                       @RequestParam(required = false) String manager,
                       @RequestParam(required = false) String company,
                       @RequestParam(required = false) String communityOrg,
                       @RequestParam(required = false) String propertyType,
                       @RequestParam(required = false) String takeoverMode) throws IOException {

        List<Community> list = communityService.list(
                buildWrapper(name, manager, company, communityOrg, propertyType, takeoverMode));
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
