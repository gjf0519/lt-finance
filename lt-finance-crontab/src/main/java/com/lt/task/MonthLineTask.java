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

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/3
 */
@Slf4j
public class MonthLineTask {

    @Autowired
    private TushareScriptService tushareScriptService;

    @Scheduled(cron = "0 0 21 * * ? ")
    public void execute() {
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        if(today.compareTo(lastDay) != 0){
            return;
        }
        log.info("==========================月线收集数据开始======================");
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        this.obtainData(TsCodes.STOCK_CODE,tradeDate,tradeDate);
        log.info("==========================月线收集数据完成======================");
    }

    private void obtainData(List<String> codes,String startDate,String endDate){
        for(String item : codes){
            try {
                tushareScriptService.obtainMonthLine(item,startDate,endDate);
                Thread.sleep(150);
            } catch (Exception e) {
                tushareScriptService.repairData(TushareUtil.TUSHARE_MONTHLINE_TOPIC,item,startDate);
                log.info("月线获取数据异常exception：{}", JSON.toJSONString(e));
            }
        }
    }
}
