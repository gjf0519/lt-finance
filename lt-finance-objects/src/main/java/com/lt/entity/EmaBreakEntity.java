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
public class EmaBreakEntity {
    private String tsCode;
    private String klineType;
    private String breakType;
    private double volumeRatio;
    private double turnoverRate;
    private double rose;
    private int risingNumber;
    private String tradeDate;
}
