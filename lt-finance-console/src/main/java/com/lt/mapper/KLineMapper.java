package com.lt.mapper;

import com.lt.entity.KLineEntity;
import com.lt.entity.EmaBreakEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface KLineMapper {

    @Insert({"insert into lt_day_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,five_price,ten_price,twenty_price,thirty_price,sixty_price) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{five_price},#{ten_price},#{twenty_price},#{thirty_price},#{sixty_price})"})
    void saveDayLine(Map<String,Object> map);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryDayLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} and trade_date <= #{trade_date} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryDayLineByLimitDate(
            @Param("code") String code,
            @Param("limit") int limit,
            @Param("trade_date") String tradeDate);

    @Insert({"insert into lt_ema_break (ts_code,kline_type,trade_date,break_day,rose" +
            ",fivetoten,fivetotwenty,fivetothirty,fivetosixty,tentotwenty,tentothirty,tentosixty" +
            ",twentytothirty,twentytosixty,kline_flat,kline_angle) values" +
            " (#{tsCode},#{klineType},#{tradeDate},#{breakDay},#{rose}" +
            ",#{fivetoten},#{fivetotwenty},#{fivetothirty},#{fivetosixty},#{tentotwenty},#{tentothirty},#{tentosixty}" +
            ",#{twentytothirty},#{twentytosixty},#{klineFlat},#{klineAngle})"})
    void saveEmaBreak(EmaBreakEntity entity);

    @Select({"SELECT count(1) from lt_day_line m WHERE m.ts_code=#{tscode} and m.trade_date = #{tradeDate}"})
    int hasSaveDayLine(@Param("tscode") String tscode,@Param("tradeDate") String tradeDate);

    @Select({"SELECT count(1) from lt_week_line m WHERE m.ts_code=#{tscode} and m.trade_date = #{tradeDate}"})
    int hasSaveWeekLine(@Param("tscode") String tscode,@Param("tradeDate") String tradeDate);

    @Select({"SELECT * from lt_week_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryWeekLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Insert({"insert into lt_week_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,five_price,ten_price,twenty_price,thirty_price,sixty_price,semester_price) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{five_price},#{ten_price},#{twenty_price},#{thirty_price},#{sixty_price},#{semester_price})"})
    void saveWeekLine(Map<String,Object> map);

    @Insert({"insert into lt_month_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,five_price,ten_price,twenty_price,thirty_price,sixty_price,semester_price) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{five_price},#{ten_price},#{twenty_price},#{thirty_price},#{sixty_price},#{semester_price})"})
    void saveMonthLine(Map map);

    @Select({"SELECT count(1) from lt_month_line m WHERE m.ts_code=#{tscode} and m.trade_date = #{tradeDate}"})
    int hasSaveMonthLine(@Param("tscode") String tscode,@Param("tradeDate") String tradeDate);

    @Select({"SELECT * from lt_week_line m WHERE m.ts_code=#{code} and trade_date <= #{trade_date} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryWeekLineByLimitDate(@Param("code") String tscode,
                                               @Param("limit") int limit,
                                               @Param("trade_date") String tradeDate);
}
