package com.lt.controller;

import com.lt.service.TushareService;
import com.lt.view.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    private TushareService tushareService;

    /**
     * 获取股票列表
     * @return
     */
    @GetMapping("/stock/doce")
    public ResultEntity<List<String>> obtainStockBasic(){
        List<String> codes = tushareService.obtainStockBasic();
        return ResultEntity.success(codes);
    }

    /**
     * 获取基本信息
     * @param tradeDate
     * @return
     */
    @GetMapping("/day/basic/{tradeDate}")
    public ResultEntity<String> obtainDayBasic(@PathVariable("tradeDate") String tradeDate){
        tushareService.obtainDayBasic(tradeDate);
        return ResultEntity.success();
    }

    /**
     * 获取日K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/day/line/{tsCode}")
    public ResultEntity<String> obtainDayLine(@PathVariable("tsCode") String tsCode){
        tushareService.obtainDayLine(tsCode);
        return ResultEntity.success();
    }

    /**
     * 获取周K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/week/line/{tsCode}")
    public ResultEntity<String> obtainWeekLine(@PathVariable("tsCode") String tsCode){
        tushareService.obtainWeekLine(tsCode);
        return ResultEntity.success();
    }

    /**
     * 获取月K数据
     * @param tsCode
     * @return
     */
    @GetMapping("/month/line/{tsCode}")
    public ResultEntity<String> obtainMonthLine(@PathVariable("tsCode") String tsCode){
        tushareService.obtainMonthLine(tsCode);
        return ResultEntity.success();
    }

    /**
     * 获取概念列表
     * @return
     */
    @GetMapping("/plate/list")
    public ResultEntity<List<Map<String,String>>> obtainPlates(){
        List<Map<String,String>> list = tushareService.obtainPlates();
        return ResultEntity.success(list);
    }

    /**
     * 获取概念指数
     * @return
     */
    @GetMapping("/plate/line")
    public ResultEntity<String> obtainPlateIndex(){
        tushareService.obtainPlateIndex();
        return ResultEntity.success();
    }

    /**
     * 获取概念成分股
     * @param plateCode
     * @return
     */
    @GetMapping("/plate/element/{plateCode}")
    public ResultEntity<String> obtainPlateElement(@PathVariable("plateCode") String plateCode){
        tushareService.obtainPlateElement(plateCode);
        return ResultEntity.success();
    }
}
