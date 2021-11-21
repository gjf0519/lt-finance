package com.lt.common;

import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLineType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaijf
 * @description 均线取值工具类
 * @date 2020/12/2
 */
public class EmaLineUtil {

    /**
     * 获取某天全部均值 横向
     * @param entity
     * @return
     */
    public static List<Double> emaCross(KLineEntity entity){
        List<Double> values = new ArrayList<>();
        for(EmaLineType lineType : EmaLineType.values()){
            Double value = emaValue(entity,lineType);
            if(null == value){
                return values;
            }
            values.add(value);
        }
        return values;
    }

    /**
     * 获取全部均值 横向
     * @param lineEntities
     * @return
     */
    public static List<List<Double>> emaCrossList(List<KLineEntity> lineEntities){
        List<List<Double>> values = new ArrayList<>(lineEntities.size());
        for(KLineEntity entity : lineEntities){
            List<Double> items = emaCross(entity);
            values.add(items);
        }
        return values;
    }

    /**
     * 根据均线类型获取一组均线值 纵向
     * @param lineEntities
     * @param lineType
     * @return
     */
    public static List<Double> emaParallelList(List<KLineEntity> lineEntities,
                                           EmaLineType lineType){
        List<Double> values = new ArrayList<>(lineEntities.size());
        for(KLineEntity entity : lineEntities){
            Double value = emaValue(entity,lineType);
            if(null == value){
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
    public static Double emaValue(KLineEntity entity, EmaLineType lineType){
        Double kline = null;
        switch (lineType.getCode()){
            case 5:
                kline = entity.getEmaFive();
                break;
            case 10:
                kline = entity.getEmaTen();
                break;
            case 20:
                kline = entity.getEmaTwenty();
                break;
            case 30:
                kline = entity.getEmaMonth();
                break;
            case 60:
                kline = entity.getEmaQuarter();
                break;
            case 120:
                kline = entity.getEmaHalfYear();
                break;
            case 250:
                kline = entity.getEmaFullYear();
                break;
        }
        return kline;
    }
}
