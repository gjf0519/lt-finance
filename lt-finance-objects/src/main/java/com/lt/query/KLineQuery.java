package com.lt.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @date 2021/11/18
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineQuery {
    private String tsCode;
    private String startDate;
    private String endDate;
}
