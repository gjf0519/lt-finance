package com.lt;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.rules.SquareKLineRule;
import com.lt.screen.LineFormFilter;
import com.lt.screen.day.DayLongSunFilter;
import com.lt.service.KLineService;
import com.lt.utils.BigDecimalUtil;
import com.lt.utils.TsCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gaijf
 * @description
 * @date 2021/3/17
 */
@Slf4j
@SpringBootTest
public class SquareRuleTest {
    @Autowired
    KLineService kLineService;
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

    private Map<String,Double> mapAll = new ConcurrentHashMap<>();

    private AtomicInteger  atomicIntegerAll = new AtomicInteger(0);
    private AtomicInteger  atomicIntegerRise = new AtomicInteger(0);
//    private AtomicInteger  atomicIntegerDow1 = new AtomicInteger(0);
//    private AtomicInteger  atomicIntegerDow2 = new AtomicInteger(0);
//    private AtomicInteger  atomicIntegerDow3 = new AtomicInteger(0);
//    private AtomicInteger  atomicIntegerDow4 = new AtomicInteger(0);

    private List<Double> riseList = new Vector<>();

    @Test
    public void daybreak(){
        for(int i = 0;i < (trades.size()-1);i++){
            dayExecute(trades.get(i),trades.get(i+1));
            double ratio = BigDecimalUtil.div(atomicIntegerRise.get(),atomicIntegerAll.get(),2);
            mapAll.put(trades.get(i),ratio);
            atomicIntegerAll.set(0);
            atomicIntegerRise.set(0);
            System.out.println(ratio+"==========================================="+trades.get(i));
        }
        System.out.println(JSON.toJSONString(mapAll));
        System.out.println(JSON.toJSONString(riseList));
        double[] riseArray = new double[riseList.size()];
        for(int i = 0;i < riseList.size();i++){
            riseArray[i] = riseList.get(i);
        }
        double[] res = StatUtils.mode(riseArray);
        System.out.println(JSON.toJSONString(res));

//        dayExecute("20201102","20201103");
    }

    public void dayExecute(String tradeDate,String nextTradeDate){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        SquareKLineRule  squareKLineRule = new SquareKLineRule();
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,tradeDate,30);
                    if(list.size() < 3){
                        return;
                    }
                    int riseNum = squareKLineRule.verify(list);
                    if(riseNum < 0){
                        return;
                    }
                    atomicIntegerAll.incrementAndGet();
                    List<KLineEntity> listNext = kLineService.queryDayLineList(list.get(0).getTsCode(),nextTradeDate,1);
                    if(listNext.get(0).getPctChg() < 0){
                        return;
                    }
                    atomicIntegerRise.incrementAndGet();
                    double pctChg = 0;
                    if(riseNum == 1){
                        pctChg = list.get(1).getPctChg()*-1;
                    }
                    if(riseNum == 2){
                        pctChg = list.get(2).getPctChg()*-1;
                    }
                    double ratioPct = BigDecimalUtil.div(pctChg,list.get(0).getPctChg(),2);
                    if(ratioPct == 1.0){
                        System.out.println(list.get(0).getTsCode()+"======================================="+nextTradeDate);
                    }
                    riseList.add(ratioPct);
//                    if(ratioPct < 1.2){
//                        atomicIntegerDow1.incrementAndGet();
//                    }else if(ratioPct < 1.5){
//                        atomicIntegerDow2.incrementAndGet();
//                    }else if(ratioPct < 1.8){
//                        atomicIntegerDow3.incrementAndGet();
//                    }else {
//                        atomicIntegerDow4.incrementAndGet();
//                    }
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

    //1、K线涨幅比例
    //根据前一日下跌比例  区分涨幅比例的权重 分为-3、-5、-9
    //2、均线排列状态
    //在全部均线下方、交叉、在全部均线上方
    //3、趋势向下：10日内 ma5与ma10或ma20有过交叉

}