package com.lt.mapper;

import com.lt.dto.DayLineDto;
import com.lt.dto.KLineDto;
import com.lt.entity.EmaBreakEntity;
import com.lt.entity.KLineEntity;
import com.lt.entity.RepairDataEntity;
import com.lt.vo.DayLineVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReceiveMapper {

    @Select({"SELECT count(1) from lt_daily_basic m WHERE m.ts_code=#{tsCode} and m.trade_date = #{tradeDate}"})
    int hasSaveDaily(@Param("tsCode") String tsCode, @Param("tradeDate") String tradeDate);

    @Insert({"insert into lt_daily_basic (ts_code,trade_date,close,turnover_rate,turnover_rate_free,volume_ratio,circ_mv) " +
            "values (#{ts_code},#{trade_date},#{close},#{turnover_rate},#{turnover_rate_f},#{volume_ratio},#{circ_mv})"})
    void saveDaily(Map map);

    @Select({"SELECT count(1) from lt_day_line m WHERE m.ts_code=#{tsSode} and m.trade_date = #{tradeDate}"})
    int hasSaveDayLine(@Param("tsSode") String tsSode, @Param("tradeDate") String tradeDate);

    @Select({"SELECT * from lt_day_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryDayLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Insert({"insert into lt_day_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,ma_five,ma_ten,ma_twenty,ma_month,ma_quarter,ma_semester,ma_year) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{ma_five},#{ma_ten},#{ma_twenty},#{ma_month},#{ma_quarter},#{ma_semester},#{ma_year})"})
    void saveDayLine(Map<String, String> map);

    @Select({"SELECT count(1) from lt_week_line m WHERE m.ts_code=#{tsCode} and m.trade_date = #{tradeDate}"})
    int hasSaveWeekLine(@Param("tsCode") String tsCode, @Param("tradeDate") String tradeDate);

    @Select({"SELECT * from lt_week_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryWeekLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Insert({"insert into lt_week_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,ma_five,ma_ten,ma_twenty,ma_month,ma_quarter,ma_semester,ma_year) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{ma_five},#{ma_ten},#{ma_twenty},#{ma_month},#{ma_quarter},#{ma_semester},#{ma_year})"})
    void saveWeekLine(Map<String, String> map);

    @Select({"SELECT count(1) from lt_plate_line m WHERE m.ts_code=#{tsCode} and m.trade_date = #{tradeDate}"})
    int hasSavePlateLine(@Param("tsCode") String tsCode, @Param("tradeDate") String tradeDate);

    @Select({"SELECT * from lt_plate_line m WHERE m.ts_code=#{code} ORDER BY trade_date desc LIMIT #{limit}"})
    List<KLineEntity> queryPlateLineByLimit(@Param("code") String code, @Param("limit") int limit);

    @Insert({"insert into lt_plate_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,ma_five,ma_ten,ma_twenty,ma_month,ma_quarter,ma_semester,ma_year) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_change}" +
            ",#{vol},#{float_mv},#{ma_five},#{ma_ten},#{ma_twenty},#{ma_month},#{ma_quarter},#{ma_semester},#{ma_year})"})
    void savePlateLine(Map<String, String> map);

    @Select({"SELECT count(1) from lt_repair_data m WHERE m.repair_code = #{repairCode} and m.repair_date = #{repairDate}"})
    int hasSaveRepairData(RepairDataEntity repairDataEntity);

    @Insert({"insert into lt_repair_data (repair_code,repair_date,repair_topic) values (#{repairCode},#{repairDate},#{repairTopic})"})
    void saveRepairData(RepairDataEntity repairDataEntity);

    @Select({"SELECT count(1) from lt_month_line m WHERE m.ts_code=#{tsCode} and m.trade_date = #{tradeDate}"})
    int hasSaveMonthLine(@Param("tsCode") String tsCode, @Param("tradeDate") String tradeDate);

    @Insert({"insert into lt_month_line (ts_code,trade_date,open,high,low,close,pre_close,price_chg,pct_chg," +
            "vol,amount,ma_five,ma_ten,ma_twenty,ma_month,ma_quarter,ma_semester,ma_year) values" +
            " (#{ts_code},#{trade_date},#{open},#{high},#{low},#{close},#{pre_close},#{change},#{pct_chg}" +
            ",#{vol},#{amount},#{ma_five},#{ma_ten},#{ma_twenty},#{ma_month},#{ma_quarter},#{ma_semester},#{ma_year})"})
    void saveMonthLine(Map<String, String> map);
}
