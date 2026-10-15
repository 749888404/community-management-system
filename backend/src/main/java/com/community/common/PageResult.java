package com.community.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回对象
 */
@Data
public class PageResult<T> implements Serializable {
    private Long total;
    private List<T> records;

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
