package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description K线与均线位置
 * @date 2021/1/14
 */
public class SiteKlineMaLineRule
        extends AbstractBaseRule<KLineEntity,Map<String,Integer>>
        implements MaLineRule<KLineEntity,MaLineType,Map<String,Integer>>{

    //1上0交-1下
    public static int [] SITES = new int[]{1,0,-1};
    public static List<MaLineType> TYPES = Arrays.asList(MaLineType.LINE005,
            MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030);

    @Override
    public Map<String,Integer> verify(KLineEntity entity) {
        if(null == entity){
            return null;
        }
        Map<String,Integer> map = new HashMap<>();
        for(MaLineType lineType : TYPES){
            Map<String,Integer> item = verify(entity, lineType);
            if(null == item){
                continue;
            }
            map.put(lineType.getName(),
                            item.get(lineType.getName()));
        }
        return map;
    }

    @Override
    public Map<String,Integer> verify(KLineEntity entity,
                       MaLineType lineType) {
        if(null == entity){
            return null;
        }
        double kline = klineVal(entity,lineType);
        if(0 == kline){
            return null;
        }
        Map<String,Integer> map = new HashMap<>();
        double open = entity.getOpen();
        double close = entity.getClose();
        if(open >= kline && close >= kline){
            map.put(lineType.getName(),SITES[0]);
            return map;
        }
        if((open >= kline && close <= kline)
                || (open <= kline && close >= kline)){
            map.put(lineType.getName(),SITES[1]);
            return map;
        }
        if(open <= kline && close <= kline){
            map.put(lineType.getName(),SITES[2]);
            return map;
        }
        return map;
    }
}
