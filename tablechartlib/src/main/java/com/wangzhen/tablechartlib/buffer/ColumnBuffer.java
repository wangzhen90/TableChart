package com.wangzhen.tablechartlib.buffer;

import com.wangzhen.tablechartlib.data.CellType;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.EmptyCell;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/10.
 */

public class ColumnBuffer extends AbstractBuffer<Column<ICell>> {

    public ColumnBuffer(int size) {
        super(size);
    }


    private void addCell(int left, int top, int right, int bottom) {
        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;

    }

    @Override
    public void feed(Column<ICell> column) {
        int cellSize = column.getData().size();
        List<ICell> cells = column.getData();
        ICell cell;
        int left, top, right, bottom;

        for (int i = 0; i < cellSize; i++) {
            cell = cells.get(i);

            if (cell.getType() == CellType.EMPTY) {
                left = 0;
                top = 0;
                right = 0;
                bottom = 0;
            } else {
                left = column.getPreColumnsWidth();
                top = cell.getRow() * column.getRowHeight() + column.titleHeight;

                right = left + column.getWidth();
                bottom = top + column.getRowHeight() * (cell.getLastRow() - cell.getRow() + 1);

            }
            addCell(left, top, right, bottom);

        }
        reset();
    }

    @Override
    public void feed(Column<ICell> column, List<Column<ICell>> columns) {
        int cellSize = column.getData().size();
        List<ICell> cells = column.getData();
        ICell cell;
        int left, top, right, bottom;
        Column<ICell> realColumn;

        for (int i = 0; i < cellSize; i++) {
            cell = cells.get(i);

            if (cell.getType() == CellType.EMPTY) {
                //这种处理方式会导致同一区域重绘多次，需要优化
                cell = ((EmptyCell)cell).getRealCell();
                realColumn = columns.get(cell.getColumn());
                left = realColumn.getPreColumnsWidth();
                top = cell.getRow() * realColumn.getRowHeight() + realColumn.titleHeight;

                right = left;

                for(int j = cell.getColumn(); j < cell.getLastColumn() + 1;j++){
                    right += columns.get(j).getWidth();
                }

                bottom = top + realColumn.getRowHeight() * (cell.getLastRow() - cell.getRow() + 1);

            } else {
                left = column.getPreColumnsWidth();
                top = cell.getRow() * column.getRowHeight() + column.titleHeight;

                right = left;

                for(int j = cell.getColumn(); j < cell.getLastColumn() + 1;j++){
                    right += columns.get(j).getWidth();
                }

                bottom = top + column.getRowHeight() * (cell.getLastRow() - cell.getRow() + 1);

            }
            addCell(left, top, right, bottom);

        }
        reset();
    }
}
