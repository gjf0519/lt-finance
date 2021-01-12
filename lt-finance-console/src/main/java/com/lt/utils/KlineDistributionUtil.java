package com.lt.utils;

import com.lt.entity.KLineEntity;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.Collections;
import java.util.List;

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
}
