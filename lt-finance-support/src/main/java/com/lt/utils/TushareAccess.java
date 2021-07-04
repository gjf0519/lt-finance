package com.lt.utils;

public class TushareAccess {
    public static final String URL = "http://api.waditu.com";
    public static final String TUSHARE_TOKEN = "79d2b64fa07ce8f0fe6009ae8f25e5b4fd3cdcf78cf785eec3b5ab12";
    /*概念列表*/
    public static final String [] STOCK_CODE_API = new String[]{"stock_basic","ts_code,symbol,name,area,industry,market,list_status,is_hs"};
    /*概念列表*/
    public final static String [] PLATE_API = new String[]{"ths_index","ts_code,name,count"};
    /*概念指数*/
    public final static String [] PLATE_INDEX_API = new String[]{"ths_daily","ts_code,trade_date,close,open,high,low,pre_close,avg_price,change,pct_change,vol,turnover_rate,float_mv"};
    /*概念成分股*/
    public final static String [] PLATE_ELEMENT_API = new String[]{"ths_member","ts_code,code,name,weight,in_date,is_new"};
    /*每日指标*/
    public final static String [] DAY_BASIC_API = new String[]{"daily_basic","ts_code,trade_date,close,turnover_rate,turnover_rate_f,volume_ratio,circ_mv"};

    public final static String PYTHON_HOME = "/usr/local/python3.8/Python-3.8.0/python";

    public final static String PY_DAY_LINE = "/home/python/day_line.py";

    public final static String PY_WEEK_LINE = "/home/python/week_line.py";

    public final static String PY_MONTH_LINE = "/home/python/month_line.py";

//    public final static String PYTHON_HOME = "C:/python37/python";
//
//    public final static String PY_DAY_LINE = "D:/workspace-python/day_line.py";
//
//    public final static String PY_WEEK_LINE = "D:/workspace-python/week_line.py";
//
//    public final static String PY_MONTH_LINE = "D:/workspace-python/month_line.py";

    public static final String [] LINE_FIELDS = new String[]{"ts_code","trade_date","close","open","high","low","pre_close","change","pct_chg","vol"};

    public static final String [] DAY_LINE_FIELDS = new String[]{"ts_code","trade_date","open","high","low","close","pre_close","change","pct_chg","vol"};
}
