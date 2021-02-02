package com.lt.view;

/**
 * @author gaijf
 * @description
 * @date 2019/11/5
 */
public class PageParams {
    private static final Integer PAGE_SIZE = 5;
    private Integer offset;
    private Integer limit;
    private Integer pageSize;
    private Integer pageNumber;

    public static PageParams build(Integer pageSize, Integer pageNumber){
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }
        if (pageNumber == null) {
            pageNumber = 1;
        }
        return new PageParams(pageSize, pageNumber);
    }

    public PageParams(){
        this(PAGE_SIZE, 1);
    }

    public PageParams(Integer pageSize,Integer pageNumber){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.offset = pageSize * (pageNumber -1);
        this.limit = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
