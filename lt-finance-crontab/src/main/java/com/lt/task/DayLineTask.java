package com.lt.task;

import com.alibaba.fastjson.JSON;
import com.lt.web.service.TushareScriptService;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/3
 */
@Slf4j
public class DayLineTask {

    @Autowired
    private TushareScriptService tushareScriptService;

    @Scheduled(cron = "0 30 19 * * ? ")// 0/1 * * * * *
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return;
        }
        log.info("==========================日线收集数据开始======================");
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        this.obtainData(TsCodes.STOCK_CODE,tradeDate,tradeDate);
        log.info("==========================日线收集数据完成======================");
    }

    public void obtainData(List<String> codes,String startDate,String endDate) {
        for(String item : codes){
            try {
                Thread.sleep(150);
                tushareScriptService.obtainDayLine(item,startDate,endDate);
            } catch (Exception e) {
                tushareScriptService.repairData(TushareUtil.TUSHARE_DAYLINE_TOPIC,item,startDate);
                log.info("日线获取数据异常exception：{}", JSON.toJSONString(e));
            }
        }
    }
}
