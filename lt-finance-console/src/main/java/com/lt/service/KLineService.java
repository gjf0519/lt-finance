package com.lt.service;

import com.lt.entity.KLineEntity;
import com.lt.entity.EmaBreakEntity;
import com.lt.mapper.KLineMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Transactional(rollbackFor = Exception.class)
    public void deleteWeekByCode(String tscode) {
        kLineMapper.deleteWeekByCode(tscode);
    }
}
