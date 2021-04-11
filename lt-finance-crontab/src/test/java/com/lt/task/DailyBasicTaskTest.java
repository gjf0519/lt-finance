package com.lt.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DailyBasicTaskTest {

    @Autowired
    DailyBasicTask dailyBasicTask;

    @Test
    public void execute() {
        dailyBasicTask.execute();
    }
}