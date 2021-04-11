package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.entity.RuleFilterEntity;
import com.lt.rules.GreatBreakRule;
import com.lt.rules.LineRoseRule;
import com.lt.rules.MaLineArrangeRule;
import com.lt.screen.LineFormFilter;
import com.lt.screen.day.DayRiseFormFilter;
import com.lt.service.KLineService;
import com.lt.service.RuleFilterService;
import com.lt.shape.MaLineType;
import com.lt.utils.BigDecimalUtil;
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
    RuleFilterService ruleFilterService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    List<String> trades = Arrays.asList("20201102",
            "20201103",
            "20201104",
            "20201105",
            "20201106",
            "20201109",
            "20201110",
            "20201111",
            "20201112",
            "20201113",
            "20201116",
            "20201117",
            "20201118",
            "20201119",
            "20201120",
            "20201123",
            "20201124",
            "20201125",
            "20201126",
            "20201127",
            "20201130",
            "20201201",
            "20201202",
            "20201203",
            "20201204",
            "20201207",
            "20201208",
            "20201209",
            "20201210",
            "20201211",
            "20201214",
            "20201215",
            "20201216",
            "20201217",
            "20201218",
            "20201221",
            "20201222",
            "20201223",
            "20201224",
            "20201225",
            "20201228",
            "20201229",
            "20201230",
            "20201231",
            "20210104",
            "20210105",
            "20210106",
            "20210107",
            "20210108",
            "20210111",
            "20210112",
            "20210113",
            "20210114",
            "20210115",
            "20210118",
            "20210119",
            "20210120",
            "20210121",
            "20210122",
            "20210125",
            "20210126",
            "20210127",
            "20210128",
            "20210129",
            "20210201",
            "20210202",
            "20210203",
            "20210204",
            "20210205",
            "20210208",
            "20210209",
            "20210210",
            "20210218",
            "20210219");

    @Test
    public void daybreak(){
//        for(String trade : trades){
//            dayExecute(trade);
//            System.out.println("==========================================="+trade);
//        }
        dayExecute(null);
    }

    public void dayExecute(String tradeDate){
        LineFormFilter lineFormFilter = new DayRiseFormFilter();
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,tradeDate,30);
                    int riseNum = lineFormFilter.execute(list);
                    if(riseNum < 1){
                        return;
                    }
                    int f = maLineFive(list);
                    int t = maLineTen(list);
                    if(riseNum == 1 && t == 0 && f == 0){
                        return;
                    }
                    if(riseNum != 4){
                        return;
                    }
                    System.out.println(list.get(0).getTsCode()+"==================================="+riseNum);
//                    RuleFilterEntity ruleFilterEntity = RuleFilterEntity.builder()
//                            .tsCode(list.get(0).getTsCode())
//                            .tradeDate(list.get(0).getTradeDate())
//                            .pctChg(list.get(0).getPctChg())
//                            .ruleName("小步上涨").build();
//                    ruleFilterService.insertRuleFilter(ruleFilterEntity);
                }catch (Exception e){
                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
//        List<KLineEntity> list = kLineService.
//                        queryDayLineList("000793.SZ","20210406",30);
//        int riseNum = lineFormFilter.execute(list);
////        if(riseNum < 1){
////            return;
////        }
////        int f = maLineFive(list);
////        int t = maLineTen(list);
////        if(riseNum == 1 && t == 0 && f == 0){
////            return;
////        }
//        System.out.println(list.get(0).getTsCode()+"==================================="+riseNum);
    }

    public int maLineFive(List<KLineEntity> list){
        for(int i = 0;i < 5;i++){
            KLineEntity entity = list.get(i);
            double ratio1 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTen(),2), 1,3);
            double ratio2 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTwenty(),2), 1,3);
            double ratio3 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaMonth(),2), 1,3);
            if(ratio1 > 0.01 || ratio1 < -0.01){
                return 0;
            }
            if(ratio2 > 0.01 || ratio2 < -0.01){
                return 0;
            }
            if(ratio3 > 0.01 || ratio3 < -0.01){
                return 0;
            }
        }
        return 1;
    }

    public int maLineTen(List<KLineEntity> list){
        for(int i = 0;i < 10;i++){
            KLineEntity entity = list.get(i);
            double ratio1 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTen(),2), 1,3);
            double ratio2 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTwenty(),2), 1,3);
            double ratio3 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaMonth(),2), 1,3);
            if(ratio1 > 0.02 || ratio1 < -0.02){
                return 0;
            }
            if(ratio2 > 0.02 || ratio2 < -0.02){
                return 0;
            }
            if(ratio3 > 0.02 || ratio3 < -0.02){
                return 0;
            }
        }
        return 1;
    }

    @Test
    public void updateRuleFilter(){
        for(String trade : trades){
            List<String> codes = ruleFilterService.queryByTradeDate(trade);
            for(String code : codes){
                List<KLineEntity> list = kLineService
                        .queryDayLineListAsc(code,trade,4);
                if(list.size() < 4){
                    return;
                }
                double price = list.get(0).getPctChg() > 0 ? list.get(0).getClose()
                        : list.get(0).getOpen();
                if(list.get(1).getClose() > price){
                    ruleFilterService.updateNextBreak(code,
                            list.get(0).getTradeDate(),
                            list.get(3).getTradeDate(),1);
                }else {
                    ruleFilterService.updateNextBreak(code,
                            list.get(0).getTradeDate(),
                            list.get(3).getTradeDate(),0);
                }
            }
        }
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
