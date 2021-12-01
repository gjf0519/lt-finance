package com.lt.task;

import com.lt.task.DayLineTask;
import com.lt.task.WeekLineTask;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

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
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
        String startDate = localDate.format(formatters);
        String endDate = localDate.plusDays(-7).format(formatters);
        weekLineTask.obtainData(TsCodes.STOCK_CODE,"20211125","20211125");
    }
}
