package com.lt.service;

import com.lt.dto.KlineChartsDto;
import com.lt.dto.RuleLineDto;
import com.lt.entity.RuleFilterEntity;
import com.lt.mapper.RuleFilterMapper;
import com.lt.view.PageData;
import com.lt.vo.RuleLineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/2/22
 */
@Service
public class RuleFilterService {

    @Resource
    private RuleFilterMapper ruleFilterMapper;
    @Autowired
    private KLineService kLineService;


    @Transactional(rollbackFor = Exception.class)
    public void insertRuleFilter(RuleFilterEntity ruleFilterEntity){
        ruleFilterMapper.insertRuleFilter(ruleFilterEntity);
    };

    public List<String> queryByTradeDate(String tradeDate) {
        return ruleFilterMapper.queryByTradeDate(tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNextBreak(String tsCode,String tradeDate,String nextDate,int nextBreak) {
        ruleFilterMapper.updateNextBreak(tsCode,tradeDate,nextDate,nextBreak);
    }

    public PageData<List<RuleLineDto>> queryRuleLineList(RuleLineVo ruleLineVo) {
        int total = ruleFilterMapper.queryRuleLineCount(ruleLineVo);
        List<RuleLineDto> ruleLineDtos = ruleFilterMapper.queryRuleLineList(ruleLineVo);
        return PageData.build(total,ruleLineDtos);
    }

    public KlineChartsDto queryRuleLineByCode(String tsCode,String tradeDate) {
        String nextDate = ruleFilterMapper.queryRuleLineNextDate(tsCode,tradeDate);
        return kLineService.queryRuleDayLine(tsCode,nextDate);
    }
}
