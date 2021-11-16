package com.lt.service;

import com.alibaba.fastjson.JSONArray;
import com.lt.config.MqConfiguration;
import com.lt.entity.RepairDataEntity;
import com.lt.utils.Constants;
import com.lt.utils.TushareAccess;
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
        MqConfiguration.send(Constants.TUSHARE_REPAIR_TOPIC,entity,defaultMQProducer);
    }

    /**
     * 获取日K数据
     * @param tsCode
     */
    @Async
    public void obtainDayLine(String tsCode,String startDate,
                              String endDate) throws Exception {
        List<String> list = this.executePython(TushareAccess.PY_DAY_LINE,
                tsCode,startDate,endDate);
        if(null == list || list.isEmpty()){
            this.repairData(Constants.TUSHARE_DAYLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(this::transDayLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(Constants.TUSHARE_DAYLINE_TOPIC,
                            item, item.get("ts_code").hashCode(),defaultMQProducer);
                });
    }

    /**
     * 获取周K线
     * @param tsCode
     */
    @Async
    public void obtainWeekLine(String tsCode,String startDate,
                               String endDate) throws Exception {
        List<String> list = this.executePython(TushareAccess.PY_WEEK_LINE,
                tsCode,startDate,endDate);
        if(null == list || list.isEmpty()){
            this.repairData(Constants.TUSHARE_WEEKLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(this::transWeekMonthLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(Constants.TUSHARE_WEEKLINE_TOPIC,
                            item,item.get("ts_code").hashCode(), defaultMQProducer);
                });
    }

    /**
     * 获取月K数据
     * @param tsCode
     * @return
     */
    @Async
    public void obtainMonthLine(String tsCode,String startDate,
                                String endDate) throws Exception {
        List<String> list = this.executePython(TushareAccess.PY_MONTH_LINE,
                tsCode,startDate,endDate);
        if(null == list || list.isEmpty()){
            this.repairData(Constants.TUSHARE_MONTHLINE_TOPIC,tsCode,startDate);
            return;
        }
        list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(this::transWeekMonthLineMap)
                .forEach(item -> {
                    MqConfiguration.sendOrder(Constants.TUSHARE_MONTHLINE_TOPIC,
                            item,item.get("ts_code").hashCode(), defaultMQProducer);
                });
    }

    /**
     * 转换日K数据
     * @param values
     * @return
     */
    private Map<String,String> transDayLineMap(List<String> values){
        Map<String,String> map = new HashMap<>();
        for(int i = 0;i < TushareAccess.DAY_LINE_FIELDS.length;i++){
            map.put(TushareAccess.DAY_LINE_FIELDS[i],values.get(i));
        }
        return map;
    }

    /**
     * 转换周、月K数据
     * @param values
     * @return
     */
    private Map<String,String> transWeekMonthLineMap(List<String> values){
        Map<String,String> map = new HashMap<>();
        for(int i = 0;i < TushareAccess.LINE_FIELDS.length;i++){
            map.put(TushareAccess.LINE_FIELDS[i],values.get(i));
        }
        return map;
    }

    /**
     * 执行python脚本
     * @param pyPath
     * @param tsCode
     * @return
     */
    private List<String> executePython(String pyPath, String tsCode,
                                       String startDate, String endDate) throws Exception {
        String[] args = new String[]{TushareAccess.PYTHON_ORDER,
                pyPath,tsCode,startDate,endDate};
        Process process = Runtime.getRuntime().exec(args);
        List<String> list = this.receiveData(process);
        if(list.size() > 1){
            Collections.reverse(list);
        }
        process.waitFor();
        log.info("脚本收集数据 python:{},tsCode:{},size:{}",pyPath,tsCode,list.size());
        return list;
    }

    /**
     * 接收脚本数据
     * @param process
     * @return
     * @throws Exception
     */
    private List<String> receiveData(Process process) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line = null;
        List<String> datas = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            datas = JSONArray.parseArray(line,String.class);
        }
        reader.close();
        return datas;
    }
}
