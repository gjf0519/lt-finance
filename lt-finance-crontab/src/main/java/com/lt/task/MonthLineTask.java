package com.lt.task;

import com.lt.common.DataType;
import com.lt.service.TushareService;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/3
 */
@Slf4j
public class MonthLineTask {

    @Autowired
    private TushareService tushareService;
    @Autowired
    private RepairDataTask repairDataTask;

    @Scheduled(cron = "0 0 21 * * ? ")
    public void execute() {
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
        if(today.compareTo(lastDay) != 0){
            return;
        }
        repairDataTask.setMonthDay(today);
        log.info("==========================日线收集数据开始======================");
        this.obtainData(TsCodes.STOCK_CODE);
        log.info("==========================日线收集数据完成======================");
    }

    public void repairData(List<String> codes){
        this.obtainData(codes);
    }

    private void obtainData(List<String> codes){
        List<String> repairList = new ArrayList<>();
        for(String item : codes){
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isOk = tushareService.obtainMonthLine(item);
            if(!isOk){
                repairList.add(item);
            }
        }
        repairDataTask.getRepairTsCodeMap().put(DataType.MONTH_LINE,repairList);
    }
}
