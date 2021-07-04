package com.lt;

import com.lt.utils.RestTemplateUtil;
import com.lt.utils.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class DailyBasicTest {

    @Test
    public void initDay(){
        String tradeDate = TimeUtil.dateFormat(new Date(),"yyyyMMdd");
        String result = RestTemplateUtil.get("http://212.64.69.77:9090/day/basic/20210702", null);
        System.out.println(result);
    }
}
