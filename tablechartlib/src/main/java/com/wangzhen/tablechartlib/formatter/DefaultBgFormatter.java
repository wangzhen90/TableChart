package com.wangzhen.tablechartlib.formatter;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/30.
 */

public class DefaultBgFormatter implements IBgFormatter{

    @Override
    public String getBackgroundColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
        if(cell.getRow() % 2 == 0){
            return null;
        }else{
            return "#C8C2C6";
        }
    }
}
