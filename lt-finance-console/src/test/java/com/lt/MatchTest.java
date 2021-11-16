package com.lt;

import com.alibaba.fastjson.JSON;
import com.lt.common.MaLineUtil;
import com.lt.entity.KLineEntity;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.CalculateUtil;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description: TODO
 * @date 2021/8/2513:38
 */
@SpringBootTest
public class MatchTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void test(){
//        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
//        for(String item : TsCodes.STOCK_CODE){
//            threadPoolExecutor.execute(()->{
//                try {
//                    if(item.startsWith("3")){
//                        return;
//                    }
//                    int num = 0;
//                    String date = null;
//                    for(int i = 1;i < 100;i++){
//                        String time = sate(i,item);
//                        if(null != time){
//                            num++;
//                            date = time;
//                        }else {
//                            if(num > 4){
//                                System.out.println(item+"============="+date+"==================="+i);
//                                break;
//                            }else {
//                                num = 0;
//                            }
//                        };
//                    }
//                }catch (Exception e){
////                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                }finally {
//                    latch.countDown();
//                }
//            });
//        }
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int num = 0;
//        String date = null;
//        for(int i = 1;i < 100;i++){
//            String time = sate(i,item);
//            if(null != time){
//                num++;
//                date = time;
//            }else {
//                if(num > 4){
//                    System.out.println(item+"============="+date+"==================="+i);
//                    break;
//                }else {
//                    num = 0;
//                }
//            };
//        }
////        System.out.println("=============================================================");
////        int num1 = 0;
////        for(int i = 1;i < 100;i++){
////            sate1(i);
////        }

        for(String item : TsCodes.STOCK_CODE){
            KLineEntity kLineEntity = kLineService.queryMinKline(item);
            List<KLineEntity> list = kLineService.queryDayLineListAsc(kLineEntity.getTsCode(),kLineEntity.getTradeDate());
            System.out.println(list.size()+"======================================================");
        }
    }

    public String sate(int days,String tsCode){
        //平均数大于中位数代表上升形态，众数越多代表偏斜程度越大，例如：众数与对比数据的个数，比值越大偏斜程度越大
        //标准差越大表示数据越分散
        //特点:众数最少有一个相同、平均数大于中位数
        List<KLineEntity> list = kLineService
                .queryDayLineList(tsCode,null,days);
        List<Double> lint005 = MaLineUtil.portraitMaValues(list, MaLineType.LINE005);
        double [] values = new double[lint005.size()];
        for(int i = 0;i < values.length;i++){
            values[i] = lint005.get(i);
        }
        Median median= new Median();
        double medianValue = CalculateUtil.round(median.evaluate(values),2);
//        System.out.println("中位数：" + medianValue);
        //mode--众数
        double[] res = StatUtils.mode(values);
//        System.out.println(JSON.toJSONString(res));
        //mean--算数平均数
        double avgValue = CalculateUtil.round(StatUtils.mean(values),2);
//        System.out.println("平均数：" + avgValue);
        if(medianValue > avgValue){
            return null;
        }
        double geometryAvg = CalculateUtil.round(StatUtils.geometricMean(values),2);
//        System.out.println("几何平均数："+geometryAvg);

//        System.out.println(list.get(0).getTradeDate()+"一组数据的峰度系数：" + new Kurtosis().evaluate(values));
//        System.out.println(list.get(lint005.size()-1).getTradeDate()+"一组数据的偏度系数：" + new Skewness().evaluate(values));

//        StandardDeviation standardDeviation =new StandardDeviation();
//        System.out.println("一组数据的标准差为：" + standardDeviation.evaluate(values));
//        System.out.println(list.get(lint005.size()-1).getTradeDate()+"==="+"中位数:"+medianValue+"===平均数:"+avgValue+"===众数:"+JSON.toJSONString(res)+"===几何平均数："+geometryAvg);
        return list.get(lint005.size()-1).getTradeDate();
    }

    public boolean sate1(int days){
        List<KLineEntity> list = kLineService
                .queryDayLineList("600816.SH",null,days);
        List<Double> lint005 = MaLineUtil.portraitMaValues(list, MaLineType.LINE005);
        double [] values = new double[lint005.size()];
        for(int i = 0;i < values.length;i++){
            values[i] = lint005.get(i);
        }
        Median median= new Median();
        double medianValue = CalculateUtil.round(median.evaluate(values),2);
//        System.out.println("中位数：" + medianValue);
        //mode--众数
        double[] res = StatUtils.mode(values);
//        System.out.println(JSON.toJSONString(res));
        //mean--算数平均数
        double avgValue = CalculateUtil.round(StatUtils.mean(values),2);
//        System.out.println("平均数：" + avgValue);
//        if(medianValue > avgValue){
//            return false;
//        }
        double geometryAvg = CalculateUtil.round(StatUtils.geometricMean(values),2);
//        System.out.println("几何平均数："+geometryAvg);

//        System.out.println(list.get(0).getTradeDate()+"一组数据的峰度系数：" + new Kurtosis().evaluate(values));
        System.out.println("一组数据的偏度系数：" + new Skewness().evaluate(values));

//        StandardDeviation standardDeviation =new StandardDeviation();
//        System.out.println("一组数据的标准差为：" + standardDeviation.evaluate(values));
//        System.out.println(list.get(lint005.size()-1).getTradeDate()+"==="+"中位数:"+medianValue+"===平均数:"+avgValue+"===众数:"+JSON.toJSONString(res)+"===几何平均数："+geometryAvg);
        return true;
    }
}
