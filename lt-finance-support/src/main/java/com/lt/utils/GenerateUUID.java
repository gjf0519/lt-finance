package com.lt.utils;

import java.util.UUID;

/**
 * @author gaijf
 * @description
 * @date 2020/11/6
 */
public class GenerateUUID {

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    public static String getUUIDFormat(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
