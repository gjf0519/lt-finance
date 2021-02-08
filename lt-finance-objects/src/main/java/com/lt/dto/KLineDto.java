package com.lt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineDto {
    private int id;
    private String tsCode;
    private String tradeDate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double vol;
}
