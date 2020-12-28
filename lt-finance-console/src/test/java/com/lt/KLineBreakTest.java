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
//        receiveService.dayLineBreak("600415.SH");
//        receiveService.dayLineBreak("000816.SZ","20201023");
//        receiveService.dayLineBreak("000713.SZ","20201217");
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
