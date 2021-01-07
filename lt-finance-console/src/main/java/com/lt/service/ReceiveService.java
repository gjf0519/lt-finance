package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.BigDecimalUtil;
import com.lt.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public void dayLineBreak(String tscode,String tradeDate){
//        int limit = 90;
        int limit = 20;
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
//            filterSemesterAndYear(list);
            demonLine(list);
//            parallelDay(list);
//            dayLinePeriod(list);
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
     * 过滤出突破半年或年线的数据
     */
    public void filterSemesterAndYear(List<KLineEntity> list){
        if(list.get(0).getClose() > 50){
            return;
        }
        if(list.get(0).getMaFive() -  list.get(0).getMaSemester() < 0
                && list.get(0).getMaFive() -  list.get(0).getMaYear() < 0){
            return;
        }
        double sy = list.get(0).getMaSemester() - list.get(0).getMaYear();
        if(0 == list.get(0).getMaSemester()){
            return;
        }
        double syradio = BigDecimalUtil.div(sy,list.get(0).getMaSemester(),4);
        if(syradio < -0.1 || syradio > 0.2){
            return;
        }
        double ys1 = list.get(0).getMaYear() - list.get(0).getMaSemester();
        double ysradio1 = BigDecimalUtil.div(ys1,list.get(0).getMaSemester(),4);

        double ys2 = list.get(10).getMaYear() - list.get(10).getMaSemester();
        double ysradio2 = BigDecimalUtil.div(ys2,list.get(10).getMaSemester(),4);


        if(ysradio1 - ysradio2 > 0){
            return;
        }
        int sindex = 0;
        for(int i = 0;i < list.size();i++){
            if(list.get(i).getMaFive() - list.get(i).getMaSemester() < 0){
                break;
            }
            sindex = i;
        }

        int yindex = 0;
        for(int i = 0;i < list.size();i++){
            if(list.get(i).getMaFive() - list.get(i).getMaYear() < 0){
                break;
            }
            yindex = i;
        }

        int ysindex = 0;
        for(int i = 0;i < list.size();i++){
            if(list.get(i).getMaYear() - list.get(i).getMaSemester() > 0){
                break;
            }
            ysindex = i;
        }

        int stime = sindex+1;
        double ssub = list.get(0).getClose() - list.get(sindex).getClose();
        double sratio = BigDecimalUtil.div(ssub,list.get(sindex).getClose(),4);

        int ytime = yindex+1;
        double ysub = list.get(0).getClose() - list.get(yindex).getClose();
        double yratio = BigDecimalUtil.div(ysub,list.get(yindex).getClose(),4);

        int ystime = ysindex+1;
        double yssub = list.get(0).getClose() - list.get(ysindex).getClose();
        double ysratio = BigDecimalUtil.div(yssub,list.get(ysindex).getClose(),4);
        if(stime == 1 && ytime == 1){
            return;
        }
        if(ytime == 1 && sratio > 0.1){
            return;
        }
        if(ytime != 1 && yratio > 0.3){
            return;
        }
        if(ystime != 1 && ysratio > 0.5){
            return;
        }
        //5日均线在20日以下剔除
        if(list.get(0).getMaFive() - list.get(0).getMaTwenty() < 0){
            return;
        }
        //10日20日都在30日以下剔除并且30日在60日以下
        if(list.get(0).getMaTen() - list.get(0).getMaMonth() < 0 ||
                list.get(0).getMaTwenty() - list.get(0).getMaMonth() < 0 ||
                list.get(0).getMaMonth() - list.get(0).getMaQuarter() < 0 ){
            return;
        }
        System.out.println(list.get(0).getTsCode()+"=========="+sratio+"======="+stime+"======"+yratio+"========"+ytime+"=========="+ystime);
    }

    public void parallelWeek(List<KLineEntity> list){
        if(list.isEmpty() || list.size() < 10){
            return;
        }

        //小于20日均线全部剔除
        if(list.get(0).getPctChg() <= 0){
            if(list.get(0).getMaFive() - list.get(0).getMaTwenty() < 0 ||
                    list.get(0).getMaFive() - list.get(0).getMaTen() < 0){
                return;
            }
        }else {
            if(list.get(0).getMaFive() - list.get(1).getMaTen() < 0){
                return;
            }
            if(list.get(0).getMaFive() - list.get(1).getMaTwenty() < 0){
                return;
            }
        }
        for(int i =0;i < 5;i++){
            //当前5日内10日均线必须在20日均线以上
            if(list.get(i).getMaFive() - list.get(i).getMaTwenty()<0){
                return;
            }
            if(list.get(i).getMaTen() - list.get(i).getMaTwenty()<0){
                return;
            }
            double sub = list.get(i).getMaFive() - list.get(i).getMaTwenty();
            double ratio = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
            if(ratio > 0.04){
                return;
            }
        }
        int sing = 0;
        double pctchg = 0.0;
        for(int i = 0;i < (list.size()-1);i++){
            KLineEntity entity1 = list.get(i);
            KLineEntity entity2 = list.get(i+1);
            if(entity1.getMaFive() - entity2.getMaFive() >= 0){
                sing++;
            }else {
                sing = 0;
            }
            if(entity1.getPctChg() > 10 || entity1.getPctChg() <= -10){
                return;
            }
            pctchg = pctchg+entity1.getPctChg();
        }
        if(sing < 3){
            return;
        }
        double ratio1 = 0.0;
        if(list.get(0).getPctChg() < 0){
            double sub = list.get(0).getClose() - list.get(0).getMaTwenty();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
        }else {
            double sub = list.get(0).getOpen() - list.get(0).getMaTwenty();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
        }
        System.out.println(list.get(0).getTsCode()+"======"+pctchg+"========"+sing+"============"+ratio1);
    }

    public void parallelDay(List<KLineEntity> list){
        if(list.isEmpty() || list.size() < 10){
            return;
        }

        if(list.get(0).getPctChg() <= 0){
            //5日均线在20日以下剔除
            if(list.get(0).getMaFive() - list.get(1).getMaTwenty() < 0){
                return;
            }
            //5日10日20日都在30日以下剔除
            if(list.get(0).getMaFive() - list.get(0).getMaMonth() < 0 &&
                    list.get(0).getMaTen() - list.get(0).getMaMonth() < 0 &&
                    list.get(0).getMaTwenty() - list.get(0).getMaMonth() < 0 ){
                return;
            }
            //当前连续三日下跌剔除
            if(list.get(0).getMaFive() - list.get(1).getMaFive() < 0 &&
                    list.get(1).getMaFive() - list.get(2).getMaFive() < 0 &&
                    list.get(2).getMaFive() - list.get(3).getMaFive() < 0){
                return;
            }
        }else {
            if(list.get(0).getMaFive() - list.get(1).getMaTwenty() < 0){
                return;
            }
        }
        for(int i =0;i < 5;i++){
            //当前5日内10日均线必须在20日均线以上
            if(list.get(i).getMaFive() - list.get(i).getMaTwenty()<0){
                return;
            }
            if(list.get(i).getMaTen() - list.get(i).getMaTwenty()<0){
                return;
            }
            double sub = list.get(i).getMaFive() - list.get(i).getMaTwenty();
            double ratio = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
            if(ratio > 0.04){
                return;
            }
        }

        int sing = 0;
        double pctchg = 0.0;
        String prev = "后五";
        for(int i = 0;i < (list.size()-1);i++){
            KLineEntity entity1 = list.get(i);
            KLineEntity entity2 = list.get(i+1);
            if(entity1.getMaFive() - entity2.getMaFive() >= 0){
                sing++;
            }else if(sing < 3) {
                sing = 0;
            }
            if(sing>=3 && i < 5){
                prev = "前五";
            }
            if(entity1.getPctChg() > 5 && entity1.getPctChg() < 9){
               if(entity1.getOpen() > entity1.getMaFive()
                       || entity1.getClose() < entity1.getMaFive()){
                   return;
               }
            }
            if(entity1.getPctChg() <= -5){
                return;
            }
            pctchg = pctchg+entity1.getPctChg();
        }
        if(sing < 3 || pctchg < 0){
            return;
        }
        if(sing < 7 || pctchg < 0 || pctchg > 4){
            return;
        }
        double ratio1 = 0.0;
        if(list.get(0).getPctChg() < 0){
            double sub = list.get(0).getClose() - list.get(0).getMaTwenty();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
        }else {
            double sub = list.get(0).getOpen() - list.get(0).getMaTwenty();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getMaTwenty(),2);
        }
        if(ratio1 > 0.03){
            return;
        }
        System.out.println(list.get(0).getTsCode()+"======="+pctchg+"======="+sing+"==========="+prev+"==========="+ratio1);
    }

    /**
     * 妖妖过滤规则
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
                || list.get(0).getMaFive() - list.get(1).getMaTwenty() <= 0){
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

    public List<Set<String>> dayLinePeriod(List<KLineEntity> list){
        Double [] fiveArrays = list.stream().map(o -> o.getMaFive()).collect(Collectors.toList()).toArray(new Double[list.size()]);
        Map<String,Integer> bands = StockAlgorithm.calculateBand(4,fiveArrays);
        List<KLineEntity> peeks = new ArrayList<>();
        List<KLineEntity> ravines = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : bands.entrySet()){
            if(entry.getKey().startsWith("波峰")){
                peeks.add(list.get(entry.getValue()));
            }else if(entry.getKey().startsWith("波谷")){
                ravines.add(list.get(entry.getValue()));
            }
        }
        List<Set<String>> breaks = new ArrayList<>();
        for (KLineEntity entity:peeks) {
            Set<String> bandSet = klineDistribute(entity);
            breaks.add(bandSet);
        }

        //对比波峰突破均线
//        List<Set<String>> breaks = new ArrayList<>();
//        for(int i = 0;i < (lineStatus.size()-1);i++){
//            Set<String> item = new HashSet<>();
//            Set<String> line1 = lineStatus.get(i);
//            Set<String> line2 = lineStatus.get(i+1);
//            item.addAll(line1);
//            item.removeAll(line2);
//            if(!item.isEmpty()){
//                breaks.add(item);
//            }
//        }

        //记录波峰突破记录
//        Map<String,Set<String>> result = new HashMap<>();
//        for (int i = 0;i < breaks.size();i++) {
//            result.put(peeks.get(i).getTradeDate(),breaks.get(i));
//        }
        return breaks;
    }

    public void weekLinePeriod(List<KLineEntity> list){
        Double [] fiveArrays = list.stream().map(o -> o.getMaFive()).collect(Collectors.toList()).toArray(new Double[list.size()]);
        Map<String,Integer> bands = StockAlgorithm.calculateBand(4,fiveArrays);
        int size = list.size() - 1;
        if(size == bands.get("最高")){
            return;
        }
        if(size == bands.get("最低")){
            return;
        }
        String lastKey = "";
        int lastPeekIndex = 0;
        int lastRavineIndex = 0;
        List<KLineEntity> peeks = new ArrayList<>();
        List<KLineEntity> ravines = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : bands.entrySet()){
            lastKey = entry.getKey();
            if(entry.getKey().startsWith("波峰")){
                lastPeekIndex = entry.getValue();
                peeks.add(list.get(entry.getValue()));
            }else if(entry.getKey().startsWith("波谷")){
                lastRavineIndex = entry.getValue();
                ravines.add(list.get(entry.getValue()));
            }
        }

        int num = 1;//上涨或下跌趋势中第几次波段
        for(int i = (ravines.size() - 1);i > 0;i--){
            if(ravines.get(i).getMaFive()
                    - ravines.get(i - 1).getMaFive() < 0){
                break;
            }
            num++;
        }

        Map<String,String> trunMap = null;
        if(lastKey.startsWith("波谷")){
            if(lastPeekIndex == 0){
                return;
            }
            trunMap = ravineTrun(lastPeekIndex,list);
        }else {
            if(lastRavineIndex == 0){
                return;
            }
            trunMap = peekTrun(lastRavineIndex,list);
        }

        KLineEntity entity = list.get(size);
        KLineEntity bandEntity = list.get(bands.get(lastKey));
        double rose = entity.getMaFive() - bandEntity.getMaFive();
        //上涨幅度
        double roseRatio = BigDecimalUtil.div(rose,bandEntity.getMaFive(),2);
        //本次上涨次数
        int alt = size - bands.get(lastKey);
//        if(num < 2 ){
//            return;
//        }
//        if(alt < 4){
//            return;
//        }
        if(lastKey.startsWith("波峰")){
            if("0".equals(trunMap.get("isTrun"))){
                return;
            }
            if("1".equals(trunMap.get("isBreak")) && entity.getPctChg() > 0.1){
                return;
            }
        }else {
            if("0".equals(trunMap.get("isBreak"))){
                return;
            }
        }
        if(roseRatio > 0.1){
            return;
        }
        System.out.println(entity.getTsCode()+"========="+num+"========="+alt+"====="+trunMap.get("isTrun")+"========"+trunMap.get("isBreak")+"======"+roseRatio);
        klineStatus(bandEntity,list);
    }

    /**
     * 判断当前K线状态
     * @param list
     */
    public void klineStatus(KLineEntity bandEntity,List<KLineEntity> list){
        //是否拐头向下 向下天数 是否跌破20日均线 当前上涨下跌幅度
        int size = list.size() - 1;
        int day = 0;
        for(int i = size;i > -1;i--){
            if(list.get(i).getMaFive() - list.get(i-1).getMaFive() > 0){
                day++;
                continue;
            }
            break;
        }
        //均线变化
//        Map<String,String> bandMap = klineDistribute(bandEntity);
//        Map<String,String> realMap = klineDistribute(list.get(size));
//        Map<String,String> breaks = thanKline(bandMap,realMap);
//        System.out.println(bandEntity.getTsCode()+"========"+day+"========="+breaks);
        // 当前均线状态 例如多头向上
    }

    public Map<String,String> thanKline(Map<String,String> bandMap,Map<String,String> realMap){
        Map<String,String> breaks = new HashMap<>();
        for (Map.Entry<String,String> entry : bandMap.entrySet()) {
            String v = entry.getValue();
            String k = entry.getKey();
            if("0".equals(v) && "1".equals(realMap.get(k))){
                breaks.put(k,"1");
            }
        }
        return breaks;
    }

    public Set<String> klineDistribute(KLineEntity entity){
        Set<String> result = new HashSet<>();
        //10日突破
        if(entity.getMaFive() - entity.getMaTen() > 0){
            result.add(Constants.BREAK_5_10);
        }
        //20日突破
        if(entity.getMaTwenty() > 0){
            if(entity.getMaFive() - entity.getMaTwenty() > 0){
                result.add(Constants.BREAK_5_20);
            }
            if(entity.getMaTen() - entity.getMaTwenty() > 0){
                result.add(Constants.BREAK_10_20);
            }
        }
        //30日突破
        if(entity.getMaMonth() > 0){
            if(entity.getMaFive() - entity.getMaMonth() > 0){
                result.add(Constants.BREAK_5_30);
            }
            if(entity.getMaTen() - entity.getMaMonth() > 0){
                result.add(Constants.BREAK_10_30);
            }
            if(entity.getMaTwenty() - entity.getMaMonth() > 0){
                result.add(Constants.BREAK_20_30);
            }
        }
        //季突破
        if(entity.getMaQuarter() > 0){
            if(entity.getMaFive() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_5_60);
            }
            if(entity.getMaTen() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_10_60);
            }
            if(entity.getMaTwenty() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_20_60);
            }
            if(entity.getMaMonth() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_30_60);
            }
        }
        //半年突破
        if(entity.getMaSemester() > 0){
            if(entity.getMaFive() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_5_120);
            }
            if(entity.getMaTen() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_10_120);
            }
            if(entity.getMaTwenty() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_20_120);
            }
            if(entity.getMaMonth() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_30_120);
            }
            if(entity.getMaMonth() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_60_120);
            }
        }
        //整年突破
        if(entity.getMaYear() > 0){
            if(entity.getMaFive() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_5_250);
            }
            if(entity.getMaTen() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_10_250);
            }
            if(entity.getMaTwenty() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_20_250);
            }
            if(entity.getMaMonth() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_30_250);
            }
            if(entity.getMaMonth() - entity.getMaQuarter() > 0){
                result.add(Constants.BREAK_60_250);
            }
        }
        return result;
    }


    public Map<String,String> ravineTrun(int index,List<KLineEntity> list){
        Map<String,String> map = new HashMap<>();
        //波峰最高价
        double price = limitMax(index,list);
        int size = list.size() - 1;
        double sub = list.get(size).getMaFive() - price;
        String isBreak = "0";
        //突破上次波峰
        if(sub > 0){
            isBreak = "1";
        }
        String isTrun = "0";
        //开始转头
        if(list.get(size).getMaFive() - list.get(size-1).getMaFive() < 0){
            isTrun = "1";
        }
        map.put("isTrun",isTrun);
        map.put("isBreak",isBreak);
        return map;
    }

    public Map<String,String> peekTrun(int index,List<KLineEntity> list){
        Map<String,String> map = new HashMap<>();
        //波谷最低价
        double price = limitMin(index,list);
        int size = list.size() - 1;
        double sub = list.get(size).getMaFive() - price;
        String isBreak = "0";
        //突破上次波谷
        if(sub > 0){
            isBreak = "1";
        }
        String isTrun = "0";
        //开始转头
        if(list.get(size).getMaFive() - list.get(size-1).getMaFive() > 0){
            isTrun = "1";
        }
        map.put("isTrun",isTrun);
        map.put("isBreak",isBreak);
        return map;
    }

    private double limitMin(int index,List<KLineEntity> list){
        int end = index > 4 ? 5 : index;
        double min = list.get(index).getMaFive();
        for(int i = 1;i < end;i++){
            if(min > list.get(index-i).getMaFive()){
                min = list.get(index-i).getMaFive();
            }
        }
        return min;
    }

    private double limitMax(int index,List<KLineEntity> list){
        int end = index > 4 ? 5 : index;
        double min = list.get(index).getMaFive();
        for(int i = 1;i < end;i++){
            if(min < list.get(index-i).getMaFive()){
                min = list.get(index-i).getMaFive();
            }
        }
        return min;
    }
}
