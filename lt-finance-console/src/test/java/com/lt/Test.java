package com.lt;

import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaijf
 * @description
 * @date 2020/12/16
 */
public class Test {
    public static void main(String[] args) {
        List<String> list = executePython("E:\\workspace-python\\week_line1.py","002455.SZ");
        for(String line : list){
                List<String> vals = JSONArray.parseArray(line,String.class);
                Map<String,Object> result = new HashMap<>();
                result.put("ts_code",vals.get(0));
                result.put("trade_date",vals.get(1));
                result.put("close",vals.get(2));
                result.put("open",vals.get(3));
                result.put("high",vals.get(4));
                result.put("low",vals.get(5));
                result.put("pre_close",vals.get(6));
                result.put("change",vals.get(7));
                result.put("pct_chg",vals.get(8));
                result.put("vol",vals.get(9));
        }
        System.out.println(list);
//        for(String item : Constants.STOCK_CODE){
//            String flag = item.substring(0,2);
//            String code = item.substring(2,item.length());
//            String tscode = code+"."+flag.toUpperCase();
//            List<String> list = executePython("E:\\workspace-python\\week_line.py",tscode);
//            for(String line : list){
//                List<String> vals = JSONArray.parseArray(line,String.class);
//                Map<String,Object> result = new HashMap<>();
//                result.put("ts_code",vals.get(0));
//                result.put("trade_date",vals.get(1));
//                result.put("close",vals.get(2));
//                result.put("open",vals.get(3));
//                result.put("high",vals.get(4));
//                result.put("low",vals.get(5));
//                result.put("pre_close",vals.get(6));
//                result.put("change",vals.get(7));
//                result.put("pct_chg",vals.get(8));
//                result.put("vol",vals.get(9));
//            }
//        }
    }

    public static List<Map<String,Object>> getPyData(String tscode){
        List<String> list = executePython("E:\\workspace-python\\week_line.py",tscode);
        List<Map<String,Object>> results = new ArrayList();
        for(String line : list){
            List<String> vals = JSONArray.parseArray(line,String.class);
            Map<String,Object> result = new HashMap<>();
            result.put("ts_code",vals.get(0));
            result.put("trade_date",vals.get(1));
            result.put("close",vals.get(2));
            result.put("open",vals.get(3));
            result.put("high",vals.get(4));
            result.put("low",vals.get(5));
            result.put("vol",vals.get(6));
            results.add(result);
        }
        return results;
    }

    public static List<String> executePython(String pyPath,String tscode){
        List<String> list = new ArrayList<>();
        Process proc;
        String[] args = new String[]{"C:\\python3.8\\python",pyPath,tscode};
        try {
            proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                list = JSONArray.parseArray(line,String.class);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
