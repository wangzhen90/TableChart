package com.wangzhen.tablechartlib.data;

import android.util.Log;

import com.wangzhen.tablechartlib.formatter.DefaultBgFormatter;
import com.wangzhen.tablechartlib.formatter.DefaultTextFormatter;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;
import com.wangzhen.tablechartlib.interfaces.ICellRange;
import com.wangzhen.tablechartlib.interfaces.ISheet;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhen on 2018/7/2.
 */

public class Sheet<T extends Cell> implements ISheet {


    private static final String TAG = Sheet.class.getSimpleName();
    private T[][] data;
    private List<T> dataList;
    public String tableName;
    private List<Column<T>> columns;
    private List<Column<T>> childColumns;

    private List<ICell> sumCells;

    private int mHeight;
    private int mWidth;
    private int cellCounts;

    private int rowHeight = 70;

    private int columnLeftOffset = (int) Utils.convertDpToPixel(8);
    private int columnRightOffset = (int) Utils.convertDpToPixel(8);


    private ArrayList<ICellRange> mergedCells = new ArrayList();

    private int mTitleHeight = 80;

    private int maxRowCount;
    private int maxColumnCount;

    private boolean hasMergedCell = false;

    private ITextFormatter mTextFormatter;
    private IBgFormatter mBgFormatter;




    public Sheet(List columns, List data) {

        this.columns = columns;
        this.dataList = data;

        calculate();

        mBgFormatter = new DefaultBgFormatter();
        mTextFormatter = new DefaultTextFormatter();

    }

    public Sheet(List columns, List data,List<ICell> sumCells) {

        this.columns = columns;
        this.dataList = data;
        this.sumCells = sumCells;
        calculate();

        mBgFormatter = new DefaultBgFormatter();
        mTextFormatter = new DefaultTextFormatter();

    }


    public void setData(T[][] data) {
        this.data = data;
        calculate();
    }



    public T[][] getData() {
        return data;
    }


    public void setColumns(List<Column<T>> columns) {
        this.columns = columns;
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public int getColumns() {
        return columns.size();
    }

    @Override
    public List<Column<T>> getColumnList() {
        return columns;
    }

    @Override
    public ICell[] getRow(int rowIndex) {
        return new ICell[0];
    }

    @Override
    public ICell[] getColumn(int columnIndex) {
        return new ICell[0];
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public ICellRange[] getMergedCells() {

        return new ICellRange[0];
    }

    @Override
    public int getColumnWidth(int var1) {
        return 0;
    }

    @Override
    public int getRowHeight() {
        return rowHeight;
    }

    @Override
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public void calculate() {
        mHeight = 0;
        mWidth = 0;
        int maxRowCount = 0;
        for (int i = 0; i < columns.size(); i++) {

            Column<T> column = columns.get(i);

            column.columnIndex = i;
            column.fillPaint(Utils.paint);
            column.setLeftOffset(columnLeftOffset);
            column.setRightOffset(columnRightOffset);

            column.computeWidth();
            column.setPreColumnsWidth(mWidth);
            column.setRowHeight(rowHeight);
            column.setTitleHeight(mTitleHeight);

            mWidth += column.getWidth();

            if(sumCells != null && i < sumCells.size()){
                column.setSumCell(sumCells.get(i));
            }


            if (column.getData() != null) {
                if (column.getData().size() > maxRowCount) {
                    maxRowCount = column.getData().size();
                }
            }
        }

        mHeight = maxRowCount * rowHeight + mTitleHeight;

        cellCounts = maxRowCount * getColumns();

        this.maxColumnCount = columns.size();
        this.maxRowCount = maxRowCount;


//        if(sumCells != null){
//            mHeight += rowHeight;
//        }

    }


    public ICellRange mergeCells(int firstRow, int firstColumn, int lastRow, int lastColumn) {

        if (lastColumn < firstColumn || lastRow < firstRow) {
//            Log.e(TAG, "Cannot merge cells - top left and bottom right incorrectly specified");
        }

        //TODO 如果超出了最大行数和列数，就添加这个cell
//        if(col2 >= this.numColumns || row2 >= this.numRows) {
//            this.addCell(new Blank(col2, row2));
//        }

        CellRange range = new CellRange(firstRow, firstColumn, lastRow, lastColumn);
        this.mergedCells.add(range);

        return range;

    }

    public int getCellCounts() {
        return cellCounts;
    }

    public int getTitleHeight() {
        return mTitleHeight;
    }

    public void setTitleHeight(int mTitleHeight) {
        this.mTitleHeight = mTitleHeight;
    }


    public Column<T> getColumnByXValue(double xValue) {

        if (columns == null || columns.isEmpty()) {
            return null;
        }

        int low = 0;
        int high = columns.size();

        int mid;
        Column<T> targetColumn = null;

        while (low < high) {
            mid = (low + high) / 2;

            targetColumn = columns.get(mid);

            if (targetColumn.getLeft() < xValue && targetColumn.getRight() > xValue) {
                return targetColumn;
            } else if (targetColumn.getLeft() > xValue) {
                high = mid;
            } else if (targetColumn.getRight() < xValue) {
                low = mid;
            }
        }

        return null;
    }

    @Override
    public ICell getCellByTouchPoint(double xValue, double yValue) {

        Column<T> column = getColumnByXValue(xValue);

        ICell cell;

        if (column != null && !column.getData().isEmpty()) {

            List<T> cells = column.getData();

            int low = 0;
            int high = cells.size();

            int mid;

            while (low < high) {
                mid = (low + high) / 2;
                cell = cells.get(mid);

                if (yValue >= (cell.getRow() * rowHeight + mTitleHeight) && yValue <= ((cell.getLastRow() + 1) * rowHeight + mTitleHeight)) {
                    return cell;
                } else if (yValue < cell.getRow() * rowHeight + mTitleHeight) {
                    high = mid;
                } else if (yValue >= (cell.getLastRow() + 1) * rowHeight + mTitleHeight) {
                    low = mid;
                } else {
                    break;
                }
            }
        }


        return null;
    }


    public void merge(int firstRow, int firstColumn, int lastRow, int lastColumn) {

        if (firstRow < 0 || firstColumn < 0) return;
        if (lastRow >= maxRowCount || lastColumn >= maxColumnCount) return;
        if (firstRow >= lastRow && firstColumn >= lastColumn) return;

        T cell = findCellByRowAndColumn(firstRow, firstColumn);
        if (cell != null) {
            cell.setLastRow(lastRow);
            cell.setLastColumn(lastColumn);
            setEmptyCells(firstRow, firstColumn, lastRow, lastColumn, cell);
            hasMergedCell = true;
        }
    }


    public T findCellByRowAndColumn(int rowIndex, int columnIndex) {

        Column<T> column = columns.get(columnIndex);

        T cell = column.getData().get(rowIndex);

        return cell;
    }

    private void setEmptyCells(int firstRow, int firstColumn, int lastRow, int lastColumn, T realCell) {


        for (int i = firstColumn; i < lastColumn + 1; i++) {

            Column<T> column = columns.get(i);

            if (column == null || column.getData().isEmpty()) continue;

            for (int j = firstRow; j < lastRow + 1; j++) {
                if (i != firstColumn || j != firstRow) {
                    column.setEmptyCell(j, realCell);
                }
            }

        }

    }

    public boolean hasMergedCell() {
        return hasMergedCell;
    }

    public ITextFormatter getTextFormatter() {
        return mTextFormatter;
    }

    public void setTextFormatter(ITextFormatter mTextFormatter) {
        this.mTextFormatter = mTextFormatter;
    }


    public IBgFormatter getBgFormatter() {
        return mBgFormatter;
    }

    public void setBgFormatter(IBgFormatter mBgFormatter) {
        this.mBgFormatter = mBgFormatter;
    }

    public List<ICell> getSumCells(){
        return sumCells;
    }
}
