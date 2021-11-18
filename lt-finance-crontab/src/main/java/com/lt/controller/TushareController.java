package com.lt.controller;

import com.lt.service.TushareApiService;
import com.lt.service.TushareScriptService;
import com.lt.utils.TimeUtil;
import com.lt.view.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description
 * @date 2020/12/25
 */
@RestController
public class TushareController {

    @Autowired
    private TushareApiService apiService;
    @Autowired
    private TushareScriptService scriptService;

    /**
     * 获取股票列表
     * @return
     */
    @GetMapping("/stock/doce")
    public ResultEntity<List<Map<String,String>>> obtainStockBasic(){
        List<Map<String,String>> list = apiService.obtainStockBasic();
        return ResultEntity.success(list);
    }

    /**
     * 获取概念列表
     * @return
     */
    @GetMapping("/plate/list")
    public ResultEntity<List<Map<String,String>>> obtainPlates(){
        List<Map<String,String>> list = apiService.obtainPlates();
        return ResultEntity.success(list);
    }

    /**
     * 获取概念指数
     * @return
     */
    @GetMapping("/plate/line/{tradeDate}")
    public ResultEntity<String> obtainPlateIndex(@PathVariable("tradeDate") String tradeDate){
        apiService.obtainPlateIndex(tradeDate);
        return ResultEntity.success();
    }

    /**
     * 获取基本信息
     * @param tradeDate
     * @return
     */
    @GetMapping("/day/basic/{tradeDate}")
    public ResultEntity<String> obtainDayBasic(@PathVariable("tradeDate") String tradeDate){
        apiService.obtainDayBasic(tradeDate);
        return ResultEntity.success();
    }

    /**
     * 获取概念成分股
     * @param plateCode
     * @return
     */
    @GetMapping("/plate/element/{plateCode}")
    public ResultEntity<String> obtainPlateElement(@PathVariable("plateCode") String plateCode){
        apiService.obtainPlateElement(plateCode);
        return ResultEntity.success();
    }

    /**
     * 获取日K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/day/line/{tsCode}")
    public ResultEntity<String> obtainDayLineByDoce(@PathVariable("tsCode") String tsCode) throws Exception {
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        scriptService.obtainDayLine(tsCode,tradeDate,tradeDate);
        return ResultEntity.success();
    }

    /**
     * 获取周K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/week/line/{tsCode}")
    public ResultEntity<String> obtainWeekLine(@PathVariable("tsCode") String tsCode) throws Exception {
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        scriptService.obtainWeekLine(tsCode,tradeDate,tradeDate);
        return ResultEntity.success();
    }

    /**
     * 获取月K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/month/line/{tsCode}")
    public ResultEntity<String> obtainMonthLine(@PathVariable("tsCode") String tsCode) throws Exception {
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        scriptService.obtainMonthLine(tsCode,tradeDate,tradeDate);
        return ResultEntity.success();
    }
}
