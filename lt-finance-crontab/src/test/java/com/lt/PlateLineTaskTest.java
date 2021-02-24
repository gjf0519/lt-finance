package com.lt;

import com.lt.service.TushareService;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gaijf
 * @description
 * @date 2021/2/23
 */
@SpringBootTest
public class PlateLineTaskTest {

    public static void main(String[] args) {
        TushareService tushareService = new TushareService();
        tushareService.requestPlates();
    }
}
