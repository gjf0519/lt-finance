package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.rules.GreatBreakRule;
import com.lt.rules.LineRoseRule;
import com.lt.rules.MaLineArrangeRule;
import com.lt.screen.LineFormFilter;
import com.lt.screen.day.DayRiseFormFilter;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description
 * @date 2021/1/15
 */
@SpringBootTest
public class RuleTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        LineFormFilter lineFormFilter = new DayRiseFormFilter();
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
//                    dayGreatRule(list);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
//                        queryDayLineList("002092.SZ","20210114",30);
//        int riseNum = lineFormFilter.execute(list);
//        if(riseNum > 0){
//            System.out.println(list.get(0).getTsCode()+"==================================="+riseNum);
//        }
    }

    @Test
    public void weekbreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryWeekLineList(item,null,30);
                    weekUprule(list);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void weekUprule(List<KLineEntity> list){
        //新股过滤
        if(list.size() < 30){
            return;
        }
        MaLineArrangeRule maLineArrangeRule = new MaLineArrangeRule();
        List<MaLineType> maLineTypes = Arrays.asList(MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);
        if(list.get(0).getMaQuarter() > list.get(0).getMaMonth()){
            int arrange = maLineArrangeRule.verify(list.get(0),maLineTypes);
            if(arrange != 1){
                return;
            }
        }
        System.out.println(list.get(0).getTsCode()+"======================");
    }

    public static void dayGreatRule(List<KLineEntity> list){
        GreatBreakRule greatBreakRule = new GreatBreakRule();
        int breakNum = greatBreakRule.verify(list);
        if(breakNum < 1){
            return;
        }
        LineRoseRule klineRoseRule = new LineRoseRule(2,2.1,-2.1);
        int rose = klineRoseRule.verify(list,5);
        if(rose == 0){
            return;
        }
        System.out.println(list.get(0).getTsCode()+"======================"+breakNum);
    }
}
