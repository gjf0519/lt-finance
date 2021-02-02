package com.lt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaijf
 * @description
 * @date 2021/2/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo {
    private Integer limit = 10;
    private Integer offset = 0;
}
