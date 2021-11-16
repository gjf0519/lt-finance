package com.lt;

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
 * @author gaijf
 * @description: 均线依次突破 示例：南岭民爆 002096 20210902-20210930
 * @date 2021/11/721:39
 */
@SpringBootTest
public class MaLineSequentBreak {

    @Autowired
    KLineService kLineService;
    @Autowired
    DailyBasicServie dailyBasicServie;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void execute(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    if(item.startsWith("3")){
                        return;
                    }
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,100);
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
}
