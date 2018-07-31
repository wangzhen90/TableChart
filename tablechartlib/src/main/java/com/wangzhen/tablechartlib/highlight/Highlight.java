package com.wangzhen.tablechartlib.highlight;

import android.graphics.Rect;
import android.graphics.RectF;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

/**
 * Created by wangzhen on 2018/7/16.
 */

public class Highlight {


    protected TableChart mChart;
    private int columnIndex = -1;

//    private int row = -1;
//    private int column = -1;
//    private int lastRow = -1;
//    private int lastColumn = -1;

    private ICell mCell;
    private Column mColumn;
    private boolean isTitle;

    public Highlight(TableChart chart){
        this.mChart = chart;
    }

    public Highlight(TableChart chart, int columnIndex, ICell cell, boolean isTitle){
        this.mChart = chart;
        this.columnIndex = columnIndex;
        this.mCell = cell;
        this.isTitle = isTitle;

        mColumn = mChart.getColumnList().get(columnIndex);

    }

    public boolean isTitle() {

        return isTitle;
    }

    public int getColumnIndex() {
        return columnIndex;
    }


    public Column getColumnData(){
        return mColumn;
    }

    public ICell getCell(){
        return mCell;
    }

    public int getColumn() {
        return mCell == null ? -1 : mCell.getColumn();
    }

    public int getRow() {
        return mCell == null ? -1 : mCell.getRow();
    }

    public int getLastRow() {
        return mCell == null ? -1 : mCell.getLastRow();
    }

    public int getLastColumn() {
        return mCell == null ? -1 : mCell.getLastColumn();
    }

    public boolean equalTo(Highlight h) {

        if(h == null) return false;

        return h.getRect().equals(getRect());

    }

    public RectF getRect() {

        if (mColumn == null) return null;
        RectF highlightPort = new RectF();
        if (isTitle) {
            highlightPort.set(mColumn.getLeft(), 0, mColumn.getRight(), mChart.getContentHeight());
        } else if (mCell != null) {
            int left = mColumn.getLeft();
            int top = getRow() * mColumn.getRowHeight() + mChart.getTitleHeight();
            int right = left;

            for (int i = columnIndex; i < getLastColumn() + 1; i++) {
                right += mChart.getColumnList().get(i).getWidth();
            }

            int bottom = top + (getLastRow() - getRow() + 1) * mColumn.getRowHeight();
            highlightPort.set(left, top, right, bottom);
        }

        return highlightPort;
    }

}
