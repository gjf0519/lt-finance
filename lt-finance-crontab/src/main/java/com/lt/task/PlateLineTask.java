package com.lt.task;

import com.lt.service.TushareService;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * @author gaijf
 * @description
 * @date 2021/2/23
 */
@Slf4j
public class PlateLineTask {
    @Autowired
    TushareService tushareService;

    @Scheduled(cron = "0 10 21 * * ? ")// 0/1 * * * * *
    public void execute() {
//        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
//        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
//            return;
//        }
        log.info("==========================板块收集数据开始======================");
        tushareService.obtainPlateIndex();
        log.info("==========================板块收集数据完成======================");
    }
}
