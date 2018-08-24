package com.wangzhen.tablechartlib.formatter;

import android.graphics.Color;
import android.text.TextPaint;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/27.
 */

public class DefaultTextFormatter implements ITextFormatter {
    @Override
    public int getTextSize(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
        return 9;
    }

    @Override
    public TextPaint.Align getTextAlign(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
        return TextPaint.Align.CENTER;
    }

    @Override
    public int getTextColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
        return Color.parseColor("#4D4D4D");
    }
}
