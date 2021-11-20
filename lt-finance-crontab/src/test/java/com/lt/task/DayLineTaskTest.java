package com.lt.task;

import com.lt.task.DayLineTask;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@SpringBootTest
public class DayLineTaskTest {

    @Autowired
    DayLineTask dayLineTask;

    @Test
    public void execute() {
        dayLineTask.obtainData(TsCodes.STOCK_CODE,"20211119","20211119");
        try {
            Thread.sleep(1000*60*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}