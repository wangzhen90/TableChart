package com.wangzhen.tablechartlib.utils;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.formatter.BiColorFormatSet;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/8/23.
 */

public class MultiColorFormatUtils {


    public static final String OPERATOR_GREAER_THAN = "greaterThan";
    public static final String OPERATOR_LESS_THAN = "lessThan";
    public static final String OPERATOR_EQUAL = "equal";
    public static final String OPERATOR_GREAER_THAN_OR_EQUAL = "greaterThanOrEqual";
    public static final String OPERATOR_LESS_THAN_OR_EQUAL = "lessThanOrEqual";
    public static final String OPERATOR_RANGE = "range";


    public static final int COLOR_TEXT = 0;
    public static final int COLOR_BG = 1;

    List<BiColorFormatSet> colorSets;
    int defaultTextColor;
    int defaultBgColor;

    public MultiColorFormatUtils(List<BiColorFormatSet> colorSets, int defaultTextColor, int defaultBgColor) {

        this.colorSets = colorSets;
        this.defaultBgColor = defaultBgColor;
        this.defaultTextColor = defaultTextColor;

    }

    //TODO 此处使用-1可能会有误差...但可以忽略不计
    public int getTextColor(ICell entry, Column<ICell> column) {

        if (entry.getTextColor() != -1) return entry.getTextColor();

        float value;
        try {
            value = Float.parseFloat(entry.getContents());
            if (Float.isNaN(value)) {
                return defaultTextColor;
            }

            BiColorFormatSet colorSet;
            int resultColor;
            for (int i = colorSets.size() - 1; i >= 0; i--) {
                colorSet = colorSets.get(i);
                if (colorSet.fontColor == -1) continue;
                resultColor = getColor(colorSet, value, COLOR_TEXT);
                if (resultColor != -1) {
                    return resultColor;
                }
            }

            entry.setTextColor(defaultTextColor);
            return defaultTextColor;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            entry.setTextColor(defaultTextColor);
            return defaultTextColor;
        }

    }


    public int getTextBgColor(ICell entry, Column<ICell> column) {
        if (entry.getBgColor() != -1) return entry.getBgColor();

        float value;
        try {
            value = Float.parseFloat(entry.getContents());
            if (Float.isNaN(value)) {
                return defaultBgColor;
            }

            BiColorFormatSet colorSet;
            int resultColor;
            for (int i = colorSets.size() - 1; i >= 0; i--) {
                colorSet = colorSets.get(i);
                if (colorSet.bgColor == -1) continue;
                resultColor = getColor(colorSet, value, COLOR_BG);
                if (resultColor != -1) {
                    return resultColor;
                }
            }

            return defaultBgColor;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            entry.setBgColor(defaultBgColor);
            return defaultBgColor;
        }


    }


    private int getColor(BiColorFormatSet colorSet, float value, int type) {

        switch (colorSet.oprate) {

            case OPERATOR_EQUAL:

                if (value == colorSet.belowNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;

            case OPERATOR_GREAER_THAN:

                if (value > colorSet.belowNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;

            case OPERATOR_GREAER_THAN_OR_EQUAL:

                if (value >= colorSet.belowNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;

            case OPERATOR_LESS_THAN:

                if (value < colorSet.belowNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;

            case OPERATOR_LESS_THAN_OR_EQUAL:

                if (value <= colorSet.belowNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;

            case OPERATOR_RANGE:

                if (value >= colorSet.belowNum && value <= colorSet.aboveNum)
                    return type == 0 ? colorSet.fontColor : colorSet.bgColor;

                break;
        }

        return -1;

    }


}
