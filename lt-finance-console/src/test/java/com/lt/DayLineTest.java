package com.lt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.result.TushareResult;
import com.lt.service.KLineService;
import com.lt.shape.StockAlgorithm;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description 日K线数据
 * @date 2020/12/2
 */
@SpringBootTest
public class DayLineTest {

    public static final String URL = "http://api.waditu.com";
    public static final String TUSHARE_TOKEN = "79d2b64fa07ce8f0fe6009ae8f25e5b4fd3cdcf78cf785eec3b5ab12";

    @Autowired
    private KLineService kLineService;

    /**
     * 第一次初始化数据
     */
    @Test
    public void init(){
        int i = 0;
        for(String item : Constants.STOCK_CODE){
            System.out.println(item+"============================="+i++);
            if(i <= 2000){
                continue;
            }
            String flag = item.substring(0,2);
            String code = item.substring(2,item.length());
//            int have = kLineService.hasSaveWeekLine(code+"."+flag.toUpperCase(),"20201204");
//            if(have > 0){
//                continue;
//            }
            List<Map<String,Object>> result = avgPyKline(code+"."+flag.toUpperCase());
            if(null == result){
                continue;
            }
            for(Map<String,Object> map : result){
                kLineService.saveWeekLine(map);
            }
        }
    }

    @Test
    public void init2(){
        int i = 0;
        for(String item : Constants.STOCK_CODE){
            System.out.println(item+"============================="+i++);
            if(i <= 600 || i > 1300){
                continue;
            }
            String flag = item.substring(0,2);
            String code = item.substring(2,item.length());
//            int have = kLineService.hasSaveWeekLine(code+"."+flag.toUpperCase(),"20201204");
//            if(have > 0){
//                continue;
//            }
            List<Map<String,Object>> result = avgPyKline(code+"."+flag.toUpperCase());
            if(null == result){
                continue;
            }
            for(Map<String,Object> map : result){
                kLineService.saveWeekLine(map);
            }
        }
    }

    @Test
    public void init3(){
        int i = 0;
        for(String item : Constants.STOCK_CODE){
            System.out.println(item+"============================="+i++);
            if(i <= 1300 || i > 2000){
                continue;
            }
            String flag = item.substring(0,2);
            String code = item.substring(2,item.length());
//            int have = kLineService.hasSaveWeekLine(code+"."+flag.toUpperCase(),"20201204");
//            if(have > 0){
//                continue;
//            }
            List<Map<String,Object>> result = avgPyKline(code+"."+flag.toUpperCase());
            if(null == result){
                continue;
            }
            for(Map<String,Object> map : result){
                kLineService.saveWeekLine(map);
            }
        }
    }

    public List<Map<String,Object>> avgPyKline(String code){
        List<Map<String,Object>> result = requestPyData(code);
        if(result.size() <= 0){
            return null;
        }
        List<Double> list = new ArrayList<>();
        for(int i = (result.size()-1);i > -1;i--){
            Map<String,Object> map = result.get(i);
            list.add(Double.valueOf(map.get("close").toString()));
        }
        avgLine(list,result);
        Collections.reverse(result);
        return result;
    }

    public List<Map<String,Object>> avgKline(String code){
        TushareResult tushareResult = requestData(code);
        List<Map<String,Object>> result = transitionMap(tushareResult);
        if(result.size() <= 0){
            return null;
        }
        List<Double> list = new ArrayList<>();
        for(int i = (result.size()-1);i > -1;i--){
            Map<String,Object> map = result.get(i);
            list.add(Double.valueOf(map.get("close").toString()));
        }
        avgLine(list,result);
        Collections.reverse(result);
        return result;
    }

    public List<Map<String,Object>> requestPyData(String code){
        List<Map<String,Object>> result = getPyData(code);
        return result;
    }

    public TushareResult requestData(String code){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> item = new HashMap<>();
        item.put("ts_code", code);
        String start_date = "20190101";
//        String end_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        String end_date = "20201207";
        item.put("start_date", start_date);
        item.put("end_date", end_date);
        params.put("params", item);
        params.put("api_name", "weekly");
        params.put("token", TUSHARE_TOKEN);
        params.put("fields", "ts_code,trade_date,open,high,low,close,pre_close,change,pct_chg,vol,amount");
        String res = RestTemplateUtil.post(URL,JSON.toJSONString(params),null);
        TushareResult tushareResult = JSON.parseObject(res, TushareResult.class);
        return tushareResult;
    }

    public List<Map<String,Object>> transitionMap(TushareResult tushareResult){
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

    public void avgLine(List<Double> list,List<Map<String,Object>> result){
        List<Double> avgs5 = StockAlgorithm.calculate(list,5);
        Collections.reverse(avgs5);
        for(int i = 0;i < avgs5.size();i++){
            result.get(i).put("five_price",avgs5.get(i));
        }
        List<Double> avgs10 = StockAlgorithm.calculate(list,10);
        Collections.reverse(avgs10);
        for(int i = 0;i < avgs10.size();i++){
            result.get(i).put("ten_price",avgs10.get(i));
        }
        List<Double> avgs20 = StockAlgorithm.calculate(list,20);
        Collections.reverse(avgs20);
        for(int i = 0;i < avgs20.size();i++){
            result.get(i).put("twenty_price",avgs20.get(i));
        }
        List<Double> avgs30 = StockAlgorithm.calculate(list,30);
        Collections.reverse(avgs30);
        for(int i = 0;i < avgs30.size();i++){
            result.get(i).put("thirty_price",avgs30.get(i));
        }
        List<Double> avgs60 = StockAlgorithm.calculate(list,60);
        Collections.reverse(avgs60);
        for(int i = 0;i < avgs60.size();i++){
            result.get(i).put("sixty_price",avgs60.get(i));
        }
        List<Double> avgs120 = StockAlgorithm.calculate(list,120);
        Collections.reverse(avgs120);
        for(int i = 0;i < avgs120.size();i++){
            result.get(i).put("semester_price",avgs120.get(i));
        }
    }

    public static List<Map<String,Object>> getPyData(String tscode){
        List<String> list = executePython("E:\\workspace-python\\week_line.py",tscode);
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

    public static List<String> executePython(String pyPath,String tscode){
        List<String> list = new ArrayList<>();
        Process proc;
        String[] args = new String[]{"C:\\python3.8\\python",pyPath,tscode};
        try {
            proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                list = JSONArray.parseArray(line,String.class);
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
