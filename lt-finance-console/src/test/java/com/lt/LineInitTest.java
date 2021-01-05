package com.lt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.result.TushareResult;
import com.lt.service.KLineService;
import com.lt.service.ReceiveService;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/2
 */
@SpringBootTest
public class LineInitTest {

    public static final String URL = "http://api.waditu.com";
    public static final String TUSHARE_TOKEN = "79d2b64fa07ce8f0fe6009ae8f25e5b4fd3cdcf78cf785eec3b5ab12";

    @Value("${finanace.system.python.profile}")
    private String pyHome;
    @Value("${finanace.system.python.day-line}")
    private String dayLinePath;
    @Value("${finanace.system.python.week-line}")
    private String weekLinePath;
    @Value("${finanace.system.python.month-line}")
    private String monthLinePath;
    @Autowired
    private KLineService kLineService;
    @Autowired
    private ReceiveService receiveService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void initDay(){
//        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
//        for(String item : Constants.STOCK_CODE){
//            threadPoolExecutor.execute(()->{
//                String flag = item.substring(0,2);
//                String code = item.substring(2,item.length());
//                List<Map<String,Object>> result = requestDayPyData(code+"."+flag.toUpperCase());
//                System.out.println("============="+latch.getCount());
//                if(null == result){
//                latch.countDown();
//                    return;
//                }
//                //均线计算
//                expma(result);
//                for(Map<String,Object> map : result){
//                    kLineService.saveDayLine(map);
//                }
////                if(null == result || result.isEmpty()){
////                    latch.countDown();
////                    return;
////                }
////                receiveService.receiveDayLine(result.get(0));
//                latch.countDown();
//            });
//        }
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<Map<String,Object>> result = requestDayPyData("000029.SZ");
        if(null == result){
            return;
        }
        expma(result);
        for(Map<String,Object> map : result){
            kLineService.saveDayLine(map);
        }
    }

    @Test
    public void initWeek(){
        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
        for(String item : Constants.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                String flag = item.substring(0,2);
                String code = item.substring(2,item.length());
                List<Map<String,Object>> result = requestWeekPyData(code+"."+flag.toUpperCase());
                //全量初始化
                if(null == result){
                    latch.countDown();
                    return;
                }
                //均线计算
                expma(result);
                for(Map<String,Object> map : result){
                    kLineService.saveWeekLine(map);
                }
                //数据补充
//                if(null == result || result.isEmpty()){
//                    latch.countDown();
//                    return;
//                }
//                receiveService.receiveWeekLine(result.get(0));
                latch.countDown();
                System.out.println("============="+latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initMonth(){
        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
        for(String item : Constants.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                String flag = item.substring(0,2);
                String code = item.substring(2,item.length());
                List<Map<String,Object>> result = requestMonthPyData(code+"."+flag.toUpperCase());
                if(null == result){
                    latch.countDown();
                    return;
                }
                //均线计算
                expma(result);
                for(Map<String,Object> map : result){
                    kLineService.saveMonthLine(map);
                }
                latch.countDown();
                System.out.println("============="+latch.getCount());
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String,Object>> requestDayPyData(String code){
        List<String> list = executePython(dayLinePath,code);
        List<Map<String,Object>> result = transPyDataDay(list);
        return result;
    }

    public List<Map<String,Object>> requestWeekPyData(String code){
        List<String> list = executePython(weekLinePath,code);
        List<Map<String,Object>> result = transPyDataWeek(list);
        return result;
    }

    public List<Map<String,Object>> requestMonthPyData(String code){
        List<String> list = executePython(monthLinePath,code);
        List<Map<String,Object>> result = transPyDataWeek(list);
        return result;
    }

    public List<Map<String,Object>> expma(List<Map<String,Object>> result){
        if(result.size() <= 0){
            return null;
        }
        List<Double> list = new ArrayList<>();
        for(int i = (result.size()-1);i > -1;i--){
            Map<String,Object> map = result.get(i);
            list.add(Double.valueOf(map.get("close").toString()));
        }
        expma(list,result);
        Collections.reverse(result);
        return result;
    }

    public void expma(List<Double> list,List<Map<String,Object>> result){
        for (int i = 0;i < Constants.MA_NUM_ARREY.length;i++) {
            List<Double> mas = StockAlgorithm.calculate(list,Constants.MA_NUM_ARREY[i]);
            Collections.reverse(mas);
            for(int y = 0;y < mas.size();y++){
                result.get(y).put(Constants.MA_NAME_ARREY[i],mas.get(y));
            }
        }
    }

    public static List<Map<String,Object>> transPyDataDay(List<String> list){
        List<Map<String,Object>> results = new ArrayList();
        for(String line : list){
            List<String> vals = JSONArray.parseArray(line,String.class);
            Map<String,Object> result = new HashMap<>();
            result.put("ts_code",vals.get(0));
            result.put("trade_date",vals.get(1));
            result.put("close",vals.get(5));
            result.put("open",vals.get(2));
            result.put("high",vals.get(3));
            result.put("low",vals.get(4));
            result.put("pre_close",vals.get(6));
            result.put("change",vals.get(7));
            result.put("pct_chg",vals.get(8));
            result.put("vol",vals.get(9));
            results.add(result);
        }
        return results;
    }

    public static List<Map<String,Object>> transPyDataWeek(List<String> list){
        List<Map<String,Object>> results = new ArrayList();
        for(String line : list){
            List<String> vals = JSONArray.parseArray(line,String.class);
            Map<String,Object> result = new HashMap<>();
            result.put("ts_code",vals.get(0));
            result.put("trade_date",vals.get(1));
            result.put("close",vals.get(2));
            result.put("open",vals.get(3));
            result.put("high",vals.get(4));
            result.put("low",vals.get(5));
            result.put("pre_close",vals.get(6));
            result.put("change",vals.get(7));
            result.put("pct_chg",vals.get(8));
            result.put("vol",vals.get(9));
            results.add(result);
        }
        return results;
    }

    public List<String> executePython(String pyPath,String tscode){
        List<String> list = new ArrayList<>();
        Process proc;
        String[] args = new String[]{pyHome,pyPath,tscode};
        try {
            proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                try {
                    list = JSONArray.parseArray(line,String.class);
                }catch (Exception e){
                    System.out.println(line+"==================="+tscode);
                }
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
