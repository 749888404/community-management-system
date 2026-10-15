package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.PageResult;
import com.community.entity.Community;
import com.community.entity.DisabledPerson;
import com.community.entity.vo.DisabledVO;
import com.community.mapper.DisabledMapper;
import com.community.service.CommunityService;
import com.community.service.DisabledService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DisabledServiceImpl extends ServiceImpl<DisabledMapper, DisabledPerson> implements DisabledService {

    private final CommunityService communityService;

    public DisabledServiceImpl(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Override
    public PageResult<DisabledVO> pageVo(int page, int size, String name, String disabilityType, Long communityId) {
        LambdaQueryWrapper<DisabledPerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), DisabledPerson::getName, name)
               .eq(disabilityType != null && !disabilityType.isEmpty(), DisabledPerson::getDisabilityType, disabilityType)
               .eq(communityId != null, DisabledPerson::getCommunityId, communityId)
               .orderByAsc(DisabledPerson::getId);

        Page<DisabledPerson> pageResult = page(new Page<>(page, size), wrapper);

        // 小区名称映射
        Map<Long, String> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getId, Community::getName, (a, b) -> a));

        List<DisabledVO> records = pageResult.getRecords().stream().map(p -> {
            DisabledVO vo = new DisabledVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCommunityName(communityMap.get(p.getCommunityId()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records);
    }

    @Override
    public List<DisabledVO> listVo(String name, String disabilityType, Long communityId) {
        LambdaQueryWrapper<DisabledPerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), DisabledPerson::getName, name)
               .eq(disabilityType != null && !disabilityType.isEmpty(), DisabledPerson::getDisabilityType, disabilityType)
               .eq(communityId != null, DisabledPerson::getCommunityId, communityId)
               .orderByAsc(DisabledPerson::getId);

        List<DisabledPerson> list = list(wrapper);
        Map<Long, String> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getId, Community::getName, (a, b) -> a));

        return list.stream().map(p -> {
            DisabledVO vo = new DisabledVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCommunityName(communityMap.get(p.getCommunityId()));
            return vo;
        }).collect(Collectors.toList());
    }
}
