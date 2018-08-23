package com.wangzhen.tablechartlib.formatter;

import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

import java.util.List;

/**
 * Created by wangzhen on 2018/3/13.
 */

public interface IValueFormatter {

    String getFormattedValue(ICell cell, Column<ICell> column, List<Column<ICell>> columns);
}
