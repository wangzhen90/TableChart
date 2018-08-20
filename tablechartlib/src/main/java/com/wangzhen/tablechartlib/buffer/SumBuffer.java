package com.wangzhen.tablechartlib.buffer;

import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.interfaces.ICell;

import java.util.List;

/**
 * Created by wangzhen on 2018/8/20.
 */

public class SumBuffer extends AbstractBuffer<List<Column<ICell>>> {

    float mBottom;
    public SumBuffer(int size,float bottom) {
        super(size);
        this.mBottom = bottom;
    }

    private void addCell(float left, float top, float right, float bottom){
        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;
    }

    @Override
    public void feed(List<Column<ICell>> data) {

        float size = data.size();
        int width;
        int height;
        for(int i = 0; i < size; i++){

            Column column = data.get(i);
            width = column.getWidth();
            height = column.getRowHeight();

            float left = column.getPreColumnsWidth() ;
            float right = left + width;
            float top = mBottom - height;

            addCell(left, top,right,mBottom);

        }
        reset();

    }
}
