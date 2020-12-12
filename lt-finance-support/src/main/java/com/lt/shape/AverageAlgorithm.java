package com.lt.shape;

import com.lt.utils.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description 均线算法
 * @date 2020/12/1
 */
public class AverageAlgorithm {

    /**
     * 均线值计算
     * @param prices
     * @param day
     * @return
     */
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

    /**
     * 均线角度计算
     * @param price1 当天价格
     * @param price2 前一天价格
     * @return
     */
    public static Double calculateAngle(Double price1,Double price2){
        double m = price1/price2-1;
        double h = Math.atan(m*100)*180/3.1416;
        return BigDecimalUtil.round(h,2);
    }
}
