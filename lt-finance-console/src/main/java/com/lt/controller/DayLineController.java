package com.lt.controller;

import com.lt.entity.KLineEntity;
import com.lt.service.KLineService;
import com.lt.web.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("day-line")
public class DayLineController {

    @Autowired
    private KLineService kLineService;

    @PostMapping("/line-list")
    public ResultEntity<List<KLineEntity>> queryDayLineList(String tradeDate){
        tradeDate = "20210129";
        List<KLineEntity> list = kLineService.queryDayLineList(tradeDate);
        return ResultEntity.success(list);
    }
}
