package com.lt.web.mapper;

import com.lt.dto.DayLineDto;
import com.lt.dto.KLineDto;
import com.lt.entity.KLineEntity;
import com.lt.entity.RepairDataEntity;
import com.lt.vo.DayLineVo;
import org.apache.ibatis.annotations.*;

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

    @Select({"select * from lt_repair_data where repair_num = 0 and repair_date = #{tradeDate}"})
    List<RepairDataEntity> queryRepairData(@Param("tradeDate") String tradeDate);

    @Select({"SELECT count(1) from lt_plate_line where trade_date = #{tradeDate}"})
    int queryCountByDate(@Param("tradeDate") String tradeDate);

    @Delete({"delete from lt_repair_data where id = #{id}"})
    void deleteRepairById(@Param("id") int id);

    @Update({"update lt_repair_data set repair_num = 1 where id = #{id}"})
    void updateRepairById(@Param("id") int id);

    @Select({"SELECT d.* FROM lt_day_line d where d.low = ( " +
            "SELECT MIN(t.low) low FROM lt_day_line t where t.ts_code = #{tsCode} ) and d.ts_code = #{tsCode} "})
    KLineEntity queryMinKline(@Param("tsCode") String tsCode);

    @Select({"SELECT m.* FROM lt_day_line m where m.ts_code=#{tsCode} and m.trade_date >= #{tradeDate} order by m.trade_date "})
    List<KLineEntity> queryDayLineByMin(@Param("tsCode") String code,
                                        @Param("tradeDate") String tradeDate);
}
