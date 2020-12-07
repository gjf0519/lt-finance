package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import com.lt.entity.EmaBreakEntity;
import com.lt.utils.AverageAlgorithm;
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
        int limit = 11;
        List<KLineEntity> list = kLineService.queryDayLineByLimit(tscode,limit);
        EmaBreakEntity entity = TwentyPrice(list, limit,"日K");
        if(null == entity){
            return;
        }
        entity.setTsCode(tscode);
        kLineService.saveEmaBreak(entity);
    }

    /**
     * 周K均线突破
     * @param tscode
     */
    public void weekLineBreak(String tscode){
        int limit = 11;
        List<KLineEntity> list = kLineService.queryWeekLineByLimit(tscode,limit);
        EmaBreakEntity entity = TwentyPrice(list, limit,"周K");
        if(null == entity){
            return;
        }
        entity.setTsCode(tscode);
        kLineService.saveEmaBreak(entity);
    }

    /**
     * 20日均线计算
     * @param list
     * @param limit
     * @param klineType
     * @return
     */
    public EmaBreakEntity TwentyPrice(List<KLineEntity> list, int limit, String klineType){
        if(list.get(0).getTwentyPrice() <= 0){
            return null;
        }
        //均线方向 -> 均线头向下
        if(list.get(0).getTwentyPrice() - list.get(limit-1).getTwentyPrice() > 0){
            return null;
        }
        int num = 0;
        for(KLineEntity entity : list){
            if((entity.getClose() - entity.getTwentyPrice()) < 0
                    || (entity.getOpen() - entity.getTwentyPrice()) < 0){
                return null;
            }
            num++;
        }

        return EmaBreakEntity.builder()
                .klineType(klineType)
                .breakType("20")
                .rose(list.get(0).getPctChg())
                .tradeDate(list.get(0).getTradeDate())
                .risingNumber(num)
                .build();
    }
}
