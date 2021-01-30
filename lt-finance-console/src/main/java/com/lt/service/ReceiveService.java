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
}
