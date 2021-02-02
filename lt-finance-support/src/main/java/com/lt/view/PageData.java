package com.lt.view;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/11/5
 */
public class PageData<T> {

    private List<T> rows;
    private Integer total;

    public PageData (Integer total,List<T> rows){
        this.rows = rows;
        this.total = total;
    }

    public static PageData build(Integer total, List<?> list){
        if (total == null) {
            total = 0;
        }
        return new PageData(total,list);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageDataResult{" +
                ", total=" + total +
                ", rows=" + rows +
                '}';
    }
}
