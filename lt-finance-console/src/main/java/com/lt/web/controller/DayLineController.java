package com.lt.web.controller;

import com.lt.dto.DayLineDto;
import com.lt.dto.KlineChartsDto;
import com.lt.web.service.KLineService;
import com.lt.utils.TimeUtil;
import com.lt.view.PageData;
import com.lt.view.ResultEntity;
import com.lt.vo.DayLineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("day-line")
public class DayLineController {

    @Autowired
    private KLineService kLineService;

    @PostMapping("/line-list")
    public ResultEntity<PageData<List<DayLineDto>>> queryDayLineList(@RequestBody DayLineVo dayLineVo){
        if(StringUtils.isEmpty(dayLineVo.getTradeDate())){
            String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
            dayLineVo.setTradeDate(tradeDate);
        }
        PageData<List<DayLineDto>> result = kLineService.queryDayLineList(dayLineVo);
        return ResultEntity.success(result);
    }

    @PostMapping("/line/{tsCode}")
    public ResultEntity<KlineChartsDto> queryDayLineByCode(@PathVariable(value="tsCode") String tsCode){
        KlineChartsDto klineChartsDto = kLineService.queryDayLineByCode(tsCode);
        return ResultEntity.success(klineChartsDto);
    }
}
