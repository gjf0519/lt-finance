package com.lt;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.rules.MaLineArrangeRule;
import com.lt.rules.SquareKLineRule;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.BigDecimalUtil;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description
 * @date 2021/3/18
 */
@Slf4j
@SpringBootTest
public class TrendTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,60);

                    MaLineArrangeRule maLineArrangeRule = new MaLineArrangeRule();
                    int lastIndex = list.size() - 1;
                    maLineArrangeRule.verify(list.get(lastIndex));
                }catch (Exception e){
                    log.info("阳线回补过滤异常:code{},exception:{}",item,e);
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

    public void maLineStatus(List<KLineEntity> list){
        int [] days = new int[]{10,30,60};
        //1上升0平行-1下降
        KLineEntity kLineEntity = list.get(0);
        for(int i = 0;i < days.length;i++){

            double s = list.get(days[i]-1).getMaYear() - kLineEntity.getMaYear();
        }
    }
}
