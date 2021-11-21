package com.lt.shape;

/**
 * @author gaijf
 * @description: TODO
 * @date 2021/11/2118:49
 */
public enum EmaLevel {
    LEVEL005(null, EmaLineType.LINE005,EmaLineType.LINE010),
    LEVEL010(EmaLineType.LINE005,EmaLineType.LINE010,EmaLineType.LINE020),
    LEVEL020(EmaLineType.LINE010,EmaLineType.LINE020,EmaLineType.LINE030),
    LEVEL030(EmaLineType.LINE020,EmaLineType.LINE030,EmaLineType.LINE060),
    LEVEL060(EmaLineType.LINE030,EmaLineType.LINE060,EmaLineType.LINE120),
    LEVEL120(EmaLineType.LINE060,EmaLineType.LINE120,EmaLineType.LINE250),
    LEVEL250(EmaLineType.LINE120,EmaLineType.LINE250,null);

    private EmaLineType child;
    private EmaLineType real;
    private EmaLineType parent;

    private EmaLevel(EmaLineType child,EmaLineType real,EmaLineType parent){
        this.child = child;
        this.real = real;
        this.parent = parent;
    }

    public EmaLineType getChild() {
        return child;
    }

    public EmaLineType getReal() {
        return real;
    }

    public EmaLineType getParent() {
        return parent;
    }
}
