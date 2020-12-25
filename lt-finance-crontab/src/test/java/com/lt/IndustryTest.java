package com.lt;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author gaijf
 * @description
 * @date 2020/12/25
 */
public class IndustryTest {
    public static void main(String[] args) {
        //1、打开浏览器
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.get("http://q.10jqka.com.cn/thshy/");
        String str = chromeDriver.getTitle();
        System.out.println(str);
    }
}
