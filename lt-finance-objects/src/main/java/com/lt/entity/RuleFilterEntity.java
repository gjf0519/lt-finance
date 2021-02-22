package com.lt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2021/2/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleFilterEntity {
    private String tsCode;
    private String tradeDate;
    private double pctChg;
    private String ruleName;
    private String nextBreak;
    private String threeBreak;
    private String weekBreak;
}
