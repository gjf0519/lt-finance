package com.lt;

import com.lt.service.ReceiveService;
import com.lt.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaijf
 * @description
 * @date 2020/12/8
 */
@SpringBootTest
public class KLineBreakTest {

    @Autowired
    ReceiveService receiveService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Test
    public void daybreak(){
        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
        for(String item : Constants.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                String flag = item.substring(0,2);
                String code = item.substring(2,item.length());
                receiveService.dayLineBreak(code+"."+flag.toUpperCase());
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        receiveService.dayLineBreak("600106.SH");
////        receiveService.dayLineBreak("603239.SH");
//        receiveService.dayLineBreak("000687.SZ");//1.93-1.71 下降小于20
////        receiveService.dayLineBreak("601016.SH","20201222");
//        //半年突破年但5 10 20 30 都在下方
////        receiveService.dayLineBreak("600644.SH","20201222");
//
////        receiveService.dayLineBreak("600292.SH","20201218");
//
////        receiveService.dayLineBreak("002529.SZ","20201216");
//        receiveService.dayLineBreak("000816.SZ","20201022");
////        receiveService.dayLineBreak("000816.SZ","20200703");
////        receiveService.dayLineBreak("002342.SZ","20201113");
//
//        //丰乐
//        receiveService.dayLineBreak("000713.SZ","20201217");
////        receiveService.dayLineBreak("000713.SZ","20201209");
//        receiveService.dayLineBreak("600189.SH","20201106");

          //急速下跌
//        receiveService.dayLineBreak("688299.SH","20210108");
    }

    @Test
    public void weekbreak(){
        CountDownLatch latch = new CountDownLatch(Constants.STOCK_CODE.size());
        for(String item : Constants.STOCK_CODE){
            threadPoolExecutor.execute(()->{
                String flag = item.substring(0,2);
                String code = item.substring(2,item.length());
                receiveService.weekLineBreak(code+"."+flag.toUpperCase());
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        receiveService.weekLineBreak("600319.SH","20191227");
    }

}
