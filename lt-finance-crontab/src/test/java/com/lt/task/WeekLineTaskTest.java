package com.lt.task;

import com.lt.task.DayLineTask;
import com.lt.task.WeekLineTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@SpringBootTest
public class WeekLineTaskTest {

    @Autowired
    WeekLineTask weekLineTask;

    @Test
    public void execute() {
        weekLineTask.execute();
    }
}
