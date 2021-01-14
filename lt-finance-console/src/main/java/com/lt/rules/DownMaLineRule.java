package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 回踩均线 -1下方0穿过1上方
 * @date 2021/1/14
 */
public class DownMaLineRule
        extends AbstractBaseRule<KLineEntity,Map<String,Integer>> implements MaLineRule<KLineEntity,Map<String,Integer>>{

    public static int [] SITES = new int[]{1,0,-1};

    @Override
    public Map<String,Integer> verify(KLineEntity entity) {
        if(null == entity){
            return null;
        }
        Map<String,Integer> map = new HashMap<>();
        for(MaLineType lineType :
                MaLineType.values()){
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
        return null;
    }
}
