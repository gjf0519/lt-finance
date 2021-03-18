package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.utils.BigDecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description 各均线趋势
 * @date 2021/1/14
 */
public class MaLineTrendRule
        extends AbstractBaseRule<List<KLineEntity>,Map<Integer,MaLineTrendRule.LineTread>>{

    private int [] days = new int[]{10,30,60};

    public MaLineTrendRule() {
        super();
    }

    public MaLineTrendRule(int [] days) {
        this.days = days;
    }

    @Override
    public Map<Integer,MaLineTrendRule.LineTread> verify(List<KLineEntity> entitys) {
        //1上升0平行-1下降
        List<Double> realMaValues = super.klineVals(entitys.get(0));
        Map<Integer,MaLineTrendRule.LineTread> result = new HashMap<>();
        for(int i = 0;i < days.length;i++){
            List<Double> items = super.klineVals(entitys.get(days[i]-1));
            List<Double> angles = new ArrayList<>();
            List<Double> directions = new ArrayList<>();
            for(int y = 0;y < realMaValues.size();y++){
                //判断均线方向
                double s = items.get(y) - realMaValues.get(y);
                directions.add(s);
                //判断均线倾斜度
                double r = BigDecimalUtil.div(s,realMaValues.get(y),2);
                angles.add(r);
            }
            LineTread lineTread = LineTread.builder()
                    .angles(angles).directions(directions).build();
            result.put(days[i],lineTread);
        }
        return result;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class LineTread{
        private List<Double> angles;
        private List<Double> directions;
    }
}
