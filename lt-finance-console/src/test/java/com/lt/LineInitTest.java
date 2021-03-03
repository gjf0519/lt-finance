package com.lt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.result.TushareResult;
import com.lt.service.KLineService;
import com.lt.service.ReceiveService;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
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
//        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
//        for(String item : TsCodes.STOCK_CODE){
//            threadPoolExecutor.execute(()->{
//                List<Map<String,Object>> result = requestDayPyData(item);
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

        String [] codes = new String[]{
                "002966.SZ",
                "003022.SZ",
                "003026.SZ",
                "003030.SZ",
                "003032.SZ",
                "003031.SZ",
                "003033.SZ",
                "003035.SZ",
                "300787.SZ",
                "300925.SZ",
                "300928.SZ",
                "300927.SZ",
                "300926.SZ",
                "605005.SH",
                "605277.SH"
        };
        for(String code : codes){
            List<Map<String,Object>> result = requestDayPyData(code);
            if(null == result){
                return;
            }
            expma(result);
            for(Map<String,Object> map : result){
                kLineService.saveDayLine(map);
            }
        }
    }

    @Test
    public void initWeek(){
//        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
//        for(String item : TsCodes.STOCK_CODE){
//            threadPoolExecutor.execute(()->{
//                try{
//                    List<Map<String,Object>> result = requestWeekPyData(item);
//                    //全量初始化
//                    if(null == result){
//                        latch.countDown();
//                        return;
//                    }
//                    //均线计算
//                    expma(result);
//                    for(Map<String,Object> map : result){
//                        kLineService.saveWeekLine(map);
//                    }
//                    //数据补充
////                if(null == result || result.isEmpty()){
////                    latch.countDown();
////                    return;
////                }
////                receiveService.receiveWeekLine(result.get(0));
//                }catch (Exception e){
//                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!"+item);
//                }
//                latch.countDown();
//                System.out.println("============="+latch.getCount());
//            });
//        }
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<Map<String,Object>> result = requestWeekPyData("000001.SZ");
        if(null == result){
            return;
        }
        expma(result);
        for(Map<String,Object> map : result){
            kLineService.saveWeekLine(map);
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


    @Test
    public void initPlate(){
        CountDownLatch latch = new CountDownLatch(TsCodes.PLATE_CODE.size());
        String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        for(String code : TsCodes.PLATE_CODE){
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadPoolExecutor.execute(()->{
                String fields = "ts_code,trade_date,close,open,high,low,pre_close,avg_price,change,pct_change,vol,turnover_rate,float_mv";
                Map<String,Object> item = new HashMap<>();
                item.put("ts_code", code);
                item.put("start_date", "20200101");
                item.put("end_date", trade_date);
                TushareResult tushareResult = requestData(item,"ths_daily",fields);
                List<Map<String,Object>> result = transitionMap(tushareResult);
                if(null == result){
                    latch.countDown();
                    return;
                }
                //均线计算
                expma(result);
                for(Map<String,Object> map : result){
                    kLineService.savePlateLine(map);
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

    public TushareResult requestData(Map<String,Object> item,String apiname,String fields){
        Map<String,Object> params = new HashMap<>();
        params.put("params", item);
        params.put("api_name", apiname);
        params.put("token", Constants.TUSHARE_TOKEN);
        params.put("fields", fields);
        String res = RestTemplateUtil.post(Constants.URL, JSON.toJSONString(params),null);
        TushareResult tushareResult = JSON.parseObject(res, TushareResult.class);
        if(!"0".equals(tushareResult.getCode())){
            System.out.println("获取Tushare数据异常"+tushareResult.getMsg());
            return null;
        }
        return tushareResult;
    }

    public List<Map<String,Object>> transitionMap(TushareResult tushareResult){
        if(null == tushareResult || tushareResult.getData().getFields().isEmpty()){
            return null;
        }
        List<String> fields = tushareResult.getData().getFields();
        List<List<String>> items = tushareResult.getData().getItems();
        List<Map<String,Object>> result = new ArrayList<>();
        items.stream().forEach(o -> {
            Map<String,Object> map = new HashMap<>();
            Stream.iterate(0, i -> i+1).limit(o.size())
                    .forEach(i -> {
                        map.put(fields.get(i),o.get(i));
                    });
            result.add(map);
        });
        return result;
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
