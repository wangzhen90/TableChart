package com.wangzhen.tablechartlib.data;

import com.wangzhen.tablechartlib.interfaces.ICell;

/**
 * Created by wangzhen on 2018/7/6.
 */

public class Cell implements ICell {

    private int row;
    private int column;
    private int lastRow;
    private int lastColumn;

    private String contents;
    //TODO 添加formatter


    public Cell(int row, int column, String contents) {
        this.row = row;
        this.column = column;
        this.contents = contents;

        lastColumn = column;
        lastRow = row;
    }

    public Cell(int row, int column, int lastRow, int lastColumn, String contents) {
        this.row = row;
        this.column = column;
        this.contents = contents;

        this.lastColumn = lastColumn;
        this.lastRow = lastRow;
    }


    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLastRow() {
        return lastRow;
    }

    @Override
    public int getLastColumn() {
        return lastColumn;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public CellType getType() {
        return CellType.LABEL;
    }

    @Override
    public ICell getRealCell() {
        return this;
    }

    public void setLastRow(int lastRow){
        this.lastRow = lastRow;
    }

    public void setLastColumn(int lastColumn){
        this.lastColumn = lastColumn;
    }

}
