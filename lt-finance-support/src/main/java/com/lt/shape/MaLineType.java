package com.lt.shape;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public enum MaLineType {
    LINE005(5,"5日"),
    LINE010(10,"10日"),
    LINE020(20,"20日"),
    LINE030(30,"30日"),
    LINE060(60,"60日"),
    LINE120(120,"120日"),
    LINE250(250,"250日");
    private int code;
    private String name;
    private MaLineType(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
