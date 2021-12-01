package com.lt;

import com.lt.common.EmaLineUtil;
import com.lt.entity.KLineEntity;
import com.lt.shape.EmaLevel;
import com.lt.shape.EmaLineType;
import com.lt.utils.MathUtil;
import com.lt.utils.TimeUtil;
import com.lt.utils.TsCodes;
import com.lt.web.service.KLineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gaijf
 * @description: ST柏龙 002776 20210930 均线均匀反向排列形态
 * @date 2021/11/2015:50
 */
@SpringBootTest
public class EmaOrderBreakTest {

    @Autowired
    KLineService kLineService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private static AtomicInteger number = new AtomicInteger(0);
    private static AtomicInteger highs = new AtomicInteger(0);
    private static AtomicInteger rise = new AtomicInteger(0);

    @Test
    public void test(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    if(item.startsWith("3")){
                        return;
                    }
                    String date = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
                    this.calculation(item,date,268);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(MathUtil.div(highs.get(),number.get(),2));
        System.out.println(MathUtil.div(rise.get(),number.get(),2));
    }

    @Test
    public void testByOne(){
        this.calculation("002776.SZ","20211119",268);
    }

    public void calculation(String item,String tradeDate,int limit){
        List<KLineEntity> list = kLineService
                .queryDayLineList(item,tradeDate,limit);//"002776.sz","20211119"
        //计算60/120/250均线三角形形态时间段
        List<Map<String,String>> timeBucketList = this.emaTimeBucket(list);
        if(timeBucketList.isEmpty()){
            return;
        }

        for(Map<String,String> timeBucket : timeBucketList){
            String tsCode = timeBucket.get("tsCode");
            String limitStart = timeBucket.get("limitStart");
            String limitEnd = timeBucket.get("limitEnd");
            String lineTypeName = timeBucket.get("lineType");
            //计算均线振幅，过滤掉振幅不等于0.0的数据范围
            List<KLineEntity> bucketEntityList = kLineService.queryDayByTimeBucket(tsCode,limitStart,limitEnd);
            int timeSubByRate = this.emaChgRate(lineTypeName,bucketEntityList);
            if(timeSubByRate <= 0){
                continue;
            }
            bucketEntityList = bucketEntityList.subList(0,timeSubByRate);
            //计算均线向上运行形态判定
            int timeSubByUp = this.littleEmaUp(lineTypeName,bucketEntityList);
            if(timeSubByUp <= 0){
                continue;
            }
            bucketEntityList = bucketEntityList.subList(0,timeSubByUp);
            if(bucketEntityList.size() < 25){
                continue;
            }
            //判断当前待突破局限的下级均线是否一直在当前局限下方
            boolean isJuniorDown = this.juniorEmaDown(lineTypeName,bucketEntityList);
            if(isJuniorDown){
                continue;
            }
            //特征买入点计算1-均线向下250/120/60/30/20/10/5均匀排列
            KLineEntity entity = this.emaEvenDown(bucketEntityList);
            //计算后期上涨的概率
            this.riseProb(entity,list);
        }
    }

    /**
     * 计算60/120/250均线三角形形态时间段
     * @param list
     * @return
     */
    public List<Map<String,String>> emaTimeBucket(List<KLineEntity> list){
        List<EmaLineType> maLineTypeList = Arrays.asList(EmaLineType.LINE060, EmaLineType.LINE120, EmaLineType.LINE250);
        int limitStart = 0;
        int limitEnd = 0;
        List<Map<String,String>> timeBucketList = new ArrayList<>();
        for(int i = 1;i < maLineTypeList.size();i++){
            List<Double> emaLow = EmaLineUtil.emaParallelList(list, maLineTypeList.get(i-1));
            List<Double> emaHigh = EmaLineUtil.emaParallelList(list, maLineTypeList.get(i));
            List<Double> emaSus = new ArrayList<>();
            //均线时间段计算
            for(int y = 0;y < emaHigh.size();y++){
                Double sub = MathUtil.sub(emaHigh.get(y),emaLow.get(y),2);
                if(y == 0 && sub > 0){
                    limitStart = y;
                    emaSus.add(sub);
                    continue;
                }else if(sub < 0){
                    if(emaSus.size() > 0){
                        emaSus = new ArrayList<>();
                    }
                    continue;
                }else if(sub >= 0 && emaSus.isEmpty()){
                    limitStart = y;
                    emaSus.add(sub);
                    continue;
                }
                if(emaSus.get(emaSus.size()-1) > sub){
                    limitEnd = y;
                    if(emaSus.size() < 30){
                        continue;
                    }
                    Map<String,String> map = new HashMap<>();
                    map.put("tsCode",list.get(0).getTsCode());
                    map.put("limitStart",list.get(limitStart).getTradeDate());
                    map.put("limitEnd",list.get(limitEnd).getTradeDate());
                    map.put("lineType",maLineTypeList.get(i-1).getName());
                    timeBucketList.add(map);
                    emaSus = new ArrayList<>();
                    limitStart = y;
                }else {
                    emaSus.add(sub);
                }
            }
        }
        return timeBucketList;
    }

    /**
     * 计算均线振幅，过滤掉振幅不等于0.0的数据范围
     * @param lineTypeName
     * @param kLineEntityList
     * @return
     */
    public int emaChgRate(String lineTypeName,List<KLineEntity> kLineEntityList){
        List<Double> emaValues = null;
        for(EmaLineType lineType : EmaLineType.values()){
            if(lineTypeName.equals(lineType.getName())){
                emaValues = EmaLineUtil.emaParallelList(kLineEntityList,lineType);
                break;
            }
        }
        List<String> emaChgs = new ArrayList<>();
        int num = 0;
        int limitEnd = 0;
        for(int h = 0;h < kLineEntityList.size() - 1;h++){
            BigDecimal item1 = new BigDecimal(emaValues.get(h));
            BigDecimal item2 = new BigDecimal(emaValues.get(h+1));
            BigDecimal mean = item1.divide(item2,4, BigDecimal.ROUND_HALF_UP)
                    .setScale(4, BigDecimal.ROUND_UP);
            BigDecimal chg = mean.subtract(new BigDecimal("1")).setScale(2, BigDecimal.ROUND_HALF_UP);
            if(chg.doubleValue() != 0.0){
                num++;
            }else {
                num = 0;
            }
            if(num > 2){
                break;
            }
            limitEnd = h;
            emaChgs.add(chg.toString());
        }
        return limitEnd;
    }

    /**
     * 计算均线向上运行形态判定
     * @param lineTypeName
     * @param kLineEntityList
     * @return
     */
    public int littleEmaUp(String lineTypeName,List<KLineEntity> kLineEntityList){
        List<Double> emaValues = null;
        for(EmaLineType lineType : EmaLineType.values()){
            if(lineTypeName.equals(lineType.getName())){
                emaValues = EmaLineUtil.emaParallelList(kLineEntityList,lineType);
                break;
            }
        }
        int num = 0;
        int limitEnd = 0;
        for (int i = 1;i < kLineEntityList.size();i++){
            if(emaValues.get(i-1) < emaValues.get(i)){
                num++;
            }else if(num > 0 && emaValues.get(i-2) < emaValues.get(i)){
                num++;
            }else {
                num = 0;
            }
            if(num > 2){
                break;
            }
            limitEnd = i - 2;
        }
        return limitEnd;
    }

    /**
     * 判断某一均线是否一直在父均线下方运行
     * @param lineTypeName
     * @param kLineEntityList
     * @return
     */
    public boolean juniorEmaDown(String lineTypeName,List<KLineEntity> kLineEntityList){
        List<Double> realEmaValues = null;
        List<Double> juniorEmaValues = null;
        for(EmaLevel level : EmaLevel.values()){
            if(lineTypeName.equals(level.getReal().getName())){
                realEmaValues = EmaLineUtil.emaParallelList(kLineEntityList,level.getReal());
                juniorEmaValues = EmaLineUtil.emaParallelList(kLineEntityList,level.getChild());
                break;
            }
        }
        List<Boolean> downs = new ArrayList<>();
        int num = 0;
        for (int i = 0;i < realEmaValues.size();i++){
            if(realEmaValues.get(i) > juniorEmaValues.get(i)){
                num++;
                downs.add(true);
            }else {
                downs.add(true);
            }
        }
        return num == downs.size() ? true : false;
    }

    public KLineEntity emaEvenDown(List<KLineEntity> list){
        int index = 0;
        boolean isAllDown = false;
        for(int i = 0;i < list.size();i++){
            index = i;
            List<Double> realEmas = EmaLineUtil.emaCross(list.get(i));
            for(int y = 0;y < (realEmas.size()-1);y++){
                if(realEmas.get(y) > realEmas.get(y+1)){
                    isAllDown = false;
                    break;
                }
                isAllDown = true;
            }
            if(isAllDown){
                break;
            }
        }
        Date date = TimeUtil.StringToDate(list.get(index).getTradeDate(),"yyyyMMdd");
        int limitDay = TimeUtil.getDiffDays(date,new Date());
        KLineEntity resultEntity = null;
        //为了计算上涨概率
        if(isAllDown){
            resultEntity = list.get(index);
        }
        //10日内上出现该形态的数据
        if(isAllDown && limitDay < 10){
            System.out.println(list.get(index).getTradeDate()+"************************************************"+list.get(0).getTsCode());
        }
        return resultEntity;
    }

    /**
     *  //计算后期上涨的概率
     * @param entity
     * @param list
     */
    public void riseProb(KLineEntity entity,List<KLineEntity> list){
        if(null == entity){
            return;
        }
        Double max = 0.0;
        for(int i = 0;i < list.size();i++){
            if(list.get(i).getTradeDate()
                    .equals(entity.getTradeDate())){
                break;
            }
            if(list.get(i).getClose() > max){
                max = list.get(i).getClose();
            }
        }
        //计算涨幅
        BigDecimal mean = new BigDecimal(max).divide(new BigDecimal(entity.getClose()),4, BigDecimal.ROUND_HALF_UP)
                .setScale(4, BigDecimal.ROUND_UP);
        BigDecimal chg = mean.subtract(new BigDecimal("1")).setScale(2, BigDecimal.ROUND_HALF_UP);
        if(chg.doubleValue() > 0.5){
            rise.incrementAndGet();
            System.out.println(entity.getTsCode()+"========="+entity.getTradeDate()+"============"+chg);
        }
        if(max > entity.getClose()){
            highs.incrementAndGet();
        }else {
            //System.out.println(entity.getTsCode()+"========="+entity.getTradeDate());
        }
        number.incrementAndGet();
    }
}
