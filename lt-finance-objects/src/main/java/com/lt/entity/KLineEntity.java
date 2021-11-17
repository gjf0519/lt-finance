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
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double preClose;
    private Double priceChg;
    private Double pctChg;
    private Double vol;
    private Double amount;
    private Double maFive;
    private Double maTen;
    private Double maTwenty;
    private Double maMonth;
    private Double maQuarter;
    private Double maSemester;
    private Double maYear;
}
