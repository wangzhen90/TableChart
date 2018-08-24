package com.wangzhen.tablechartlib.formatter;

/**
 * Created by wangzhen on 2018/8/23.
 */

public class BiColorFormatSet {

    public BiColorFormatSet(){

    }
    public BiColorFormatSet(String oprate,int fontColor,int bgColor,float belowNum,float aboveNum){

        this.oprate = oprate;
        this.fontColor = fontColor;
        this.bgColor = bgColor;
        this.belowNum = belowNum;
        this.aboveNum = aboveNum;

    }


    public String oprate;

    public int fontColor = -1;

    public int bgColor = -1;
    //区间下限
    public float belowNum;
    //区间上限
    public float aboveNum;

}
