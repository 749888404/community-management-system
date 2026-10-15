package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.Community;
import com.community.entity.Parking;
import com.community.entity.excel.ParkingExcel;
import com.community.entity.vo.ParkingVO;
import com.community.service.CommunityService;
import com.community.service.ParkingService;
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

/**
 * 既有住宅小区车位情况统计
 */
@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private CommunityService communityService;

    /** 分页查询 */
    @GetMapping("/page")
    public Result<PageResult<ParkingVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String communityName,
            @RequestParam(required = false) String town,
            @RequestParam(required = false) String communityOrg,
            @RequestParam(required = false) String developer,
            @RequestParam(required = false) String propertyCompany) {

        PageResult<ParkingVO> result = parkingService.pageVo(page, size, communityName, town,
                communityOrg, developer, propertyCompany);
        return Result.success(result);
    }

    /** 汇总统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam(required = false) String communityName,
            @RequestParam(required = false) String town,
            @RequestParam(required = false) String communityOrg,
            @RequestParam(required = false) String developer,
            @RequestParam(required = false) String propertyCompany) {
        return Result.success(parkingService.stats(communityName, town, communityOrg, developer, propertyCompany));
    }

    /** 新增 */
    @PostMapping
    public Result<?> save(@RequestBody Parking parking) {
        fillCommunityId(parking);
        parkingService.save(parking);
        return Result.success("新增成功");
    }

    /** 修改 */
    @PutMapping
    public Result<?> update(@RequestBody Parking parking) {
        fillCommunityId(parking);
        parkingService.updateById(parking);
        return Result.success("修改成功");
    }

    /** 单个删除 */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        parkingService.removeById(id);
        return Result.success("删除成功");
    }

    /** 批量删除 */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        parkingService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /** 导出 Excel（按查询条件导出全部） */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String communityName,
                       @RequestParam(required = false) String town,
                       @RequestParam(required = false) String communityOrg,
                       @RequestParam(required = false) String developer,
                       @RequestParam(required = false) String propertyCompany) throws IOException {
        List<ParkingVO> list = parkingService.listVo(communityName, town, communityOrg, developer, propertyCompany);
        List<ParkingExcel> data = list.stream().map(v -> {
            ParkingExcel e = new ParkingExcel();
            BeanUtils.copyProperties(v, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("既有住宅小区车位情况统计表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ParkingExcel.class)
                .sheet("车位情况统计").doWrite(data);
    }

    /** 导入 Excel（按 ID 更新，无 ID 则新增；小区名称自动关联本系统小区） */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<ParkingExcel> data = EasyExcel.read(file.getInputStream())
                .head(ParkingExcel.class).sheet().doReadSync();
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));

        int count = 0;
        for (ParkingExcel e : data) {
            Parking p = new Parking();
            BeanUtils.copyProperties(e, p);
            if (e.getCommunityName() != null && !e.getCommunityName().trim().isEmpty()) {
                p.setCommunityId(communityMap.get(e.getCommunityName().trim()));
            } else {
                p.setCommunityId(null);
            }
            if (e.getId() != null && parkingService.getById(e.getId()) != null) {
                parkingService.updateById(p);
            } else {
                p.setId(null);
                parkingService.save(p);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }

    /** 小区名称 → 本系统小区ID（匹配不上则置空） */
    private void fillCommunityId(Parking parking) {
        if (parking.getCommunityName() == null || parking.getCommunityName().trim().isEmpty()) {
            parking.setCommunityId(null);
            return;
        }
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));
        parking.setCommunityId(communityMap.get(parking.getCommunityName().trim()));
    }
}
