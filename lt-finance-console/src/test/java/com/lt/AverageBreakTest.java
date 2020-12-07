package com.lt;

import com.lt.entity.KLineEntity;
import com.lt.entity.EmaBreakEntity;
import com.lt.service.KLineService;
import com.lt.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author gaijf
 * @description 计算均线突破
 * @date 2020/12/2
 */
@SpringBootTest
public class AverageBreakTest {

    @Autowired
    KLineService kLineService;

    @Test
    public void calculate(){
        int i = 0;
        for(String item : Constants.STOCK_CODE){
            System.out.println(item+"============================="+i++);
            String flag = item.substring(0,2);
            String code = item.substring(2,item.length());
            saveEmaBreak(code+"."+flag.toUpperCase());
        }
    }

    //日K均线突破计算
    public void saveEmaBreak(String code){
        int limit = 11;
        List<KLineEntity> list = kLineService.queryDayLineByLimit(code,limit);
        int num = TwentyPrice(list, limit);
        EmaBreakEntity entity = EmaBreakEntity.builder()
                .tsCode(code)
                .klineType("日K")
                .breakType("20")
                .tradeDate(list.get(0).getTradeDate())
                .risingNumber(num)
                .build();
        kLineService.saveEmaBreak(entity);
    }

    //20日均线计算
    public int TwentyPrice(List<KLineEntity> list, int limit){
        if(list.get(0).getTwentyPrice() - list.get(limit-1).getTwentyPrice() > 0){
            return 0;
        }
        int num = 0;
        for(KLineEntity entity : list){
            if((entity.getClose() - entity.getTwentyPrice()) < 0
                    || (entity.getOpen() - entity.getTwentyPrice()) < 0){
                return num;
            }
            num++;
        }
        return num;
    }
}
