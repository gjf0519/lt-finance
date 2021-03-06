package com.lt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2021/2/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleLineDto {
    private int id;
    private String tsCode;
    private String tradeDate;
    private double pctChg;
    private String ruleName;
    private Integer nextBreak;
}
