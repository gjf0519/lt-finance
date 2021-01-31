package com.lt.shape;

import com.lt.utils.BigDecimalUtil;

import java.util.*;

/**
 * @author gaijf
 * @description 均线算法
 * @date 2020/12/1
 */
public class StockAlgorithm {

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
                avg = BigDecimalUtil.add(avg,prices.get(y),4);
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

    /**
     * 均线位置 1上0下-1未知
     * @param price1 当前均线价格
     * @param price2 对比均线价格
     * @return
     */
    public static String calculateSite(Double price1,Double price2){
        String site = "-1";
        if(price1 - price2 >= 0){
            site = "1";
        }else{
            site = "0";
        }
        return site;
    }

    /**
     * K线形态
     * @param high
     * @param low
     * @param open
     * @param close
     * @return
     */
    public static String calculateForm(Double high,Double low,Double open,Double close){
        //上线影线比例
        double ulen = high - close;
        double dlen = open - low;
        double ud = dlen - ulen;
        //十字星
        double doji = BigDecimalUtil.div(open,close,2);
        //整体长度
        double klen = high - low;
        //身体长度
        double blen = close - open;
        double bratio = 0.0;
        if(0 == klen){
            bratio = 1;
        }else {
            bratio = BigDecimalUtil.div(blen,klen,2);
        }
        //身体长度占比
        String kform = "";
        if(ulen == 0){
            kform = "平头";
        }else if(bratio >= 0.5){
            kform = "K体长";
        }else if (ud > 0){
            kform = "长下影线";
        }else if(ud < 0){
            kform = "长下影线";
        }else if(doji < 0.01){
            kform = "十字星";
        }
        return kform;
    }

    /**
     * 波段计算
     * @param nums
     * @return
     */
    public static Map<String,Integer> calculateBand(int limit,Double [] nums){
        Map<String,Integer> bands = new LinkedHashMap<>();
        int maxIndex = 0;
        double maxNum = 0;
        int minIndex = 0;
        double minNum = 10000;
        for(int i = 0;i < nums.length;i++){
            if(nums[i] > maxNum){
                maxNum = nums[i];
                maxIndex = i;
            }
            if(nums[i] < minNum){
                minNum = nums[i];
                minIndex = i;
            }
        }
        bands.put("最高",maxIndex);
        bands.put("最低",minIndex);
        //波峰波谷转向标识
        int turn = -1;
        int index = 1;
        while (true){
            if(turn < 0){
                index = ravines(index,limit,nums);
                turn = 1;
                if(index <= 0){
                    index = 1;
                    continue;
                }
                if(index >= nums.length){
                    break;
                }
                bands.put("波谷"+index,index+1);
            }else {
                index = peaks(index,limit,nums);
                turn = -1;
                if(index >= nums.length){
                    break;
                }
                bands.put("波峰"+index,index+1);
            }
        }
        return bands;
    }

    /**
     * 波峰
     * @param index 下标位置
     * @param limit 持续次数
     * @param num 过滤的数组
     * @return
     */
    public static int peaks(int index,int limit,Double [] num){
        int sign = 0;
        for(;index < num.length;index++){
            if(num[index] - num[index-1] >= 0){
                sign = 0;
                continue;
            }
            sign++;
            if(sign > (limit-1)){
                return index - (limit+1);
            }
        }
        return index;
    }

    /**
     * 波谷
     * @param index 下标位置
     * @param limit 持续次数
     * @param num 过滤的数组
     * @return
     */
    public static int ravines(int index,int limit,Double [] num){
        int sign = 0;
        for(;index < num.length;index++){
            if(num[index] - num[index-1] <= 0){
                sign = 0;
                continue;
            }
            sign++;
            if(sign > (limit-1)){
                return index - (limit+1);
            }
        }
        return index;
    }

    public static void main(String[] args) {
        double an = BigDecimalUtil.div(calculateAngle(5.85,5.67),8);
        System.out.println(an);
        System.out.println(calculateAngle(10.48,10.2));
    }
}
