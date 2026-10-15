package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.PageResult;
import com.community.entity.Parking;
import com.community.entity.vo.ParkingVO;

import java.util.List;
import java.util.Map;

public interface ParkingService extends IService<Parking> {

    /** 分页查询车位统计 */
    PageResult<ParkingVO> pageVo(int page, int size, String communityName, String town,
                                 String communityOrg, String developer, String propertyCompany);

    /** 按条件查询全部，用于导出 */
    List<ParkingVO> listVo(String communityName, String town,
                           String communityOrg, String developer, String propertyCompany);

    /** 汇总统计（记录数 / 规划户数 / 车位总数 / 已售 / 已租 / 闲置 / 已关联小区数） */
    Map<String, Object> stats(String communityName, String town,
                              String communityOrg, String developer, String propertyCompany);
}
