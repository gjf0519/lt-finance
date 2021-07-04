package com.lt.task;

import com.lt.common.DataType;
import com.lt.service.TushareService;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description 周K线数据获取
 * @date 2020/12/7
 */
@Slf4j
public class WeekLineTask {

    @Autowired
    private TushareService tushareService;
    @Autowired
    private RepairDataTask repairDataTask;

    @Scheduled(cron = "0 0 10 * * ? ")
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek != DayOfWeek.SATURDAY){
            return;
        }
        this.obtainData(TsCodes.STOCK_CODE);
        log.info("==========================周线收集数据完成======================");
    }

    public void repairData(List<String> codes){
        this.obtainData(codes);
    }

    private void obtainData(List<String> codes){
        List<String> repairList = new ArrayList<>();
        for(String item : codes){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isOk = tushareService.obtainWeekLine(item);
            if(!isOk){
                repairList.add(item);
            }
        }
        repairDataTask.getRepairTsCodeMap().put(DataType.WEEK_LINE,repairList);
    }
}
