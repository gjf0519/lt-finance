package com.lt;

import com.lt.common.MaAlgorithmUtil;
import com.lt.entity.DailyBasicEntity;
import com.lt.entity.KLineEntity;
import com.lt.service.DailyBasicServie;
import com.lt.service.KLineService;
import com.lt.utils.TsCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 重要均线突破
 */
@SpringBootTest
public class BreachYearLine {
    @Autowired
    KLineService kLineService;
    @Autowired
    DailyBasicServie dailyBasicServie;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(TsCodes.STOCK_CODE.size());
        for(String item : TsCodes.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                try {
                    List<KLineEntity> list = kLineService
                            .queryDayLineList(item,null,30);
                    //图形计算
                    this.calculation(list);
                }catch (Exception e){
//                    System.out.println(item+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
    }

    private void calculation(List<KLineEntity> list){
        //5日线向上运行
        if(list.get(0).getMaFive() < list.get(4).getMaFive()){
            return;
        }
        //突破年线
        if(list.get(0).getMaFive() < list.get(0).getMaYear() &&
                list.get(1).getHigh() < list.get(1).getMaYear() &&
                list.get(0).getHigh() > list.get(0).getMaYear()){
            if(breakDirection(list,0)){
                System.out.println("突破年线===========>"+list.get(0).getTsCode());
                return;
            }
        }
        //突破半年线
        if(list.get(0).getMaFive() < list.get(0).getMaSemester() &&
                list.get(1).getHigh() < list.get(1).getMaSemester() &&
                list.get(0).getHigh() > list.get(0).getMaSemester()){
            if(breakDirection(list,1)){
                System.out.println("突破半年线===========>"+list.get(0).getTsCode());
                return;
            }
        }
        //突破季线
        if(list.get(0).getMaFive() < list.get(0).getMaQuarter() &&
                list.get(1).getHigh() < list.get(1).getMaQuarter() &&
                list.get(0).getHigh() > list.get(0).getMaQuarter()){
            if(breakDirection(list,1)){
                System.out.println("突破季线===========>"+list.get(0).getTsCode());
                return;
            }
        }
    }

    //均线突破方向
    private boolean breakDirection(List<KLineEntity> list,int maType){
        //5日内没有新均线乡下突破
        if(maType == 0){
            if(list.get(0).getMaTen() < list.get(0).getMaYear()
                    && list.get(9).getMaTen() > list.get(9).getMaYear()){
                return false;
            }
            if(list.get(0).getMaTwenty() < list.get(0).getMaYear()
                    && list.get(9).getMaTwenty() > list.get(9).getMaYear()){
                return false;
            }
            if(list.get(0).getMaMonth() < list.get(0).getMaYear()
                    && list.get(9).getMaMonth() > list.get(9).getMaYear()){
                return false;
            }
            if(list.get(0).getMaQuarter() < list.get(0).getMaYear()
                    && list.get(9).getMaQuarter() > list.get(9).getMaYear()){
                return false;
            }
            if(list.get(0).getMaSemester() < list.get(0).getMaYear()
                    && list.get(9).getMaSemester() > list.get(9).getMaYear()){
                return false;
            }
        }
        if(maType == 1){
            if(list.get(0).getMaTen() < list.get(0).getMaSemester()
                    && list.get(5).getMaTen() > list.get(9).getMaSemester()){
                return false;
            }
            if(list.get(0).getMaTwenty() < list.get(0).getMaSemester()
                    && list.get(9).getMaTwenty() > list.get(9).getMaSemester()){
                return false;
            }
            if(list.get(0).getMaMonth() < list.get(0).getMaSemester()
                    && list.get(9).getMaMonth() > list.get(9).getMaSemester()){
                return false;
            }
            if(list.get(0).getMaQuarter() < list.get(0).getMaSemester()
                    && list.get(9).getMaQuarter() > list.get(9).getMaSemester()){
                return false;
            }
        }
        if(maType == 2){
            if(list.get(0).getMaTen() < list.get(0).getMaQuarter()
                    && list.get(9).getMaTen() > list.get(9).getMaSemester()){
                return false;
            }
            if(list.get(0).getMaTwenty() < list.get(0).getMaQuarter()
                    && list.get(9).getMaTwenty() > list.get(9).getMaSemester()){
                return false;
            }
            if(list.get(0).getMaMonth() < list.get(0).getMaQuarter()
                    && list.get(9).getMaMonth() > list.get(9).getMaSemester()){
                return false;
            }
        }
        return true;
    }


    /**
     * MA方向计算
     * @param list
     */
    public void maDirection(List<KLineEntity> list){
        //方向分类：向上  向上横盘  向下 向下横盘
        List<List<Double>> maValues = MaAlgorithmUtil.transverseMaValues(list);
        //
        for(List<Double> items : maValues){

        }
    }
}
