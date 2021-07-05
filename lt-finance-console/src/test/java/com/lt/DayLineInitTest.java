package com.lt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lt.mapper.ReceiveMapper;
import com.lt.utils.Constants;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 日K数据初始化
 */
@SpringBootTest(classes = {ConsoleApplication.class })
public class DayLineInitTest {

    @Value("${finanace.system.python.profile}")
    private String pyHome;
    @Value("${finanace.system.python.day-line}")
    private String dayLinePath;
    @Resource
    private ReceiveMapper receiveMapper;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private static List<String> CODES = Collections.synchronizedList(new ArrayList<>());

    /**
     * 全量初始化
     */
    @Test
    public void initDay(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                List<Map<String,String>> result = null;
                try {
                    result = requestDayPyData(item);
                } catch (Exception e) {
                    CODES.add(item);
                    latch.countDown();
                }
                if(null == result || result.isEmpty()){
                    latch.countDown();
                    return;
                }
                this.calculationMa(result);
                for(Map<String,String> map : result){
                    receiveMapper.saveDayLine(map);
                }
                latch.countDown();
                System.out.println(latch.getCount());
            });
        }
        try {
            latch.await();
            System.out.println(JSON.toJSONString(CODES));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String,String>> calculationMa(List<Map<String,String>> result){
        if(result.size() <= 0 || result.size() < 5){
            return null;
        }
        double [] closes = new double[result.size()];
        for(int i = 0;i < result.size();i++){
            closes[i] = Double.valueOf(result.get(i).get("close"));
        }
        this.maValue(closes,result);
        return result;
    }

    public void maValue(double [] closes,List<Map<String,String>> result){
        for(int y = 0;y < result.size();y++){
            for (int i = 0; i < Constants.MA_NUM_ARREY.length; i++) {
                if(closes.length < Constants.MA_NUM_ARREY[i]){
                    return;
                }
                int from = y;
                int to = y + Constants.MA_NUM_ARREY[i];
                if(to >= closes.length){
                    continue;
                }
                double [] item = Arrays.copyOfRange(closes,from,to);
                double mean = StatUtils.mean(item);
                result.get(y).put(Constants.MA_NAME_ARREY[i],String.valueOf(mean));
            }
        }
    }

    public List<Map<String,String>> requestDayPyData(String code) throws Exception {
        List<String> list = executePython(dayLinePath,code);
        List<Map<String,String>> result = transPyDataDay(list);
        return result;
    }

    private List<Map<String,String>> transPyDataDay(List<String> list){
        List<Map<String,String>> results = new ArrayList();
        for(String line : list){
            List<String> values = JSONArray.parseArray(line,String.class);
            Map<String,String> result = new HashMap<>();
            for(int i = 0; i < TushareAccess.DAY_LINE_FIELDS.length; i++){
                result.put(TushareAccess.DAY_LINE_FIELDS[i],values.get(i));
            }
            results.add(result);
        }
        return results;
    }

    private List<String> executePython(String pyPath,String tsCode) throws Exception{
        List<String> list = new ArrayList<>();
        Process proc;
        String[] args = new String[]{pyHome,pyPath,tsCode};
        BufferedReader reader = null;
        try {
            proc = Runtime.getRuntime().exec(args);
            reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                list = JSONArray.parseArray(line,String.class);
            }
            reader.close();
            proc.waitFor();
        } finally {
            if (reader != null){
                reader.close();
            }
        }
        return list;
    }
}
