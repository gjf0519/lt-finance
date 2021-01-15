package com.lt;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.rules.*;
import com.lt.service.ReceiveService;
import com.lt.shape.MaLineType;
import com.lt.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
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
    ReceiveService receiveService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
//        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
//        for(String item : Constants.STOCK_CODE){
//            threadPoolExecutor.execute(()->{
//                String flag = item.substring(0,2);
//                String code = item.substring(2,item.length());
//                List<KLineEntity> list = receiveService.
//                        dayLineBreakRuleTest(code+"."+flag.toUpperCase(),null,30);
//                rule(list);
//                latch.countDown();
//            });
//        }
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<KLineEntity> list = receiveService.
                        dayLineBreakRuleTest("002040.SZ",null,30);
        rule(list);
        list = receiveService.
                dayLineBreakRuleTest("002263.SZ","20210113",30);
        rule(list);
////        receiveService.dayLineBreak("603239.SH");
//        receiveService.dayLineBreak("000687.SZ");//1.93-1.71 下降小于20
////        receiveService.dayLineBreak("601016.SH","20201222");
//        //半年突破年但5 10 20 30 都在下方
////        receiveService.dayLineBreak("600644.SH","20201222");
//
////        receiveService.dayLineBreak("600292.SH","20201218");
//
////        receiveService.dayLineBreak("002529.SZ","20201216");
//        receiveService.dayLineBreak("000816.SZ","20201022");
////        receiveService.dayLineBreak("000816.SZ","20200703");
////        receiveService.dayLineBreak("002342.SZ","20201113");
//
//        //丰乐
//        receiveService.dayLineBreak("000713.SZ","20201217");
////        receiveService.dayLineBreak("000713.SZ","20201209");
//        receiveService.dayLineBreak("600189.SH","20201106");

        //急速下跌
//        receiveService.dayLineBreak("688299.SH","20210108");
    }

    public static void rule(List<KLineEntity> list){
        //均匀排列
        MaLineArrangeRule maLineArrangeRule = new MaLineArrangeRule();
         int arrange = maLineArrangeRule.verify(list.get(0));
        System.out.println("arrange=="+list.get(0).getTsCode()+"=="+arrange);
        //持续性
        KlineContinueRule klineContinueRule = new KlineContinueRule();
        int continueNum = klineContinueRule.verify(list,MaLineType.LINE005,10);
        System.out.println("continueNum=="+list.get(0).getTsCode()+"=="+continueNum);
        //K位置
        SiteKlineMaLineRule siteKlineMaLineRule = new SiteKlineMaLineRule();
        Map<String,Integer> sites = siteKlineMaLineRule.verify(list.get(0));
        System.out.println("sites=="+list.get(0).getTsCode()+"=="+JSON.toJSONString(sites));
        //K距离
        KmKlineMaLineRule kmKlineMaLineRule = new KmKlineMaLineRule();
        double km = kmKlineMaLineRule.verify(list.get(0));
        System.out.println("km=="+list.get(0).getTsCode()+"=="+km);
        //5日内回踩或拐头
        DownMaLineRule downMaLineRule = new DownMaLineRule();
        int dw = downMaLineRule.verify(list);
        System.out.println("dw=="+list.get(0).getTsCode()+"=="+dw);
        //均线振幅过滤
        LineRoseRule mlineRoseRule = new LineRoseRule(0.08,-0.03);
        int mrose = mlineRoseRule.verify(list,10);
        System.out.println("mrose=="+list.get(0).getTsCode()+"=="+mrose);
        //K线振幅过滤
        LineRoseRule klineRoseRule = new LineRoseRule(2,5.9,-4.9);
        int krose = klineRoseRule.verify(list,10);
        System.out.println("krose=="+list.get(0).getTsCode()+"=="+krose);
        //振幅小于3大于-3数量
        LineRoseRule roseNums = new LineRoseRule(1,3,-3);
        int nums = roseNums.verify(list,10);
        System.out.println("nums=="+list.get(0).getTsCode()+"=="+nums);
        //重要突破
    }
}
