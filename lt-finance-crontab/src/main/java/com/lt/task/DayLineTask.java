package com.lt.task;

import com.lt.common.DataType;
import com.lt.entity.RepairDataEntity;
import com.lt.service.TushareService;
import com.lt.utils.Constants;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
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
 * @description 日K线数据
 * @date 2020/12/3
 */
@Slf4j
public class DayLineTask {

    @Autowired
    private TushareService tushareService;

    @Scheduled(cron = "0 30 19 * * ? ")// 0/1 * * * * *
    public void execute() {
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return;
        }
        log.info("==========================日线收集数据开始======================");
        this.obtainData(TsCodes.STOCK_CODE);
        log.info("==========================日线收集数据完成======================");
    }

    public void repairData(List<String> codes){
        this.obtainData(codes);
    }

    private void obtainData(List<String> codes){
        String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        List<RepairDataEntity> repairList = new ArrayList<>();
        for(String item : codes){
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isOk = tushareService.obtainDayLine(item);
            if(!isOk){
                RepairDataEntity entity = RepairDataEntity.builder()
                        .repairCode(item)
                        .repairDate(trade_date)
                        .repairTopic(Constants.TUSHARE_DAYLINE_TOPIC).build();
                repairList.add(entity);
            }
        }
        tushareService.repairData(repairList);
    }
}
