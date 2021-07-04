package com.lt.task;

import com.alibaba.fastjson.JSON;
import com.lt.common.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据补充定时任务
 */
@Slf4j
public class RepairDataTask {

    @Autowired
    private DayLineTask dayLineTask;
    @Autowired
    private WeekLineTask weekLineTask;
    @Autowired
    private MonthLineTask monthLineTask;

    /*日K补充次数*/
    private int dayLineNum = 0;

    /*周K补充次数*/
    private int weekLineNum = 0;

    /*周K获取间隔次数*/
    private int weekIntervalNum = 0;

    /*月K获取日期*/
    private LocalDate monthDay;

    /*月K补充次数*/
    private int monthLineNum = 0;

    /*月K获取间隔次数*/
    private int monthIntervalNum = 0;

    /**
     *每日待补充数据
     */
    private final static Map<DataType, List<String>> REPAIR_TSCODE = new ConcurrentHashMap<>();

    public Map<DataType,List<String>> getRepairTsCodeMap(){
        return REPAIR_TSCODE;
    }

    public void setMonthDay(LocalDate monthDay) {
        this.monthDay = monthDay;
    }

    @Scheduled(cron = "0 0 */1 * * ?")
    public void execute() {
        for(Map.Entry<DataType,List<String>> entry : REPAIR_TSCODE.entrySet()){
            switch (entry.getKey()){
                case DAY_LINE:
                    this.dayLine(entry.getValue());
                    break;
                case WEEK_LINE:
                    this.weekLine(entry.getValue());
                    break;
                case MONTH_LINE:
                    this.monthLine(entry.getValue());
                    break;
                default:
                    log.info("数据补充定时任务未知业务类型:{}",entry.getKey());
            }
        }
        log.info("==========================数据补充定时任务结束======================");
    }

    private void dayLine(List<String> codes){
        if(dayLineNum == 5){
            this.dayLineNum = 0;
            REPAIR_TSCODE.remove(DataType.DAY_LINE);
            return;
        }
        this.dayLineNum++;
        dayLineTask.repairData(codes);
        log.info("日K数据补充剩余数据：{}", JSON.toJSONString(REPAIR_TSCODE.get(DataType.DAY_LINE)));
    }

    private void weekLine(List<String> codes){
        if(weekLineNum == 2){
            this.weekLineNum = 0;
            this.weekIntervalNum = 0;
            REPAIR_TSCODE.remove(DataType.WEEK_LINE);
            return;
        }
        this.weekIntervalNum++;
        if(weekIntervalNum == 4){
            this.weekLineNum++;
            weekLineTask.repairData(codes);
        }
    }

    private void monthLine(List<String> codes){
        if(monthLineNum == 2){
            this.monthDay = null;
            this.monthIntervalNum = 0;
            REPAIR_TSCODE.remove(DataType.MONTH_LINE);
        }
        this.monthIntervalNum++;
        if(null != this.monthDay && monthIntervalNum == 12){
            this.monthLineNum++;
            monthLineTask.repairData(codes);
        }
    }
}
