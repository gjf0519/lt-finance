package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.BigDecimalUtil;
import com.lt.utils.Constants;
import com.lt.utils.KlineDistributionUtil;
import com.lt.utils.Mutil;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@Service
public class ReceiveService {

    @Autowired
    private KLineService kLineService;

    public void receiveDailyBasic(String record) {
        try {
            Map map =  JSON.parseObject(record, Map.class);
//            consumerService.saveDailyBasic(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 消费日K数据
     * @param map
     */
    public void receiveDayLine(Map map) {
        try {
            String tscode = map.get("ts_code").toString();
            String tradeDate = map.get("trade_date").toString();
            //判断日K数据是否已保存
            int isSave = kLineService.hasSaveDayLine(tscode,tradeDate);
            if(isSave > 0){
                return;
            }
            List<KLineEntity> list = kLineService.queryDayLineByLimit(tscode,249);
            List<Double> closes = new ArrayList<>(250);
            closes.add(Double.valueOf(map.get("close").toString()));
            for(KLineEntity item : list){
                closes.add(item.getClose());
            }
            Collections.reverse(closes);
            //计算均线价格
            calculateAvg(closes,map);
            kLineService.saveDayLine(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 消费周K数据
     * @param map
     */
    public void receiveWeekLine(Map map) {
        try {
            String tscode = map.get("ts_code").toString();
            String tradeDate = map.get("trade_date").toString();
            //判断周K数据是否已保存
            int isSave = kLineService.hasSaveWeekLine(tscode,tradeDate);
            if(isSave > 0){
                return;
            }
            List<KLineEntity> list = kLineService.queryWeekLineByLimit(tscode,249);
            List<Double> closes = new ArrayList<>(250);
            closes.add(Double.valueOf(map.get("close").toString()));
            for(KLineEntity item : list){
                closes.add(item.getClose());
            }
            Collections.reverse(closes);
            //计算均线价格
            calculateAvg(closes,map);
            kLineService.saveWeekLine(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteWeekByCode(String item) {
        kLineService.deleteWeekByCode(item);
    }
    /**
     * 计算均线价格
     * @param closes
     * @param map
     */
    public void calculateAvg(List<Double> closes,Map<String,Object> map){
        for (int i = 0; i < Constants.MA_NUM_ARREY.length; i++) {
            List<Double> mas = StockAlgorithm.calculate(closes,Constants.MA_NUM_ARREY[i]);
            if(mas.isEmpty()){
                return;
            }
            map.put(Constants.MA_NAME_ARREY[i],mas.get(mas.size()-1));
        }
    }

    /**
     * 日K均线突破
     * @param tscode
     */
    public void dayLineBreak(String tscode){
        dayLineBreak(tscode,null);
    }

    /**
     * 日K均线突破
     * @param tscode
     */
    public List<KLineEntity> dayLineBreakRuleTest(String tscode,String tradeDate,int limit){
        List<KLineEntity> list = null;
        if(null == tradeDate){
            list = kLineService.queryDayLineByLimit(tscode,limit);
        }else {
            list = kLineService.queryDayLineByLimitDate(tscode,limit,tradeDate);
        }
        return list;
    }

    /**
     * 日K均线突破
     * @param tscode
     */
    public void dayLineBreak(String tscode,String tradeDate){
//        int limit = 90;
        int limit = 30;
        List<KLineEntity> list = null;
        if(null == tradeDate){
            list = kLineService.queryDayLineByLimit(tscode,limit);
        }else {
            list = kLineService.queryDayLineByLimitDate(tscode,limit,tradeDate);
        }
        if(list.isEmpty()){
            return;
        }
        try {
            filterForm(list);
            demonLine(list);
        }catch (Exception e){
            System.out.println(tscode+"=========================================");
            e.printStackTrace();
        }
//        EmaBreakEntity entity = klineBreak(list,"日K");
//        if(null == entity){
//            return;
//        }
//        entity.setTsCode(tscode);
//        kLineService.saveEmaBreak(entity);
    }

    public void filterForm(List<KLineEntity> list){
        Set<String> breaks = klineTrend(list);
        if(breaks.isEmpty()){
            return;
        }
        boolean isdw = klineSite(list);
        if(!isdw){
            return;
        }
        boolean isDisperse = disperseLevel(list);
        if(!isDisperse){
            return;
        }
        boolean isGather = KlineDistributionUtil.modeFilter(list);
        if(!isGather){
            return;
        }
        String dcode = KlineDistributionUtil.deviateTest(list);
        if(null != dcode){
            System.out.println(list.get(0).getTsCode()+"*******************************************************");
            return;
        }
        String pcode = KlineDistributionUtil.peakTest(list);
        if(null != pcode){
            System.out.println(list.get(0).getTsCode()+"#######################################################");
            return;
        }
        int cohereLevel = cohereLevel(list);
        System.out.println(list.get(0).getTsCode()+"============================================"+cohereLevel);
    }

    public Set<String> klineTrend(List<KLineEntity> list){
        Set<String> result = new HashSet<>();
        int size = list.size() - 1;
        if(list.get(size).getMaYear() < list.get(size).getMaSemester()
                && list.get(0).getMaYear() > list.get(0).getMaSemester()){
            return result;
        }

        for(KLineEntity entity : list){
            if(entity.getMaQuarter() > 0){
                if(entity.getMaTwenty() > entity.getMaQuarter()){
                    result.add(Constants.BREAK_20_60);
                }
                if(entity.getMaMonth() > entity.getMaQuarter()){
                    result.add(Constants.BREAK_30_60);
                }
            }
            if(entity.getMaSemester() > 0){
                if(entity.getMaTwenty() > entity.getMaSemester()){
                    result.add(Constants.BREAK_20_120);
                }
                if(entity.getMaMonth() > entity.getMaSemester()){
                    result.add(Constants.BREAK_30_120);
                }
                if(entity.getMaMonth() > entity.getMaSemester()){
                    result.add(Constants.BREAK_60_120);
                }
            }
            if(entity.getMaYear() > 0){
                if(entity.getMaTwenty() > entity.getMaYear()){
                    result.add(Constants.BREAK_20_250);
                }
                if(entity.getMaMonth() > entity.getMaYear()){
                    result.add(Constants.BREAK_30_250);
                }
                if(entity.getMaMonth() > entity.getMaYear()){
                    result.add(Constants.BREAK_60_250);
                }
            }
        }
        return result;
    }

    /**
     * 5日内位置
     * @param list
     * @return
     */
    public boolean klineSite(List<KLineEntity> list){
        if(list.get(0).getPctChg() > 6){
            return false;
        }
        if(list.get(0).getClose() < list.get(0).getMaTwenty()){
            return false;
        }
        for(int i = 0;i < 5;i++){
            KLineEntity entity = list.get(i);
            if(entity.getPctChg() > 5 || entity.getPctChg() < -5){
                return false;
            }
            if(entity.getMaFive() < entity.getMaTwenty() &&
                    entity.getMaFive() < entity.getMaTen() &&
                    entity.getMaFive() < entity.getMaMonth()){
                return false;
            }
        }
        return true;
    }

    public int cohereLevel(List<KLineEntity> list){
        Map<String,List<Double>> coheres = maCohere(list,10);
        List<Double> faveTenCoheres = coheres.get("faveTenCoheres");
        List<Double> tenTwentyCoheres = coheres.get("tenTwentyCoheres");
        List<Double> twentyMonthCoheres = coheres.get("twentyMonthCoheres");
        List<Double> monthQuarterCoheres = coheres.get("monthQuarterCoheres");
        int level = 0;
        int fiveSign = 0;
        for(Double five : faveTenCoheres){
            if(five == 0){
                fiveSign++;
            }
        }
        if((faveTenCoheres.get(0) <= 0 && faveTenCoheres.get(0) >= -0.03)
                && (tenTwentyCoheres.get(0) <= 0 && tenTwentyCoheres.get(0) >= -0.03)
                && (twentyMonthCoheres.get(0) <= 0 && twentyMonthCoheres.get(0) >= -0.03)
                && (monthQuarterCoheres.get(0) <= 0 && monthQuarterCoheres.get(0) >= -0.03)){
            if(list.get(0).getClose() > list.get(0).getMaTwenty()
                    && list.get(0).getMaFive() < list.get(1).getMaFive()
                    && list.get(1).getMaFive() > list.get(2).getMaFive()
                    && list.get(2).getMaFive() > list.get(3).getMaFive()
                    && list.get(3).getMaFive() > list.get(4).getMaFive()
                    && list.get(4).getMaFive() > list.get(5).getMaFive()){
                return level = 5;//"000713.SZ","20201217"
            }
            if(list.get(0).getClose() > list.get(0).getMaTwenty()
                    && list.get(0).getMaFive() > list.get(1).getMaFive()
                    && list.get(1).getMaFive() < list.get(2).getMaFive()
                    && list.get(2).getMaFive() > list.get(3).getMaFive()
                    && list.get(3).getMaFive() > list.get(4).getMaFive()
                    && list.get(4).getMaFive() > list.get(5).getMaFive()){
                return level = 5;//"000713.SZ","20201217"
            }
            if(list.get(0).getMaFive() > list.get(1).getMaFive()
                    && list.get(1).getMaFive() > list.get(2).getMaFive()
                    && list.get(2).getMaFive() > list.get(3).getMaFive()
                    && list.get(3).getMaFive() > list.get(4).getMaFive()){
                return level = 4;
            }
            if(list.get(0).getClose() < list.get(0).getMaMonth()){
                return level = 0;
            }
            level = 3;
        }else if(fiveSign >= 6 && tenTwentyCoheres.get(0) <= 0
                && twentyMonthCoheres.get(0) <= 0
                && monthQuarterCoheres.get(0) <= 0){
            if(list.get(0).getClose() > list.get(0).getMaTwenty()){
                return level = 5;//"600189.SH","20201106"
            }
            level = 3;
        }else if(faveTenCoheres.get(0) == 0
                || tenTwentyCoheres.get(0) == 0
                || twentyMonthCoheres.get(0) == 0
                || monthQuarterCoheres.get(0) == 0){
            level = 1;
        }
        return level;
    }

    /**
     * 离散程度
     * @param list
     * @return
     */
    public boolean disperseLevel(List<KLineEntity> list){
        int size = 30;
        double [] arr1 = new double[size];
        for(int i = 0;i < size;i++){
            arr1[i] = list.get(i).getMaFive();
        }
        double disperse1 = BigDecimalUtil.round(KlineDistributionUtil.distribution(arr1),0);
        double [] arr2 = new double[size];
        for(int i = 0;i < size;i++){
            arr2[i] = list.get(i).getMaTen();
        }
        double meanSub2 = StatUtils.meanDifference(arr1, arr2);
        //平均差直接影响平均数的代表性
        if(meanSub2 > 0.1 || meanSub2 < -0.1){
            return false;
        }
        double disperse2 = BigDecimalUtil.round(KlineDistributionUtil.distribution(arr2),0);
        double [] arr3 = new double[size];
        for(int i = 0;i < size;i++){
            arr3[i] = list.get(i).getMaTwenty();
        }
//        double meanSub3 = StatUtils.meanDifference(arr2, arr3);
        double disperse3 = BigDecimalUtil.round(KlineDistributionUtil.distribution(arr3),0);
        double [] arr4 = new double[size];
        for(int i = 0;i < size;i++){
            arr4[i] = list.get(i).getMaMonth();
        }
//        double meanSub4 = StatUtils.meanDifference(arr3, arr4);
        double disperse4 = BigDecimalUtil.round(KlineDistributionUtil.distribution(arr4),0);
        if(disperse3 < 100 && disperse4 < 100){
            if(disperse1 > disperse2
                    || disperse2 > disperse3
                    || disperse3 > disperse4){
                return false;
            }
        }
        double fold = BigDecimalUtil.div(disperse4,disperse1,2);
        if(fold < 2 || (disperse1 > 16 && disperse4 < 90)){
            return false;
        }
        return true;
    }

    /**
     * 粘合程度
     * @param list
     */
    public Map<String,List<Double>> maCohere(List<KLineEntity> list,int limit){
        List<Double> faveTenCoheres = new ArrayList<>();
        List<Double> tenTwentyCoheres = new ArrayList<>();
        List<Double> twentyMonthCoheres = new ArrayList<>();
        List<Double> monthQuarterCoheres = new ArrayList<>();
        List<Double> quarterSemesterCoheres = new ArrayList<>();
        List<Double> semesterYearCoheres = new ArrayList<>();
        for(int i = 0;i < limit;i++){
            KLineEntity entity = list.get(i);
            double faveTenCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaFive(),entity.getMaTen(),2),2);
            double faveTwentyCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaTen(),entity.getMaTwenty(),2),2);
            double faveMonthCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaTwenty(),entity.getMaMonth(),2),2);
            double faveQuarterCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaMonth(),entity.getMaQuarter(),2),2);
            if(entity.getMaSemester() == 0){
                continue;
            }
            double faveSemesterCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaQuarter(),entity.getMaSemester(),2),2);
            if(entity.getMaYear() == 0){
                continue;
            }
            double faveYearCohere = BigDecimalUtil.sub(1,
                    BigDecimalUtil.div(entity.getMaSemester(),entity.getMaYear(),2),2);
            faveTenCoheres.add(faveTenCohere);
            tenTwentyCoheres.add(faveTwentyCohere);
            twentyMonthCoheres.add(faveMonthCohere);
            monthQuarterCoheres.add(faveQuarterCohere);
            quarterSemesterCoheres.add(faveSemesterCohere);
            semesterYearCoheres.add(faveYearCohere);
        }
        Map<String,List<Double>> soheres = new HashMap();
        soheres.put("faveTenCoheres",faveTenCoheres);
        soheres.put("tenTwentyCoheres",tenTwentyCoheres);
        soheres.put("twentyMonthCoheres",twentyMonthCoheres);
        soheres.put("monthQuarterCoheres",monthQuarterCoheres);
        soheres.put("quarterSemesterCoheres",quarterSemesterCoheres);
        soheres.put("semesterYearCoheres",semesterYearCoheres);
        return soheres;
    }

    /**
     * 集中趋势量数：极差（不包含）
     * @param in
     * @return
     */
    public static double range(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            max = Math.max(max, in[i]);
            min = Math.min(min, in[i]);
        }
        // return max - min;
        return Mutil.subtract(max, min);
    }

    public static List mode(double[] in) {
        HashMap map = new HashMap();
        double imode = 0;
        for (int i = 0; i < in.length; i++) {
            double x = in[i];
            if (map.containsKey(String.valueOf(x))) {
                // 如果出现多次，取出以前的计数，然后加1
                int len = Integer.parseInt(map.get(String.valueOf(x)).toString());
                map.put(String.valueOf(x), String.valueOf(len + 1));
                imode = Math.max(imode, len + 1);
            } else {
                // 如果是第一次出现，计数为1
                map.put(String.valueOf(x), String.valueOf(1));
                imode = Math.max(imode, 1);
            }
        }
        Iterator iter = map.keySet().iterator();
        ArrayList lst = new ArrayList();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object v = map.get(key);
            if (Integer.parseInt(v.toString()) == imode) {
                lst.add(key);
            }
        }
        return lst;
    }

    /**
     * 周K均线突破
     * @param tscode
     */
    public void weekLineBreak(String tscode){
        weekLineBreak(tscode,null);
    }

    /**
     * 周K均线突破
     * @param tscode
     */
    public void weekLineBreak(String tscode,String tradeDate){
        int limit = 10;
        List<KLineEntity> list = null;
        if(null == tradeDate){
            list = kLineService.queryWeekLineByLimit(tscode,limit);
        }else {
            list = kLineService.queryWeekLineByLimitDate(tscode,limit,tradeDate);
        }
        if(list.isEmpty()){
            return;
        }
//        parallelWeek(list);
//        Collections.reverse(list);
//        weekLinePeriod(list);
//        EmaBreakEntity entity = klineRise(list,"周K");
//        if(null == entity){
//            return;
//        }
//        entity.setTsCode(tscode);
//        kLineService.saveEmaBreak(entity);
    }

    /**
     * 过滤规则
     * @param list
     */
    public void demonLine(List<KLineEntity> list){
        if(list.isEmpty() || list.size() < 10){
            return;
        }
        //过滤掉价格大于50
        if(list.get(0).getClose() > 50){
            return;
        }

        //当前价格小于20剔除
        if(list.get(0).getClose() -
                list.get(0).getMaTwenty() < 0 ||
                list.get(0).getLow() - list.get(0).getMaTwenty() < 0){
            return;
        }
        //5小于10或小于20剔除
        if(list.get(0).getMaFive() - list.get(1).getMaTen() < 0
                || list.get(0).getMaFive() - list.get(1).getMaTwenty() < 0){
            return;
        }
        //10小于20剔除
        if(list.get(0).getMaTen() - list.get(1).getMaTwenty() < 0){
            return;
        }
        //5、10、20都在30以下剔除
        if(list.get(0).getMaFive() - list.get(0).getMaMonth() < 0 &&
                list.get(0).getMaTen() - list.get(0).getMaMonth() < 0 &&
                list.get(0).getMaTwenty() - list.get(0).getMaMonth() < 0 ){
            return;
        }
        //K线与20K比值
        double subtt0 = list.get(0).getLow() - list.get(0).getMaTwenty();
        double ratiott0 = BigDecimalUtil.div(subtt0,list.get(0).getMaTwenty(),2);
        if(ratiott0 >= 0.03){
            return;
        }
        //10与20K比值
        double subtt1 = list.get(0).getMaTwenty() - list.get(0).getMaMonth();
        double ratiott1 = BigDecimalUtil.div(subtt1,list.get(0).getMaMonth(),4);
        if(ratiott1 > 0.03){
            return;
        }
        //20与30K比值
        double subtt2 = list.get(0).getMaTwenty() - list.get(0).getMaMonth();
        double ratiott2 = BigDecimalUtil.div(subtt2,list.get(0).getMaMonth(),4);
        if(ratiott2 > 0.01 || ratiott2 < -0.01){
            return;
        }
        //30与60K比值
        double subtt3 = list.get(0).getMaMonth() - list.get(0).getMaQuarter();
        double ratiott3 = BigDecimalUtil.div(subtt2,list.get(0).getMaQuarter(),4);
        if(subtt3 > 0.1 || ratiott3 < -0.1){
            return;
        }
        //连续6日5在20以上,并且没有大波动
        for (int i = 0;i < 6;i++){
            if(list.get(i).getMaFive() -
                    list.get(i).getMaTwenty() < 0){
                return;
            }
            if(list.get(i).getPctChg() > 5 || list.get(i).getPctChg() < -4){
                return;
            }
        }

        List<Double> minList = new ArrayList<>();
        List<Double> maxList = new ArrayList<>();
        for(int i = 0;i < list.size();i++){
            KLineEntity entity1 = list.get(i);
            double sub = entity1.getMaFive() - entity1.getMaTen();
            double ratio = 0;
            if(sub >= 0){
                ratio = BigDecimalUtil.div(sub,entity1.getMaTen(),4);
            }else {
                ratio = BigDecimalUtil.div(sub,entity1.getMaFive(),4);
            }
            if(ratio < 0.05 && ratio > -0.05){
                minList.add(ratio);
            }else if(ratio > 0.05 || ratio < -0.05){
                return;
            } else {
                maxList.add(ratio);
            }

            double sub1 = entity1.getMaFive() - entity1.getMaTwenty();
            double ratio1 = 0;
            if(sub1 >= 0){
                ratio1 = BigDecimalUtil.div(sub1,entity1.getMaTwenty(),4);
            }else {
                ratio1 = BigDecimalUtil.div(sub1,entity1.getMaFive(),4);
            }
            if(ratio1 < 0.1 && ratio1 > -0.1){
                minList.add(ratio1);
            }else if(ratio1 > 0.1 || ratio1 < -0.1){
                return;
            }else {
                maxList.add(ratio1);
            }
            double sub2 = entity1.getMaFive() - entity1.getMaMonth();
            double ratio2 = 0;
            if(sub2 >= 0){
                ratio2 = BigDecimalUtil.div(sub2,entity1.getMaMonth(),4);
            }else {
                ratio2 = BigDecimalUtil.div(sub2,entity1.getMaFive(),4);
            }
            if(ratio2 < 0.1 && ratio2 > -0.1){
                minList.add(ratio2);
            }else if(ratio2 > 0.1 || ratio2 < -0.1){
                return;
            }else {
                maxList.add(ratio2);
            }

            double sub5 = entity1.getMaTen() - entity1.getMaTwenty();
            double ratio5 = 0;
            if(sub5 >= 0){
                ratio5 = BigDecimalUtil.div(sub5,entity1.getMaTwenty(),4);
            }else {
                ratio5 = BigDecimalUtil.div(sub5,entity1.getMaTen(),4);
            }
            if(ratio5 < 0.05 && ratio5 > -0.05){
                minList.add(ratio5);
            }else if(ratio5 > 0.05 || ratio5 < -0.05){
                return;
            }else {
                maxList.add(ratio5);
            }

            double sub6 = entity1.getMaTen() - entity1.getMaMonth();
            double ratio6 = 0;
            if(sub6 >= 0){
                ratio6 = BigDecimalUtil.div(sub6,entity1.getMaMonth(),4);
            }else {
                ratio6 = BigDecimalUtil.div(sub6,entity1.getMaTen(),4);
            }
            if(ratio6 < 0.1 && ratio6 > -0.1){
                minList.add(ratio6);
            }else if(ratio6 > 0.1 || ratio6 < -0.1){
                return;
            }else {
                maxList.add(ratio6);
            }

            double sub8 = entity1.getMaTen() - entity1.getMaTwenty();
            double ratio8 = 0;
            if(sub8 >= 0){
                ratio8 = BigDecimalUtil.div(sub8,entity1.getMaTwenty(),4);
            }else {
                ratio8 = BigDecimalUtil.div(sub8,entity1.getMaTen(),4);
            }
            if(ratio8 < 0.03 && ratio8 > -0.03){
                minList.add(ratio8);
            } else if(ratio8 > 0.03 || ratio8 < -0.03){
                return;
            }else {
                maxList.add(ratio8);
            }

            double sub10 = entity1.getMaTwenty() - entity1.getMaMonth();
            double ratio10 = 0;
            if(sub10 >= 0){
                ratio10 = BigDecimalUtil.div(sub10,entity1.getMaMonth(),4);
            }else {
                ratio10 = BigDecimalUtil.div(sub10,entity1.getMaTwenty(),4);
            }
            if(ratio10 < 0.03 && ratio10 > -0.03){
                minList.add(ratio10);
            } else if(ratio10 > 0.03 || ratio10 < -0.03){
                return;
            }else {
                maxList.add(ratio10);
            }
        }

        int size = list.size() - 1;
        //10日内必须趋势向上
        if(list.get(0).getMaFive() -
                list.get(size).getMaFive() < 0){
            return;
        }

        //5K上涨连续时长
        int dwSign = 0;
        int upSign = 0;
        int eqSign = 0;
        double pctchg = 0.0;
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i < size;i++){
            KLineEntity entity1 = list.get(i);
            KLineEntity entity2 = list.get(i+1);
            double sub = entity1.getMaFive() - entity2.getMaFive();
            if(sub > 0){
                upSign++;
            }else if(sub < 0) {
                dwSign++;
            }else {
                eqSign++;
            }
            if(i < 5){
                map.put("dwSign",dwSign);
                map.put("upSign",upSign);
                map.put("eqSign",eqSign);
            }
            pctchg = pctchg + list.get(i).getPctChg();
        }
        System.out.println(list.get(0).getTsCode()+"**"+ratiott0+"**"+pctchg+"**"+map);
    }
}
