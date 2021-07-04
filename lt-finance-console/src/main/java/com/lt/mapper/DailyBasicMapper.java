package com.lt.mapper;

import com.lt.entity.DailyBasicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DailyBasicMapper {

    @Select({"SELECT * from lt_daily_basic b WHERE b.ts_code = #{code} ORDER BY b.trade_date DESC limit #{limit}"})
    List<DailyBasicEntity> queryDailyBasicBylimit(@Param("code") String code, @Param("limit") int limit);

    @Select({"SELECT * from lt_daily_basic b WHERE b.ts_code = #{code} and b.trade_date <= #{tradeDate} ORDER BY b.trade_date DESC limit #{limit}"})
    List<DailyBasicEntity> queryDailyBasicByLimitDate(@Param("code") String code,@Param("tradeDate") String tradeDate, @Param("limit") int limit);
}
