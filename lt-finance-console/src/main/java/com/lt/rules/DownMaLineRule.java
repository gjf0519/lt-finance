package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;

import java.util.List;

/**
 * @author gaijf
 * @description K线与均线位置
 * @date 2021/1/14
 */
public class DownMaLineRule
        extends AbstractBaseRule<List<KLineEntity>,Integer>
        implements MaLineRule<List<KLineEntity>,Integer,Integer>{

    private EmaLineType breakLine = EmaLineType.LINE020;
    private EmaLineType directionalLine = EmaLineType.LINE005;

    public DownMaLineRule() {
        super();
    }

    public DownMaLineRule(EmaLineType breakLine, EmaLineType directionalLine) {
        this.breakLine = breakLine;
        this.directionalLine = directionalLine;
    }

    @Override
    public Integer verify(List<KLineEntity> entitys) {
        if(null == entitys || entitys.isEmpty()){
            return null;
        }
        return verify(entitys,5);
    }

    /**
     *
     * @param entitys 数据
     * @param limit 数据范围
     * @return -1破线或连续下跌2次0回踩1拐头
     */
    @Override
    public Integer verify(List<KLineEntity> entitys,Integer limit) {
        if(null == entitys || entitys.isEmpty()){
            return null;
        }
        double pckchg = entitys.get(0).getPctChg();
        int num = 0;
        double prev = 0;
        for (int i = 0;i < limit;i++) {
            KLineEntity entity = entitys.get(i);
            double breakVal = klineVal(entity,this.breakLine);
            double directional = klineVal(entity,this.directionalLine);
            double price = entity.getPctChg() >= 0 ?
                    entity.getOpen() : entity.getClose();
            if(0 == i){
                if(price < breakVal){
                    return -1;
                }
                prev = directional;
                continue;
            }
            num = prev < directional ? (num+1) : 0;
            if(num == 2 && pckchg < 0){
                return -1;
            }
            prev = directional;
        }
        int result = entitys.get(0).getClose() > entitys.get(1).getClose() ? 1 : 0;
        return result;
    }
}
