package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author gaijf
 * @description 均线排列状态
 * 位置、夹角、突破
 * @date 2021/1/15
 */
public class MaLineArrangeRuleV2 extends AbstractBaseRule<List<KLineEntity>,Integer>{

    public static List<MaLineType> TYPES = Arrays.asList(MaLineType.LINE005,
            MaLineType.LINE010,MaLineType.LINE020,MaLineType.LINE030,MaLineType.LINE060,MaLineType.LINE120,MaLineType.LINE250);

    /**
     * 默认就算全部排列情况
     * @param kLineEntities
     * @return 1全上0交叉-1全下
     */
    @Override
    public Integer verify(List<KLineEntity> kLineEntities) {
        Collections.reverse(kLineEntities);
        int lastSize = kLineEntities.size() - 1;
        for(int i = 0;i < kLineEntities.size();i++){

        }
        return 0;
    }
}
