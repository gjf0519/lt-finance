package com.lt.task;

import com.lt.service.TushareService;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@Slf4j
public class DailyBasicTask {

    @Autowired
    TushareService tushareService;

    @Scheduled(cron = "0 30 17 * * ? ")// 0/1 * * * * *
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return;
        }
        log.info("==========================每日指标收集数据开始======================");
        String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
//        String trade_date = "20210407";
        tushareService.requestDayBasic(trade_date);
    }
}
