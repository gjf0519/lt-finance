package com.lt.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gaijf
 * @date 2021/11/17
 * @description 执行Python脚本
 */
public class PythonUtil {

    /**
     * 执行python脚本
     * @param params
     * @return
     */
    public static List<String> executePython(String[] params) throws Exception {
        String[] args = new String[params.length+1];
        args [0] = TushareUtil.PYTHON_ORDER;
        for(int i = 1;i < args.length;i++){
            args[i] = params[i-1];
        }
        Process process = Runtime.getRuntime().exec(args);
        List<String> list = receiveData(process);
        if(list.size() > 1){
            Collections.reverse(list);
        }
        process.waitFor();
        return list;
    }

    /**
     * 接收脚本数据
     * @param process
     * @return
     * @throws Exception
     */
    private static List<String> receiveData(Process process) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line = null;
        List<String> datas = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            datas = JSONArray.parseArray(line,String.class);
        }
        reader.close();
        return datas;
    }
}
