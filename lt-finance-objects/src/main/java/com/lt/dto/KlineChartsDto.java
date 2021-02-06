package com.lt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * k图数据结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KlineChartsDto {
    private String tsCode;
    private List<List<Object>> lines;
}
