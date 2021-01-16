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
        double ratio = BigDecimalUtil.sub(
                BigDecimalUtil.div(entity.getLow(),kline,2), 1,2);
        double ratio2 = BigDecimalUtil.sub(
                BigDecimalUtil.div(entity.getClose(),kline,2), 1,2);
        ratio = ratio < -0.01 && (ratio2 == 0.01 || ratio2 == 0) ? ratio2 : ratio;
        return ratio;
    }
}
