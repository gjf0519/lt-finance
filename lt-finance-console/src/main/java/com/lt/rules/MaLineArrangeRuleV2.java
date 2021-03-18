package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description 均线排列状态
 * 位置、夹角、突破
 * @date 2021/1/15
 */
public class MaLineArrangeRuleV2 extends AbstractBaseRule<KLineEntity,Integer>
        implements MaLineRule<KLineEntity,List<MaLineType>,Integer>{

    public static List<MaLineType> TYPES = Arrays.asList(MaLineType.LINE005,
            MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030,MaLineType.LINE060,MaLineType.LINE120,MaLineType.LINE250);

    /**
     * 默认就算全部排列情况
     * @param kLineEntitie
     * @return 1全上0交叉-1全下
     */
    @Override
    public Integer verify(KLineEntity kLineEntitie) {
        return verify(kLineEntitie,TYPES);
    }

    /**
     *
     * @param kLineEntitie
     * @param maLineTypes
     * @return 1全上0交叉-1全下
     */
    @Override
    public Integer verify(KLineEntity kLineEntitie,List<MaLineType> maLineTypes) {
        List<Double> items = new ArrayList<>(maLineTypes.size());
        for(MaLineType lineType : maLineTypes){
            items.add(klineVal(kLineEntitie,lineType));
        }
        return 0;
    }
}
