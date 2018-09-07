package com.wangzhen.tablechartlib.interfaces;

import com.wangzhen.tablechartlib.data.CellType;

/**
 * Created by wangzhen on 2018/6/11.
 */

public interface ICell  {
    int getRow();

    int getRawRow();

    int getColumn();

    int getLastRow();

    int getLastColumn();


    boolean isHidden();

    String getContents();

    CellType getType();

    ICell getRealCell();

    String getFormatValue();

    void setFormatValue(String value);

    int getTextColor();

    void setTextColor(int color);

    int getBgColor();

    void setBgColor(int color);

    void setRow(int index);
//
//    CellFormat getCellFormat();
//
//    CellFeatures getCellFeatures();
}
