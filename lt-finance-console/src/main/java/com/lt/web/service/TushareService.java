package com.lt.web.service;

import com.lt.entity.KLineEntity;
import com.lt.entity.RepairDataEntity;
import com.lt.web.mapper.TushareMapper;
import com.lt.utils.TushareUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 计算保存MQ数据
 * @date 2020/12/3
 */
@Service
public class TushareService {

    @Resource
    private TushareMapper tushareMapper;
    private final static int LIMIT_NUM = 144;
    private final static String CODE_KEY = "ts_code";
    private final static String DATE_KEY = "trade_date";

    public void receiveDailyBasic(Map map) {
        String tsCode = map.get(CODE_KEY).toString();
        String tradeDate = map.get(DATE_KEY).toString();
        int isSave = tushareMapper.hasSaveDailyBasic(tsCode,tradeDate);
        if(isSave <= 0){
            tushareMapper.saveDailyBasic(map);
        }
    }

    /**
     * 消费日K数据
     * @param map
     */
    public void receiveDayLine(Map<String,String> map) {
        String tsCode = map.get(CODE_KEY);
        String tradeDate = map.get(DATE_KEY);
        int isSave = tushareMapper.hasSaveDayLine(tsCode,tradeDate);
        if(isSave <= 0){
            List<KLineEntity> list = tushareMapper
                    .queryDayLineByLimit(tsCode,LIMIT_NUM);
            this.avgLine(map,list);
            tushareMapper.saveDayLine(map);
        }
    }

    /**
     * 消费周K数据
     * @param map
     */
    public void receiveWeekLine(Map<String,String> map) {
        String tsCode = map.get(CODE_KEY);
        String tradeDate = map.get(DATE_KEY);
        int isSave = tushareMapper.hasSaveWeekLine(tsCode,tradeDate);
        if(isSave <= 0){
            List<KLineEntity> list = tushareMapper
                    .queryWeekLineByLimit(tsCode,LIMIT_NUM);
            this.avgLine(map,list);
            tushareMapper.saveWeekLine(map);
        }
    }

    /**
     * 消费板块K数据
     * @param map
     */
    public void receivePlateLine(Map map) {
        String tsCode = map.get(CODE_KEY).toString();
        String tradeDate = map.get(DATE_KEY).toString();
        int isSave = tushareMapper.hasSavePlateLine(tsCode,tradeDate);
        if(isSave <= 0){
            List<KLineEntity> list = tushareMapper
                    .queryPlateLineByLimit(tsCode,LIMIT_NUM);
            this.avgLine(map,list);
            tushareMapper.savePlateLine(map);
        }
    }

    /**
     * 消费补充数据
     * @param repairDataEntity
     */
    public void receiveRepairData(RepairDataEntity repairDataEntity) {
        //判断数据是否已保存
        int isSave = tushareMapper.hasSaveRepairData(repairDataEntity);
        if(isSave <= 0){
            tushareMapper.saveRepairData(repairDataEntity);
        }
    }

    public void avgLine(Map<String,String> map,List<KLineEntity> list){
        BigDecimal[] closes = new BigDecimal[list.size()+1];
        closes[0] = new BigDecimal(map.get("close"));
        for(int i = 0;i < list.size();i++){
            closes[i+1] = new BigDecimal(list.get(i).getClose().toString());
        }
        //计算均线价格
        this.calculateAvg(closes,map);
    }

    /**
     * 计算均线价格
     * @param closes
     * @param map
     */
    public void calculateAvg(BigDecimal [] closes,Map<String,String> map){
        for (int i = 0; i < TushareUtil.MA_NUM_ARREY.length; i++) {
            if(closes.length < TushareUtil.MA_NUM_ARREY[i]){
                continue;
            }
            BigDecimal [] items = Arrays.copyOf(closes,TushareUtil.MA_NUM_ARREY[i]);
            BigDecimal decimalSum = new BigDecimal("0");
            for(BigDecimal decimal : items){
                decimalSum = decimalSum.add(decimal);
            }
            BigDecimal mean = decimalSum.divide(BigDecimal.valueOf(items.length),2, BigDecimal.ROUND_HALF_UP)
                    .setScale(2, BigDecimal.ROUND_UP);
            map.put(TushareUtil.EMA_NAME_ARRAY[i],mean.toString());
        }
    }
}
