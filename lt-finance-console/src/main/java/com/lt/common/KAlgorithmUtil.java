package com.lt.common;

import com.lt.entity.KLineEntity;
import com.lt.utils.CalculateUtil;

import java.util.List;

/**
 * K线计算工具类
 */
public class KAlgorithmUtil {

    public static double readKlineRate(List<KLineEntity> list){
        int readNum = 0;
        for(KLineEntity kLineEntity : list){
            if(kLineEntity.getClose() >= kLineEntity.getOpen()){
                readNum++;
            }
        }
        return CalculateUtil.div(readNum,list.size(),2);
    }
}
