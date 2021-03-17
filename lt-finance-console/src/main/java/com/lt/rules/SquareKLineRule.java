package com.lt.rules;

import com.lt.entity.KLineEntity;

import java.util.List;

/**
 * @author gaijf
 * @description 前一天阴线第二或第三日回补
 * @date 2021/1/14
 */
public class SquareKLineRule
        extends AbstractBaseRule<List<KLineEntity>,Integer>{

    public SquareKLineRule() {
        super();
    }

    @Override
    public Integer verify(List<KLineEntity> entitys) {
        if(null == entitys || entitys.isEmpty()){
            return null;
        }
        KLineEntity kLineEntity1 = entitys.get(0);
        KLineEntity kLineEntity2 = entitys.get(1);
        KLineEntity kLineEntity3 = entitys.get(2);
        //第二日回补前一天下跌
        if(kLineEntity2.getPctChg() < 0 && kLineEntity1.getPctChg() > 0){
            if(kLineEntity2.getOpen() < kLineEntity2.getClose()){
                return -1;
            }
            if(kLineEntity1.getHigh() > kLineEntity2.getOpen()){
                return 1;
            }
        }
        //第三日回补前一天下跌
        if(kLineEntity3.getPctChg() < 0 &&
                kLineEntity2.getPctChg() > 0
                && kLineEntity1.getPctChg() > 0){
            if(kLineEntity3.getOpen() < kLineEntity3.getClose()){
                return -1;
            }
            if(kLineEntity2.getHigh() < kLineEntity3.getOpen()
                    && kLineEntity1.getHigh() > kLineEntity3.getOpen()){
                return 2;
            }
        }
        return -1;
    }
}
