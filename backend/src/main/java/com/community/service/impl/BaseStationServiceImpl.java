package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.PageResult;
import com.community.entity.BaseStation;
import com.community.entity.vo.BaseStationVO;
import com.community.mapper.BaseStationMapper;
import com.community.service.BaseStationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseStationServiceImpl extends ServiceImpl<BaseStationMapper, BaseStation> implements BaseStationService {

    private LambdaQueryWrapper<BaseStation> buildWrapper(String communityName, String townCommunity,
                                                         String operatorName, String deviceType) {
        LambdaQueryWrapper<BaseStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(notBlank(communityName), BaseStation::getCommunityName, communityName)
               .like(notBlank(townCommunity), BaseStation::getTownCommunity, townCommunity)
               .like(notBlank(operatorName), BaseStation::getOperatorName, operatorName)
               .like(notBlank(deviceType), BaseStation::getDeviceType, deviceType)
               .orderByAsc(BaseStation::getId);
        return wrapper;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Override
    public PageResult<BaseStationVO> pageVo(int page, int size, String communityName, String townCommunity,
                                            String operatorName, String deviceType) {
        Page<BaseStation> pageResult = page(new Page<>(page, size),
                buildWrapper(communityName, townCommunity, operatorName, deviceType));

        List<BaseStationVO> records = pageResult.getRecords().stream()
                .map(this::toVo)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records);
    }

    @Override
    public List<BaseStationVO> listVo(String communityName, String townCommunity,
                                      String operatorName, String deviceType) {
        return list(buildWrapper(communityName, townCommunity, operatorName, deviceType)).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    private BaseStationVO toVo(BaseStation s) {
        BaseStationVO vo = new BaseStationVO();
        BeanUtils.copyProperties(s, vo);
        return vo;
    }
}
