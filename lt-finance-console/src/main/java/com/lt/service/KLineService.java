package com.lt.service;

import com.lt.dto.DayLineDto;
import com.lt.dto.KLineDto;
import com.lt.dto.KlineChartsDto;
import com.lt.entity.EmaBreakEntity;
import com.lt.entity.KLineEntity;
import com.lt.mapper.KLineMapper;
import com.lt.view.PageData;
import com.lt.vo.DayLineVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * 保存日K线数据
     * @param map
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDayLine(Map<String,Object> map){
        kLineMapper.saveDayLine(map);
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
     * 保存K线突破数据
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEmaBreak(EmaBreakEntity entity) {
        kLineMapper.saveEmaBreak(entity);
    }

    public int hasSaveDayLine(String tscode, String tradeDate) {
        return kLineMapper.hasSaveDayLine(tscode,tradeDate);
    }

    public int hasSaveWeekLine(String tscode, String tradeDate) {
        return kLineMapper.hasSaveWeekLine(tscode,tradeDate);
    }

    public List<KLineEntity> queryWeekLineByLimit(String tscode, int limit) {
        return kLineMapper.queryWeekLineByLimit(tscode,limit);
    }

    public List<KLineEntity> queryWeekLineByLimitDate(String tscode, int limit,String tradeDate) {
        return kLineMapper.queryWeekLineByLimitDate(tscode,limit,tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveWeekLine(Map map) {
        kLineMapper.saveWeekLine(map);
    }

    public int hasSaveMonthLine(String tscode, String tradeDate) {
        return kLineMapper.hasSaveMonthLine(tscode,tradeDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMonthLine(Map map) {
        kLineMapper.saveMonthLine(map);
    }

    public KlineChartsDto queryDayLineByCode(String tsCode) {
        List<KLineDto> klineDtos = kLineMapper.queryDayLineByCode(tsCode);
        List<List<Object>> result = new ArrayList<>();
        for(KLineDto dto : klineDtos){
            List<Object> item = new ArrayList<>();
            item.add(dto.getTradeDate());
            item.add(dto.getOpen());
            item.add(dto.getClose());
            item.add(dto.getLow());
            item.add(dto.getHigh());
            result.add(item);
        }
        KlineChartsDto klineChartsDto = KlineChartsDto.builder()
                .tsCode(klineDtos.get(0).getTsCode())
                .lines(result).build();
        return klineChartsDto;
    }
}
