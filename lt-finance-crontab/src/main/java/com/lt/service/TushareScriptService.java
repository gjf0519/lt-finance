package com.lt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.config.MqConfiguration;
import com.lt.entity.RepairDataEntity;
import com.lt.utils.Constants;
import com.lt.utils.PythonUtil;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author gaijf
 * @description: 脚本收集数据
 * @date 2021/11/1422:29
 */
@Slf4j
@Service
public class TushareScriptService {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    public void repairData(String topic,String tsCode,String tradeDate){
        RepairDataEntity entity = RepairDataEntity.builder()
                        .repairCode(tsCode)
                        .repairDate(tradeDate)
                        .repairTopic(topic).build();
        MqConfiguration.send(TushareUtil.TUSHARE_REPAIR_TOPIC,entity,defaultMQProducer);
    }

    /**
     * 获取日K数据
     * @param tsCode
     */
    @Async
    public void obtainDayLine(String tsCode,String startDate,
                              String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_DAY_LINE,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        if(null == list || list.isEmpty()){
            this.repairData(TushareUtil.TUSHARE_DAYLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transDayLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(TushareUtil.TUSHARE_DAYLINE_TOPIC,
                            item, item.get("ts_code").hashCode(),defaultMQProducer);
                });
        log.info("收集日K数据：tsCode:{},params:{},size:{}",tsCode,JSON.toJSONString(params),list.size());
    }

    /**
     * 获取周K线
     * @param tsCode
     */
    @Async
    public void obtainWeekLine(String tsCode,String startDate,
                               String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_WEEK_LINE,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        if(null == list || list.isEmpty()){
            this.repairData(TushareUtil.TUSHARE_WEEKLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transWeekMonthLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(TushareUtil.TUSHARE_WEEKLINE_TOPIC,
                            item,item.get("ts_code").hashCode(), defaultMQProducer);
                });
        log.info("收集周K数据：tsCode:{},params:{},size:{}",tsCode,JSON.toJSONString(params),list.size());
    }

    /**
     * 获取月K数据
     * @param tsCode
     * @return
     */
    @Async
    public void obtainMonthLine(String tsCode,String startDate,
                                String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_MONTH_LINE,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        if(null == list || list.isEmpty()){
            this.repairData(TushareUtil.TUSHARE_MONTHLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transWeekMonthLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(TushareUtil.TUSHARE_MONTHLINE_TOPIC,
                            item,item.get("ts_code").hashCode(), defaultMQProducer);
                });
        log.info("收集月K数据：tsCode:{},params:{},size:{}",tsCode,JSON.toJSONString(params),list.size());
    }
}
