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
        Date monthEnd = Date.from(localDate.plusMonths(-1).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println(TimeUtil.dateFormat(monthEnd,"yyyyMMdd"));
        LocalDate lastDay = localDate.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(lastDay.toString());
//        weekLineTask.obtainData(TsCodes.STOCK_CODE,"20211119","20211119");
    }
}
