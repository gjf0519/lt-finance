package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.rules.KmKlineMaLineRule;
import com.lt.rules.SiteKlineMaLineRule;
import com.lt.screen.LineFormFilter;
import com.lt.screen.day.DayTwitchFilter;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.BigDecimalUtil;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
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
 * @date 2021/2/24
 */
@Slf4j
@SpringBootTest
public class DayTest {
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
                            .queryDayLineList(item,null,5);
                    dayTrTest(list);
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
//                queryDayLineList("603789.SH","20210218",10);
//        dayTrTest(list);
//        List<KLineEntity> list1 = kLineService.
//                queryDayLineList("000591.SZ","20201222",5);
//        dayTrTest(list1);
//        List<KLineEntity> list2 = kLineService.
//                queryDayLineList("002092.SZ","20210113",5);
//        dayTrTest(list2);
//        0.030258148581093925==============================================================================================================0.011737877907772672

//        0.08248905651323957==============================================================================================================0.017029386365926404
//        0.07575545451575678==============================================================================================================0.0051639777949432225
//        0.04306326095924964==============================================================================================================0.010593499054713802
//        0.19189406799933484==============================================================================================================0.022211108331943577

//        0.02387467277262663==============================================================================================================0.005477225575051661
//        0.04494441010848841==============================================================================================================0.013038404810405298
//        0.08689073598491379==============================================================================================================0.004472135954999578
//        0.06978538528947163==============================================================================================================0.02280350850198276
    }

    public void dayTest(List<KLineEntity> list){
        SiteKlineMaLineRule siteKlineMaLineRule = new SiteKlineMaLineRule();
        Map<String,Integer> sites = siteKlineMaLineRule.verify(list.get(0), MaLineType.LINE020);
        if(0 != sites.get(MaLineType.LINE020.getName())){
            return;
        };
        if(list.get(0).getPctChg() <= 0){
            return;
        }
        int riseNum = 1;
        double prevChg = list.get(0).getPctChg();
        double prevPrice = list.get(0).getClose();
        for (int i = 1;i < list.size();i++) {
            KLineEntity kLineEntity = list.get(i);
            if(kLineEntity.getPctChg() > 0){
               riseNum++;
            }
            //5日内突破20日均线的K线涨幅最大
            if(kLineEntity.getPctChg() > prevChg){
                return;
            }
            //5日内最高价
            if(kLineEntity.getHigh() > prevPrice){
                return;
            }
        }
        if(riseNum < 3){
            return;
        }
        System.out.println("==============================================================="+list.get(0).getTsCode());
    }

    /**
     * 底部拐头7连涨
     * 600714 600776 002017 002480
     * @param list
     */
    public void dayMdTest(List<KLineEntity> list){
        if(list.get(0).getMaMonth() > list.get(0).getMaQuarter()){
            return;
        }
        int riseNum = 0;
        for(KLineEntity kLineEntity : list){
            if(kLineEntity.getClose() > kLineEntity.getOpen()){
                riseNum++;
            }
        }
        if(riseNum < 5){
            return;
        }
        System.out.println("==============================================================="+list.get(0).getTsCode());
    }

    /**
     * 底部倍数阳线
     * @param list
     */
    public void dayTrTest(List<KLineEntity> list){
        double [] values = new double[list.size()];
        double [] values2 = new double[list.size()];
        for(int i = 0;i < list.size();i++){
            KLineEntity entity = list.get(i);
            double ratio1 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTen(),2), 1,3);
            double ratio2 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTwenty(),2), 1,3);
            double ratio3 = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaMonth(),2), 1,3);
//            if(ratio1 > 0.03 || ratio1 < -0.03){
//                return;
//            }
//            if(ratio2 > 0.03 || ratio2 < -0.03){
//                return;
//            }
//            if(ratio3 > 0.03 || ratio3 < -0.03){
//                return;
//            }
            values[i] = ratio1;
            values2[i] = entity.getMaFive();
//            System.out.println(ratio1+"===================="+ratio2+"============="+ratio3);600928.SH
        }
        StandardDeviation standardDeviation =new StandardDeviation();
        double result = standardDeviation.evaluate(values);
        double result2 = standardDeviation.evaluate(values2);
//        if(result2 > 0.01){
//            return;
//        }
//        System.out.println(result2+"=============================================================================================================="+result);

        if(result2 > 0.07){
            return;
        }
        if(list.get(0).getMaFive() < list.get(list.size()-1).getMaFive()){
            return;
        }
        int dwNum = 0;
        for(KLineEntity entity : list){
            if(entity.getOpen() > entity.getClose()){
                dwNum++;
            }
        }
        if(dwNum > 2){
            return;
        }
        if(dwNum == 0){
            System.out.println(result2+"=============================================================================================================="+list.get(0).getTsCode());
        }
//        System.out.println(result2+"=============================================================================================================="+list.get(0).getTsCode());
//        System.out.println("==============================================================="+list.get(0).getTsCode());
//        for(KLineEntity entity : list){
//            double ratio2 = BigDecimalUtil.sub(
//                    BigDecimalUtil.div(entity.getMaTen(),entity.getMaTwenty(),2), 1,3);
//            double ratio3 = BigDecimalUtil.sub(
//                    BigDecimalUtil.div(entity.getMaTen(),entity.getMaMonth(),2), 1,3);
////            double ratio4 = BigDecimalUtil.sub(
////                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTen(),2), 1,3);
//            System.out.println(ratio2+"============="+ratio3);
//        }
    }
}
