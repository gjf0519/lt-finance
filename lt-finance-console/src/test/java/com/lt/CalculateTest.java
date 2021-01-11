package com.lt;

import com.lt.calculate.Mutil;
import com.lt.entity.KLineEntity;
import com.lt.service.KLineService;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author gaijf
 * @description
 * @date 2021/1/11
 */
@SpringBootTest
public class CalculateTest {

    @Autowired
    private KLineService kLineService;

    @Test
    public void distribution(){
        //分布形状 峰值、偏态
        List<KLineEntity> list1 = kLineService.queryDayLineByLimit("000593.SZ",60);
        Collections.reverse(list1);
        List<Double> mas1 = new ArrayList<>();
        double [] ar1 = new double[list1.size()];
        for(int i = 0;i < list1.size();i++){
            ar1[i] = list1.get(i).getMaFive();
        }
        List<KLineEntity> list2 = kLineService.queryDayLineByLimitDate("000593.SZ",60,"20201201");
        Collections.reverse(list2);
        double [] ar2 = new double[list2.size()];
        for(int i = 0;i < list2.size();i++){
            ar2[i] = list2.get(i).getMaFive();
        }
        List<KLineEntity> list3 = kLineService.queryDayLineByLimitDate("000713.SZ",60,"20201209");
        Collections.reverse(list3);
        double [] ar3 = new double[list3.size()];
        for(int i = 0;i < list3.size();i++){
            ar3[i] = list3.get(i).getMaFive();
        }
        List<KLineEntity> list4 = kLineService.queryDayLineByLimitDate("000890.SZ",60,"20201217");
        Collections.reverse(list4);
        double [] ar4 = new double[list4.size()];
        for(int i = 0;i < list4.size();i++){
            ar4[i] = list4.get(i).getMaFive();
        }
        Skewness skewness =new Skewness(); //偏态
        Kurtosis kurtosis =new Kurtosis(); //Kurtosis,峰度
        System.out.println(kurtosis.evaluate(ar1));
        System.out.println(kurtosis.evaluate(ar2));
        System.out.println(kurtosis.evaluate(ar3));
        System.out.println(kurtosis.evaluate(ar4));
        System.out.println(mode(ar1));
        System.out.println(mode(ar2));
        System.out.println(mode(ar3));
        System.out.println(mode(ar4));
        //小于0右偏
        System.out.println(skewness.evaluate(ar1));//右偏
        System.out.println(skewness.evaluate(ar2));//左偏
        System.out.println(skewness.evaluate(ar3));//左偏
        System.out.println(skewness.evaluate(ar4));//右偏
    }

    public static double kurtosis(double[] in) {
        double mean = mean(in);
        double SD = standardDeviation(in);
        int n = in.length;
        double sum = 0;
        for (int i = 0; i < in.length; i++) {
            sum = Mutil.add(sum, Math.pow(Mutil.divide(Mutil.subtract(in[i], mean), SD, 2), 4));
        }
        return Mutil.round(Mutil.divide(sum, n, 2) - 3, 2);
    }

    public static double standardDeviation(double[] in) {
        return Math.sqrt(variance(in));
    }
    public static double variance(double[] in) {
        double t_mean = mean(in);
        double t_sumPerPow = 0;
        for (int i = 0; i < in.length; i++) {
            t_sumPerPow = Mutil.add(t_sumPerPow, Math.pow(Mutil.subtract(in[i], t_mean), 2));
        }
        return Mutil.divide(t_sumPerPow, (in.length - 1), 2);
    }

    /**
     * 集中趋势量数：均值/算术平均数
     * @param in
     * @return
     */
    public static double mean(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        if (in.length == 1) {
            return in[0];
        }
        double sum = 0;
        for (int i = 0; i < in.length; i++) {
            sum = Mutil.add(sum, in[i]);
            // sum += in[i];
        }
        // return sum/in.length;
        return Mutil.divide(sum, in.length, 2);
    }

    /**
     * 集中趋势量数：计算中位数
     * @param in
     * @return
     */
    public static double median(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        Arrays.sort(in);

        // for (int i = 0; i < in.length; i++) {
        // log.debug("sort: "+i+":::"+in[i]);
        // }
        if (in.length % 2 == 1) {
            return in[(int) Math.floor(in.length / 2)];
        } else {
            double[] avg = new double[2];
            avg[0] = in[(int) Math.floor(in.length / 2) - 1];
            avg[1] = in[(int) Math.floor(in.length / 2)];
            return mean(avg);

        }
    }

    /**
     * 集中趋势量数：计算众数
     * @param in
     * @return
     */
    public static List mode(double[] in) {
        HashMap map = new HashMap();
        double imode = 0;
        for (int i = 0; i < in.length; i++) {
            double x = in[i];
            if (map.containsKey(String.valueOf(x))) {
                // 如果出现多次，取出以前的计数，然后加1
                int len = Integer.parseInt(map.get(String.valueOf(x)).toString());
                map.put(String.valueOf(x), String.valueOf(len + 1));
                imode = Math.max(imode, len + 1);
            } else {
                // 如果是第一次出现，计数为1
                map.put(String.valueOf(x), String.valueOf(1));
                imode = Math.max(imode, 1);
            }
        }
        Iterator iter = map.keySet().iterator();
        ArrayList lst = new ArrayList();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object v = map.get(key);
            if (Integer.parseInt(v.toString()) == imode) {
                lst.add(key);
            }
        }
        return lst;
    }

    /**
     * 集中趋势量数：极差（不包含）
     * @param in
     * @return
     */
    public static double range(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            max = Math.max(max, in[i]);
            min = Math.min(min, in[i]);
        }
        // return max - min;
        return Mutil.subtract(max, min);
    }

    /**
     * 变异性量数：极差（包含）
     * @param in
     * @return
     */
    public static double range2(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            max = Math.max(max, in[i]);
            min = Math.min(min, in[i]);
        }
        // return max - min + 1;
        return Mutil.subtract(max, min) + 1;
    }
}
