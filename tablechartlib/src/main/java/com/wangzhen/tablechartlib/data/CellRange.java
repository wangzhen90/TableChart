package com.wangzhen.tablechartlib.data;

import com.wangzhen.tablechartlib.interfaces.ICellRange;
import com.wangzhen.tablechartlib.interfaces.ISheet;
import com.wangzhen.tablechartlib.interfaces.ICell;

/**
 * Created by wangzhen on 2018/7/6.
 */

public class CellRange implements ICellRange{

    private int columnFirst;
    private int rowFirst;
    private int columnLast;
    private int rowLast;

    private ISheet sheet;

    public CellRange(int rowFirst, int columnFirst,int rowLast, int columnLast){

        this.rowFirst = rowFirst;
        this.columnFirst = columnFirst;
        this.rowLast = rowLast;
        this.columnLast = columnLast;
    }

    public CellRange(int rowFirst, int columnFirst, int rowLast, int columnLast, ISheet sheet){

        this.rowFirst = rowFirst;
        this.columnFirst = columnFirst;
        this.rowLast = rowLast;
        this.columnLast = columnLast;
        this.sheet = sheet;

    }

    @Override
    public ICell getTopLeft() {
        //TODO
//        return (Cell)(this.column1 < this.sheet.getColumns() && this.row1 < this.sheet.getRows()?this.sheet.getCell(this.column1, this.row1):new EmptyCell(this.column1, this.row1));
          return null;
    }

    @Override
    public ICell getBottomRight() {
        //TODO
        //return (Cell)(this.column2 < this.sheet.getColumns() && this.row2 < this.sheet.getRows()?this.sheet.getCell(this.column2, this.row2):new EmptyCell(this.column2, this.row2));
        return null;
    }
}
