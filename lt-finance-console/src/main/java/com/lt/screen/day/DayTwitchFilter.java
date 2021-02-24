package com.lt.screen.day;

import com.lt.entity.KLineEntity;
import com.lt.rules.KmKlineMaLineRule;
import com.lt.rules.SiteKlineMaLineRule;
import com.lt.screen.LineFormFilter;
import com.lt.shape.MaLineType;

import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 下跌急拉
 * @date 2021/2/24
 */
public class DayTwitchFilter implements LineFormFilter {
    @Override
    public int execute(List<KLineEntity> kLineEntities) {
        KLineEntity kLineEntity = kLineEntities.get(0);
        KLineEntity kLineEntityUp = kLineEntities.get(1);
        //过滤掉前一天上涨当天下跌的票
        if(kLineEntityUp.getPctChg() > 0
                || kLineEntity.getOpen() > kLineEntity.getClose()){
            return 0;
        }
        //当天未超过前一天的开盘价
        if(kLineEntity.getClose() < kLineEntityUp.getOpen()){
            return 0;
        }
        //K先与均线的位置
        SiteKlineMaLineRule siteKlineMaLineRule = new SiteKlineMaLineRule();
        Map<String,Integer> sites = siteKlineMaLineRule.verify(kLineEntity,MaLineType.LINE020);
        if(-1 == sites.get(MaLineType.LINE020.getName())){
            return 0;
        };
        if(1 == sites.get(MaLineType.LINE020.getName())){
            KmKlineMaLineRule kmKlineMaLineRule = new KmKlineMaLineRule();
            double km = kmKlineMaLineRule.verify(kLineEntity);
            if(km > 0.1){
                return 0;
            }
        }
        return 1;
    }
}
