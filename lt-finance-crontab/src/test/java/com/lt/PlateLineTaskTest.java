package com.lt;

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

    @Test
    public void plateIndex(){
        plateLineTask.execute();
    }


    public static void main(String[] args) {
        TushareService tushareService = new TushareService();
        tushareService.requestPlateIndex();
    }
}
