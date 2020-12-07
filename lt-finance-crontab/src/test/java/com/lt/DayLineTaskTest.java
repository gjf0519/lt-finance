package com.lt;

import com.lt.task.DayLineTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        dayLineTask.execute();
    }
}
