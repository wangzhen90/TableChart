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

    int getRowHeight(int var1);

    int getWidth();

    int getHeight();

    void calculate();

    boolean hasMergedCell();

    ITextFormatter getTextFormatter();
    IBgFormatter getBgFormatter();

    Column<T> getColumnByXValue(double xValue);
    T getCellByTouchPoint(double xValue, double yValue);


}
