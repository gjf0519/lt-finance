package com.lt.mapper;

import com.lt.dto.DayLineDto;
import com.lt.dto.KLineDto;
import com.lt.entity.KLineEntity;
import com.lt.vo.DayLineVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KLineMapper {

    int queryDayLineCount(DayLineVo dayLineVo);

    List<DayLineDto> queryDayLineList(DayLineVo dayLineVo);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryDayLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} and trade_date <= #{trade_date} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryDayLineByLimitDate(
            @Param("code") String code,
            @Param("limit") int limit,
            @Param("trade_date") String tradeDate);

    @Select({"SELECT * from lt_week_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryWeekLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Select({"SELECT * from lt_week_line m WHERE m.ts_code=#{code} and trade_date <= #{trade_date} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryWeekLineByLimitDate(@Param("code") String tscode,
                                               @Param("limit") int limit,
                                               @Param("trade_date") String tradeDate);

    @Select({"SELECT id,ts_code,trade_date,open,high,low,close,vol from lt_day_line m WHERE m.ts_code=#{tsCode}"})
    List<KLineDto> queryDayLineByCode(@Param("tsCode") String tsCode);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} and trade_date >= #{trade_date} ORDER BY trade_date LIMIT #{limit}"})
    List<KLineEntity> queryDayLineListAsc(@Param("code") String code,
                                          @Param("limit") int limit,
                                          @Param("trade_date") String tradeDate);

    @Select({"SELECT id,ts_code,trade_date,open,high,low,close,vol from lt_day_line m WHERE m.ts_code=#{tsCode} and m.trade_date <= #{tradeDate}"})
    List<KLineDto> queryRuleDayLine(@Param("tsCode") String code,
                                    @Param("tradeDate") String tradeDate);
}
