package com.lt.service;

import com.alibaba.fastjson.JSONArray;
import com.lt.mapper.TushareMapper;
import com.lt.utils.PythonUtil;
import com.lt.utils.TushareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @date 2021/11/17
 * @description 初始化、补充Tushare数据
 */
@Slf4j
@Service
public class KlineInitService {

    @Resource
    private TushareMapper receiveMapper;

    /**
     * 初始化日K数据
     * @param tsCode
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    public void initDayLine(String tsCode,String startDate,
                            String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_DAY_LINE_HOME,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        List<Map<String,String>> maps = list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transDayLineMap)
                .collect(Collectors.toList());
        Collections.reverse(maps);
        this.calculationMa(maps);
        for(Map<String,String> map : maps){
            receiveMapper.saveDayLine(map);
        }
    }

    /**
     * 初始化月K数据
     * @param tsCode
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    public void initWeekLine(String tsCode,String startDate,
                            String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_WEEK_LINE_HOME,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        List<Map<String,String>> maps = list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transWeekMonthLineMap)
                .collect(Collectors.toList());
        Collections.reverse(maps);
        this.calculationMa(maps);
        for(Map<String,String> map : maps){
            receiveMapper.saveWeekLine(map);
        }
    }

    /**
     * 初始化月K数据
     * @param tsCode
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    public void initMonthLine(String tsCode,String startDate,
                             String endDate) throws Exception {
        String[] params = new String[]{TushareUtil.PY_MONTH_LINE_HOME,tsCode,startDate,endDate};
        List<String> list = PythonUtil.executePython(params);
        List<Map<String,String>> maps = list.stream()
                .map(line -> JSONArray.parseArray(line, String.class))
                .map(TushareUtil::transWeekMonthLineMap)
                .collect(Collectors.toList());
        Collections.reverse(maps);
        this.calculationMa(maps);
        for(Map<String,String> map : maps){
            receiveMapper.saveMonthLine(map);
        }
    }

    public List<Map<String,String>> calculationMa(List<Map<String,String>> result){
        if(result.size() <= 0 || result.size() < 5){
            return null;
        }
        BigDecimal[] closes = new BigDecimal[result.size()];
        for(int i = 0;i < result.size();i++){
            closes[i] = new BigDecimal(result.get(i).get("close"));
        }
        this.maValue(closes,result);
        return result;
    }

    public void maValue(BigDecimal [] closes,List<Map<String,String>> result){
        for(int y = 0;y < result.size();y++){
            for (int i = 0; i < TushareUtil.MA_NUM_ARREY.length; i++) {
                if(closes.length < TushareUtil.MA_NUM_ARREY[i]){
                    continue;
                }
                int from = y;
                int to = y + TushareUtil.MA_NUM_ARREY[i];
                if(to > closes.length){
                    continue;
                }
                BigDecimal [] items = Arrays.copyOfRange(closes,from,to);
                BigDecimal decimalSum = new BigDecimal("0");
                for(BigDecimal decimal : items){
                    decimalSum = decimalSum.add(decimal);
                }
                BigDecimal mean = decimalSum.divide(BigDecimal.valueOf(items.length),2, BigDecimal.ROUND_HALF_UP)
                        .setScale(2, BigDecimal.ROUND_UP);
                result.get(y).put(TushareUtil.MA_NAME_ARREY[i],mean.toString());
            }
        }
    }
}
