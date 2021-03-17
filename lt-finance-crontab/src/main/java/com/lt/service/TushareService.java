package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.config.MqConfiguration;
import com.lt.result.TushareResult;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description Tushare数据收集
 * @date 2020/12/3
 */
@Slf4j
@Service
public class TushareService {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 获取股票列表
     */
    @Async
    public void requestStockBasic() {
        try {
            String fields = "ts_code,symbol,name,area,industry,market,list_status,is_hs";
            Map<String,Object> item = new HashMap<>();
            item.put("list_status", "L");
            TushareResult tushareResult = requestData(item,"stock_basic",fields);
            List<Map<String,Object>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            List<String> codes = new ArrayList<>();
            for(Map<String,Object> map : list){
                if("主板".equals(map.get("market"))){
                    codes.add(map.get("ts_code").toString());
                    continue;
                };
                if("中小板".equals(map.get("market"))){
                    codes.add(map.get("ts_code").toString());
                    continue;
                };
                if("创业板".equals(map.get("market"))){
                    codes.add(map.get("ts_code").toString());
                    continue;
                };
            }
            System.out.println(JSON.toJSONString(codes));
        }catch (Exception e){
            log.info("获取市场代码数据异常 Exception:{}",e);
        }
    }

    /**
     * 获取概念列表
     */
    public void requestPlates() {
        try {
            String fields = "ts_code,name,count";
            Map<String,Object> item = new HashMap<>();
            item.put("exchange", "A");
            item.put("type", "N");
            TushareResult tushareResult = requestData(item,"ths_index",fields);
            List<Map<String,Object>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            List<String> codes = new ArrayList<>();
            for(Map<String,Object> plate : list){
                codes.add(plate.get("ts_code").toString());
            }
            System.out.println(JSON.toJSONString(codes));
        }catch (Exception e){
            log.info("获取板块代码数据异常 Exception:{}",e);
        }
    }

    /**
     * 获取概念指数
     */
    public void requestPlateIndex() {
        try {
            String fields = "ts_code,trade_date,close,open,high,low,pre_close,avg_price,change,pct_change,vol,turnover_rate,float_mv";
            Map<String,Object> item = new HashMap<>();
            String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
//            item.put("trade_date", trade_date);
            item.put("trade_date", "20210309");
            TushareResult tushareResult = requestData(item,"ths_daily",fields);
            List<Map<String,Object>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            for(Map<String,Object> map : list){
                MqConfiguration.send(Constants.TUSHARE_PLATE_TOPIC,map,defaultMQProducer);
            }
        }catch (Exception e){
            log.info("获取板块指数数据异常 Exception:{}",e);
        }
    }

    /**
     * 获取概念成分股
     */
    public void requestPlateElement(String plateCode) {
        try {
            String fields = "ts_code,code,name,weight,in_date,is_new";
            Map<String,Object> item = new HashMap<>();
            item.put("ts_code", plateCode);
            TushareResult tushareResult = requestData(item,"ths_member",fields);
            List<Map<String,Object>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
        }catch (Exception e){
            log.info("获取板块成分数据股异常 Exception:{}",e);
        }
    }

    /**
     * 获取每日基本信息
     * @param tscode
     */
    @Async
    public void requestDayBasic(String tscode){
        try {
            String fields = "ts_code,trade_date,close,turnover_rate,turnover_rate_f,volume_ratio,circ_mv";
            Map<String,Object> item = new HashMap<>();
            item.put("ts_code", tscode);
            String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
            item.put("start_date", trade_date);
            item.put("end_date", trade_date);
            TushareResult tushareResult = requestData(item,"daily_basic",fields);
            List<Map<String,Object>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            MqConfiguration.send(Constants.TUSHARE_BASIC_TOPIC,list.get(0),defaultMQProducer);
        }catch (Exception e){
            log.info("获取每日指标数据异常 tscode:{}",tscode);
        }
    }

    /**
     * 获取日K数据
     * @param tscode
     */
    @Async
    public void requestDayLine(String tscode){
        try {
//            List<String> list = executePython("E:/workspace-python/day_line.py",tscode);
            List<String> list = executePython("/home/python/day_line.py",tscode);
            if(list.isEmpty()){
                return;
            }
            List<Map<String,Object>> result = transPyDataDay(list);
            MqConfiguration.send(Constants.TUSHARE_DAYLINE_TOPIC,result.get(0),defaultMQProducer);
        }catch (Exception e){
            log.info("获取日K数据异常 tscode:{} exception:{}",tscode,e);
        }
    }

    /**
     * 获取周K线
     * @param tscode
     */
    @Async
    public void requestWeekLine(String tscode) {
        try {
            List<String> list = executePython("/home/python/week_line.py",tscode);
//            List<String> list = executePython("D:/workspace-python/week_line.py",tscode);
            if(list.isEmpty()){
                return;
            }
            List<Map<String,Object>> result = transPyDataWeek(list);
            MqConfiguration.send(Constants.TUSHARE_WEEKLINE_TOPIC,result.get(0),defaultMQProducer);
        }catch (Exception e){
            log.info("获取周K数据异常 tscode:{} exception:{}",tscode,e);
        }
    }

    /**
     * 获取月K线
     */
    @Async
    public void requestMonthLine() {
        try {
            for(String item : Constants.STOCK_CODE){
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String flag = item.substring(0,2);
                String code = item.substring(2,item.length());
                String tscode = code+"."+flag.toUpperCase();
                List<String> list = executePython("/home/python/month_line.py",tscode);
                if(list.isEmpty()){
                    return;
                }
                List<Map<String,Object>> result = transPyDataWeek(list);
                MqConfiguration.send(Constants.TUSHARE_MONTHLINE_TOPIC,result.get(0),defaultMQProducer);
            }
        }catch (Exception e){
            log.info("获取月K数据异常 exception:{}",e);
        }
    }

    public TushareResult requestData(Map<String,Object> item,String apiname,String fields){
        Map<String,Object> params = new HashMap<>();
        params.put("params", item);
        params.put("api_name", apiname);
        params.put("token", Constants.TUSHARE_TOKEN);
        params.put("fields", fields);
        String res = RestTemplateUtil.post(Constants.URL,JSON.toJSONString(params),null);
        TushareResult tushareResult = JSON.parseObject(res, TushareResult.class);
        if(!"0".equals(tushareResult.getCode())){
            log.info("获取Tushare数据异常 msg:{}",tushareResult.getMsg());
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

    private List<Map<String,Object>> transPyDataDay(List<String> list){
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

    private List<Map<String,Object>> transPyDataWeek(List<String> list){
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

    private List<Map<String,Object>> transPyDataMonth(List<String> list){
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

    private List<String> executePython(String pyPath,String tscode){
        List<String> list = new ArrayList<>();
        Process proc;
//        String[] args = new String[]{"C:/python3.8/python",pyPath,tscode};
        String[] args = new String[]{"/usr/local/python3.8/Python-3.8.0/python",pyPath,tscode};
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
        log.info("脚本收集数据code:{},size:{}",tscode,list.size());
        return list;
    }
}
