package com.lt.web.service;

import com.lt.dto.DayLineDto;
import com.lt.dto.KLineDto;
import com.lt.dto.KlineChartsDto;
import com.lt.entity.KLineEntity;
import com.lt.entity.RepairDataEntity;
import com.lt.web.mapper.KLineMapper;
import com.lt.utils.TimeUtil;
import com.lt.view.PageData;
import com.lt.vo.DayLineVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2020/12/2
 */
@Service
public class KLineService {

    @Resource
    private KLineMapper kLineMapper;

    /**
     * 列表日K数据
     * @param dayLineVo
     */
    public PageData<List<DayLineDto>> queryDayLineList(DayLineVo dayLineVo){
        int total = kLineMapper.queryDayLineCount(dayLineVo);
        List<DayLineDto> dayLineDtos = kLineMapper.queryDayLineList(dayLineVo);
        return PageData.build(total,dayLineDtos);
    }

    /**
     * 查询日K数据
     * @param tscode
     */
    public List<KLineEntity> queryDayLineList(String tscode,String tradeDate,int limit){
        List<KLineEntity> list = null;
        if(null == tradeDate){
            list = this.queryDayLineByLimit(tscode,limit);
        }else {
            list = this.queryDayLineByLimitDate(tscode,limit,tradeDate);
        }
        return list;
    }

    public List<KLineEntity> queryDayLineListAsc(String tscode,String tradeDate,int limit){
        return kLineMapper.queryDayLineListAsc(tscode,limit,tradeDate);
    }

    public List<KLineEntity> queryDayLineListAsc(String tscode,String tradeDate){
        return kLineMapper.queryDayLineByMin(tscode,tradeDate);
    }

    /**
     * 查询周K数据
     * @param tscode
     */
    public List<KLineEntity> queryWeekLineList(String tscode,String tradeDate,int limit){
        List<KLineEntity> list = null;
        if(null == tradeDate){
            list = this.queryWeekLineByLimit(tscode,limit);
        }else {
            list = this.queryWeekLineByLimitDate(tscode,limit,tradeDate);
        }
        return list;
    }

    /**
     * 查询日K线数据
     * @param code
     * @param limit
     * @return
     */
    public List<KLineEntity> queryDayLineByLimit(String code, int limit){
        return kLineMapper.queryDayLineByLimit(code,limit);
    }

    /**
     * 查询日K线数据
     * @param code
     * @param limit
     * @return
     */
    public List<KLineEntity> queryDayLineByLimitDate(String code, int limit,String tradeDate){
        return kLineMapper.queryDayLineByLimitDate(code,limit,tradeDate);
    }

    /**
     * 查询周K线数据
     * @param tscode
     * @param limit
     * @return
     */
    public List<KLineEntity> queryWeekLineByLimit(String tscode, int limit) {
        return kLineMapper.queryWeekLineByLimit(tscode,limit);
    }

    public List<KLineEntity> queryWeekLineByLimitDate(String tscode, int limit,String tradeDate) {
        return kLineMapper.queryWeekLineByLimitDate(tscode,limit,tradeDate);
    }

    public KlineChartsDto queryRuleDayLine(String tsCode,String tradeDate) {
        List<KLineDto> klineDtos = kLineMapper.queryRuleDayLine(tsCode,tradeDate);
        return assembleLineCharts(klineDtos);
    }

    public KlineChartsDto queryDayLineByCode(String tsCode) {
        List<KLineDto> klineDtos = kLineMapper.queryDayLineByCode(tsCode);
        return assembleLineCharts(klineDtos);
    }

    private KlineChartsDto assembleLineCharts(List<KLineDto> klineDtos){
        List<List<Object>> result = new ArrayList<>();
        for(KLineDto dto : klineDtos){
            List<Object> item = new ArrayList<>();
            item.add(dto.getTradeDate());
            item.add(dto.getOpen());
            item.add(dto.getClose());
            item.add(dto.getLow());
            item.add(dto.getHigh());
            item.add(dto.getVol());
            result.add(item);
        }
        KlineChartsDto klineChartsDto = KlineChartsDto.builder()
                .tsCode(klineDtos.get(0).getTsCode())
                .lines(result).build();
        return klineChartsDto;
    }

    public List<RepairDataEntity> queryRepairData(Date date) {
        String dateStr = TimeUtil.dateFormat(date,"yyyyMMdd");
        return kLineMapper.queryRepairData(dateStr);
    }

    public int queryCountByDate(Date date) {
        String dateStr = TimeUtil.dateFormat(date,"yyyyMMdd");
        return kLineMapper.queryCountByDate(dateStr);
    }

    public void deleteRepairById(int id) {
        kLineMapper.deleteRepairById(id);
    }

    public void updateRepairById(int id) {
        kLineMapper.updateRepairById(id);
    }

    public KLineEntity queryMinKline(String tsCode){
        return kLineMapper.queryMinKline(tsCode);
    }

    public List<KLineEntity> queryDayByTimeBucket(String tsCode, String limitStart, String limitEnd) {
        return kLineMapper.queryDayByTimeBucket(tsCode,limitStart,limitEnd);
    }
}
