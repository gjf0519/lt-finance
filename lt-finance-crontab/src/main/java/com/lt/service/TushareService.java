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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description
 * @date 2020/12/3
 */
@Slf4j
@Service
public class TushareService {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 获取每日基本信息
     * @param tscode
     */
    @Async
    public void requestDayBasic(String tscode){
        try {
            String fields = "ts_code,trade_date,close,turnover_rate,volume_ratio,circ_mv";
            TushareResult tushareResult = requestData(tscode,"daily_basic",fields);
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
    public void requestDayLine(String tscode){
        try {
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
    public void requestWeekLine(String tscode) {
        try {
            //List<String> list = executePython("/home/python/week_line.py",tscode);
            List<String> list = executePython("D:/workspace-python/week_line.py",tscode);
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

    public TushareResult requestData(String tscode,String apiname,String fields){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> item = new HashMap<>();
        item.put("ts_code", tscode);
        String trade_date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
//        String trade_date = "20201215";
        item.put("start_date", trade_date);
        item.put("end_date", trade_date);
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
        String[] args = new String[]{"C:/python37/python",pyPath,tscode};
        //String[] args = new String[]{"/usr/local/python3.8/Python-3.8.0/python",pyPath,tscode};
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
