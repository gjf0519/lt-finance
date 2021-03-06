package com.lt.config;

import com.lt.task.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @author gaijf
 * @description
 * @date 2019/9/17
 */
@Configuration
@EnableScheduling
public class TaskConfiguration implements SchedulingConfigurer {

    /**
     * 设置线程池类型，默认是单线程池执行
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(2));
    }

    /**
     * 股票代码
     * @return
     */
    @Bean
    public StockBasicTask stockBasicTask(){
        StockBasicTask stockBasicTask = new StockBasicTask();
        return stockBasicTask;
    }

    /**
     * 每日指标
     * @return
     */
    @Bean
    public DailyBasicTask dailyBasicTask(){
        DailyBasicTask dailyBasicTask = new DailyBasicTask();
        return dailyBasicTask;
    }

    /**
     * 日K线
     * @return
     */
    @Bean
    public DayLineTask dayLineTask(){
        DayLineTask dayLineTask = new DayLineTask();
        return dayLineTask;
    }

    /**
     * 周K线
     * @return
     */
    @Bean
    public WeekLineTask weekLineTask(){
        WeekLineTask weekLineTask = new WeekLineTask();
        return weekLineTask;
    }

    /**
     * 月K线
     * @return
     */
    @Bean
    public MonthLineTask monthLineTask(){
        MonthLineTask monthLineTask = new MonthLineTask();
        return monthLineTask;
    }

    /**
     * 概念指数
     * @return
     */
    @Bean
    public PlateLineTask plateLineTask(){
        PlateLineTask plateLineTask = new PlateLineTask();
        return plateLineTask;
    }
}
