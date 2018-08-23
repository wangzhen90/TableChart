package com.wangzhen.tablechartlib.formatter;

/**
 * Created by wangzhen on 2018/7/17.
 */

public class BiFormatResult {

    public float number;
    public String unit;
    public String signBit;
    public String prefix;
    public String suffix;


    public BiFormatResult(){}

    public BiFormatResult(float number, String unit, String signBit,String prefix,String suffix) {
        this.number = number;
        this.unit = unit;
        this.signBit = signBit;
        this.prefix = prefix;
        this.suffix = suffix;
    }




    public void reset(float value,BiFormatSet formatSet){
        this.number = value;
        this.unit = null;
        this.signBit =   (formatSet.positiveSign == 1 && value > 0) ? "+" : null;
        this.prefix = formatSet.prefix;
        this.suffix = formatSet.suffix;
    }
}
