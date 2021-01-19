package com.lt.task;

import com.lt.service.TushareService;
import com.lt.utils.Constants;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/3
 */
@Slf4j
public class DayLineTask {

    @Autowired
    TushareService tushareService;

    @Scheduled(cron = "0 0 17 * * ? ")// 0/1 * * * * *
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return;
        }
        for(String item : TsCodes.STOCK_CODE){
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tushareService.requestDayLine(item);
        }
        log.info("==========================日线收集数据完成======================");
    }
}
