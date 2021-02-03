package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.screen.LineFormFilter;
import com.lt.screen.day.DayLongSunFilter;
import com.lt.service.KLineService;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description
 * @date 2021/1/15
 */
@Slf4j
@SpringBootTest
public class LongSunRuleTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        LineFormFilter lineFormFilter = new DayLongSunFilter();
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,30);
                    int riseNum = lineFormFilter.execute(list);
                    if(riseNum > 0){
                        System.out.println(list.get(0).getTsCode()+"==================================="+riseNum);
                    }
                }catch (Exception e){
                    log.info("长阳过滤异常:code{},exception:{}",item,e);
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        List<KLineEntity> list = kLineService.
//                        queryDayLineList("002707.SZ",null,30);
//        int riseNum = lineFormFilter.execute(list);
//        System.out.println(list.get(0).getTsCode()+"==================================="+riseNum);
    }
}
