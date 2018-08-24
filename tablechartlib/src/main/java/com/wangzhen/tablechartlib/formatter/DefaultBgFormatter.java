package com.wangzhen.tablechartlib.formatter;

import android.graphics.Color;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/30.
 */

public class DefaultBgFormatter implements IBgFormatter {

    @Override
    public int getContentBackgroundColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
        return Color.TRANSPARENT;
    }

    @Override
    public int getTitleBackgroundColor() {
        return Color.parseColor("#F0F0F0");
    }
}
