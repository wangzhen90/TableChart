package com.wangzhen.tablechartlib.interfaces;

import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;

/**
 * Created by wangzhen on 2018/7/18.
 */

public interface ITableOnClickListener {

    void onColumnClick(Column column);

    void onCellClick(ICell cell);

}
