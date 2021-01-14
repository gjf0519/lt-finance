package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public abstract class AbstractBaseRule<T,R> implements BaseRule<T,R> {

    public List<Double> klineVals(List<KLineEntity> list,
                                   MaLineType lineType){
        List<Double> values = new ArrayList<>();
        for(KLineEntity entity : list){
            values.add(klineVal(entity,lineType));
        }
        return values;
    }

    public double klineVal(KLineEntity entity,MaLineType lineType){
        double kline = 0;
        switch (lineType.getCode()){
            case 5:
                kline = entity.getMaFive();
                break;
            case 10:
                kline = entity.getMaFive();
                break;
            case 20:
                kline = entity.getMaFive();
                break;
            case 30:
                kline = entity.getMaFive();
                break;
            case 60:
                kline = entity.getMaFive();
                break;
            case 120:
                kline = entity.getMaFive();
                break;
            case 250:
                kline = entity.getMaFive();
                break;
            default:
                kline = 0;
        }
        return kline;
    }
}
