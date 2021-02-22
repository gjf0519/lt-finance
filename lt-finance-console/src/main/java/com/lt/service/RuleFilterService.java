package com.lt.service;

import com.lt.entity.RuleFilterEntity;
import com.lt.mapper.RuleFilterMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/2/22
 */
@Service
public class RuleFilterService {

    @Autowired
    private RuleFilterMapper ruleFilterMapper;

    @Transactional(rollbackFor = Exception.class)
    public void insertRuleFilter(RuleFilterEntity ruleFilterEntity){
        ruleFilterMapper.insertRuleFilter(ruleFilterEntity);
    };

    public List<String> queryByTradeDate(String tradeDate) {
        return ruleFilterMapper.queryByTradeDate(tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNextBreak(String tsCode,String tradeDate) {
        ruleFilterMapper.updateNextBreak(tsCode,tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateThreeBreak(String tsCode,String tradeDate) {
        ruleFilterMapper.updateThreeBreak(tsCode,tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateWeekBreak(String tsCode,String tradeDate) {
        ruleFilterMapper.updateWeekBreak(tsCode,tradeDate);
    }
}
