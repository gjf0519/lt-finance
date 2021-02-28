package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;
import com.lt.utils.BigDecimalUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaijf
 * @description K线与均线距离
 * @date 2021/1/15
 */
public class KmKlineMaLineRule
        extends AbstractBaseRule<KLineEntity,Double>
        implements MaLineRule<KLineEntity,MaLineType,Double>{

    private static MaLineType LINETYPE = MaLineType.LINE020;

    @Override
    public Double verify(KLineEntity entity) {
        if(null == entity){
            return null;
        }
        return verify(entity, LINETYPE);
    }

    @Override
    public Double verify(KLineEntity entity, MaLineType lineType) {
        if(null == entity){
            return null;
        }
        double kline = klineVal(entity,lineType);
        if(0 == kline){
            return null;
        }
        double ratio;
        if(entity.getClose() >= entity.getOpen()){//阳线
            ratio = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getLow(),kline,2), 1,3);
        }else {
            ratio = BigDecimalUtil.sub(
                    BigDecimalUtil.div(entity.getHigh(),kline,2), 1,2);
        }
        return ratio;
    }
}
