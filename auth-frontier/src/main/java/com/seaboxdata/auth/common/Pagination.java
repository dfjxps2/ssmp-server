package com.seaboxdata.auth.common;

import lombok.Data;

import java.util.List;

@Data
public abstract class Pagination<T> {
    private final Integer total;
    private final Integer offset;
    private final Integer limit;

    protected List<T> data;

    public Pagination(Integer total, Integer offset, Integer limit) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }
}
