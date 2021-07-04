package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBasicEntity {

    private String tsCode;
    private String tradeDate;
    private double close;
    private double turnoverRate;
    private double turnoverRateFree;
    private double volumeRatio;
    private double circMv;
}
