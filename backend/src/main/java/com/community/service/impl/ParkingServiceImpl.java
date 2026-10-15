package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.PageResult;
import com.community.entity.Parking;
import com.community.entity.vo.ParkingVO;
import com.community.mapper.ParkingMapper;
import com.community.service.ParkingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ParkingServiceImpl extends ServiceImpl<ParkingMapper, Parking> implements ParkingService {

    /** 数量字段可能带说明文字（如「规划11个，实际15」），取其中第一个数字 */
    private static final Pattern NUM = Pattern.compile("\\d+");

    private LambdaQueryWrapper<Parking> buildWrapper(String communityName, String town,
                                                     String communityOrg, String developer,
                                                     String propertyCompany) {
        LambdaQueryWrapper<Parking> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(notBlank(communityName), Parking::getCommunityName, communityName)
               .like(notBlank(town), Parking::getTown, town)
               .like(notBlank(communityOrg), Parking::getCommunityOrg, communityOrg)
               .like(notBlank(developer), Parking::getDeveloper, developer)
               .like(notBlank(propertyCompany), Parking::getPropertyCompany, propertyCompany)
               .orderByAsc(Parking::getId);
        return wrapper;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** 从「123」/「规划11个，实际15」这类文本里取出数值 */
    private long numOf(String s) {
        if (s == null) {
            return 0L;
        }
        Matcher m = NUM.matcher(s);
        if (m.find()) {
            try {
                return Long.parseLong(m.group());
            } catch (NumberFormatException ignore) {
                return 0L;
            }
        }
        return 0L;
    }

    @Override
    public PageResult<ParkingVO> pageVo(int page, int size, String communityName, String town,
                                        String communityOrg, String developer, String propertyCompany) {
        Page<Parking> pageResult = page(new Page<>(page, size),
                buildWrapper(communityName, town, communityOrg, developer, propertyCompany));
        List<ParkingVO> records = pageResult.getRecords().stream()
                .map(this::toVo)
                .collect(Collectors.toList());
        return new PageResult<>(pageResult.getTotal(), records);
    }

    @Override
    public List<ParkingVO> listVo(String communityName, String town,
                                  String communityOrg, String developer, String propertyCompany) {
        return list(buildWrapper(communityName, town, communityOrg, developer, propertyCompany)).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> stats(String communityName, String town,
                                     String communityOrg, String developer, String propertyCompany) {
        List<Parking> all = list(buildWrapper(communityName, town, communityOrg, developer, propertyCompany));

        long plannedHouseholds = 0, plannedTotal = 0, sold = 0, rented = 0, idle = 0, linked = 0;
        for (Parking p : all) {
            plannedHouseholds += numOf(p.getPlannedHouseholds());
            plannedTotal += numOf(p.getPlannedTotal());
            sold += numOf(p.getSoldCount());
            rented += numOf(p.getRentedCount());
            idle += numOf(p.getIdleCount());
            if (p.getCommunityId() != null) {
                linked++;
            }
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("recordCount", all.size());
        map.put("plannedHouseholds", plannedHouseholds);
        map.put("plannedTotal", plannedTotal);
        map.put("soldCount", sold);
        map.put("rentedCount", rented);
        map.put("idleCount", idle);
        map.put("linkedCount", linked);
        return map;
    }

    private ParkingVO toVo(Parking p) {
        ParkingVO vo = new ParkingVO();
        BeanUtils.copyProperties(p, vo);
        return vo;
    }
}
