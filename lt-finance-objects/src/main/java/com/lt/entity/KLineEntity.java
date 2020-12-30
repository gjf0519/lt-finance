package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2020/12/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineEntity {
    private String tsCode;
    private String tradeDate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double preClose;
    private double priceChg;
    private double pctChg;
    private double vol;
    private double amount;
    private double maFive;
    private double maTen;
    private double maTwenty;
    private double maMonth;
    private double maQuarter;
    private double maSemester;
    private double maYear;
}
