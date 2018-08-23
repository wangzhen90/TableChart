package com.wangzhen.tablechartlib.formatter;

/**
 * Created by wangzhen on 2018/7/17.
 */

public class BiFormatSet {

    //单位，除了常用的单位，还有一种是auto
    public String unitSet;
    //小数点位数，unitSet为auto时，固定显示两位小数
    public int decimalDigitsNum;
    //格式化方式：commonValue：普通格式化；percentValue：百分比；default:只设置千分位
    public String formatType;
    //是否显示符号位
    public int positiveSign;
    //是否显示千分位符号
    public int thousandsSeparator;
    //前拼
    public String prefix;
    //后拼
    public String suffix;

    public String language = "zh";

    public boolean isEmpty(){
        return formatType == null;
    }

}
