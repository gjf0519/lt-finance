package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gaijf
 * @description 均线排列状态
 * @date 2021/1/15
 */
public class MaLineArrangeRule extends AbstractBaseRule<KLineEntity,Integer>
        implements MaLineRule<KLineEntity,List<EmaLineType>,Integer>{

    public static List<EmaLineType> TYPES = Arrays.asList(EmaLineType.LINE005,
            EmaLineType.LINE010, EmaLineType.LINE020, EmaLineType.LINE030, EmaLineType.LINE060);

    /**
     * 默认计算5/10/20/30/60排列情况
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
    public Integer verify(KLineEntity kLineEntitie,List<EmaLineType> maLineTypes) {
        List<Double> items = new ArrayList<>(maLineTypes.size());
        for(EmaLineType lineType : maLineTypes){
            items.add(klineVal(kLineEntitie,lineType));
        }
        int dwnum = 0;
        int hihum = 0;
        double prev = items.get(0);
        for(int i = 1;i < items.size();i++){
            if(prev >= items.get(i)){
                hihum++;
            }else{
                dwnum++;
            }
            prev = items.get(i);
        }
        int size = items.size() - 1;
        if(hihum == size){
            return 1;
        }
        if(dwnum == size){
            return -1;
        }
        return 0;
    }
}
