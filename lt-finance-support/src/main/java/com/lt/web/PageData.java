package com.lt.web;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2019/11/5
 */
public class PageData<T> {
    //总记录数量
    private Integer totals;
    private List<T> rows;

    public PageData (Integer totals,List<T> rows){
        this.totals = totals;
        this.rows = rows;
    }

    public static PageData build(Integer totals, List<?> list){
        if (totals == null) {
            totals = 0;
        }
        return new PageData(totals,list);
    }

    public Integer getTotals() {
        return totals;
    }

    public void setTotals(Integer totals) {
        this.totals = totals;
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
                ", totals=" + totals +
                ", rows=" + rows +
                '}';
    }
}
