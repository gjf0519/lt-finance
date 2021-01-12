package com.lt.utils;

import com.alibaba.fastjson.JSON;
import com.lt.entity.KLineEntity;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.*;

/**
 * @author gaijf
 * @description
 * @date 2021/1/12
 */
public class KlineDistributionUtil {

    public static double distribution(double [] values){
        StandardDeviation standardDeviation =new StandardDeviation();
        double mean = StatUtils.mean(values);
        double deviation = standardDeviation.evaluate(values);
        double disperse = BigDecimalUtil.div(mean,deviation,2);
        return disperse;
    }

    public static void distributionTest(List<KLineEntity> list){
        Collections.reverse(list);
        double [] arr1 = new double[list.size()];
        for(int i = 0;i < list.size();i++){
            arr1[i] = list.get(i).getMaFive();
        }
        double [] arr2 = new double[list.size()];
        for(int i = 0;i < list.size();i++){
            arr2[i] = list.get(i).getMaTen();
        }
        double [] arr3 = new double[list.size()];
        for(int i = 0;i < list.size();i++){
            arr3[i] = list.get(i).getMaTwenty();
        }
        double [] arr4 = new double[list.size()];
        for(int i = 0;i < list.size();i++){
            arr4[i] = list.get(i).getMaMonth();
        }
        StandardDeviation standardDeviation =new StandardDeviation();
        //算数平均数
//        double mean1 = StatUtils.mean(arr1);
        //几何平均数
        double mean1 = StatUtils.geometricMean(arr1);
        double deviation1 = standardDeviation.evaluate(arr1);
        double disperse1 = BigDecimalUtil.div(mean1,deviation1,2);
//        System.out.println("一组数据的均值为：" + mean);
//        System.out.println("一组数据的标准差为：" + deviation);
        double sub1 = list.get(0).getMaFive() - list.get(list.size()-1).getMaFive();
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse1+"==="+sub1);
//        double mean2 = StatUtils.mean(arr2);
        double mean2 = StatUtils.geometricMean(arr2);
        double deviation2 = standardDeviation.evaluate(arr2);
        double disperse2 = BigDecimalUtil.div(mean2,deviation2,2);
        double sub2 = list.get(0).getMaTen() - list.get(list.size()-1).getMaTen();
        System.out.println("平均差为："+StatUtils.meanDifference(arr1, arr2));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse2+"==="+sub2);
//        double mean3 = StatUtils.mean(arr3);
        double mean3 = StatUtils.geometricMean(arr3);
        double deviation3 = standardDeviation.evaluate(arr3);
        double disperse3 = BigDecimalUtil.div(mean3,deviation3,2);
        double sub3 = list.get(0).getMaTwenty() - list.get(list.size()-1).getMaTwenty();
        System.out.println("平均差为："+StatUtils.meanDifference(arr2, arr3));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse3+"==="+sub3);
//        double mean4 = StatUtils.mean(arr4);
        double mean4 = StatUtils.geometricMean(arr4);
        double deviation4 = standardDeviation.evaluate(arr4);
        double disperse4 = BigDecimalUtil.div(mean4,deviation4,2);
        double sub4 = list.get(0).getMaMonth() - list.get(list.size()-1).getMaMonth();
        System.out.println("平均差为："+StatUtils.meanDifference(arr3, arr4));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse4+"==="+sub4);
    }

    public static boolean peakTest(List<KLineEntity> list){
        int [] limits = new int[]{5,10,20,30};
        Map<Integer,List<Integer>> result = new HashMap<>();
        for(int l = 0;l < limits.length;l++){
            List<Integer> items = new ArrayList<>();
            int size = limits[l];
            double [] arr1 = new double[size];
            for(int i = 0;i < size;i++){
                arr1[i] = list.get(i).getMaFive();
            }
            double [] arr2 = new double[size];
            for(int i = 0;i < size;i++){
                arr2[i] = list.get(i).getMaTen();
            }
            double [] arr3 = new double[size];
            for(int i = 0;i < size;i++){
                arr3[i] = list.get(i).getMaTwenty();
            }
            double [] arr4 = new double[size];
            for(int i = 0;i < size;i++){
                arr4[i] = list.get(i).getMaMonth();
            }
            Kurtosis kurtosis = new Kurtosis(); //峰值
            Skewness skewness =new Skewness(); //偏态 小于0右偏
            double[] res1 = StatUtils.mode(arr1);//众数
            double kurtosi1 = kurtosis.evaluate(arr1);
            kurtosi1 = Double.isNaN(kurtosi1) ? 0.0:BigDecimalUtil.round(kurtosi1,2);
            double skewnes1 = skewness.evaluate(arr1);
            skewnes1 = Double.isNaN(skewnes1) ? 0.0:BigDecimalUtil.round(skewnes1,2);
//            System.out.println(list.get(0).getTsCode()+"================="+kurtosi1+"============"+skewnes1+"==="+ JSON.toJSONString(res1));
            double[] res2 = StatUtils.mode(arr2);
            double kurtosi2 = kurtosis.evaluate(arr2);
            kurtosi2 = Double.isNaN(kurtosi2) ? 0.0:BigDecimalUtil.round(kurtosi2,2);
            double skewnes2 = skewness.evaluate(arr2);
            skewnes2 = Double.isNaN(skewnes2) ? 0.0:BigDecimalUtil.round(skewnes2,2);
//            System.out.println(list.get(0).getTsCode()+"================="+kurtosi2+"============"+skewnes2+"==="+JSON.toJSONString(res2));
            double[] res3 = StatUtils.mode(arr3);
            double kurtosi3 = kurtosis.evaluate(arr3);
            kurtosi3 = Double.isNaN(kurtosi3) ? 0.0:BigDecimalUtil.round(kurtosi3,2);
            double skewnes3 = skewness.evaluate(arr3);
            skewnes3 = Double.isNaN(skewnes3) ? 0.0:BigDecimalUtil.round(skewnes3,2);
//            System.out.println(list.get(0).getTsCode()+"================="+kurtosi3+"============"+skewnes3+"==="+JSON.toJSONString(res3));
            double[] res4 = StatUtils.mode(arr4);
            double kurtosi4 = kurtosis.evaluate(arr4);
            kurtosi4 = Double.isNaN(kurtosi4) ? 0.0:BigDecimalUtil.round(kurtosi4,2);
            double skewnes4 = skewness.evaluate(arr4);
            skewnes4 = Double.isNaN(skewnes4) ? 0.0:BigDecimalUtil.round(skewnes4,2);
//            System.out.println(list.get(0).getTsCode()+"================="+kurtosi4+"============"+skewnes4+"==="+JSON.toJSONString(res4));
            items.add(res1.length);
            items.add(res2.length);
            items.add(res3.length);
            items.add(res4.length);
            result.put(limits[l],items);
        }

        int sign = 0;
        for (Map.Entry<Integer,List<Integer>> entry : result.entrySet()) {
            double red = entry.getKey()/2;
            for(Integer len : entry.getValue()){
                if(len > red){
                    sign++;
                    break;
                }
            }
        }
        if (sign == limits.length)
            return false;
        return true;
//        System.out.println(list.get(0).getTsCode()+"=================");
    }
}
