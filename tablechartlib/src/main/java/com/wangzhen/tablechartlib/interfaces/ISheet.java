package com.wangzhen.tablechartlib.interfaces;

import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/5.
 */

public interface ISheet<T extends ICell> {

    int getRows();

    int getColumns();

    List<Column<T>> getColumnList();

    ICell[] getRow(int rowIndex);

    ICell[] getColumn(int columnIndex);

    String getName();

    boolean isHidden();

    ICellRange[] getMergedCells();

    int getColumnWidth(int var1);

    int getRowHeight();

    void setRowHeight(int rowHeight);

    int getWidth();

    int getHeight();

    void calculate();

    //用于列数较少无法占满全屏
    void setViewWidth(int viewWidth);

    boolean hasMergedCell();

    ITextFormatter getTextFormatter();
    IBgFormatter getBgFormatter();

    Column<T> getColumnByXValue(double xValue);
    T getCellByTouchPoint(double xValue, double yValue);

    List<ICell> getSumCells();

}
