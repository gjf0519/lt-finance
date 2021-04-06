package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.service.KLineService;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description
 * @date 2021/2/24
 */
@Slf4j
@SpringBootTest
public class LowTest {
    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private final Map<String,Integer> BREAK_MAP = new ConcurrentHashMap<>();

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,3);
                    int day = 0;
                    int last = 0;
                    for(int i = 0;i < list.size();i++){
                        KLineEntity kLineEntity = kLineService.queryLowEntity(list.get(i));
                        if(null == kLineEntity){
                            return;
                        }
                        Date beginDate = TimeUtil.StringToDate(kLineEntity.getTradeDate(),"yyyyMMdd");
                        Date endDate = TimeUtil.StringToDate(list.get(i).getTradeDate(),"yyyyMMdd");
                        int t = TimeUtil.getDiffDays(beginDate,endDate);
                        if(t > day){
                            last = i;
                            day = t;
                        }
                    }
                    if(last > 0){
                        if(list.get(0).getOpen() > list.get(0).getClose()){
                            return;
                        }
                        if(list.get(1).getOpen() > list.get(1).getClose()){
                            return;
                        }
                    }else {
                        if(list.get(0).getOpen() > list.get(0).getClose()){
                            return;
                        }
                    }

                    BREAK_MAP.put(list.get(0).getTsCode()+last,day);
//                    System.out.println(kLineEntity.getTsCode()+"=================="+kLineEntity.getTradeDate()+"==================="+day);
                }catch (Exception e){
                    log.info("长阳过滤异常:code{},exception:{}",item,e);
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
        Map<String,Integer> map = DayTest.sortMap(BREAK_MAP);
        for (Map.Entry<String,Integer> entry: map.entrySet()) {
            System.out.println(entry.getKey()+"================="+entry.getValue());
        }
    }

    public void dayTest(List<KLineEntity> list){
        System.out.println("==============================================================="+list.get(0).getTsCode());
    }
}