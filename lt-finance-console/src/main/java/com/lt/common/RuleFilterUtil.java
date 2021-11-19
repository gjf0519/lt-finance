package com.lt.common;

import com.lt.entity.KLineEntity;
import com.lt.utils.MathUtil;

import java.util.List;

/**
 * @author gaijf
 * @description 规则过滤基础工具类
 * @date 2020/12/2
 */
public class RuleFilterUtil {

    public static double readKlineRate(List<KLineEntity> list){
        int readNum = 0;
        for(KLineEntity kLineEntity : list){
            if(kLineEntity.getClose() >= kLineEntity.getOpen()){
                readNum++;
            }
        }
        return MathUtil.div(readNum,list.size(),2);
    }
}
