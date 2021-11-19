package com.lt.screen.day;

import com.lt.entity.KLineEntity;
import com.lt.rules.LineRoseRule;
import com.lt.screen.LineFormFilter;
import com.lt.utils.MathUtil;

import java.util.List;

/**
 * @author gaijf
 * @description 长阳不破
 * @date 2021/2/3
 */
public class DayLongSunFilter implements LineFormFilter {
    @Override
    public int execute(List<KLineEntity> kLineEntities) {
        //过滤出涨停位置
        LineRoseRule klineRoseRule = new LineRoseRule(4,4.9,21);
        int site = klineRoseRule.verify(kLineEntities,15);
        if(site < 3){
            return 0;
        }
        for(int i = 0;i < site;i++){
            double ratio = MathUtil.sub(MathUtil.div(kLineEntities.get(i).getClose(),
                    kLineEntities.get(site).getClose(),2),1,2);
            if(ratio < -0.02 || ratio > 0.02){
                return 0;
            }
        }
        return 1;
    }
}
