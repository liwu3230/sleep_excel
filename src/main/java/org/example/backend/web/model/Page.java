package org.example.backend.web.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class Page<U> {

    @ApiModelProperty(value = "当前页", example = "1")
    private int currPage;
    private List<U> list;
    @ApiModelProperty(value = "每页大小", example = "10")
    private int pageSize;
    @ApiModelProperty(value = "数据总条数", example = "0")
    private long totalCount;
    @ApiModelProperty(value = "总页数", example = "0")
    private int totalPage;

    public Page() {
    }

    public Page(List<U> list, int currPage, int pageSize, long totalCount) {
        this.currPage = currPage;
        this.list = list;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public static <U> Page of(List<U> list, int currPage, int pageSize, long totalCount) {
        return new Page(list, currPage, pageSize, totalCount);
    }

    public static <U> Page<U> getPage(List<U> list, int currPage, int pageSize, long totalCount) {
        return new Page<>(list, currPage, pageSize, totalCount);
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<U> getList() {
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return list;
    }

    public void setList(List<U> list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}


