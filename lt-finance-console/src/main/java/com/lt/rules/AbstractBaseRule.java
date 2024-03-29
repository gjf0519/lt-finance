package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public abstract class AbstractBaseRule<T,R> implements BaseRule<T,R> {

    public List<Double> klineVals(KLineEntity kLineEntity){
        List<Double> values = new ArrayList<>();
        for(EmaLineType lineType : EmaLineType.values()){
            values.add(klineVal(kLineEntity,lineType));
        }
        return values;
    }

    public List<Double> klineVals(List<KLineEntity> list,
                                   EmaLineType lineType){
        List<Double> values = new ArrayList<>();
        for(KLineEntity entity : list){
            values.add(klineVal(entity,lineType));
        }
        return values;
    }

    public double klineVal(KLineEntity entity, EmaLineType lineType){
        double kline = 0;
        switch (lineType.getCode()){
            case 5:
                kline = entity.getEmaFive();
                break;
            case 10:
                kline = entity.getEmaTen();
                break;
            case 20:
                kline = entity.getEmaTwenty();
                break;
            case 30:
                kline = entity.getEmaMonth();
                break;
            case 60:
                kline = entity.getEmaQuarter();
                break;
            case 120:
                kline = entity.getEmaHalfYear();
                break;
            case 250:
                kline = entity.getEmaFullYear();
                break;
            default:
                kline = 0;
        }
        return kline;
    }
}
