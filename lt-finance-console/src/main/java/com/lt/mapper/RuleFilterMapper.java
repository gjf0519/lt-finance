package com.lt.mapper;

import com.lt.dto.RuleLineDto;
import com.lt.entity.RuleFilterEntity;
import com.lt.vo.RuleLineVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RuleFilterMapper {

    @Insert({"insert into lt_rule_filter (ts_code,trade_date,pct_chg,rule_name) values" +
            " (#{tsCode},#{tradeDate},#{pctChg},#{ruleName})"})
    public void insertRuleFilter(RuleFilterEntity ruleFilterEntity);

    @Select({"select ts_code from lt_rule_filter where trade_date=#{tradeDate}"})
    List<String> queryByTradeDate(@Param("tradeDate") String tradeDate);

    @Update({"update lt_rule_filter set next_break = #{nextBreak},next_date = #{nextDate} where ts_code=#{tsCode} and trade_date=#{tradeDate}"})
    void updateNextBreak(@Param("tsCode")String tsCode,
                         @Param("tradeDate") String tradeDate,
                         @Param("nextDate") String nextDate,
                         @Param("nextBreak") int nextBreak);

    int queryRuleLineCount(RuleLineVo ruleLineVo);

    List<RuleLineDto> queryRuleLineList(RuleLineVo ruleLineVo);

    @Select({"select next_date from lt_rule_filter where ts_code=#{tsCode} and trade_date=#{tradeDate}"})
    String queryRuleLineNextDate(@Param("tsCode") String tsCode,@Param("tradeDate") String tradeDate);
}
