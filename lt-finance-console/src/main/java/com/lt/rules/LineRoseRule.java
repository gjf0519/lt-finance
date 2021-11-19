package com.lt.rules;

import com.lt.entity.KLineEntity;
import com.lt.shape.MaLineType;
import com.lt.utils.MathUtil;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/1/15
 */
public class LineRoseRule extends AbstractBaseRule<List<KLineEntity>,Integer>
        implements MaLineRule<List<KLineEntity>,Integer,Integer>{

    private double low = 0;
    private double high = 0;
    private int roseType = 0;
    private MaLineType roseLine = MaLineType.LINE005;

    public LineRoseRule(double high,double low) {
        this.low = low;
        this.high = high;
    }

    public LineRoseRule(int roseType,double high,double low) {
        this.low = low;
        this.high = high;
        this.roseType = roseType;
    }

    public LineRoseRule(double low, double high, MaLineType roseLine) {
        this.low = low;
        this.high = high;
        this.roseLine = roseLine;
    }

    public LineRoseRule(int roseType, double low, double high, MaLineType roseLine) {
        this.low = low;
        this.high = high;
        this.roseType = roseType;
        this.roseLine = roseLine;
    }

    private Integer kLineRose(List<KLineEntity> entitys){
        for(KLineEntity entity : entitys){
            if(entity.getPctChg() >= high
                    || entity.getPctChg() <= low){
                return 0;
            }
        }
        return 1;
    }

    private Integer kLineRose(List<KLineEntity> entitys,int limit){
        for(int i = 0;i < limit;i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() >= high
                    || entity.getPctChg() <= low){
                return 0;
            }
        }
        return 1;
    }

    private Integer kLineRoseNum(List<KLineEntity> entitys){
        int num = 0;
        for(KLineEntity entity : entitys){
            if(entity.getPctChg() >= high
                    && entity.getPctChg() <= low){
                num++;
            }
        }
        return num;
    }

    private Integer kLineRoseNum(List<KLineEntity> entitys,int limit){
        int num = 0;
        for(int i = 0;i < limit;i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() >= high
                    && entity.getPctChg() <= low){
                num++;
            }
        }
        return num;
    }

    private Integer kLineRoseSite(List<KLineEntity> entitys){
        for(int i = 0;i < entitys.size();i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() >= high
                    && entity.getPctChg() <= low){
                return i;
            }
        }
        return -1;
    }

    private Integer kLineRoseSite(List<KLineEntity> entitys,int limit){
        for(int i = 0;i < limit;i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() >= high
                    && entity.getPctChg() <= low){
                return i;
            }
        }
        return -1;
    }

    private Integer maLineRose(List<KLineEntity> entitys){
        double fline = klineVal(entitys.get(0),this.roseLine);
        double lline = klineVal(entitys.get(entitys.size()-1),this.roseLine);
        double ratio = MathUtil.sub(1,
                MathUtil.div(fline,lline,2),2);
        if(ratio >= high || ratio <= low){
            return 0;
        }
        return 1;
    }

    private Integer maLineRose(List<KLineEntity> entitys,int limit){
        double fline = klineVal(entitys.get(0),this.roseLine);
        double lline = klineVal(entitys.get(limit),this.roseLine);
        double ratio = MathUtil.sub(
                MathUtil.div(fline,lline,2),1,2);
        if(ratio >= high || ratio <= low){
            return 0;
        }
        return 1;
    }

    private Integer roseNums(List<KLineEntity> entitys){
        int num = 0;
        for(int i = 0;i < entitys.size();i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() <= high
                    && entity.getPctChg() >= low){
                num++;
            }
        }
        return num;
    }

    private Integer roseNums(List<KLineEntity> entitys,int limit){
        int num = 0;
        for(int i = 0;i < limit;i++){
            KLineEntity entity = entitys.get(i);
            if(entity.getPctChg() <= high
                    && entity.getPctChg() >= low){
                num++;
            }
        }
        return num;
    }

    private Integer checkAlg(List<KLineEntity> kLineEntities, Integer limit){
        Integer result = null;
        switch (this.roseType){
            case 1:
                result = limit <= 0 ?roseNums(kLineEntities):roseNums(kLineEntities,limit);
                break;
            case 2:
                result = limit <= 0 ?kLineRose(kLineEntities):kLineRose(kLineEntities,limit);
                break;
            case 3:
                result = limit <= 0 ?kLineRoseNum(kLineEntities):kLineRoseNum(kLineEntities,limit);
                break;
            case 4:
                result = limit <= 0 ?kLineRoseSite(kLineEntities):kLineRoseSite(kLineEntities,limit);
                break;
            default:
                result = limit <= 0 ?maLineRose(kLineEntities):maLineRose(kLineEntities,limit);
        }
        return result;
    }

    @Override
    public Integer verify(List<KLineEntity> kLineEntities) {
        return checkAlg(kLineEntities, 0);
    }

    @Override
    public Integer verify(List<KLineEntity> kLineEntities, Integer limit) {
        return checkAlg(kLineEntities, limit);
    }
}
