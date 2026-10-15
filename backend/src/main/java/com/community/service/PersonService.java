package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.PageResult;
import com.community.entity.Person;
import com.community.entity.vo.PersonVO;

import java.util.List;

public interface PersonService extends IService<Person> {
    PageResult<PersonVO> pageVo(int page, int size, String name, Long communityId);

    /** 按条件查询全部（含小区名称），用于导出 */
    List<PersonVO> listVo(String name, Long communityId);
}
