package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.PageResult;
import com.community.entity.Community;
import com.community.entity.Person;
import com.community.entity.vo.PersonVO;
import com.community.mapper.PersonMapper;
import com.community.service.CommunityService;
import com.community.service.PersonService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {

    private final CommunityService communityService;

    public PersonServiceImpl(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Override
    public PageResult<PersonVO> pageVo(int page, int size, String name, Long communityId) {
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), Person::getName, name)
               .eq(communityId != null, Person::getCommunityId, communityId)
               .orderByAsc(Person::getId);

        Page<Person> pageResult = page(new Page<>(page, size), wrapper);

        Map<Long, String> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getId, Community::getName, (a, b) -> a));

        List<PersonVO> records = pageResult.getRecords().stream().map(p -> {
            PersonVO vo = new PersonVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCommunityName(communityMap.get(p.getCommunityId()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records);
    }

    @Override
    public List<PersonVO> listVo(String name, Long communityId) {
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), Person::getName, name)
               .eq(communityId != null, Person::getCommunityId, communityId)
               .orderByAsc(Person::getId);

        List<Person> list = list(wrapper);
        Map<Long, String> communityMap = communityService.list().stream()
                .collect(Collectors.toMap(Community::getId, Community::getName, (a, b) -> a));

        return list.stream().map(p -> {
            PersonVO vo = new PersonVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCommunityName(communityMap.get(p.getCommunityId()));
            return vo;
        }).collect(Collectors.toList());
    }
}
