package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;

import java.util.List;

/**
 * @author gaijf
 * @description 持续性计算 默认方向向上
 * @date 2021/1/14
 */
public class KlineContinueRule
        extends AbstractBaseRule<List<KLineEntity>,Integer>
        implements MaLineRule<List<KLineEntity>, EmaLineType,Integer>{

    private boolean isContinue = false;

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    @Override
    public Integer verify(List<KLineEntity> entitys) {
        return null;
    }

    @Override
    public Integer verify(List<KLineEntity> entitys, EmaLineType lineType) {
        if(null == entitys || entitys.isEmpty()){
            return null;
        }
        int num = 0;
        int maxNum = 0;
        double prev = 0;
        for (int i = 0;i < entitys.size();i++) {
            KLineEntity entity = entitys.get(i);
            double kline = klineVal(entity,lineType);
            if(0 == i){
                prev = kline;
                continue;
            }
            if(isContinue){
                num = prev >= kline ? (num+1) : num;
            }else {
                num = prev >= kline ? (num+1) : 0;
            }
            maxNum = num > maxNum ? num : maxNum;
            prev = kline;
        }
        return maxNum;
    }

    /**
     *
     * @param entitys
     * @param lineType
     * @param limit
     * @return 向上天数
     */
    public Integer verify(List<KLineEntity> entitys,
                          EmaLineType lineType, int limit) {
        if(null == entitys ||
                entitys.isEmpty()){
            return null;
        }
        return verify(entitys.subList(0,limit),lineType);
    }
}
