package com.lt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TushareUtil {
    public static final String URL = "http://api.waditu.com";
    public static final String TUSHARE_TOKEN = "79d2b64fa07ce8f0fe6009ae8f25e5b4fd3cdcf78cf785eec3b5ab12";

    public static final String TUSHARE_PLATE_TOPIC = "TUSHARE-PLATELINE";
    public static final String TUSHARE_DAYLINE_TOPIC = "TUSHARE-DAYLINE";
    public static final String TUSHARE_WEEKLINE_TOPIC = "TUSHARE-WEEKLINE";
    public static final String TUSHARE_BASIC_TOPIC = "TUSHARE-DAILY-BASIC";
    public static final String TUSHARE_REPAIR_TOPIC = "TUSHARE-REPAIR-DATA";
    public static final String TUSHARE_MONTHLINE_TOPIC = "TUSHARE-MONTHLINE";
    public static final String TUSHARE_PLATE_ELEMENT_TOPIC = "TUSHARE-PLATE-ELEMENT";
    public static final int [] MA_NUM_ARREY = new int[]{3,6,12,18,36,72,144};
    public static final String [] EMA_NAME_ARRAY = new String[]{"ema_five","ema_ten","ema_twenty","ema_month","ema_quarter","ema_half_year","ema_full_year"};

    /*股票列表*/
    public static final String [] STOCK_CODE_API = new String[]{"stock_basic","ts_code,symbol,name,area,industry,market,list_status,is_hs"};
    /*概念列表*/
    public final static String [] PLATE_API = new String[]{"ths_index","ts_code,name,count"};
    /*概念指数*/
    public final static String [] PLATE_INDEX_API = new String[]{"ths_daily","ts_code,trade_date,close,open,high,low,pre_close,avg_price,change,pct_change,vol,turnover_rate,float_mv"};
    /*概念成分股*/
    public final static String [] PLATE_ELEMENT_API = new String[]{"ths_member","ts_code,code,name,weight,in_date,is_new"};
    /*每日指标*/
    public final static String [] DAY_BASIC_API = new String[]{"daily_basic","ts_code,trade_date,close,turnover_rate,turnover_rate_f,volume_ratio,circ_mv"};

    public final static String PYTHON_ORDER = "python";

    public final static String PY_DAY_LINE = "/home/python/day_line.py";

    public final static String PY_WEEK_LINE = "/home/python/week_line.py";

    public final static String PY_MONTH_LINE = "/home/python/month_line.py";

    public final static String PY_DAY_LINE_HOME = "D:/workspace-python/day_line.py";

    public final static String PY_WEEK_LINE_HOME = "D:/workspace-python/week_line.py";

    public final static String PY_MONTH_LINE_HOME = "D:/workspace-python/month_line.py";

    public static final String [] LINE_FIELDS = new String[]{"ts_code","trade_date","close","open","high","low","pre_close","change","pct_chg","vol"};

    public static final String [] DAY_LINE_FIELDS = new String[]{"ts_code","trade_date","open","high","low","close","pre_close","change","pct_chg","vol"};

    /**
     * 转换日K数据
     * @param values
     * @return
     */
    public static Map<String,String> transDayLineMap(List<String> values){
        Map<String,String> map = new HashMap<>();
        for(int i = 0; i < TushareUtil.DAY_LINE_FIELDS.length; i++){
            map.put(TushareUtil.DAY_LINE_FIELDS[i],values.get(i));
        }
        return map;
    }

    /**
     * 转换周、月K数据
     * @param values
     * @return
     */
    public static Map<String,String> transWeekMonthLineMap(List<String> values){
        Map<String,String> map = new HashMap<>();
        for(int i = 0; i < TushareUtil.LINE_FIELDS.length; i++){
            map.put(TushareUtil.LINE_FIELDS[i],values.get(i));
        }
        return map;
    }
}
