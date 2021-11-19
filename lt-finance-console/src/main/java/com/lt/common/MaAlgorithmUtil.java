package com.lt.common;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;
import com.lt.utils.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 均线计算工具类
 */
public class MaAlgorithmUtil {

    /**
     * 均线向上突破
     * @param maLineType1
     * @param maLineType2
     * @param entitys
     * @return 大于0的整数
     */
    public static int maLineUpBreakDay(MaLineType maLineType1,
                                       MaLineType maLineType2,
                                       List<KLineEntity> entitys){
        List<Integer> breaks = lineUpOtherLine(maLineType1, maLineType2, entitys);
        if(0 == breaks.size()){
            return -1;
        }
        int breakSize = breaks.size() - 1;
        //突破天数
        int breakNum = 0;
        //均线向上,-1 0;-1 1;0 1
        if(breaks.get(0) > breaks.get(breakSize)){
            //已向上突破过去或刚好相等到临界值0
            for (int i = 0; i < (breaks.size() - 1); i++) {
                if (breaks.get(i) >= 0) {
                    breakNum++;
                }
            }
        }
        return breakNum;
    }

    /**
     * 判断均线是否一直在另一条均线上方或下方或平行
     * @param maLineType1
     * @param maLineType2
     * @param entitys
     * @return 1上方、0平行、-1下方、10有交叉
     */
    public static int maLineParallelBreakDay(MaLineType maLineType1,
                                             MaLineType maLineType2,
                                             List<KLineEntity> entitys){
        List<Integer> breaks = lineUpOtherLine(maLineType1, maLineType2, entitys);
        int breakSize = breaks.size() - 1;
        //突破天数
        int breakNum = 0;
        //一直上方或一直下方或一直平行,-1 -1；1 1；0 0；
        if(breaks.get(breakSize) == breaks.get(0)){
            int val0 = breaks.get(0);
            for (int i = 1; i < breaks.size(); i++) {
                if(val0 != breaks.get(i)){
                    return 10;
                }
            }
        }
        return breaks.get(0);
    }

    /**
     * 均线向下突破
     * @param maLineType1
     * @param maLineType2
     * @param entitys
     * @return
     */
    public static int maLineDownBreakDay(MaLineType maLineType1,
                                       MaLineType maLineType2,
                                       List<KLineEntity> entitys){
        List<Integer> breaks = lineUpOtherLine(maLineType1, maLineType2, entitys);
        int breakSize = breaks.size() - 1;
        //突破天数
        int breakNum = 0;
        //均线向下,1 0;1 -1;0 -1
        if(breaks.get(0) < breaks.get(breakSize)){
            //已向下突破过去或刚好相等到临界值0
            for (int i = 0; i < (breaks.size() - 1); i++) {
                if (breaks.get(i) >= 0) {
                    breakNum++;
                }
            }
        }
        return breakNum;
    }

    public static int maBreakDirectionDay(MaLineType maLineType1,
                                        MaLineType maLineType2,
                                        List<KLineEntity> entitys) {
        List<Integer> breaks = lineUpOtherLine(maLineType1, maLineType2, entitys);
        int breakSize = breaks.size() - 1;
        //突破天数
        int breakNum = 0;
        if (breaks.get(0) >= 0 && breaks.get(breakSize) == -1) {
            //已向上突破过去或刚好相等到临界值0
            for (int i = 0; i < (breaks.size() - 1); i++) {
                if (breaks.get(i) >= 0) {
                    breakNum++;
                }
            }
        }else if (breaks.get(0) < 0 && breaks.get(breakSize) >= 0){
            //已向上突破过去或刚好相等到临界值0
            for (int i = 0; i < (breaks.size() - 1); i++) {
                if (breaks.get(i) <= 0) {
                    breakNum--;
                }
            }
        } else if (breaks.get(0) > 0 && breaks.get(breakSize) > 0) {
            //一直在上方
            for (int i = 0; i < (breaks.size() - 1); i++) {
                if (breaks.get(i) > 0) {
                    breakNum++;
                }
            }
        }else if(breaks.get(0) < 0 && breaks.get(breakSize) < 0){
            //一直在下方
            return 0;
        }
        return breakNum;
    }

    /**
     * 向下：-1持续向下 -2向下后涨回又跌回
     * 缠绕：0
     * 向上：2持续向上 1向上后跌下又涨回
     *
     * @param maLineType1
     * @param maLineType2
     * @param entitys
     * @return
     */
    public static int maBreakDirectionPersistence(MaLineType maLineType1,
                                 MaLineType maLineType2,
                                 List<KLineEntity> entitys){
        List<Integer> breaks = lineUpOtherLine(maLineType1,maLineType2,entitys);
        int breakSize = breaks.size()-1;
        //方向判断
        boolean isUp = false;
        List<Boolean> persistences = new ArrayList<>();
        //已向上突破过去或刚好相等到临界值0
        if(breaks.get(0) >= 0 && breaks.get(breakSize) == -1){
            for(int i = 0;i < (breaks.size()-1);i++){
                if(breaks.get(i) < breaks.get(i+1)){
                    persistences.add(false);
                }else {
                    persistences.add(true);
                }
            }
            isUp = true;
        }else {
            for(int i = 0;i < (breaks.size()-1);i++){
                if(breaks.get(i) < breaks.get(i+1)){
                    persistences.add(true);
                }else {
                    persistences.add(false);
                }
            }
        }
        boolean isAll = true;
        for(Boolean persistence : persistences){
            if(persistence == false){
                isAll = false;
            }
        }
        if(isUp && isAll){
            return 1;
        }else if(isUp && !isAll){
            return 0;
        }
        return -1;
    }

    /**
     * 均线波动方向 -1下0等1上
     * @param maLineType1
     * @param maLineType2
     * @param entitys
     * @return
     */
    public static List<Integer> lineUpOtherLine(MaLineType maLineType1,
                               MaLineType maLineType2,
                               List<KLineEntity> entitys) {
        List<Double> maLine1Vals = portraitMaValues(entitys, maLineType1);
        List<Double> maLine2Vals = portraitMaValues(entitys, maLineType2);
        List<Integer> breaks = new ArrayList<>();
        for(int i = 0;i < maLine2Vals.size();i++){
            double val1 = maLine1Vals.get(i);
            double val2 = maLine2Vals.get(i);
            if(0 == val2){
                return null;
            }
            double val3 = -1;
            //处理数组越界问题
            if(maLine2Vals.size() > (i+1)){
                val3 = maLine2Vals.get(i+1);
            }
            //由于计算误差，连续两日小于界定为向下
            if(val1 > val2){
                breaks.add(1);
            }else if(val1 == val2){
                breaks.add(0);
            }else if(val3 != -1 && val1 < val3){
                breaks.add(-1);
            }else {
                breaks.add(-1);
            }
        }
        return breaks;
    }

    /**
     * K线与均线距离（0.01到-0.01），或穿过K线
     * @param maLine
     * @param kLineEntity
     * @return
     */
    public static boolean klineWithMaLine(double maLine,KLineEntity kLineEntity){
        //K线穿过最少一天均线，并且最高或最低价距离另一条均线距离小于0.1
        boolean maWith = maKLineDistance(maLine,kLineEntity);
        boolean maPiercing = maLinePiercingKline(maLine,kLineEntity);
        if(!maWith && !maPiercing){
            return false;
        }
        return true;
    }

    /**
     * K线与均线最近距离
     */
    public static boolean maKLineDistance(double maValue,KLineEntity kLineEntity){
        if(kLineEntity.getOpen() > kLineEntity.getClose()){
            double lowDistance = MathUtil.sub(
                    MathUtil.div(kLineEntity.getLow(),maValue),1,2);
            if(lowDistance > -0.01 && lowDistance <= 0.02){
                return true;
            }
            lowDistance = MathUtil.sub(
                    MathUtil.div(kLineEntity.getClose(),maValue),1,2);
            if(lowDistance > -0.01 && lowDistance <= 0.02){
                return true;
            }
        }else {
            double highDistance = MathUtil.sub(
                    MathUtil.div(kLineEntity.getHigh(),maValue),1,2);
            if(highDistance > -0.01 && highDistance <= 0.02){
                return true;
            }
            highDistance = MathUtil.sub(
                    MathUtil.div(kLineEntity.getOpen(),maValue),1,2);
            if(highDistance > -0.01 && highDistance <= 0.02){
                return true;
            }
        }
        return false;
    }

    /**
     * 均线穿过K线
     */
    public static boolean maLinePiercingKline(double maValue,KLineEntity kLineEntity){
        if(kLineEntity.getHigh() >= maValue && maValue >= kLineEntity.getLow()){
            return true;
        }
        return false;
    }

    /**
     * 多条多日均线间距离
     * @param kLineEntitys
     * @param lineTypes
     * @return
     */
    public static List<List<Double>> maDistance(List<KLineEntity> kLineEntitys,List<MaLineType> lineTypes){
        List<List<Double>> list = new ArrayList<>();
        for(KLineEntity kLineEntity : kLineEntitys){
            List<Double> items = new ArrayList<>();
            for(int i = 1;i < lineTypes.size();i++){
                double maVal = maDistance(kLineEntity,
                        lineTypes.get(i-1),lineTypes.get(i));
                items.add(maVal);
            }
            list.add(items);
        }
        return list;
    }

    /**
     * 多条均线间距离
     * @param kLineEntity
     * @param lineTypes
     * @return
     */
    public static List<Double> maDistance(KLineEntity kLineEntity,List<MaLineType> lineTypes){
        List<Double> list = new ArrayList<>();
        for(int i = 1;i < lineTypes.size();i++){
            double maVal = maDistance(kLineEntity,
                    lineTypes.get(i-1),lineTypes.get(i));
            list.add(maVal);
        }
        return list;
    }

    /**
     * 两条均线间距离
     * @param kLineEntity
     * @param lineType1
     * @param lineType2
     * @return
     */
    public static double maDistance(KLineEntity kLineEntity,
                           MaLineType lineType1,MaLineType lineType2){
        double maValue1 = portraitMaValue(kLineEntity, lineType1);
        double maValue2 = portraitMaValue(kLineEntity, lineType2);
        if(0 == maValue1 || 0 == maValue2){
            return 10;
        }
        return MathUtil.sub(MathUtil.div(maValue1,maValue2),1,2);
    }

    /**
     * 获取多天全部均线 横向
     * @param lineEntities
     * @return
     */
    public static List<List<Double>> transverseMaValues(List<KLineEntity> lineEntities){
        List<List<Double>> values = new ArrayList<>(lineEntities.size());
        for(KLineEntity entity : lineEntities){
            List<Double> items = transverseMaValue(entity);
            values.add(items);
        }
        return values;
    }

    /**
     * 获取某天全部均线 横向
     * @param entity
     * @return
     */
    public static List<Double> transverseMaValue(KLineEntity entity){
        List<Double> values = new ArrayList<>();
        for(MaLineType lineType : MaLineType.values()){
            double value = portraitMaValue(entity,lineType);
            if(0 == value){
                return values;
            }
            values.add(value);
        }
        return values;
    }

    /**
     * 根据均线类型获取一组均线值 纵向
     * @param lineEntities
     * @param lineType
     * @return
     */
    public static List<Double> portraitMaValues(List<KLineEntity> lineEntities, MaLineType lineType){
        List<Double> values = new ArrayList<>(lineEntities.size());
        for(KLineEntity entity : lineEntities){
            double value = portraitMaValue(entity,lineType);
            if(0 == value){
                return values;
            }
            values.add(value);
        }
        return values;
    }

    /**
     * 根据均线类型获取均线值
     * @param entity
     * @param lineType
     * @return
     */
    public static double portraitMaValue(KLineEntity entity, MaLineType lineType){
        double kline = 0;
        switch (lineType.getCode()){
            case 5:
                kline = entity.getMaFive();
                break;
            case 10:
                kline = entity.getMaTen();
                break;
            case 20:
                kline = entity.getMaTwenty();
                break;
            case 30:
                kline = entity.getMaMonth();
                break;
            case 60:
                kline = entity.getMaQuarter();
                break;
            case 120:
                kline = entity.getMaSemester();
                break;
            case 250:
                kline = entity.getMaYear();
                break;
            default:
                kline = 0;
        }
        return kline;
    }
}
