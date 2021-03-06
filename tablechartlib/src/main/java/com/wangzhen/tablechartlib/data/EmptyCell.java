package com.wangzhen.tablechartlib.data;


import com.wangzhen.tablechartlib.interfaces.ICell;


/**
 * Created by wangzhen on 2018/7/6.
 */

public class EmptyCell implements ICell {

    private int row;
    private int column;
    private ICell realCell;


    public EmptyCell(int row, int column){

        this.row = row;
        this.column = column;
    }

    public EmptyCell(ICell oldCell,ICell realCell){
        this.row = oldCell.getRow();
        this.column = oldCell.getColumn();

        this.realCell =  realCell;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getRawRow() {
        return 0;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getLastRow() {
        return row;
    }

    @Override
    public int getLastColumn() {
        return column;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public String getContents() {
        if(realCell != null) return realCell.getContents();
        return null;
    }

    @Override
    public CellType getType() {
        return CellType.EMPTY;
    }
    @Override
    public ICell getRealCell() {
        return realCell;
    }

    @Override
    public String getFormatValue() {
        return null;
    }

    @Override
    public void setFormatValue(String value) {

    }

    @Override
    public int getTextColor() {
        return -1;
    }

    @Override
    public void setTextColor(int color) {

    }

    @Override
    public int getBgColor() {
        return -1;
    }

    @Override
    public void setBgColor(int color) {

    }

    @Override
    public void setRow(int index) {

    }
}
