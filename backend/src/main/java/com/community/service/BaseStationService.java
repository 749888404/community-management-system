package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.PageResult;
import com.community.entity.BaseStation;
import com.community.entity.vo.BaseStationVO;

import java.util.List;

public interface BaseStationService extends IService<BaseStation> {

    /** 分页查询通讯基站信息 */
    PageResult<BaseStationVO> pageVo(int page, int size, String communityName, String townCommunity,
                                     String operatorName, String deviceType);

    /** 按条件查询全部，用于导出 */
    List<BaseStationVO> listVo(String communityName, String townCommunity, String operatorName, String deviceType);
}
