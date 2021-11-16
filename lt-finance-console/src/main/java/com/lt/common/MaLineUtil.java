package com.lt.common;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;

import java.util.ArrayList;
import java.util.List;

public class MaLineUtil {

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
