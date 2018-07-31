package com.wangzhen.tablechartlib.formatter;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/27.
 */

public interface IBgFormatter {

    String getBackgroundColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns);

}
