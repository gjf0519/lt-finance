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
                        dayLineBreakRuleTest("603607.SH","20210113",30);
        rule(list);
//        list = receiveService.
//                dayLineBreakRuleTest("000678.SZ",null,30);
//        rule(list);
//        list = receiveService.
//                dayLineBreakRuleTest("601288.SH",null,30);
//        rule(list);
//        list = receiveService.
//                dayLineBreakRuleTest("600873.SH",null,30);
//        rule(list);
//        list = receiveService.
//                dayLineBreakRuleTest("002263.SZ","20210113",30);
//        rule(list);
    }

    public static void rule(List<KLineEntity> list){
        //凝聚程度
        MaLineCohereRule maLineCohereRule = new MaLineCohereRule();
        int cohere = maLineCohereRule.verify(list);
        //均匀排列
        MaLineArrangeRule maLineArrangeRule = new MaLineArrangeRule();
         int arrange = maLineArrangeRule.verify(list.get(0));
         int arrangeLevel = 0;
        if(arrange == 1){
            arrangeLevel = 2;
        } else if(arrange == 0){
            arrangeLevel = 1;
        }else {
            return;
        }
//        System.out.println("arrange=="+list.get(0).getTsCode()+"=="+arrange);
        //持续性
        KlineContinueRule klineContinueRule = new KlineContinueRule();
        int continueNum5 = klineContinueRule.verify(list,MaLineType.LINE005,5);
        int continueNum10 = klineContinueRule.verify(list,MaLineType.LINE005,10);
//        System.out.println("continueNum=="+list.get(0).getTsCode()+"=="+continueNum);
        if(continueNum10 < 5 && continueNum5 < 3){
            return;
        }
        //K位置
        SiteKlineMaLineRule siteKlineMaLineRule = new SiteKlineMaLineRule();
        Map<String,Integer> sites = siteKlineMaLineRule.verify(list.get(0));
//        System.out.println("sites=="+list.get(0).getTsCode()+"=="+JSON.toJSONString(sites));
        int siteLevel = 0;
        if(sites.get(MaLineType.LINE005.getName()) == -1
            && sites.get(MaLineType.LINE010.getName()) == 0
            && sites.get(MaLineType.LINE020.getName()) == 1
            && sites.get(MaLineType.LINE030.getName()) == 1){
            //重点
            siteLevel = 2;
        }else if(sites.get(MaLineType.LINE020.getName()) == 1
                && sites.get(MaLineType.LINE030.getName()) == 1){
            //普通
            siteLevel = 1;
        }else {
            return;
        }
        //K距离
        KmKlineMaLineRule kmKlineMaLineRule = new KmKlineMaLineRule();
        double km = kmKlineMaLineRule.verify(list.get(0));
        if(km > 0.01 || km < -0.01){
            return;
        }
//        System.out.println("km=="+list.get(0).getTsCode()+"=="+km);
        //5日内回踩或拐头
        DownMaLineRule downMaLineRule = new DownMaLineRule();
        int dw = downMaLineRule.verify(list);//-1破线或连续下跌2次0回踩1拐头
        if(dw == -1 && cohere == 0){
            return;
        }
//        System.out.println("dw=="+list.get(0).getTsCode()+"=="+dw);
        //均线振幅过滤
        LineRoseRule mlineRoseRule = new LineRoseRule(0.08,-0.03);
        int mrose = mlineRoseRule.verify(list,10);
        if(mrose == 0){
            return;
        }
//        System.out.println("mrose=="+list.get(0).getTsCode()+"=="+mrose);
        //K线振幅过滤
        LineRoseRule klineRoseRule = new LineRoseRule(2,5.9,-4.9);
        int krose = klineRoseRule.verify(list,10);
        if(krose == 0){
            return;
        }
//        System.out.println("krose=="+list.get(0).getTsCode()+"=="+krose);
        //振幅小于3大于-3数量
        LineRoseRule roseNums = new LineRoseRule(1,3,-3);
        int nums = roseNums.verify(list,10);
//        System.out.println("nums=="+list.get(0).getTsCode()+"=="+nums);
        if(nums < 6){
            return;
        }
        //重要突破
        System.out.println(list.get(0).getTsCode()+"======================"+arrangeLevel+"==========================="+siteLevel);
    }
}
