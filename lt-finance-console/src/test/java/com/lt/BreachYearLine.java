package com.lt;

import com.lt.entity.DailyBasicEntity;
import com.lt.entity.KLineEntity;
import com.lt.service.DailyBasicServie;
import com.lt.service.KLineService;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 重要均线突破
 */
@SpringBootTest
public class BreachYearLine {
    @Autowired
    KLineService kLineService;
    @Autowired
    DailyBasicServie dailyBasicServie;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,30);
                    //图形计算
                    this.calculation(list);
                }catch (Exception e){
//                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void calculation(List<KLineEntity> list){
        //突破年线
        if(list.get(0).getMaFive() < list.get(0).getMaYear() &&
                list.get(1).getHigh() < list.get(1).getMaYear() &&
                list.get(0).getHigh() > list.get(0).getMaYear()){
            System.out.println("突破年线===========>"+list.get(0).getTsCode());
            return;
        }
        //突破半年线
        if(list.get(0).getMaFive() < list.get(0).getMaSemester() &&
                list.get(1).getHigh() < list.get(1).getMaSemester() &&
                list.get(0).getHigh() > list.get(0).getMaSemester()){
            System.out.println("突破半年线===========>"+list.get(0).getTsCode());
            return;
        }
        //突破季线
        if(list.get(0).getMaFive() < list.get(0).getMaQuarter() &&
                list.get(1).getHigh() < list.get(1).getMaQuarter() &&
                list.get(0).getHigh() > list.get(0).getMaQuarter()){
            System.out.println("突破季线===========>"+list.get(0).getTsCode());
        }
    }
}
