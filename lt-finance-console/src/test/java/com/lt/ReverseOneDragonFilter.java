package com.lt;

import com.alibaba.fastjson.JSON;
import com.lt.common.KAlgorithmUtil;
import com.lt.common.MaAlgorithmUtil;
import com.lt.entity.KLineEntity;
import com.lt.service.DailyBasicServie;
import com.lt.service.KLineService;
import com.lt.shape.MaLineType;
import com.lt.utils.CalculateUtil;
import com.lt.utils.TsCodes;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 反向突破1条龙
 *
 */
@SpringBootTest
public class ReverseOneDragonFilter {
    @Autowired
    KLineService kLineService;
    @Autowired
    DailyBasicServie dailyBasicServie;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreakAll(){
        for(String date : DateUtil.tradeDates){
            daybreak(date);
        }
    }

    @Test
    public void daybreakNow(){
        daybreak(null);
    }

    public void daybreak(String tradeDate){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    if(item.startsWith("3")){
                        return;
                    }
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,tradeDate,100);
                    //图形计算
                    this.calculation1(list);
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
        List<KLineEntity> listx = kLineService
                .queryDayLineList("002229.SZ","20210810",100);
        this.calculation1(listx);

        //波峰
//        List<KLineEntity> list3 = kLineService
//                .queryDayLineList("002592.SZ","20210413",100);
//        this.calculation1(list3);
//////        this.straightLine(list3);
//////        List<KLineEntity> list = kLineService
//////                .queryDayLineList("002225.SZ","20210730",10);
//////        this.straightLine(list);
//////        //2.79===========2.79===========2.79==============2.79
//////        List<KLineEntity> list0 = kLineService
//////                .queryDayLineList("601106.SH","20210628",10);//37
//////        this.straightLine(list0);
//////        //2.8449999999999998===========2.8485===========2.834==============2.81875
//////        //2.8388235294117647===========2.8438235294117646===========2.843235294117647==============2.834705882352941
//////        //2.8320689655172413===========2.8372413793103446===========2.8372413793103446==============2.8393103448275863
//        List<KLineEntity> list1 = kLineService
//                .queryDayLineList("002128.SZ","20200630",100);//40
//        this.calculation1(list1);
////        //2.6191666666666666===========2.6208333333333336===========2.6370833333333334==============2.65875
////        //2.6185===========2.619===========2.624==============2.6430000000000002
////        //2.6153846153846154===========2.6130769230769233===========2.616923076923077==============2.620769230769231
////        //直线
//        List<KLineEntity> list2 = kLineService
//                .queryDayLineList("601106.SH","20210625",100);//24 "20200423" "20200701"
//        this.calculation1(list2);
//        List<KLineEntity> list21 = kLineService
//                .queryDayLineList("601106.SH","20200701",100);//24 "20200423" "20200701"
//        this.calculation1(list21);
//        List<KLineEntity> list22 = kLineService
//                .queryDayLineList("601106.SH","20200423",100);//24 "20200423" "20200701"
//        this.calculation1(list22);
    }

    private void calculation1(List<KLineEntity> list){
        //50天内均线凝聚程度
        int brNum = this.straightLine(list.subList(0,50));
        if(0 == brNum){
            return;
        }
        //100天内均匀向上或向下突破
        MaLineType passiveLine = this.uniformBreakthrough(list);
        if(null == passiveLine){
            return;
        }

        //10日内出现新低
        int lowIndex = this.lowIntervalDay(list.size(),list);
        if(lowIndex < 10){
            return;
        }
        //特殊形态过滤==================================================
        //长阳后连续下跌，长阳十日内最长，并且最低点开始长度不能小于前三，"601106.SH""20210625" 或 "002128.SZ","20200630"
        boolean isLongRed = this.longRedKline(list);
        if(isLongRed){
            System.out.println(passiveLine+"******************************************"+list.get(0).getTsCode());
        }
        //均线连续均匀突破60日以上均线，在待突破均线,下一级均线突破后，下一级均线以下均线持续下跌，K线下跌至已突破均线位置，并且均匀向下排列，"002592.SZ" "20210412"
        boolean iSuniformityDown = this.suniformityDown(passiveLine,list);
        if(iSuniformityDown){
            System.out.println(passiveLine+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+list.get(0).getTsCode());
        }
    }

    private boolean suniformityDown(MaLineType passiveLine,List<KLineEntity> list){
        double maValue = MaAlgorithmUtil.portraitMaValue(list.get(0),passiveLine);
        double km = CalculateUtil.sub(CalculateUtil.div(list.get(0).getLow(),maValue,2),1,2);
        if(km > 0.02 || km < -0.01){
            return false;
        }
        if(list.get(0).getMaFive() > list.get(0).getMaTen() ||
            list.get(0).getMaTen() > list.get(0).getMaTwenty() ||
            list.get(0).getMaTwenty() > list.get(0).getMaMonth()){
            return false;
        }
        return true;
    };

    private boolean longRedKline(List<KLineEntity> list){
        List<MaLineType> types = Arrays.asList(MaLineType.LINE005,
                MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);
        int num = this.klineTooMaNum(types,list);
        int maxIndex = 0;
        double maxChg = 0;
        for(int i = 0;i < 10;i++){
            if(list.get(i).getPctChg() > maxChg){
                maxChg = list.get(i).getPctChg();
                maxIndex = i;
            }
        }
        if(maxIndex < 3){
            return false;
        }
        int bigNum = 0;
        for(int i = 0;i < num;i++){
            if(list.get(i).getPctChg() > maxChg){
                bigNum++;
            }
        }
        if(bigNum > 2){
            return false;
        }
        return true;
    }

    /**
     * 均线均匀突破,并且突破均线的上级均线不能有交叉，并且突破后10日以上均线一直在上方或下方
     * @param list
     * @return
     */
    private MaLineType uniformBreakthrough(List<KLineEntity> list){
        List<MaLineType> passives = Arrays.asList(MaLineType.LINE250,
                MaLineType.LINE120,MaLineType.LINE060);
        List<MaLineType> actives = Arrays.asList(MaLineType.LINE060,
                MaLineType.LINE030,MaLineType.LINE020,MaLineType.LINE010,MaLineType.LINE005);
        MaLineType passiveUp = this.uniformBreakthroughUp(passives,actives,list);
        MaLineType passiveDown = this.uniformBreakthroughDown(passives,actives,list);
        boolean isAlway = false;
        if(null != passiveUp && null != passiveDown){
            if(passiveUp.getCode() > passiveDown.getCode()){
                isAlway = this.alwaysUpOrDown(true,passiveUp,list);
                if(!isAlway){
                    return null;
                }
                return passiveUp;
            }else {
                isAlway = this.alwaysUpOrDown(false,passiveDown,list);
                if(!isAlway){
                    return null;
                }
                return passiveDown;
            }
        }else if(null != passiveDown){
            isAlway = this.alwaysUpOrDown(false,passiveDown,list);
            if(!isAlway){
                return null;
            }
            return passiveDown;
        }else if(null != passiveUp){
            isAlway = this.alwaysUpOrDown(true,passiveUp,list);
            if(!isAlway){
                return null;
            }
            return passiveUp;
        }
        return null;
    }

    private boolean alwaysUpOrDown(boolean isUp,MaLineType maLineType,List<KLineEntity> list){
        List<MaLineType> types = Arrays.asList(MaLineType.LINE005,
                MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);
        //均线凝聚程度
        int num = this.klineTooMaNum(types,list);

        List<MaLineType> maLineTypes = Arrays.asList(MaLineType.LINE250,MaLineType.LINE120,MaLineType.LINE060,
                MaLineType.LINE030,MaLineType.LINE020,MaLineType.LINE010);

        if(maLineType.getCode() == 120){
            List<Integer> vals = MaAlgorithmUtil.lineUpOtherLine(MaLineType.LINE120,MaLineType.LINE250,list.subList(0,num));
            for(int i = 1;i < vals.size();i++){
                if(vals.get(0) != vals.get(i)){
                    return false;
                }
            }
        }
        if(maLineType.getCode() == 60){
            List<Integer> vals1 = MaAlgorithmUtil.lineUpOtherLine(MaLineType.LINE120,MaLineType.LINE250,list.subList(0,num));
            for(int i = 1;i < vals1.size();i++){
                if(vals1.get(0) != vals1.get(i)){
                    return false;
                }
            }
            List<Integer> vals2 = MaAlgorithmUtil.lineUpOtherLine(MaLineType.LINE060,MaLineType.LINE120,list.subList(0,num));
            for(int i = 1;i < vals2.size();i++){
                if(vals2.get(0) != vals2.get(i)){
                    return false;
                }
            }
        }
        if(isUp){
            for(int i = 0;i < num;i++){
                for(MaLineType type : maLineTypes){
                    if(type.getCode() < maLineType.getCode()){
                        List<Integer> vals = MaAlgorithmUtil.lineUpOtherLine(type,maLineType,list.subList(0,num));
                        int n = 0;
                        for(int y = 0;y < vals.size()-1;y++){
                            if(vals.get(y) < vals.get(y+1)){
                                n++;
                            }else {
                                n = 0;
                            }
                            if(n >= 2){
                                return false;
                            }
                        }
                    }
                }
            }
        }else {
            for(int i = 0;i < num;i++){
                for(MaLineType type : maLineTypes){
                    if(type.getCode() < maLineType.getCode()){
                        List<Integer> vals = MaAlgorithmUtil.lineUpOtherLine(type,maLineType,list.subList(0,num));
                        int n = 0;
                        for(int y = 0;y < vals.size()-1;y++){
                            if(vals.get(y) > vals.get(y+1)){
                                n++;
                            }else {
                                n = 0;
                            }
                            if(n >= 2){
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 均匀向下突破
     * @param passives
     * @param actives
     * @param list
     * @return
     */
    private MaLineType uniformBreakthroughDown(List<MaLineType> passives,
                                          List<MaLineType> actives,
                                          List<KLineEntity> list){
        for(MaLineType passive : passives){
            int breakIndex = 0;
            List<Integer> breakNums = new ArrayList<>();
            int breakLine = 0;
            for(MaLineType active : actives){
                if(passive == active){
                    continue;
                }
                List<Integer> breaks = MaAlgorithmUtil.lineUpOtherLine(active, passive, list.subList(breakIndex,list.size()));
                if(breaks == null || breaks.isEmpty()){
                    continue;
                }
                breakLine++;
                for(int i = 1;i < breaks.size();i++){
                    //代表一直在上方
                    if(breaks.get(0) == 1){
                        break;
                    }
                    if(breaks.get(i) > 0){
                        breakIndex = breakIndex+i;
                        breakNums.add(breakIndex);
                        breakIndex = breakIndex < 2 ? breakIndex : breakIndex-2;
                        break;
                    }
                }
            }
            if(breakNums.isEmpty()){
                continue;
            }
            int size = breakNums.size()-1;
            int breakSum = 1;
            for(int i = 0;i < size;i++){
                //排除误差，"002592.SZ" "20210412"
                int val1 = breakNums.get(i);
                int val2 = breakNums.get(i+1);
                //排除误差，"002592.SZ" "20210412"
                if(i >= (size-2)){
                    if((val1-2) < val2){
                        breakSum++;
                    }
                }else if(val1 < val2){
                    breakSum++;
                }
            }
            if(breakSum == breakLine){
//                System.out.println(JSON.toJSONString(breakNums)+"----------------------------------"+passive);
                return passive;
            }
        }
        return null;
    }

    /**
     * 均匀向上突破
     * @param passives
     * @param actives
     * @param list
     * @return
     */
    private MaLineType uniformBreakthroughUp(List<MaLineType> passives,
                                          List<MaLineType> actives,
                                          List<KLineEntity> list){
        for(MaLineType passive : passives){
            int breakIndex = 0;
            List<Integer> breakNums = new ArrayList<>();
            int breakLine = 0;
            for(MaLineType active : actives){
                if(passive == active){
                    continue;
                }
                List<Integer> breaks = MaAlgorithmUtil.lineUpOtherLine(active, passive, list.subList(breakIndex,list.size()));
                if(breaks == null || breaks.isEmpty()){
                    continue;
                }
                breakLine++;
                for(int i = 1;i < breaks.size();i++){
                    //代表一直在下方
                    if(breaks.get(0) == -1){
                        break;
                    }
                    if(breaks.get(i) < 0){
                        breakIndex = breakIndex+i;
                        breakNums.add(breakIndex);
                        breakIndex = breakIndex < 2 ? breakIndex : breakIndex-2;
                        break;
                    }
                }
            }
            if(breakNums.isEmpty()){
                continue;
            }
            int size = breakNums.size()-1;
            int breakSum = 1;
            for(int i = 0;i < size;i++){
                int val1 = breakNums.get(i);
                int val2 = breakNums.get(i+1);
                //排除误差，"002592.SZ" "20210412"
                if(i >= (size-2)){
                    if((val1-2) < val2){
                        breakSum++;
                    }
                }else if(val1 < val2){
                    breakSum++;
                }
            }
            if(breakSum == breakLine){
//                System.out.println(JSON.toJSONString(breakNums)+"++++++++++++++++++++++++++++++++++"+passive);
                return passive;
            }
        }
        return null;
    }

    private int straightLine(List<KLineEntity> list){
        List<MaLineType> types = Arrays.asList(MaLineType.LINE005,
                MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);
        //均线凝聚程度
        int num = this.klineTooMaNum(types,list);
        if(num < 20){
            return 0;
        }
        //20,30日均线趋势
        boolean trendDown = this.maLineTrend(list.subList(0,num+1));
        if(!trendDown){
            return 0;
        }
        //均线间距
        for(int i = num;i > 0;i--){
            List<Double> avgs = this.maLineAvgAny(types,list.subList(0,i));
            List<Double> changes = this.avgChange(types.size(),avgs);
            int chgNum = 0;
            int distanceNum = 0;
            for(Double item : changes){
                if(item >= 0.02){
                    return 0;
                }
                if(item > 0.01 || item < -0.01){
                    chgNum++;
                }
                if(chgNum > 2){
                    distanceNum++;
                }else {
                    distanceNum = 0;
                }
                if(distanceNum >= 2){
                    return 0;
                }
            }
        }
        return num;
    }

    /**
     * 过滤均线凝聚程度
     * @param list
     * @return
     */
    public int klineTooMaNum(List<MaLineType> types,List<KLineEntity> list){
        List<Integer> nums = new ArrayList<>();
        //连续几日只穿过一条K线
        int continuityNum = 0;
        int index = 0;
        for (int i = 0;i < list.size();i++){
            KLineEntity entity = list.get(i);
            int tooNum = 0;
            for(MaLineType type : types){
                double maVal = MaAlgorithmUtil.portraitMaValue(entity,type);
                if(entity.getHigh() >= maVal && entity.getLow() <= maVal){
                    tooNum++;
                }
            }
            //过滤出特殊情况 601106 20200427
            if(list.get(i).getPctChg() > 5.5 || list.get(i).getPctChg() < -5.5){
                break;
            }
            //过滤出特殊情况  601106 20210430
            if(nums.size() > 5){
                int numSize = nums.size() - 1;
                boolean numVal = false;
                if(nums.get(numSize) <= 1 &&
                        nums.get(numSize-1) <= 2 &&
                        nums.get(numSize-2) <= 1){
                    numVal = true;
                }
                boolean sort = false;
                if(entity.getMaFive() < entity.getMaTen() &&
                        entity.getMaTen() < entity.getMaTwenty() &&
                        entity.getMaTwenty() < entity.getMaMonth()){
                    sort = true;
                }
                if(sort && numVal){
                    break;
                }
            }
            if(tooNum <= 1 && i > 10){
                continuityNum++;
            }
            if (3 == continuityNum){
                break;
            }if(tooNum > 1 && continuityNum != 0) {
                continuityNum = 0;
            }
            index = i;
            nums.add(tooNum);
        }
        return index - 3;
    }

    /**
     * 20,30日均线趋势
     * @param list
     * @return
     */
    private boolean maLineTrend(List<KLineEntity> list){
        List<MaLineType> types = Arrays.asList(MaLineType.LINE020,MaLineType.LINE030);
        int trendNum = 0;
        int chgNum = 0;
        for(MaLineType type : types){
            double avgFirst = this.maLineAvgOne(type,list);
            double avgLast = this.maLineAvgOne(type,list.subList(0,1));
            double avgChg = CalculateUtil.div(avgFirst,avgLast,2);
            if(avgFirst > avgLast){
                trendNum++;
            }
            if(avgChg > 0.02){
                chgNum++;
            }
        }
        //过滤特殊情况，均线均匀向下突破后直线横盘,并且20,30日均线下跌不能低于0.02 "601106.SH" "20200423"
        int size = list.size() - 1;
        boolean flag = false;
        for(int i = size;i > size - 4;i--){
            if(list.get(i).getMaFive() < list.get(i).getMaTen() &&
                    list.get(i).getMaTen() < list.get(i).getMaTwenty() &&
                    list.get(i).getMaTwenty() < list.get(i).getMaMonth()){
                flag = true;
                break;
            }
        }
        //判断是否全部趋势向下
        if(trendNum == types.size()){
            if(!flag && chgNum > 0){
                return false;
            }
        }
        return true;
    }

    /**
     * 多条均线某段时间内的均值
     * @param types
     * @param list
     * @return
     */
    private List<Double> maLineAvgAny(List<MaLineType> types,List<KLineEntity> list){
        List<Double> avgs = new ArrayList<>(types.size());
        for(MaLineType type : types){
            double avg = this.maLineAvgOne(type,list);
            avgs.add(avg);
        }
        return avgs;
    }

    /**
     * 获取一段时间内均线的均值
     * @param lineType
     * @param list
     * @return
     */
    private double maLineAvgOne(MaLineType lineType,List<KLineEntity> list){
        List<Double> values = MaAlgorithmUtil.portraitMaValues(list,lineType);
        double [] mas = new double[values.size()];
        for(int i = 0;i < values.size();i++){
            mas[i] = values.get(i);
        }
        return CalculateUtil.round(StatUtils.mean(mas),2);
    }

    /**
     * 计算每日均线间间振幅
     * @param size
     * @param list
     * @return
     */
    private List<Double> avgChange(int size,List<Double> list){
        List<Double> result = new ArrayList<>(size);
        double avgSum = 0;
        for(double item : list){
            avgSum = avgSum + item;
        }
        double avg = CalculateUtil.div(avgSum,size,2);
        for(double item : list){
            double chg = CalculateUtil.sub(1,CalculateUtil.div(item,avg,3),3);
            result.add(chg);
        }
        return result;
    }

    private void calculation(List<KLineEntity> list){
        //============================均线接近于直线算法======================
        //基础特征1.1：距离60天内最低点距离
        int lowIndex60 = this.lowIntervalDay(60,list);
        //排除误差，有的30天内均线接近直线，但这种情况下不会连续涨停
        //基础特征1.2：距离30天内最低点距离
        int lowIndex30 = this.lowIntervalDay(30,list);
        if(lowIndex60 < 20 && lowIndex30 < 12){
            return;
        }

        //基础特征2.1：K线小幅震荡，震荡幅度在2%以内
        boolean isAmplitude30 =  this.klineAmplitude(lowIndex30,list);
        if(!isAmplitude30){
            boolean isAmplitude60 =  this.klineAmplitude(lowIndex60,list);
            if(!isAmplitude60){
                return;
            }
        }

        //基础特征3.1：5日均线振幅不能超过2%,排除误差天数减2
        boolean maAmplitude60 = this.maLineAmplitude(list.subList(0,lowIndex60-1));
        boolean maAmplitude30 = this.maLineAmplitude(list.subList(0,lowIndex30-1));
        if(!maAmplitude60 && !maAmplitude30){
            return;
        }
        int lowIndex = maAmplitude60 ? lowIndex60 : lowIndex30;
        //============================均线接近于直线算法======================
        //====================启动上涨重要特征==================
        //20日均线，10日内必须向上或平行
        boolean maLineUp20 = this.maLineUp(list);
        if(!maLineUp20){
            return;
        }
        //10日内一根长阳，长阳涨幅不能超过5%，有20日以上均线突破
        boolean isLongRead = this.longReadKline(list.subList(0,lowIndex));
        if(!isLongRead){
            return;
        }
        //K线连续试探突破待突破均线
        //周K线均匀倒排序后第二周立马拉回
        System.out.println(list.get(0).getTradeDate()+"========================"+list.get(0).getTsCode());
    }

    /**
     * 均线最高价与最低价 之间的振幅
     * @param list
     * @return
     */
    private boolean maLineAmplitude(List<KLineEntity> list){
        List<Double> maValues = MaAlgorithmUtil.portraitMaValues(list,MaLineType.LINE005);
        double kMax = list.stream().map(o -> o.getPctChg()).max(Comparator.comparing(Double::doubleValue)).get();
        double maMax = maValues.stream().max(Comparator.comparing(Double::doubleValue)).get();
        double maMin = maValues.stream().min(Comparator.comparing(Double::doubleValue)).get();
        double amplitude = CalculateUtil.sub(CalculateUtil.div(maMax,maMin,2) ,1,2);
        if(amplitude > 0.05){
            return false;
        }
        if(kMax < 2){
            if(amplitude > 0.02){
                return false;
            }
        }else if(kMax >=2  && kMax < 4){
            if(amplitude > 0.03){
                return false;
            }
        }else if(kMax >= 4 && kMax < 5){
            if(amplitude > 0.05){
                return false;
            }
        }
        return true;
    }

    /**
     * 20日均线，10日内必须向上或平行
     * @param list
     * @return
     */
    private boolean maLineUp(List<KLineEntity> list){
        if(list.get(0).getMaTwenty() < list.get(9).getMaTwenty()){
            return false;
        }
        return true;
    }

    /**
     * 10日内一根长阳，有20日以上均线突破
     * @param list
     */
    public boolean longReadKline(List<KLineEntity> list){
        double max = list.get(0).getPctChg();
        int index = 0;
        //由于最低点向上拉回是可能出现长阳，排除最低点5日内的长阳
        for(int i = 1;i < (list.size()-5);i++){
            if(max < list.get(i).getPctChg()){
                index = i;
                max = list.get(i).getPctChg();
            }
        }
        if(index >= 10){
            return false;
        }
        //=====================计算K线振幅=============================
        double [] chgMultiples = new double[list.size()-5];
        for(int i = 0;i < (list.size()-5);i++){
            double chg = list.get(i).getPctChg();
            if(list.get(i).getPctChg() == 0){
                chg = 0.01;
            }if(list.get(i).getPctChg() < 0){
                chg = -list.get(i).getPctChg();
            }
            double multiple = CalculateUtil.div(max,chg,2);
            chgMultiples[i] = multiple;
        }
        double multipleAvg = StatUtils.mean(chgMultiples);
        if(list.size() < 15){
            if(multipleAvg < 20){
                return false;
            }
        }else {
            if(multipleAvg < 39){
                return false;
            }
        }
        //======================计算是否有均线突破=======================
        //出现长阳K线前有均线突破
        boolean breakNumFront = importantUpMaLineBreak(list.subList(0,index+6));
        //出现长阳K线后有均线突破
        boolean breakNumAfter = importantUpMaLineBreak(list.subList(0,index+1));
        if(!breakNumAfter && !breakNumFront){
            return false;
        }
        return true;
    }

    /**
     * 均线突破
     * @param list
     */
    public boolean importantUpMaLineBreak(List<KLineEntity> list){
        int breakNum1 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE020, MaLineType.LINE030, list);
        int breakNum2 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE020, MaLineType.LINE060, list);
        int breakNum3 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE020, MaLineType.LINE120, list);
        int breakNum4 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE020, MaLineType.LINE250, list);
        int breakNum5 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE030, MaLineType.LINE060, list);
        int breakNum6 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE030, MaLineType.LINE120, list);
        int breakNum7 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE030, MaLineType.LINE250, list);
        int breakNum8 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE060, MaLineType.LINE120, list);
        int breakNum9 = MaAlgorithmUtil.
                maLineUpBreakDay(MaLineType.LINE060, MaLineType.LINE250, list);
        if(breakNum1 > 0 || breakNum2 > 0 || breakNum3 > 0 || breakNum4 > 0 || breakNum5 > 0
                || breakNum6 > 0 || breakNum7 > 0 || breakNum8 > 0 || breakNum9 > 0){
            return true;
        }
        return false;
    }

    /**
     *10日内新低
     * @param list
     * @return
     */
    private int lowIntervalDay(int size,List<KLineEntity> list){
        List<MaLineType> types = Arrays.asList(MaLineType.LINE005,
                MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);
        //均线凝聚程度
        int num = this.klineTooMaNum(types,list);
        int lowIndex = 10;
        double low = list.get(0).getLow();
        for(int i = 1;i <= num;i++){
            if(i > 10 && list.get(i).getPctChg() > 5){
                break;
            }
            if(low > list.get(i).getLow()){
                low = list.get(i).getLow();
                lowIndex = i;
            }
        }
        return lowIndex;
    }

    /**
     *振幅大于2%的K线占比
     * @param list
     * @return
     */
    private boolean klineAmplitude(int lowIndex,List<KLineEntity> list){
        int longNun = 0;
        for(int i = 0;i < lowIndex;i++){
            double pctChg = list.get(i).getPctChg();
            if(pctChg > 2 || pctChg < -2){
                longNun++;
            }
        }
        double proportion = CalculateUtil.div(longNun,lowIndex,2);
        //值越低越好
        if(proportion > 0.15){
            return false;
        }
        return true;
    }
}
