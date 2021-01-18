package com.lt;

import com.lt.service.TushareService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gaijf
 * @description
 * @date 2021/1/18
 */
@SpringBootTest
public class StockBasicTask {

    @Autowired
    TushareService tushareService;

    @Test
    public void execute() {
        tushareService.requestStockBasic();
    }
}
