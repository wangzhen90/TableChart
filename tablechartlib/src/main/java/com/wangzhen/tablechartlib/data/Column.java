package com.wangzhen.tablechartlib.data;

import android.graphics.Paint;
import android.text.TextUtils;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhen on 2018/6/11.
 */

public class Column<T extends ICell> {


    public String columnName;

    public int columnIndex;

    //通过计算获得
    public int titleHeight;

    public int maxWidth;

    public int minWidth = 50;

    private List<T> datas = new ArrayList<>();
    private List<T> sortDatas = new ArrayList<>();

    /**
     * 子列
     */
    private List<Column> children;

    private int level;

    private boolean isParent;


    private boolean autoComputeSize;

    public String longestString = "";

    private boolean isFixed;

    private int preColumnsWidth;

    private int rowHeight;

    private int columnWidth = 0;


    private int leftOffset;
    private int rightOffset;

    private ICell sumCell;

    private Paint.Align titleTextAlign = Paint.Align.LEFT;

    private int sortMode = TableChart.SORT_DISORDER;


    public Column() {

    }

    public Column(String columnName) {
        this.columnName = columnName;
    }

    public Column(String columnName,boolean isFixed) {
        this.columnName = columnName;
        this.isFixed = isFixed;
    }

    public void setData(List<T> datas) {
        this.datas = datas;
        this.sortDatas.addAll(datas);
    }

    public void setEmptyCell(int index,ICell realCell){

        T oldCell = datas.get(index);

        if(oldCell.getType() != CellType.EMPTY){
            this.datas.set(index,(T)new EmptyCell(oldCell,realCell));
        }

    }

    public List<T> getData() {
        return datas;
    }

    public int computeWidth() {

        if(!TextUtils.isEmpty(longestString)) return columnWidth;

        columnWidth = Utils.calcTextWidth(Utils.paint,columnName);

        int cellWidth = 0;

        if(datas != null){
            String cellContent;
            for(int i = 0; i < datas.size(); i++){
                cellContent = datas.get(i).getFormatValue() != null ? datas.get(i).getFormatValue() : datas.get(i).getContents();
                if(cellContent.length() > longestString.length()){
                    longestString = cellContent;
                    cellWidth = Utils.calcTextWidth(Utils.paint,longestString);
                }
            }
        }

        columnWidth = Math.max(columnWidth,cellWidth) + leftOffset + rightOffset;

        return columnWidth;
    }


    public void fillPaint(Paint paint){
        paint.setTextSize(Utils.convertDpToPixel(9f));

    }


    public int getWidth() {

        return columnWidth;
//        return 200;
    }

    public void setWidth(int width){
        this.columnWidth = width;
    }

    public int getTitleHeight() {
        return titleHeight;
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }


    public void setPreColumnsWidth(int width) {
        this.preColumnsWidth = width;
    }

    public int getPreColumnsWidth() {
        return preColumnsWidth;
    }

    public int getLeft(){
        return preColumnsWidth;
    }

    public int getRight(){
        return  preColumnsWidth + columnWidth;
    }


    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }


    public ICell getSumCell() {
        return sumCell;
    }

    public void setSumCell(ICell sumCell) {
        this.sumCell = sumCell;
    }

    public Paint.Align getTitleTextAlign() {
        return titleTextAlign;
    }

    public void setTitleTextAlign(Paint.Align titleTextAlign) {
        this.titleTextAlign = titleTextAlign;
    }

    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    public List<T> getSortDatas() {
        return sortDatas;
    }

}
