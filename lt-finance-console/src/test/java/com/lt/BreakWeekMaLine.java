package com.lt;

import com.lt.common.KAlgorithmUtil;
import com.lt.common.MaAlgorithmUtil;
import com.lt.entity.KLineEntity;
import com.lt.service.DailyBasicServie;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.CalculateUtil;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 断线均线突破
 */
@SpringBootTest
public class BreakWeekMaLine {
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
                    if(item.startsWith("3")){
                        return;
                    }
                    List<KLineEntity> list = kLineService
                            .queryWeekLineList(item,null,100);
                    //图形计算
                    this.calculation(list);
                    //穿起4连阳
                    this.strandReadKline(list);
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
    }

    @Test
    public void test(){
        List<KLineEntity> list = kLineService
                .queryDayLineList("600206.SH","20210719",100);
        this.calculation(list);
    }

    private void calculation(List<KLineEntity> list){
        //5日均线连续下跌天数不能超过3日
        if(list.get(0).getMaFive() < list.get(1).getMaFive() &&
                list.get(1).getMaFive() < list.get(2).getMaFive() &&
                list.get(2).getMaFive() < list.get(3).getMaFive()){
            return;
        }
        //20在30日均线下方
        if(list.get(0).getMaTwenty() < list.get(0).getMaMonth()){
            return;
        }
        //不能连续3日阴线
        double readRateThree = KAlgorithmUtil.readKlineRate(list.subList(0,3));
        if(readRateThree == 0.0){
            return;
        }
        //10日内红K数量大于50%
        double readRate = KAlgorithmUtil.readKlineRate(list.subList(0,10));
        if(readRate < 0.5){
            return;
        }
        //当前K线及5/10/20/30/60均线间距离
        boolean isDistance = this.maDistanceFilter(list.get(0));
        if(!isDistance){
            return;
        }
        //如果30日小于60日，5日内10日线突破60以上的均线
        if(list.get(0).getMaMonth() < list.get(0).getMaQuarter()){
            boolean isTenImportantBreak = this.importantUpMaLineBreak(list.subList(0,5));
            if(!isTenImportantBreak){
                return;
            }
        }
        //如果30/60/120/250正常排列,30/60,60/120上方持续时长,并且无重要均线突破和涨幅一倍以上
        if(list.get(0).getMaMonth() > list.get(0).getMaQuarter() &&
                list.get(0).getMaQuarter() > list.get(0).getMaSemester()){
            boolean isPersistence = this.importantUpPersistenceDay(list);
            if(!isPersistence){
                return;
            }
        }

        //50日内30均线以上没有向下突破
        boolean maBreakDown = this.
                importantDwonMaLineBreak(list.subList(0,50));
        if(!maBreakDown){
            return;
        }

        System.out.println("========================"+list.get(0).getTsCode());
    }

    /**
     * 5日均线连续4日串起小阳线
     * @param list
     */
    public void strandReadKline(List<KLineEntity> list){
        double readRate = KAlgorithmUtil.readKlineRate(list.subList(0,4));
        if(readRate != 1){
            return;
        }
        for(int i = 0;i < 4;i++){
            if(list.get(i).getPctChg() > 3){
                return;
            }
            if(i < 3){
                if(list.get(i).getOpen() > list.get(i+1).getClose()){
                    return;
                }
                if(list.get(i).getClose() < list.get(i+1).getClose()){
                    return;
                }
            }
            if(list.get(i).getMaFive() < list.get(i).getLow() ||
                    list.get(i).getMaFive() > list.get(i).getHigh()){
                return;
            }
        }
        System.out.println("串起4连阳===================================="+list.get(0).getTsCode());
    }

    /**
     * 踩均线小幅上涨3连阳
     * @param list
     */
    public void threeReadKline(List<KLineEntity> list){
        //示例：（600330 20210719）（600206 20210719）
    }

    /**
     * 5日内10日均线突破60日以上的均线
     * @param list
     */
    public boolean importantUpMaLineBreak(List<KLineEntity> list){
        int breakNum1 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE010, MaLineType.LINE060, list);
        int breakNum2 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE010, MaLineType.LINE120, list);
        int breakNum3 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE010, MaLineType.LINE250, list);
        if(breakNum1 > 0 || breakNum2 > 0 || breakNum3 > 0){
            return true;
        }
        return false;
    }

    /**
     * 如果30/60/120/250正常排列,30/60,60/120上方持续时长,并且无重要均线突破和涨幅一倍以上
     * @param list
     * @return
     */
    public boolean importantUpPersistenceDay(List<KLineEntity> list){
        int breakNum1 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE030, MaLineType.LINE060, list);
        int breakNum2 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE060, MaLineType.LINE120, list);
        int index = breakNum1 > breakNum2 ? breakNum2 :breakNum1;
        double up = CalculateUtil.div(
                CalculateUtil.sub(list.get(0).getClose(),list.get(index).getClose()),
                list.get(index).getClose(),2);
        if(breakNum1 >= 50 && breakNum2 >= 50 && up > 1){
            return false;
        }
        return true;
    }

    /**
     * 20日内30均线以上没有向下突破
     * @param list
     */
    public boolean importantDwonMaLineBreak(List<KLineEntity> list){
        int breakNum1 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE030, MaLineType.LINE060, list);
        int breakNum2 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE030, MaLineType.LINE120, list);
        int breakNum3 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE030, MaLineType.LINE250, list);

        int breakNum4 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE060, MaLineType.LINE120, list);
        int breakNum5 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE060, MaLineType.LINE250, list);
        int breakNum6 = MaAlgorithmUtil.
                maBreakDirectionDay(MaLineType.LINE120, MaLineType.LINE250, list);

        if(breakNum1 < 0 || breakNum2 < 0 || breakNum3 < 0 || breakNum4 < 0 || breakNum5 < 0 || breakNum6 < 0){
            return false;
        }
        return true;
    }

    /**
     * 当日K线与均线距离
     * @param kLineEntity
     * @return
     */
    public boolean maDistanceFilter(KLineEntity kLineEntity){
        List<MaLineType> maLineTypes = Arrays.
                asList(MaLineType.LINE005,MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030,MaLineType.LINE060);
        List<Double> distances = MaAlgorithmUtil.maDistance(kLineEntity,maLineTypes);
        if(distances.get(0) < -0.02 || distances.get(0) > 0.01){
            return false;
        }
        if(distances.get(3) < -0.03 || distances.get(3) > 0.1){
            return false;
        }
        for(int i = 1;i < distances.size();i++){
            if(distances.get(i) <= 0.01 && distances.get(i) >= -0.01){
                continue;
            }
            double maVal1 = 0;
            double maVal2 = 0;
            if(i == 1){
                maVal1 = kLineEntity.getMaTen();
                maVal2 = kLineEntity.getMaTwenty();
            }else {
                maVal1 = kLineEntity.getMaTwenty();
                maVal2 = kLineEntity.getMaMonth();
            }
            boolean with1 = MaAlgorithmUtil.klineWithMaLine(maVal1,kLineEntity);
            boolean with2 = MaAlgorithmUtil.klineWithMaLine(maVal2,kLineEntity);
            if(!with1 || !with2){
                return false;
            }
        }
        return true;
    }
}
