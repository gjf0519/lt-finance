package com.lt.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description 均线算法
 * @date 2020/12/1
 */
public class AverageAlgorithm {

    public static List<Double> calculate(List<Double> prices,int day){
        if(prices.size() < day){
            return new ArrayList<>(0);
        }
        List<Double> result = new ArrayList<>(prices.size());
        for(int i = (day-1);i < prices.size();i++){
            double avg = prices.get(i);
            int stopy = i-day+1;
            for(int y = (i-1);y >= stopy;y--){
                avg = BigDecimalUtil.add(avg,prices.get(y),2);
            }
            avg = BigDecimalUtil.div(avg,day,2);
            result.add(avg);
        }
        return result;
    }
}
