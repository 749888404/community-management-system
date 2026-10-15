package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.PageResult;
import com.community.entity.DisabledPerson;
import com.community.entity.vo.DisabledVO;

import java.util.List;

public interface DisabledService extends IService<DisabledPerson> {

    /** 分页查询残疾人信息（含小区名称） */
    PageResult<DisabledVO> pageVo(int page, int size, String name, String disabilityType, Long communityId);

    /** 按条件查询全部（含小区名称），用于导出 */
    List<DisabledVO> listVo(String name, String disabilityType, Long communityId);
}
