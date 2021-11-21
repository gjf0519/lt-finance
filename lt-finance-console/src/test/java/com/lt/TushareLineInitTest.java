package com.lt;

import com.lt.web.service.KlineInitService;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Tushare脚本数据初始化
 */
@SpringBootTest(classes = {ConsoleApplication.class })
public class TushareLineInitTest {

    @Autowired
    private KlineInitService initService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void initDay() throws Exception {
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    initService.initDayLine(item,"20200701","20211117");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
                System.out.println(latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        initService.initDayLine("001288.SZ","20211101","20211117");
    }

    @Test
    public void initWeek(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    initService.initWeekLine(item,"20180101","20211117");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
                System.out.println(latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initMonth(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    initService.initMonthLine(item,"20080101","20211117");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
                System.out.println(latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
