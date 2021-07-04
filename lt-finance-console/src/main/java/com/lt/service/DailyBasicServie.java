package com.lt.service;

import com.lt.entity.DailyBasicEntity;
import com.lt.mapper.DailyBasicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class DailyBasicServie {

    @Resource
    private DailyBasicMapper dailyBasicMapper;

    public List<DailyBasicEntity> queryDailyBasic(String item, String tradeDate, int limit) {
        List<DailyBasicEntity> list = null;
        if(null == tradeDate){
            list = dailyBasicMapper.queryDailyBasicBylimit(item, limit);
        }else {
            list = dailyBasicMapper.queryDailyBasicByLimitDate(item,tradeDate,limit);
        }
        return list;
    }
}
