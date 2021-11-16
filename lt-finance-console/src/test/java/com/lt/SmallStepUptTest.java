package com.lt;

import com.lt.entity.DailyBasicEntity;
import com.lt.entity.KLineEntity;
import com.lt.service.DailyBasicServie;
import com.lt.service.KLineService;
import com.lt.utils.CalculateUtil;
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
 * @description 连续小步上涨形态
 * @date 2021/2/24
 */
@Slf4j
@SpringBootTest
public class SmallStepUptTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    DailyBasicServie dailyBasicServie;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,30);
                    List<DailyBasicEntity> listBasic = dailyBasicServie.queryDailyBasic(item,null,30);
                    //图形计算
                    this.calculation(list,listBasic);
                }catch (Exception e){
//                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
        //规则示例
//        List<KLineEntity> list1 = kLineService.
//                queryDayLineList("600744.SH","20210302",30);
//        List<DailyBasicEntity> listBasic1 = dailyBasicServie.queryDailyBasic("600744.SH","20210302",30);
//        this.calculation(list1,listBasic1);//V型

//        List<KLineEntity> list3 = kLineService.
//                queryDayLineList("002136.SZ","20210602",30);
//        List<DailyBasicEntity> listBasic3 = dailyBasicServie.queryDailyBasic("002136.SZ","20210602",30);
//        this.calculation(list3,listBasic3);
//
//        List<KLineEntity> list4 = kLineService.
//                queryDayLineList("002547.SZ","20210602",30);
//        List<DailyBasicEntity> listBasic4 = dailyBasicServie.queryDailyBasic("002547.SZ","20210602",30);
//        this.calculation(list4,listBasic4);//W型
//
//        List<KLineEntity> list5 = kLineService.
//                queryDayLineList("002679.SZ","20210519",30);
//        List<DailyBasicEntity> listBasic5 = dailyBasicServie.queryDailyBasic("002679.SZ","20210519",30);
//        this.calculation(list5,listBasic5);
//        //待修正
//        List<KLineEntity> list6 = kLineService.
//                queryDayLineList("002622.SZ","20210513",30);
//        List<DailyBasicEntity> listBasic6 = dailyBasicServie.queryDailyBasic("002622.SZ","20210513",30);
//        this.calculation(list6,listBasic6);

//        List<KLineEntity> list1 = kLineService.
//                queryDayLineList("002767.SZ","20210609",30);
//        List<DailyBasicEntity> listBasic1 = dailyBasicServie.queryDailyBasic("002767.SZ","20210609",30);
//        this.calculation(list1,listBasic1);002456 20210623
    }

    private void calculation(List<KLineEntity> list,List<DailyBasicEntity> listBasic){
        if(list.get(0).getPctChg() > 9){
            return;
        }
        //最近3天换手率也必须大于1.5并且小于4
        for(int i = 0;i < 1;i++){
            if(listBasic.get(i).getTurnoverRate() < 1
                    || listBasic.get(i).getTurnoverRate() > 4 ){
                return;
            }
        }

        int riseNum = 0;//红K线次数
        double pctChg = 0;//累计涨幅
        double turnoverRate = 0;//累计涨幅
        int breakNum = 0;//跳出数量 连续两日下跌中断计算
        int count = 0;//循环总次数
        for(int i = 0;i < list.size();i++){
            KLineEntity kLineEntity = list.get(i);
            if(kLineEntity.getPctChg() == 0
                    || kLineEntity.getClose() >= kLineEntity.getOpen()){
                riseNum++;
                pctChg = pctChg + kLineEntity.getPctChg();
                turnoverRate = turnoverRate + listBasic.get(i).getTurnoverRate();
                breakNum = 0;
            }else {
                breakNum++;
                //首个为红K线，总数减一
                count--;
                if(2 == breakNum){
                    break;
                }
            }
            count++;
        }
        //连续小红K线上涨天数
        if(riseNum < 5){
            return;
        }
        //连续上涨过程中换手率不能大于4，或出现次数大于1并且涨幅大于5
        int rateNum = 0;
        for(int i = 0;i < count;i++){
            if(listBasic.get(i).getTurnoverRate() >= 4){
                rateNum++;
                if(rateNum > 1){
                    return;
                }else if(rateNum == 1 && list.get(i).getPctChg() > 4){
                    return;
                }
            }
        }
        //计算红K数量占比，过滤掉一阴一阳的K线
        double riseRate = CalculateUtil.div(riseNum,count,4);
        //计算平均涨幅
        double pctChgVag = CalculateUtil.div(pctChg,riseNum,2);
        //计算平均换手率
        double turnoverRateVag = CalculateUtil.div(turnoverRate,riseNum,2);
        //平均每日换手率大于1.5的过滤掉
        if(turnoverRateVag < 1.5){
            return;
        }
        //平均每日涨幅大于3的过滤掉
        if(riseRate < 0.8 || pctChgVag > 3){
            return;
        }
        //过滤掉阴线跌破3日内涨幅
        if(!fallDrastically(list,count)){
            return;
        };
        //是否有重要K线突破
        KLineEntity entity = list.get(0);
        int maWeight = 0;//突破均线权重
        int breakDay = 0;//突破均线天数
        if(entity.getMaYear() > 0){
            //有年线排列形态
            if(entity.getMaYear() > entity.getMaSemester() &&
                    entity.getMaSemester() > entity.getMaQuarter()){
                //年、半年、季排列状态
                if(entity.getClose() > entity.getMaYear()){
                    //突破年线
                    maWeight = 3;
                    breakDay = this.breakMaLine(list,1,count);
                }else if(entity.getClose() > entity.getMaSemester()){
                    //突破半年线
                    maWeight = 2;
                    breakDay = this.breakMaLine(list,2,count);
                }else if(entity.getClose() > entity.getMaQuarter()){
                    //突破季线
                    maWeight = 1;
                    breakDay = this.breakMaLine(list,3,count);
                }
            }else {
                //季、半年、年排列状态
                if(entity.getClose() > entity.getMaQuarter()){
                    //突破季线
                    maWeight = 3;
                    breakDay = this.breakMaLine(list,3,count);
                }else if(entity.getClose() > entity.getMaSemester()){
                    //突破半年线
                    maWeight = 2;
                    breakDay = this.breakMaLine(list,2,count);
                }else if(entity.getClose() > entity.getMaYear()){
                    //突破年线
                    maWeight = 1;
                    breakDay = this.breakMaLine(list,1,count);
                }
            }
        }else {
            //没有年线排列形态
            if(entity.getMaSemester() > entity.getMaQuarter()){
                //半年、季排列状态
                if(entity.getClose() > entity.getMaSemester()){
                    //突破半年线
                    maWeight = 3;
                    breakDay = this.breakMaLine(list,2,count);
                }else if(entity.getClose() > entity.getMaQuarter()){
                    //突破季线
                    maWeight = 2;
                    breakDay = this.breakMaLine(list,3,count);
                }
            }else {
                //季、半年排列
                if(entity.getClose() > entity.getMaQuarter()){
                    //突破半年线
                    maWeight = 3;
                    breakDay = this.breakMaLine(list,3,count);
                }else if(entity.getClose() > entity.getMaSemester()){
                    //突破季线
                    maWeight = 2;
                    breakDay = this.breakMaLine(list,2,count);
                }
            }
        }
        if(maWeight == 0){
            return;
        }
        //权重小于3，每日平均涨幅大于2的过滤掉
        if(maWeight <= 2 && pctChgVag > 2){
            return;
        }
        //成交量过滤
//        if(!this.maxVolume(list)){
//            return;
//        };
        //过滤掉长上影线
        if(!longShadowLine(list.get(0))){
            return;
        }
        System.out.println(list.get(0).getTsCode()+"==============="+riseRate+"================"+maWeight+"======================="+pctChgVag);
    }

    /**
     *突破均线天数
     * @param list
     * @param type 均线类型
     * @return
     */
    private int breakMaLine(List<KLineEntity> list,int type,int limit){
        int breakDay = 0;
        for(int i = 0;i < limit;i++){
            KLineEntity kLineEntity = list.get(i);
            if(1 == type){
                if(kLineEntity.getClose() > kLineEntity.getMaYear()){
                    breakDay ++;
                }
            }else if(2 == type){
                if(kLineEntity.getClose() > kLineEntity.getMaSemester()){
                    breakDay ++;
                }
            }else if(3 == type){
                if(kLineEntity.getClose() > kLineEntity.getMaQuarter()){
                    breakDay ++;
                }
            }
        }
        return breakDay;
    }

    //过滤点阴线跌破三日以上的涨幅
    public boolean fallDrastically(List<KLineEntity> list,int limit){
        for(int i = 0;i < limit;i++){
            KLineEntity kLineEntity = list.get(i);
            if(kLineEntity.getPctChg() < 0
                    && kLineEntity.getClose() < list.get(i+3).getLow()){
                return false;
            }
        }
        return true;
    }

    //过滤掉长上影线,并且是红K线,并且不是十字星线
    public boolean longShadowLine(KLineEntity entity){
        //影线长度
        double shadow = entity.getHigh() - entity.getClose();
        //实体长度
        double real = entity.getClose() - entity.getOpen();
        if(real > 0 &&
                shadow > real &&
                entity.getPctChg() > 1){
            return false;
        }
        return true;
    }

    //3日内是否出现过，30内上涨最大成交量
    public boolean maxVolume(List<KLineEntity> list){
        for(int i = 0;i < 3;i++){
            double volume = list.get(i).getVol();
            int breakNum = 0;
            for(int y = 0;y < list.size();y++){
                if(y == i){
                    breakNum++;
                    continue;
                }
                if(list.get(y).getPctChg() >=0
                        && volume < list.get(y).getVol()){
                    break;
                }
                breakNum++;
            }
            if(breakNum == list.size()){
                return true;
            }
        }
        return false;
    }
}
