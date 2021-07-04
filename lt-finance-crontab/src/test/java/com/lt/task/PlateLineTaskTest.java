package com.lt.task;

import com.lt.service.TushareService;
import com.lt.task.PlateLineTask;
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
    TushareService tushareService;

    @Test
    public void plateIndex(){
        plateLineTask.execute();
    }

    @Test
    public void plateElement(){
        tushareService.obtainPlateElement("884032.TI");
    }

    public static void main(String[] args) {
        TushareService tushareService = new TushareService();
    }
}
