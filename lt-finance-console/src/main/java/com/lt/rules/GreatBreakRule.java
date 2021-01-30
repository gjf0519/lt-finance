package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description 重要均线突破
 * @date 2021/1/14
 */
public class GreatBreakRule
        extends AbstractBaseRule<List<KLineEntity>,Integer>{

    private final MaLineType baseMaLineType = MaLineType.LINE060;

    @Override
    public Integer verify(List<KLineEntity> entitys) {
        KLineEntity kLineEntity = entitys.get(entitys.size()-1);
        double basekline = klineVal(kLineEntity,baseMaLineType);
        double line120 = klineVal(kLineEntity,MaLineType.LINE120);
        double line250 = klineVal(kLineEntity,MaLineType.LINE250);
        int breakNum = 0;
        KLineEntity entity = entitys.get(0);
        if(basekline < line120){
            if(entity.getMaQuarter()
                    > entity.getMaSemester()){
                breakNum++;
            }
        }
        if(basekline < line250){
            if(entity.getMaQuarter()
                    > entity.getMaYear()){
                breakNum++;
            }
        }
        if(line120 < line250){
            if(entity.getMaSemester()
                    > entity.getMaYear()){
                breakNum++;
            }
        }
        return breakNum;
    }
}
