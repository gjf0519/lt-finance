package com.lt.task;

import com.lt.service.TushareApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * @author gaijf
 * @description
 * @date 2021/1/18
 */
@Slf4j
public class StockBasicTask {

    @Autowired
    TushareApiService tushareService;

    @Scheduled(cron = "0 0 17 * * ? ")
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek != DayOfWeek.SATURDAY){
            return;
        }
        tushareService.obtainStockBasic();
        log.info("==========================市场代码收集数据完成======================");
    }
}
