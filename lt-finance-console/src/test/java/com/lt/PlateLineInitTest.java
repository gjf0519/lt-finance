package com.lt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.mapper.ReceiveMapper;
import com.lt.result.TushareResult;
import com.lt.service.ReceiveService;
import com.lt.utils.Constants;
import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TsCodes;
import com.lt.utils.TushareAccess;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * 板块数据初始化
 */
@SpringBootTest(classes = {ConsoleApplication.class })
public class PlateLineInitTest {

    @Autowired
    private ReceiveService receiveService;
    private static List<String> CODES = new ArrayList<>();

    /**
     * 全量初始化
     */
    @Test
    public void initData() throws InterruptedException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse("20210701",df);
        LocalDate now = LocalDate.now().parse("20210714",df);
        for(int i = 1;i < 400;i++){
            LocalDate itemDate = localDate.plusDays(i);
            if(itemDate.isAfter(now)){
                return;
            }
            Map<String,Object> item = new HashMap<>();
            String tradeDate = df.format(itemDate);
            item.put("trade_date", tradeDate);
            TushareResult tushareResult = this.requestData(item
                    ,TushareAccess.PLATE_INDEX_API[0],TushareAccess.PLATE_INDEX_API[1]);
            List<Map<String,String>> list = this.transitionMap(tushareResult);
            Thread.sleep(50000);
            if(null == list || list.isEmpty()){
                CODES.add(tradeDate);
                continue;
            }
            System.out.println(tradeDate+"========================================================================"+list.size());
            for(Map<String,String> map : list){
                receiveService.receivePlateLine(map);
            }
        }
        System.out.println(JSON.toJSONString(CODES));
    }

    @Test
    public void initDataOne(){
        Map<String,Object> item = new HashMap<>();
        item.put("trade_date", "20210701");
        TushareResult tushareResult = this.requestData(item
                ,TushareAccess.PLATE_INDEX_API[0],TushareAccess.PLATE_INDEX_API[1]);
        List<Map<String,String>> list = this.transitionMap(tushareResult);
        System.out.println("========================================================================"+list);
        if(null == list || list.isEmpty()){
            return;
        }
        System.out.println("========================================================================"+list.size());
        for(Map<String,String> map : list){
            receiveService.receivePlateLine(map);
        }
    }

    public List<Map<String,String>> transitionMap(TushareResult tushareResult){
        if(null == tushareResult || tushareResult.getData().getFields().isEmpty()){
            return null;
        }
        List<String> fields = tushareResult.getData().getFields();
        List<List<String>> items = tushareResult.getData().getItems();
        List<Map<String,String>> result = new ArrayList<>();
        items.stream().forEach(o -> {
            Map<String,String> map = new HashMap<>();
            Stream.iterate(0, i -> i+1).limit(o.size())
                    .forEach(i -> {
                        map.put(fields.get(i), o.get(i));
                    });
            result.add(map);
        });
        return result;
    }

    public TushareResult requestData(Map<String,Object> item,String apiName,String fields){
        Map<String,Object> params = new HashMap<>();
        params.put("params", item);
        params.put("api_name", apiName);
        params.put("token", TushareAccess.TUSHARE_TOKEN);
        params.put("fields", fields);
        String res = RestTemplateUtil.post(TushareAccess.URL,JSON.toJSONString(params),null);
        System.out.println(res);
        TushareResult tushareResult = JSON.parseObject(res, TushareResult.class);
        if(!"0".equals(tushareResult.getCode())){
            return null;
        }else {
            System.out.println(tushareResult.getMsg());
        }
        return tushareResult;
    }

    public static void main(String[] args) {

    }
}
