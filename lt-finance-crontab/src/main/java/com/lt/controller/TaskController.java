package com.lt.controller;

import com.lt.service.TushareService;
import com.lt.view.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaijf
 * @description
 * @date 2020/12/25
 */
@RestController
public class TaskController {

    @Autowired
    private TushareService tushareService;

    @GetMapping("/month_line")
    public ResultEntity<String> requestMonthLine(){
        tushareService.requestMonthLine();
        return ResultEntity.success();
    }
}
