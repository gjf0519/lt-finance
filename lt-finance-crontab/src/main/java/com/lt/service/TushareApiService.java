package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.lt.config.MqConfiguration;
import com.lt.result.TushareResult;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author gaijf
 * @description Tushare数据收集
 * @date 2020/12/3
 */
@Slf4j
@Service
public class TushareApiService {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 获取股票列表
     */
    @Async
    public List<String> obtainStockBasic() {
        List<String> codes = new ArrayList<>();
        try {
            Map<String,Object> item = new HashMap<>();
            item.put("list_status", "L");
            TushareResult tushareResult = this.requestData(item, TushareUtil.STOCK_CODE_API[0]
                    , TushareUtil.STOCK_CODE_API[1]);
            List<Map<String,String>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return null;
            }
            for(Map<String,String> map : list){
                if("主板".equals(map.get("market"))){
                    codes.add(map.get("ts_code"));
                    continue;
                };
                if("中小板".equals(map.get("market"))){
                    codes.add(map.get("ts_code"));
                    continue;
                };
                if("创业板".equals(map.get("market"))){
                    codes.add(map.get("ts_code"));
                    continue;
                };
            }
        }catch (Exception e){
            log.info("获取市场代码数据异常 Exception:{}",e);
        }
        return codes;
    }

    /**
     * 获取概念列表
     */
    public List<Map<String,String>> obtainPlates() {
        Map<String,Object> item = new HashMap<>();
        item.put("exchange", "A");
        item.put("type", "N");
        List<Map<String,String>> list = new ArrayList<>();
        try {
            TushareResult result = this.requestData(item,
                    TushareUtil.PLATE_API[0],TushareUtil.PLATE_API[1]);
            if(null == result){
                return list;
            }
            list = this.transitionMap(result);
        }catch (Exception e){
            log.info("获取板块代码数据异常 Exception:{}",e);
        }
        return list;
    }

    /**
     * 获取概念指数
     */
    public void obtainPlateIndex(String tradeDate) {
        try {
            Map<String,Object> item = new HashMap<>();
            item.put("trade_date", tradeDate);
            TushareResult tushareResult = this.requestData(item
                    , TushareUtil.PLATE_INDEX_API[0], TushareUtil.PLATE_INDEX_API[1]);
            List<Map<String,String>> list = this.transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            for(Map<String,String> map : list){
                MqConfiguration.send(TushareUtil.TUSHARE_PLATE_TOPIC,map,defaultMQProducer);
            }
        }catch (Exception e){
            log.info("获取板块指数数据异常 Exception:{}",e);
        }
    }

    /**
     * 获取概念成分股
     */
    public void obtainPlateElement(String plateCode) {
        try {
            Map<String,Object> item = new HashMap<>();
            item.put("ts_code", "plateCode");
            TushareResult tushareResult = this.requestData(item
                    , TushareUtil.PLATE_ELEMENT_API[0], TushareUtil.PLATE_ELEMENT_API[1]);
            List<Map<String,String>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            for(Map<String,String> map : list){
                MqConfiguration.send(TushareUtil.TUSHARE_PLATE_ELEMENT_TOPIC,map,defaultMQProducer);
            }
        }catch (Exception e){
            log.info("获取板块成分数据股异常 Exception:{}",e);
        }
    }

    /**
     * 获取每日基本信息
     * @param tradeDate
     */
    public void obtainDayBasic(String tradeDate){
        try {
            Map<String,Object> item = new HashMap<>();
            item.put("trade_date", tradeDate);
            TushareResult tushareResult = this.requestData(item
                    , TushareUtil.DAY_BASIC_API[0], TushareUtil.DAY_BASIC_API[1]);
            if(null == tushareResult){
                return;
            }
            List<Map<String,String>> list = transitionMap(tushareResult);
            if(null == list || list.isEmpty()){
                return;
            }
            for(Map<String,String> o : list){
                MqConfiguration.send(TushareUtil.TUSHARE_BASIC_TOPIC,o,defaultMQProducer);
            }
        }catch (Exception e){
            log.info("获取每日指标数据异常 tradeDate:{}",tradeDate);
        }
    }

    public TushareResult requestData(Map<String,Object> item,String apiName,String fields){
        Map<String,Object> params = new HashMap<>();
        params.put("params", item);
        params.put("api_name", apiName);
        params.put("token", TushareUtil.TUSHARE_TOKEN);
        params.put("fields", fields);
        String res = RestTemplateUtil.post(TushareUtil.URL,JSON.toJSONString(params),null);
        TushareResult tushareResult = JSON.parseObject(res, TushareResult.class);
        if(!"0".equals(tushareResult.getCode())){
            log.info("获取tushare数据异常 msg:{}",tushareResult.getMsg());
            return null;
        }
        return tushareResult;
    }

    public List<Map<String,String>> transitionMap(TushareResult tushareResult){
        if(tushareResult.getData().getItems().isEmpty()){
            return null;
        }
        List<String> fields = tushareResult.getData().getFields();
        List<List<String>> items = tushareResult.getData().getItems();
        List<Map<String,String>> result = new ArrayList<>();
        for(int i = 0;i < items.size();i++){
            Map<String,String> map = new HashMap<>();
            for(int y = 0;y < fields.size();y++){
                map.put(fields.get(y), items.get(i).get(y));
            }
        }
        return result;
    }
}
