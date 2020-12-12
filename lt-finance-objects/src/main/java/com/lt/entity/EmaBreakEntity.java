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
    private String klineFlat;
    private double klineAngle;
    private double volumeRatio;
    private double turnoverRate;
    private String fivetoten;
    private String fivetotwenty;
    private String fivetothirty;
    private String fivetosixty;
    private String tentotwenty;
    private String tentothirty;
    private String tentosixty;
    private String twentytothirty;
    private String twentytosixty;
    private double rose;
    private int breakDay;
    private String tradeDate;
}
