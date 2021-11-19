package com.lt.features.maline.math;

import com.lt.common.MaLineUtil;
import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;
import com.lt.utils.MathUtil;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.List;

/**
 * @author gaijf
 * @description: 集中趋势
 * @date 2021/8/2613:36
 */
public class MaLineCentralTendencyFeature implements MaLineMathFeature{

    public void execute(List<KLineEntity> list,MaLineType maLineType){
        List<Double> maList = MaLineUtil.portraitMaValues(list,maLineType);
        double [] values = new double[maList.size()];
        for(int i = 0;i < values.length;i++){
            values[i] = maList.get(i);
        }
        //中位数
        Median median= new Median();
        double medianValue = MathUtil.round(median.evaluate(values),2);
        //众数
        double[] res = StatUtils.mode(values);
        //算数平均数
        double avgValue = MathUtil.round(StatUtils.mean(values),2);
        if(medianValue > avgValue){
            return;
        }
        //几何平均数
        double geometryAvg = MathUtil.round(StatUtils.geometricMean(values),2);
//        System.out.println(list.get(maList.size()-1).getTradeDate()+"==="+"中位数:"+medianValue+"===平均数:"+avgValue+"===众数:"+ JSON.toJSONString(res));
        return ;
    }
}
