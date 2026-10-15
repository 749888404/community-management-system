package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.BaseStation;
import com.community.entity.Community;
import com.community.entity.excel.BaseStationExcel;
import com.community.entity.vo.BaseStationVO;
import com.community.service.BaseStationService;
import com.community.service.CommunityService;
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
 * 通讯基站统计
 */
@RestController
@RequestMapping("/api/station")
public class BaseStationController {

    @Autowired
    private BaseStationService baseStationService;

    @Autowired
    private CommunityService communityService;

    /** 分页查询 */
    @GetMapping("/page")
    public Result<PageResult<BaseStationVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String communityName,
            @RequestParam(required = false) String townCommunity,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String deviceType) {

        PageResult<BaseStationVO> result = baseStationService.pageVo(page, size, communityName, townCommunity, operatorName, deviceType);
        return Result.success(result);
    }

    /** 新增 */
    @PostMapping
    public Result<?> save(@RequestBody BaseStation station) {
        fillCommunityId(station);
        baseStationService.save(station);
        return Result.success("新增成功");
    }

    /** 修改 */
    @PutMapping
    public Result<?> update(@RequestBody BaseStation station) {
        fillCommunityId(station);
        baseStationService.updateById(station);
        return Result.success("修改成功");
    }

    /** 单个删除 */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        baseStationService.removeById(id);
        return Result.success("删除成功");
    }

    /** 批量删除 */
    @PostMapping("/batch-delete")
    public Result<?> batchRemove(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        baseStationService.removeByIds(ids);
        return Result.success("删除成功，共 " + ids.size() + " 条");
    }

    /** 导出 Excel（按查询条件导出全部） */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String communityName,
                       @RequestParam(required = false) String townCommunity,
                       @RequestParam(required = false) String operatorName,
                       @RequestParam(required = false) String deviceType) throws IOException {
        List<BaseStationVO> list = baseStationService.listVo(communityName, townCommunity, operatorName, deviceType);
        List<BaseStationExcel> data = list.stream().map(v -> {
            BaseStationExcel e = new BaseStationExcel();
            BeanUtils.copyProperties(v, e);
            return e;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("通讯基站统计", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), BaseStationExcel.class).sheet("通讯基站统计").doWrite(data);
    }

    /** 导入 Excel（按 ID 更新，无 ID 则新增；小区名称自动关联本系统小区） */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam("file") MultipartFile file) throws IOException {
        List<BaseStationExcel> data = EasyExcel.read(file.getInputStream()).head(BaseStationExcel.class).sheet().doReadSync();
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));

        int count = 0;
        for (BaseStationExcel e : data) {
            BaseStation s = new BaseStation();
            BeanUtils.copyProperties(e, s);
            if (e.getCommunityName() != null && !e.getCommunityName().trim().isEmpty()) {
                s.setCommunityId(communityMap.get(e.getCommunityName().trim()));
            } else {
                s.setCommunityId(null);
            }
            if (e.getId() != null && baseStationService.getById(e.getId()) != null) {
                baseStationService.updateById(s);
            } else {
                s.setId(null);
                baseStationService.save(s);
            }
            count++;
        }
        return Result.success("导入成功，共处理 " + count + " 条");
    }

    /** 小区名称 → 本系统小区ID（匹配不上则置空） */
    private void fillCommunityId(BaseStation station) {
        if (station.getCommunityName() == null || station.getCommunityName().trim().isEmpty()) {
            station.setCommunityId(null);
            return;
        }
        Map<String, Long> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getName, Community::getId, (a, b) -> a));
        station.setCommunityId(communityMap.get(station.getCommunityName().trim()));
    }
}
