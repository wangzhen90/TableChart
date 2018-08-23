package com.wangzhen.tablechartlib.utils;


import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.formatter.BiFormatResult;
import com.wangzhen.tablechartlib.formatter.BiFormatSet;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by wangzhen on 2018/7/17.
 * 格式化步骤：
 * ①获取format:小数点控制，千分位控制，不修改number
 * ②获取单位，并且更改number
 * ③添加符号位，添加前后缀
 */

public class MultiNumberFormatUtils {

    BiFormatSet mFormatSet;
    protected DecimalFormat mFormat;
    BiFormatResult result;

    private static Unit[] EN_Unit = new Unit[]{
            new Unit("K", (float)Math.pow(10, 3)),
            new Unit("M", (float)Math.pow(10, 6)),
            new Unit("B", (float)Math.pow(10, 9)),
            new Unit("T", (float)Math.pow(10, 12)),
            new Unit("Q", (float)Math.pow(10, 16)),
    };

    private static Unit[] ZH_Unit = new Unit[]{
            new Unit("万", (float) Math.pow(10, 4)),
            new Unit("亿", (float)Math.pow(10, 8)),
    };


    static class Unit {

        public float num;
        public String unit;
        public String unitType;

        public Unit(String unit, float num) {
            this.unit = unit;
            this.num = num;
        }

        public Unit(String unit, String unitType, float num) {
            this.num = num;
            this.unit = unit;
            this.unitType = unitType;
        }
    }


    private static HashMap<String, Unit> unitMap = new HashMap<>();


    public MultiNumberFormatUtils(BiFormatSet formatSet) {
        this.mFormatSet = formatSet;
        unitMap.put("K", new Unit("K", "K", (float)Math.pow(10, 3)));
        unitMap.put("M", new Unit("M", "M", (float)Math.pow(10, 6)));
        unitMap.put("B", new Unit("B", "B", (float)Math.pow(10, 9)));
        unitMap.put("wan", new Unit("万", "wan", (float)Math.pow(10, 4)));
        unitMap.put("yi", new Unit("亿", "yi", (float)Math.pow(10, 8)));

        this.mFormat = getFormatter(formatSet);

        result = new BiFormatResult();
    }

    public String getFormattedValue(ICell entry, Column<ICell> column) {

        if(entry.getFormatValue() != null) return entry.getFormatValue();

        float value;
        try {
            value = Float.parseFloat(entry.getContents());
            if( Float.isNaN(value)){
                return entry.getContents();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return entry.getContents();
        }


        return format(value,entry);
    }

    public String format(float value,ICell entry){


        if(Float.isNaN(value)){
            return "";
        }

        result.reset(value,mFormatSet);
        getUnit(mFormatSet,result);

        return getResultStr(result);
    }


    private String getResultStr(BiFormatResult result){

        return (result.prefix != null ? result.prefix : "")
                + (result.signBit != null ? result.signBit : "")
                + (mFormat.format(result.number))
                + (result.unit != null ? result.unit : "")
                + (result.suffix != null ? result.suffix : "");
    }



    private DecimalFormat getFormatter(BiFormatSet formatSet) {

        switch (formatSet.formatType) {

            case "default":
                mFormat = new DecimalFormat(",##0.00");
                break;

            case "commonValue":
                if ("auto".equals(formatSet.unitSet)) {
//                    mFormat = new DecimalFormat(",##0.00");
                    mFormat = new DecimalFormat(getFormatPattern(formatSet));
                } else {
                    mFormat = new DecimalFormat(getFormatPattern(formatSet));
                }

                break;

            case "percentValue":
                mFormat = new DecimalFormat(getFormatPattern(formatSet));
                break;

            default:
                mFormat = new DecimalFormat(",##0.00");
                break;
        }

        return mFormat;

    }

    private String getFormatPattern(BiFormatSet formatSet){

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < formatSet.decimalDigitsNum; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        if("percentValue".equals(formatSet.formatType)){
            b.append("%");
        }

        return (formatSet.thousandsSeparator == 1 ? "###,###,###,##0" : "0") + b.toString();
    }


    private void getUnitAuto(BiFormatSet formatSet, BiFormatResult result) {

        Unit[] unitMap = getUnitMap(formatSet.language);
        for (int i = 0; i < unitMap.length; i++) {

             if(result.number <= unitMap[i].num && i == 0){
                 result.unit = "";
                 break;
             }else if(result.number <= unitMap[i].num && i > 0){
                 result.number = result.number / unitMap[i-1].num;
                 result.unit = unitMap[i-1].unit;
                 break;

             }else if(i == unitMap.length - 1){
                 result.number = result.number / unitMap[i].num;
                 result.unit = unitMap[i].unit;
                 break;
             }
        }
    }


    private void getUnit(BiFormatSet formatSet, BiFormatResult result){
//        if (!TextUtils.isEmpty(formatSet.unitSet)) {
        if (formatSet.unitSet != null && !formatSet.unitSet.equals("")) {

            if ("auto".equals(formatSet.unitSet)) {
                getUnitAuto(formatSet,result);

            } else {
                Unit unit = unitMap.get(formatSet.unitSet);

                if(unit != null){

                    result.number = result.number/unit.num;
                    result.unit = unit.unit;
                }
            }
        }
    }

    private Unit[] getUnitMap(String language) {

        return language.equals("zh") ? ZH_Unit : EN_Unit;
    }



    private void test(){
        double pi = 3.1415927;//圆周率
        //取一位整数
        System.out.println(new DecimalFormat("0").format(pi));//3
        //取一位整数和两位小数
        System.out.println(new DecimalFormat("0.00").format(pi));//3.14
        //取两位整数和三位小数，整数不足部分以0填补。
        System.out.println(new DecimalFormat("00.000").format(pi));//03.142
        //取所有整数部分
        System.out.println(new DecimalFormat("#").format(pi));//3
        //以百分比方式计数，并取两位小数
        System.out.println(new DecimalFormat("#.##%").format(pi));//314.16%

        float c = 11111.112f;//光速
        //显示为科学计数法，并取五位小数
        System.out.println(new DecimalFormat("#.#####E0").format(c));//2.99792E8
        //显示为两位整数的科学计数法，并取四位小数
        System.out.println(new DecimalFormat("00.####E0").format(c));//29.9792E7
        //每三位以逗号进行分隔。
        System.out.println(new DecimalFormat(",###.00").format(c));//299,792,458
        //将格式嵌入文本
        System.out.println(new DecimalFormat("光速大小为每秒,###米").format(c)); //光速大小为每秒299,792,458米
    }



}
