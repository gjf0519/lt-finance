package com.lt.controller;

import com.lt.dto.KlineChartsDto;
import com.lt.dto.RuleLineDto;
import com.lt.service.RuleFilterService;
import com.lt.view.PageData;
import com.lt.view.ResultEntity;
import com.lt.vo.RuleLineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rule-line")
public class KlineController {

    @Autowired
    private RuleFilterService ruleFilterService;

    @PostMapping("/line-list")
    public ResultEntity<PageData<List<RuleLineDto>>> queryRuleLineList(@RequestBody RuleLineVo ruleLineVo){
        PageData<List<RuleLineDto>> result = ruleFilterService.queryRuleLineList(ruleLineVo);
        return ResultEntity.success(result);
    }

    @PostMapping("/line/{tsCode}/{tradeDate}")
    public ResultEntity<KlineChartsDto> queryRuleLineByCode(@PathVariable(value="tsCode") String tsCode,
                                                            @PathVariable(value="tradeDate") String tradeDate){
        KlineChartsDto klineChartsDto = ruleFilterService.queryRuleLineByCode(tsCode,tradeDate);
        return ResultEntity.success(klineChartsDto);
    }
}
