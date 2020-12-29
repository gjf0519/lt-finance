package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.BigDecimalUtil;
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
            List<KLineEntity> list = kLineService.queryDayLineByLimit(tscode,119);
            List<Double> closes = new ArrayList<>(120);
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
            List<KLineEntity> list = kLineService.queryWeekLineByLimit(tscode,59);
            List<Double> closes = new ArrayList<>(60);
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
        List<Double> avgs5 = StockAlgorithm.calculate(closes,5);
        if(avgs5.isEmpty()){
            return;
        }
        map.put("five_price",avgs5.get(avgs5.size()-1));
        List<Double> avgs10 = StockAlgorithm.calculate(closes,10);
        if(avgs10.isEmpty()){
            return;
        }
        map.put("ten_price",avgs10.get(avgs10.size()-1));
        List<Double> avgs20 = StockAlgorithm.calculate(closes,20);
        if(avgs20.isEmpty()){
            return;
        }
        map.put("twenty_price",avgs20.get(avgs20.size()-1));
        List<Double> avgs30 = StockAlgorithm.calculate(closes,30);
        if(avgs30.isEmpty()){
            return;
        }
        map.put("thirty_price",avgs30.get(avgs30.size()-1));
        List<Double> avgs60 = StockAlgorithm.calculate(closes,60);
        if(avgs60.isEmpty()){
            return;
        }
        map.put("sixty_price",avgs60.get(avgs60.size()-1));
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
        int limit = 60;
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
//            angleDayFilter(list);
//            demonLine(list);
//            parallelDay(list);
            dayLinePeriod(list);
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
        angleFilter(list);
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

    public void angleFilter(List<KLineEntity> list){
        double angle1 = StockAlgorithm.calculateAngle(list.get(0).getFivePrice(),list.get(1).getFivePrice());
        double sub1 = list.get(0).getFivePrice() - list.get(0).getTwentyPrice();
        if(list.get(0).getTenPrice() == 0){
            System.out.println("############################"+list.get(0).getTsCode());
            return;
        }
        double radio1 = BigDecimalUtil.div(sub1,list.get(0).getTwentyPrice(),2);
        double pctchg = BigDecimalUtil.add(list.get(0).getPctChg(),list.get(1).getPctChg(),2);
        pctchg = BigDecimalUtil.add(pctchg,list.get(2).getPctChg(),2);

//        double angle1 = StockAlgorithm.calculateAngle(list.get(0).getFivePrice(),list.get(1).getFivePrice());
//        double sub1 = list.get(0).getFivePrice() - list.get(0).getTwentyPrice();
//        if(list.get(0).getTenPrice() == 0){
//            System.out.println("############################"+list.get(0).getTsCode());
//            return;
//        }
//        double radio1 = BigDecimalUtil.div(sub1,list.get(0).getTwentyPrice(),2);

//        System.out.println(angle+"==================================="+list.get(0).getTsCode());
        if(angle1 > 70 && radio1 < 0.1){
            System.out.println(angle1+"=================="+radio1+"================="+list.get(0).getTsCode()+"============="+pctchg);
        }
    }

    public void angleDayFilter(List<KLineEntity> list){
//        double angle3 = StockAlgorithm.calculateAngle(list.get(2).getFivePrice(),list.get(3).getFivePrice());
//        double angle4 = StockAlgorithm.calculateAngle(list.get(3).getFivePrice(),list.get(4).getFivePrice());
//        double angle5 = StockAlgorithm.calculateAngle(list.get(4).getFivePrice(),list.get(5).getFivePrice());
//        double angle5 = StockAlgorithm.calculateAngle(list.get(6).getFivePrice(),list.get(7).getFivePrice());
//        double angle6 = StockAlgorithm.calculateAngle(list.get(5).getFivePrice(),list.get(6).getFivePrice());
//        System.out.println(angle1+"=="+angle2+"==="+angle3+"==="+angle4);
        double sub1 = list.get(0).getFivePrice() - list.get(0).getTwentyPrice();
        if(list.get(0).getTenPrice() == 0){
            System.out.println("############################"+list.get(0).getTsCode());
            return;
        }
        double radio1 = BigDecimalUtil.div(sub1,list.get(0).getTwentyPrice(),2);
        double pctchg = BigDecimalUtil.add(list.get(0).getPctChg(),list.get(1).getPctChg(),2);
        pctchg = BigDecimalUtil.add(pctchg,list.get(2).getPctChg(),2);
        double angle1 = StockAlgorithm.calculateAngle(list.get(0).getFivePrice(),list.get(1).getFivePrice());
        double angle2 = StockAlgorithm.calculateAngle(list.get(2).getFivePrice(),list.get(3).getFivePrice());
        double angle = angle1 - angle2;
        if((angle1 > 20 && angle2 < 0) || angle > 70){
            System.out.println(angle1+"=================="+radio1+"================="+list.get(0).getTsCode()+"============="+pctchg);
        }
    }


    public void parallelWeek(List<KLineEntity> list){
        if(list.isEmpty() || list.size() < 10){
            return;
        }

        //小于20日均线全部剔除
        if(list.get(0).getPctChg() <= 0){
            if(list.get(0).getFivePrice() - list.get(0).getTwentyPrice() < 0 ||
                    list.get(0).getFivePrice() - list.get(0).getTenPrice() < 0){
                return;
            }
        }else {
            if(list.get(0).getFivePrice() - list.get(1).getTenPrice() < 0){
                return;
            }
            if(list.get(0).getFivePrice() - list.get(1).getTwentyPrice() < 0){
                return;
            }
        }
        for(int i =0;i < 5;i++){
            //当前5日内10日均线必须在20日均线以上
            if(list.get(i).getFivePrice() - list.get(i).getTwentyPrice()<0){
                return;
            }
            if(list.get(i).getTenPrice() - list.get(i).getTwentyPrice()<0){
                return;
            }
            double sub = list.get(i).getFivePrice() - list.get(i).getTwentyPrice();
            double ratio = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
            if(ratio > 0.04){
                return;
            }
        }
        int sing = 0;
        double pctchg = 0.0;
        for(int i = 0;i < (list.size()-1);i++){
            KLineEntity entity1 = list.get(i);
            KLineEntity entity2 = list.get(i+1);
            if(entity1.getFivePrice() - entity2.getFivePrice() >= 0){
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
            double sub = list.get(0).getClose() - list.get(0).getTwentyPrice();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
        }else {
            double sub = list.get(0).getOpen() - list.get(0).getTwentyPrice();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
        }
        System.out.println(list.get(0).getTsCode()+"======"+pctchg+"========"+sing+"============"+ratio1);
    }

    public void parallelDay(List<KLineEntity> list){
        if(list.isEmpty() || list.size() < 10){
            return;
        }

        if(list.get(0).getPctChg() <= 0){
            //5日均线在20日以下剔除
            if(list.get(0).getFivePrice() - list.get(1).getTwentyPrice() < 0){
                return;
            }
            //5日10日20日都在30日以下剔除
            if(list.get(0).getFivePrice() - list.get(0).getThirtyPrice() < 0 &&
                    list.get(0).getTenPrice() - list.get(0).getThirtyPrice() < 0 &&
                    list.get(0).getTwentyPrice() - list.get(0).getThirtyPrice() < 0 ){
                return;
            }
            //当前连续三日下跌剔除
            if(list.get(0).getFivePrice() - list.get(1).getFivePrice() < 0 &&
                    list.get(1).getFivePrice() - list.get(2).getFivePrice() < 0 &&
                    list.get(2).getFivePrice() - list.get(3).getFivePrice() < 0){
                return;
            }
        }else {
            if(list.get(0).getFivePrice() - list.get(1).getTwentyPrice() < 0){
                return;
            }
        }
        for(int i =0;i < 5;i++){
            //当前5日内10日均线必须在20日均线以上
            if(list.get(i).getFivePrice() - list.get(i).getTwentyPrice()<0){
                return;
            }
            if(list.get(i).getTenPrice() - list.get(i).getTwentyPrice()<0){
                return;
            }
            double sub = list.get(i).getFivePrice() - list.get(i).getTwentyPrice();
            double ratio = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
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
            if(entity1.getFivePrice() - entity2.getFivePrice() >= 0){
                sing++;
            }else if(sing < 3) {
                sing = 0;
            }
            if(sing>=3 && i < 5){
                prev = "前五";
            }
            if(entity1.getPctChg() > 5 && entity1.getPctChg() < 9){
               if(entity1.getOpen() > entity1.getFivePrice()
                       || entity1.getClose() < entity1.getFivePrice()){
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
            double sub = list.get(0).getClose() - list.get(0).getTwentyPrice();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
        }else {
            double sub = list.get(0).getOpen() - list.get(0).getTwentyPrice();
            ratio1 = BigDecimalUtil.div(sub,list.get(0).getTwentyPrice(),2);
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
        //10日内必须趋势向上
        int size = list.size() - 1;
        if(list.get(0).getFivePrice() -
                list.get(size).getFivePrice() < 0){
            return;
        }
        //当前价格小于20剔除
        if(list.get(0).getClose() -
                list.get(0).getTwentyPrice() < 0 ||
                list.get(0).getLow() - list.get(0).getTwentyPrice() < 0){
            return;
        }
        //5小于10或小于20剔除
        if(list.get(0).getFivePrice() - list.get(1).getTenPrice() < 0
                || list.get(0).getFivePrice() - list.get(1).getTwentyPrice() <= 0){
            return;
        }
        //10小于20剔除
        if(list.get(0).getTenPrice() - list.get(1).getTwentyPrice() < 0){
            return;
        }
        //5、10、20都在30以下剔除
        if(list.get(0).getFivePrice() - list.get(0).getThirtyPrice() < 0 &&
                list.get(0).getTenPrice() - list.get(0).getThirtyPrice() < 0 &&
                list.get(0).getTwentyPrice() - list.get(0).getThirtyPrice() < 0 ){
            return;
        }
        //K线与20K比值
        double subtt0 = list.get(0).getLow() - list.get(0).getTwentyPrice();
        double ratiott0 = BigDecimalUtil.div(subtt0,list.get(0).getTwentyPrice(),2);
        if(ratiott0 >= 0.03){
            return;
        }
        //10与20K比值
        double subtt1 = list.get(0).getTwentyPrice() - list.get(0).getThirtyPrice();
        double ratiott1 = BigDecimalUtil.div(subtt1,list.get(0).getThirtyPrice(),4);
        if(ratiott1 > 0.03){
            return;
        }
        //20与30K比值
        double subtt2 = list.get(0).getTwentyPrice() - list.get(0).getThirtyPrice();
        double ratiott2 = BigDecimalUtil.div(subtt2,list.get(0).getThirtyPrice(),4);
        if(ratiott2 > 0.01 || ratiott2 < -0.01){
            return;
        }
        //30与60K比值
        double subtt3 = list.get(0).getThirtyPrice() - list.get(0).getSixtyPrice();
        double ratiott3 = BigDecimalUtil.div(subtt2,list.get(0).getSixtyPrice(),4);
        System.out.println(subtt1+"============"+ratiott3);
        if(subtt3 > 0.01 || ratiott3 < -0.01){
            return;
        }
        //连续6日5在20以上,并且没有大波动
        for (int i = 0;i < 6;i++){
            if(list.get(i).getFivePrice() -
                    list.get(i).getTwentyPrice() < 0){
                return;
            }
            if(list.get(i).getPctChg() > 5 || list.get(i).getPctChg() < -4){
                return;
            }
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
            double sub = entity1.getFivePrice() - entity2.getFivePrice();
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

    public void dayLinePeriod(List<KLineEntity> list){
        Double [] fiveArrays = list.stream().map(o -> o.getFivePrice()).collect(Collectors.toList()).toArray(new Double[list.size()]);
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
        List<Map<String,String>> lineStatus = new ArrayList<>();
        for (KLineEntity entity:peeks) {
            Map<String,String> bandMap = klineDistribute(entity);
            lineStatus.add(bandMap);
        }
        //对比波峰突破均线数量
        Map<String,Map<String,String>> breaks = new HashMap<>();
        for(int i = 0;i < (lineStatus.size()-1);i++){
            Map<String,String> item = new HashMap<>();
            Map<String,String> line1 = lineStatus.get(i);
            Map<String,String> line2 = lineStatus.get(i+1);
            for(Map.Entry<String,String> entry : line1.entrySet()){
                if("1".equals(entry.getValue()) &&
                        "0".equals(line2.get(entry.getKey()))){
                    item.put(entry.getKey(),"1");
                }
            }
            if(!item.isEmpty()){
                breaks.put(String.valueOf(i),item);
            }
        }
        System.out.println(JSON.toJSONString(peeks));
        System.out.println(JSON.toJSONString(ravines));
    }

    public void weekLinePeriod(List<KLineEntity> list){
        Double [] fiveArrays = list.stream().map(o -> o.getFivePrice()).collect(Collectors.toList()).toArray(new Double[list.size()]);
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
            if(ravines.get(i).getFivePrice()
                    - ravines.get(i - 1).getFivePrice() < 0){
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
        double rose = entity.getFivePrice() - bandEntity.getFivePrice();
        //上涨幅度
        double roseRatio = BigDecimalUtil.div(rose,bandEntity.getFivePrice(),2);
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
            if(list.get(i).getFivePrice() - list.get(i-1).getFivePrice() > 0){
                day++;
                continue;
            }
            break;
        }
        //均线变化
        Map<String,String> bandMap = klineDistribute(bandEntity);
        Map<String,String> realMap = klineDistribute(list.get(size));
        Map<String,String> breaks = thanKline(bandMap,realMap);
        System.out.println(bandEntity.getTsCode()+"========"+day+"========="+breaks);
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

    public Map<String,String> klineDistribute(KLineEntity entity){
        String breakFiveTen = "未知";
        if(entity.getFivePrice() - entity.getTenPrice() > 0){
            breakFiveTen = "1";
        }else {
            breakFiveTen = "0";
        };

        String breakFiveTwenty = "未知";
        String breakTenTwenty = "未知";
        if(entity.getTwentyPrice() > 0){
            if(entity.getFivePrice() - entity.getTwentyPrice() > 0){
                breakFiveTwenty = "1";
            }else {
                breakFiveTwenty = "0";
            };
            if(entity.getTenPrice() - entity.getTwentyPrice() > 0){
                breakTenTwenty = "1";
            }else {
                breakTenTwenty = "0";
            };
        }

        String breakFiveThirty = "未知";
        String breakTenThirty = "未知";
        String breakTwentyThirty = "未知";
        if(entity.getThirtyPrice() > 0){

            if(entity.getFivePrice() - entity.getThirtyPrice() > 0){
                breakFiveThirty = "1";
            }else {
                breakFiveThirty = "0";
            };

            if(entity.getTenPrice() - entity.getThirtyPrice() > 0){
                breakTenThirty = "1";
            }else {
                breakTenThirty = "0";
            };

            if(entity.getTwentyPrice() - entity.getThirtyPrice() > 0){
                breakTwentyThirty = "1";
            }else {
                breakTwentyThirty = "0";
            };
        }

        String breakFiveSixty = "未知";
        String breakTenSixty = "未知";
        String breakTwentySixty = "未知";
        String breakThirtySixty = "未知";
        if(entity.getSixtyPrice() > 0){
            if(entity.getFivePrice() - entity.getSixtyPrice() > 0){
                breakFiveSixty = "1";
            }else {
                breakFiveSixty = "0";
            };
            if(entity.getTenPrice() - entity.getSixtyPrice() > 0){
                breakTenSixty = "1";
            }else {
                breakTenSixty = "0";
            };
            if(entity.getTwentyPrice() - entity.getSixtyPrice() > 0){
                breakTwentySixty = "1";
            }else {
                breakTwentySixty = "0";
            };
            if(entity.getThirtyPrice() - entity.getSixtyPrice() > 0){
                breakThirtySixty = "1";
            }else {
                breakThirtySixty = "0";
            };
        }
        Map<String,String> map = new HashMap<>();
        map.put("breakFiveTen",breakFiveTen);
        map.put("breakFiveTwenty",breakFiveTwenty);
        map.put("breakTenTwenty",breakTenTwenty);
        map.put("breakFiveThirty",breakFiveThirty);
        map.put("breakTenThirty",breakTenThirty);
        map.put("breakTwentyThirty",breakTwentyThirty);
        map.put("breakFiveSixty",breakFiveSixty);
        map.put("breakTenSixty",breakTenSixty);
        map.put("breakTwentySixty",breakTwentySixty);
        map.put("breakThirtySixty",breakThirtySixty);
        return map;
    }


    public Map<String,String> ravineTrun(int index,List<KLineEntity> list){
        Map<String,String> map = new HashMap<>();
        //波峰最高价
        double price = limitMax(index,list);
        int size = list.size() - 1;
        double sub = list.get(size).getFivePrice() - price;
        String isBreak = "0";
        //突破上次波峰
        if(sub > 0){
            isBreak = "1";
        }
        String isTrun = "0";
        //开始转头
        if(list.get(size).getFivePrice() - list.get(size-1).getFivePrice() < 0){
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
        double sub = list.get(size).getFivePrice() - price;
        String isBreak = "0";
        //突破上次波谷
        if(sub > 0){
            isBreak = "1";
        }
        String isTrun = "0";
        //开始转头
        if(list.get(size).getFivePrice() - list.get(size-1).getFivePrice() > 0){
            isTrun = "1";
        }
        map.put("isTrun",isTrun);
        map.put("isBreak",isBreak);
        return map;
    }

    private double limitMin(int index,List<KLineEntity> list){
        int end = index > 4 ? 5 : index;
        double min = list.get(index).getFivePrice();
        for(int i = 1;i < end;i++){
            if(min > list.get(index-i).getFivePrice()){
                min = list.get(index-i).getFivePrice();
            }
        }
        return min;
    }

    private double limitMax(int index,List<KLineEntity> list){
        int end = index > 4 ? 5 : index;
        double min = list.get(index).getFivePrice();
        for(int i = 1;i < end;i++){
            if(min < list.get(index-i).getFivePrice()){
                min = list.get(index-i).getFivePrice();
            }
        }
        return min;
    }
}
