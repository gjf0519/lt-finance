package com.lt.task;

import com.alibaba.fastjson.JSON;
import com.lt.entity.RepairDataEntity;
import com.lt.service.TushareScriptService;
import com.lt.utils.Constants;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gaijf
 * @description 周K线数据获取
 * @date 2020/12/7
 */
@Slf4j
public class WeekLineTask {

    @Autowired
    private TushareScriptService tushareScriptService;

    @Scheduled(cron = "0 0 10 * * ? ")
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek != DayOfWeek.SATURDAY){
            return;
        }
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        this.obtainData(TsCodes.STOCK_CODE,tradeDate,tradeDate);
        log.info("==========================周线收集数据完成======================");
    }

    private void obtainData(List<String> codes,String startDate,String endDate){
        for(String item : codes){
            try {
                Thread.sleep(300);
                tushareScriptService.obtainWeekLine(item,startDate,endDate);
            } catch (Exception e) {
                tushareScriptService.repairData(TushareUtil.TUSHARE_WEEKLINE_TOPIC,item,startDate);
                log.info("周线获取数据异常exception：{}", JSON.toJSONString(e));
            }
        }
    }
}
