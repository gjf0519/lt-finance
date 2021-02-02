package com.lt.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2021/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayLineVo extends PageVo {
    private String tsCode;
    private String tradeDate;
}
