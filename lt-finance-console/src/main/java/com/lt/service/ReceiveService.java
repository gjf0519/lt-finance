package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.entity.EmaBreakEntity;
import com.lt.shape.AverageAlgorithm;
import com.lt.utils.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
     * @param record
     */
    public void receiveDayLine(String record) {
        try {
            Map map =  JSON.parseObject(record, Map.class);
            String tscode = map.get("ts_code").toString();
            String tradeDate = map.get("trade_date").toString();
            //判断日K数据是否已保存
            int isSave = kLineService.hasSaveDayLine(tscode,tradeDate);
            if(isSave > 0){
                return;
            }
            List<KLineEntity> list = kLineService.queryDayLineByLimit(tscode,59);
            List<Double> closes = new ArrayList<>(60);
            closes.add(Double.valueOf(map.get("close").toString()));
            for(KLineEntity item : list){
                closes.add(item.getClose());
            }
            Collections.reverse(closes);
            //计算均线价格
            calculateAvg(closes,map);
            kLineService.saveDayLine(map);
            //过滤均线突破数据
            dayLineBreak(tscode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void receiveWeekLine(String record) {
        try {
            Map map =  JSON.parseObject(record, Map.class);
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
            //过滤均线突破数据
            weekLineBreak(tscode);
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
        List<Double> avgs5 = AverageAlgorithm.calculate(closes,5);
        if(avgs5.isEmpty()){
            return;
        }
        map.put("five_price",avgs5.get(avgs5.size()-1));
        List<Double> avgs10 = AverageAlgorithm.calculate(closes,10);
        if(avgs10.isEmpty()){
            return;
        }
        map.put("ten_price",avgs10.get(avgs10.size()-1));
        List<Double> avgs20 = AverageAlgorithm.calculate(closes,20);
        if(avgs20.isEmpty()){
            return;
        }
        map.put("twenty_price",avgs20.get(avgs20.size()-1));
        List<Double> avgs30 = AverageAlgorithm.calculate(closes,30);
        if(avgs30.isEmpty()){
            return;
        }
        map.put("thirty_price",avgs30.get(avgs30.size()-1));
        List<Double> avgs60 = AverageAlgorithm.calculate(closes,60);
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
        int limit = 21;
        List<KLineEntity> list = kLineService.queryDayLineByLimit(tscode,limit);
//        EmaBreakEntity entity = klineBreak(list,"日K");
//        if(null == entity){
//            return;
//        }
//        entity.setTsCode(tscode);
//        kLineService.saveEmaBreak(entity);
        EmaBreakEntity angleEntity = angleKline(list,"日K");
        if(null == angleEntity){
            return;
        }
        angleEntity.setTsCode(tscode);
        kLineService.saveEmaBreak(angleEntity);
    }

    /**
     * 周K均线突破
     * @param tscode
     */
    public void weekLineBreak(String tscode){
        int limit = 15;
        List<KLineEntity> list = kLineService.queryWeekLineByLimit(tscode,limit);
        if(list.isEmpty()){
            return;
        }
        EmaBreakEntity entity = klineRise(list,"周K");
        if(null == entity){
            return;
        }
        entity.setTsCode(tscode);
        kLineService.saveEmaBreak(entity);
    }

    /**
     * 连续两日K线上涨
     * @param list
     * @param klineType
     * @return
     */
    public EmaBreakEntity klineRise(List<KLineEntity> list,String klineType){
        if(list.get(0).getPctChg() > 0.5){
            return null;
        }
        if(list.get(0).getPctChg() < list.get(1).getPctChg()){
            return null;
        }
        if(list.get(0).getPctChg() < 0 || list.get(0).getPctChg() > 0.05){
            return null;
        }
        if(list.get(1).getPctChg() > 0.05 || list.get(1).getPctChg() < 0){
            return null;
        }
        for(KLineEntity entity : list){
            double middle = entity.getTenPrice() - entity.getTwentyPrice();
            if(middle == 0){
                continue;
            }
            if(entity.getTwentyPrice() == 0){
                return null;
            }
            //均线之间差值
            double rito = BigDecimalUtil.div(middle,entity.getTwentyPrice(),2);
            if(rito > 0.05 || rito < -0.05){
                return null;
            }
        }
        if(list.get(0).getFivePrice() - list.get(0).getTenPrice() < 0){
            return null;
        }
        if(list.get(1).getFivePrice() - list.get(1).getTenPrice() >= 0){
            return null;
        }
        int breakDay = 0;
        return EmaBreakEntity.builder()
                .klineType(klineType)
                .fivetoten("1")
                .fivetotwenty("1")
                .fivetothirty("1")
                .tentotwenty("1")
                .tentothirty("1")
                .twentytothirty("1")
                .rose(list.get(0).getPctChg())
                .breakDay(breakDay)
                .tradeDate(list.get(0).getTradeDate())
                .build();
    }

    /**
     * 日K三连涨形态
     * @param list
     * @param klineType
     * @return
     */
    public EmaBreakEntity klineBreak(List<KLineEntity> list,String klineType){
        if(list.isEmpty()){
            return null;
        }
        int lastNum = list.size() - 1;
        //判断5日线均线方向
        double rose = list.get(0).getFivePrice() - list.get(lastNum).getFivePrice();
        if(rose < 0){
            return null;
        }
        if(list.get(lastNum).getFivePrice() == 0){
            return null;
        }
        //判断20内总涨幅
        double rito = BigDecimalUtil.div(rose,list.get(lastNum).getFivePrice(),2);
        if(rito > 0.02){
            return null;
        }
        //判断5日均线是否突破10日均线
        if(list.get(0).getFivePrice() - list.get(0).getTenPrice() < 0){
            return null;
        }
        //当天最低价与与10均线距离
        double distance = list.get(0).getLow() - list.get(0).getTenPrice();
        double distanceRito = BigDecimalUtil.div(distance,list.get(0).getTenPrice(),2);
        if(distanceRito > 0.015){
            return null;
        }
        //判断5日均线是否突破20日均线
        if(list.get(0).getFivePrice() - list.get(0).getTwentyPrice() < 0){
            return null;
        }
        //判断10日均线是否突破20日均线
        if(list.get(0).getFivePrice() - list.get(0).getTwentyPrice() < 0){
            return null;
        }
        int roseDay = 0;
        for(int i = 0;i < list.size();i++){
            KLineEntity entity = list.get(i);
            //最近4天是否3连涨
            if(i < 4){
                if(entity.getPctChg() >= 0){
                    roseDay++;
                }
            }
            //10日内没有大涨或大跌
            if(i < 10){
                if(entity.getPctChg() > 4 || entity.getPctChg() < -4){
                    return null;
                }
            }
        }
        if(roseDay < 3){
            return null;
        }
        double angle = AverageAlgorithm.
                calculateAngle(list.get(0).getFivePrice(),list.get(1).getFivePrice());
        return EmaBreakEntity.builder()
                .klineType(klineType)
                .klineFlat("3连涨")
                .klineAngle(angle)
                .fivetoten("1")
                .fivetotwenty("1")
                .tentotwenty("1")
                .rose(rose)
                .breakDay(roseDay)
                .tradeDate(list.get(0).getTradeDate())
                .build();
    }

    /**
     * 45°角2日内突破10日均线
     * @param list
     * @param klineType
     * @return
     */
    public EmaBreakEntity angleKline(List<KLineEntity> list,String klineType){
        if(list.isEmpty()){
            return null;
        }
        if(list.get(0).getFivePrice() - list.get(0).getTenPrice() < 0){
            return null;
        }
        double angle = AverageAlgorithm.
                calculateAngle(list.get(0).getFivePrice(),list.get(1).getFivePrice());
        if(angle < 45){
            return null;
        }
        if(list.get(0).getPctChg() < 0 || list.get(0).getPctChg() > 5){
            return null;
        }
        int lastNum = list.size() - 1;
        double rose = list.get(0).getFivePrice() - list.get(lastNum).getFivePrice();
        double rito = BigDecimalUtil.div(rose,list.get(lastNum).getFivePrice(),2);
        if(rito > 0.02){
            return null;
        }
        if(list.get(2).getFivePrice() - list.get(2).getTenPrice() > 0){
            return null;
        }
        String fivetotwenty = "0";
        //判断5日均线是否突破20日均线
        if(list.get(0).getFivePrice() - list.get(0).getTwentyPrice() < 0){
            fivetotwenty = "1";
        }
        String tentotwenty = "0";
        //判断10日均线是否突破20日均线
        if(list.get(0).getFivePrice() - list.get(0).getTwentyPrice() < 0){
            tentotwenty = "1";
        }
        return EmaBreakEntity.builder()
                .klineType(klineType)
                .klineFlat("45°角2日内突破10日均线")
                .klineAngle(angle)
                .fivetoten("1")
                .fivetotwenty(fivetotwenty)
                .tentotwenty(tentotwenty)
                .rose(rose)
                .tradeDate(list.get(0).getTradeDate())
                .build();
    }
}
