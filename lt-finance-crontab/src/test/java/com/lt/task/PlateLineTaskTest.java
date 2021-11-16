package com.lt.task;

import com.lt.service.TushareApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gaijf
 * @description
 * @date 2021/2/23
 */
@SpringBootTest
public class PlateLineTaskTest {

    @Autowired
    PlateLineTask plateLineTask;
    @Autowired
    TushareApiService tushareService;

    @Test
    public void plateIndex(){
        plateLineTask.execute();
    }

    @Test
    public void plateElement(){
        tushareService.obtainPlateElement("884032.TI");
    }

    public static void main(String[] args) {
        TushareApiService tushareService = new TushareApiService();
    }
}
