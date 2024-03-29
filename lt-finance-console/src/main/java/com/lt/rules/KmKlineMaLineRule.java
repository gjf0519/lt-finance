package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;
import com.lt.utils.MathUtil;

/**
 * @author gaijf
 * @description K线与均线距离
 * @date 2021/1/15
 */
public class KmKlineMaLineRule
        extends AbstractBaseRule<KLineEntity,Double>
        implements MaLineRule<KLineEntity, EmaLineType,Double>{

    private static EmaLineType LINETYPE = EmaLineType.LINE020;

    @Override
    public Double verify(KLineEntity entity) {
        if(null == entity){
            return null;
        }
        return verify(entity, LINETYPE);
    }

    @Override
    public Double verify(KLineEntity entity, EmaLineType lineType) {
        if(null == entity){
            return null;
        }
        double kline = klineVal(entity,lineType);
        if(0 == kline){
            return null;
        }
        double ratio;
        if(entity.getClose() >= entity.getOpen()){//阳线
            ratio = MathUtil.sub(
                    MathUtil.div(entity.getLow(),kline,2), 1,3);
        }else {
            ratio = MathUtil.sub(
                    MathUtil.div(entity.getHigh(),kline,2), 1,2);
        }
        return ratio;
    }
}
