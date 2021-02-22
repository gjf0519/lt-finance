package com.lt.mapper;

import com.lt.entity.RuleFilterEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RuleFilterMapper {

    @Insert({"insert into lt_rule_filter (ts_code,trade_date,pct_chg,rule_name) values" +
            " (#{tsCode},#{tradeDate},#{pctChg},#{ruleName})"})
    public void insertRuleFilter(RuleFilterEntity ruleFilterEntity);

    @Select({"select ts_code from lt_rule_filter where trade_date=#{tradeDate}"})
    List<String> queryByTradeDate(@Param("tradeDate") String tradeDate);

    @Update({"update lt_rule_filter set next_break = '1' where ts_code=#{tsCode} and trade_date=#{tradeDate}"})
    void updateNextBreak(@Param("tsCode")String tsCode,@Param("tradeDate") String tradeDate);

    @Update({"update lt_rule_filter set three_break = '1' where ts_code=#{tsCode} and trade_date=#{tradeDate}"})
    void updateThreeBreak(@Param("tsCode")String tsCode,@Param("tradeDate") String tradeDate);

    @Update({"update lt_rule_filter set week_break = '1' where ts_code=#{tsCode} and trade_date=#{tradeDate}"})
    void updateWeekBreak(@Param("tsCode")String tsCode,@Param("tradeDate") String tradeDate);
}
