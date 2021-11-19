package com.lt.utils;

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
        double disperse = MathUtil.div(mean,deviation,2);
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
        double disperse1 = MathUtil.div(mean1,deviation1,2);
//        System.out.println("一组数据的均值为：" + mean);
//        System.out.println("一组数据的标准差为：" + deviation);
        double sub1 = list.get(0).getMaFive() - list.get(list.size()-1).getMaFive();
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse1+"==="+sub1);
//        double mean2 = StatUtils.mean(arr2);
        double mean2 = StatUtils.geometricMean(arr2);
        double deviation2 = standardDeviation.evaluate(arr2);
        double disperse2 = MathUtil.div(mean2,deviation2,2);
        double sub2 = list.get(0).getMaTen() - list.get(list.size()-1).getMaTen();
        System.out.println("平均差为："+StatUtils.meanDifference(arr1, arr2));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse2+"==="+sub2);
//        double mean3 = StatUtils.mean(arr3);
        double mean3 = StatUtils.geometricMean(arr3);
        double deviation3 = standardDeviation.evaluate(arr3);
        double disperse3 = MathUtil.div(mean3,deviation3,2);
        double sub3 = list.get(0).getMaTwenty() - list.get(list.size()-1).getMaTwenty();
        System.out.println("平均差为："+StatUtils.meanDifference(arr2, arr3));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse3+"==="+sub3);
//        double mean4 = StatUtils.mean(arr4);
        double mean4 = StatUtils.geometricMean(arr4);
        double deviation4 = standardDeviation.evaluate(arr4);
        double disperse4 = MathUtil.div(mean4,deviation4,2);
        double sub4 = list.get(0).getMaMonth() - list.get(list.size()-1).getMaMonth();
        System.out.println("平均差为："+StatUtils.meanDifference(arr3, arr4));
        System.out.println(list.get(0).getTsCode()+"一组数据的离散系数为：" + disperse4+"==="+sub4);
    }

    private static void lineValues(List<KLineEntity> list,int sign){
    }

    public static void checkArithmetic(){
    }

    public static String peakTest(List<KLineEntity> list){
        int size = 5;
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
        double kurtosi1 = kurtosis.evaluate(arr1);
        double kurtosi2 = kurtosis.evaluate(arr2);
        double kurtosi3 = kurtosis.evaluate(arr3);
        double kurtosi4 = kurtosis.evaluate(arr4);
        if(Double.isNaN(kurtosi1) || Double.isNaN(kurtosi2)
                || Double.isNaN(kurtosi3) || Double.isNaN(kurtosi4)){
            return list.get(0).getTsCode();
        }
        return null;
    }

    public static String deviateTest(List<KLineEntity> list){
        int size = 5;
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
        Skewness skewness =new Skewness(); //偏态 小于0右偏
        double skewnes1 = skewness.evaluate(arr1);
        double skewnes2 = skewness.evaluate(arr2);
        double skewnes3 = skewness.evaluate(arr3);
        double skewnes4 = skewness.evaluate(arr4);
        if(Double.isNaN(skewnes1) || Double.isNaN(skewnes2)
                || Double.isNaN(skewnes3) || Double.isNaN(skewnes4)){
            return list.get(0).getTsCode();
        }
        return null;
    }

    /**
     * 众数计算集中度
     * @param list
     * @return
     */
    public static boolean modeFilter(List<KLineEntity> list){
        int [] limits = new int[]{5,10,20,30};
        int num = 0;
        for(int l = 0;l < limits.length;l++){
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
            double[] res1 = StatUtils.mode(arr1);//众数
            double[] res2 = StatUtils.mode(arr2);
            double[] res3 = StatUtils.mode(arr3);
            double[] res4 = StatUtils.mode(arr4);
            double redNum = size/2;
            boolean isRed1 = res1.length > redNum ? true : false;
            boolean isRed2 = res2.length > redNum ? true : false;
            boolean isRed3 = res3.length > redNum ? true : false;
            boolean isRed4 = res4.length > redNum ? true : false;
            //如果10以上有两组众数超过5个的剔除数据
            if(size != 5){
                if((isRed1 && isRed2) || (isRed1 && isRed3) || (isRed1 && isRed4)){
                    return false;
                }
                if((isRed2 && isRed3) || (isRed2 && isRed4) || (isRed3 && isRed4)){
                    return false;
                }
            }
            if(isRed1 || isRed2 || isRed3 || isRed4){
                continue;
            }
            num++;//统计5、10、20、30是否全部有大于一半以上的数据
//            System.out.println(list.get(0).getTsCode()+"================="+ JSON.toJSONString(res1));
//            System.out.println(list.get(0).getTsCode()+"================="+ JSON.toJSONString(res2));
//            System.out.println(list.get(0).getTsCode()+"================="+ JSON.toJSONString(res3));
//            System.out.println(list.get(0).getTsCode()+"================="+ JSON.toJSONString(res4));
        }
//        System.out.println(list.get(0).getTsCode()+"=================");
        if (num == limits.length)
            return false;
        return true;
    }
}
