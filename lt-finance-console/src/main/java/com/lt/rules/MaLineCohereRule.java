package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.utils.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description 均线凝聚
 * @date 2021/1/16
 */
public class MaLineCohereRule extends AbstractBaseRule<List<KLineEntity>,Integer>
        implements MaLineRule<List<KLineEntity>,Integer,Integer>{

    /**
     *
     * @param kLineEntities
     * @return 1是0否
     */
    @Override
    public Integer verify(List<KLineEntity> kLineEntities) {
        double chgRatio = filterRose(kLineEntities);
        List<Integer> thrns = filterThrn(kLineEntities);
        boolean isMatter = false;//一阳穿5线以上
        List<Integer> list = filterThrn(kLineEntities);
        for(int item : thrns){
            if(item >= 4){
                list.add(item);
            }
            if(item >= 10){
                isMatter = true;
            }
        }
        if(isMatter && list.size() >= 9 && chgRatio > 0.8){
            return 1;
        }
        return 0;
    }

    @Override
    public Integer verify(List<KLineEntity> kLineEntities, Integer integer) {
        return null;
    }

    /**
     * 过滤振幅
     * @param kLineEntities
     * @return
     */
    private double filterRose(List<KLineEntity> kLineEntities){
        List<Double> pctchgs = kLineEntities.stream()
                .map(KLineEntity::getPctChg)
                .filter(o -> o < 3 && o > -3).collect(Collectors.toList());
        double chgRatio = MathUtil.div(pctchgs.size(),kLineEntities.size(),2);
        return chgRatio;
    }

    /**
     * 穿过均线数量（1阳穿6线重要）
     * @param kLineEntities
     * @return
     */
    private List<Integer> filterThrn(List<KLineEntity> kLineEntities){
        List<Integer> result = new ArrayList<>(kLineEntities.size());
        for(KLineEntity entity : kLineEntities){
            List<Double> mavals = klineVals(entity);
            int sign = 0;
            for(Double ma : mavals){
                if(entity.getHigh() >= ma && entity.getLow() <= ma){
                    sign++;
                }
            }
            sign = (entity.getPctChg() > 0 && sign >= 5) ? sign*2 : sign;
            result.add(sign);
        }
        return result;
    }
}
