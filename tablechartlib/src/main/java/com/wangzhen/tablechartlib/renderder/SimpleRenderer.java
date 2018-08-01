package com.wangzhen.tablechartlib.renderder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import com.wangzhen.tablechartlib.buffer.ColumnBuffer;
import com.wangzhen.tablechartlib.buffer.TitleBuffer;
import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.highlight.Highlight;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.Transformer;
import com.wangzhen.tablechartlib.utils.Utils;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

import java.util.List;

/**
 * Created by wangzhen on 2018/7/7.
 */

public class SimpleRenderer extends DataRenderer {


    private TableChart mChart;

    private TitleBuffer mTitleBuffer;
    private Transformer transformer;

    private ColumnBuffer[] mBuffers;

//    private RectF checkCanDrawBuffer;

    public SimpleRenderer(ViewPortHandler viewPortHandler, TableChart chart) {
        super(viewPortHandler);
        this.mChart = chart;
        mHighlightPaint.setStrokeWidth(mChart.getHighlightBorderWidth());
    }

    @Override
    public void initBuffers() {

        List<Column<ICell>> columns = mChart.getColumnList();
        mBuffers = new ColumnBuffer[mChart.getColumnCount()];
        mTitleBuffer = new TitleBuffer(mChart.getColumnCount() * 4, mChart.getColumnCount());

//        mTitleBuffer.feed(mChart.getColumnList());
        for (int i = 0; i < mChart.getColumnCount(); i++) {
            mBuffers[i] = new ColumnBuffer(columns.get(i).getData().size() * 4);
//            mBuffers[i].feed(columns.get(i));
        }
    }

    private RectF mValuesRect = new RectF();
    private RectF mFixedRect = new RectF();

    @Override
    public void drawData(Canvas c) {
        List<Column<ICell>> columns = mChart.getColumnList();


        mValuesRect.set(mViewPortHandler.getContentRect());
        mValuesRect.top += mChart.getTitleHeight() * mViewPortHandler.getScaleY();
        int clipRestoreCount = c.save();
        c.clipRect(mValuesRect);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < mChart.getColumnCount(); i++) {
            drawColumn(c, columns.get(i), i, mValuesRect, columns);
        }

//        Log.e("1------绘制所有column的耗费时间：", (System.currentTimeMillis() - startTime) + "");

        c.restoreToCount(clipRestoreCount);

    }

//    float left, right;

    //    float[] checkBuffer = new float[]{0,0,0,0};
    RectF checkRect = new RectF();
    private String bgColorBuffer;

    private void drawColumn(Canvas c, Column<ICell> column, int index, RectF visibleRect, List<Column<ICell>> columns) {

        if (transformer == null) {
            transformer = mChart.getTransformer();
        }
        if (transformer == null) return;

        ColumnBuffer columnBuffer = mBuffers[index];

        if (columnBuffer.size() >= 4) {
            checkRect.set(column.getPreColumnsWidth(), 0, column.getPreColumnsWidth() + column.getWidth(), 0);

            transformer.rectValueToPixel(checkRect);
            if ((checkRect.left - 10 > visibleRect.right) || (checkRect.right + 10 < visibleRect.left)) {
                return;
            }
        }


        long startTime = System.currentTimeMillis();

        if (mChart.hasMergedCell()) {
            columnBuffer.feed(column, columns);
        } else {
            columnBuffer.feed(column);
        }
        Log.e("2------", "column" + index + "的feed耗费时间：" + (System.currentTimeMillis() - startTime) + "");

        long startTimeTrans = System.currentTimeMillis();
        transformer.pointValuesToPixel(columnBuffer.buffer);

//        Log.e("2------", "column" + index + "的trans耗费时间：" + (System.currentTimeMillis() - startTimeTrans) + "");

        long startTimeDraw = System.currentTimeMillis();

        for (int i = 0; i < columnBuffer.size(); i += 4) {

            if (columnBuffer.buffer[i + 2] == columnBuffer.buffer[i]) continue;

            if ((columnBuffer.buffer[i] > mViewPortHandler.contentRight()) || (columnBuffer.buffer[i + 2] < mViewPortHandler.contentLeft())) {
                return;
            }

            if (columnBuffer.buffer[i + 3] < (visibleRect.top - (columnBuffer.buffer[i + 3] - columnBuffer.buffer[i + 1])) || columnBuffer.buffer[i + 1] > visibleRect.bottom) {
                continue;
            }

            c.drawRect(columnBuffer.buffer[i], columnBuffer.buffer[i + 1], columnBuffer.buffer[i + 2],
                    columnBuffer.buffer[i + 3], mGridPaint);

            if(mChart.getBgFormatter() != null){
                bgColorBuffer = mChart.getBgFormatter().getBackgroundColor(column.getData().get(i / 4).getRealCell(), column, columns);

                if(bgColorBuffer != null){
                    mBgPaint.setColor(Color.parseColor(bgColorBuffer));
                    c.drawRect(columnBuffer.buffer[i], columnBuffer.buffer[i + 1], columnBuffer.buffer[i + 2],
                            columnBuffer.buffer[i + 3], mBgPaint);
                }
            }


            fillValuePaint(column.getData().get(i / 4).getRealCell(), column, columns);

            Utils.drawSingleText(c, mValuePaint,
                     Utils.getTextCenterX(
                             columnBuffer.buffer[i] + column.getLeftOffset() * mChart.getViewPortHandler().getScaleX(),
                             columnBuffer.buffer[i + 2] - column.getRightOffset() * mChart.getViewPortHandler().getScaleX(),
                             mValuePaint),
                    Utils.getTextCenterY((columnBuffer.buffer[i + 1] + columnBuffer.buffer[i + 3]) / 2, mValuePaint),
                    column.getData().get(i / 4).getContents()
            );
        }

//        Log.e("2------", "column" + index + "的draw耗费时间：" + (System.currentTimeMillis() - startTimeDraw) + "");

    }

    TextPaint.Align mValueTextAlignBuffer;

    private void fillValuePaint(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

        mValueTextAlignBuffer = mChart.getSheet().getTextFormatter().getTextAlign(cell, column, columns);

        mValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getSheet().getTextFormatter().getTextSize(cell, column, columns) * mViewPortHandler.getScaleX()));
        mValuePaint.setTextAlign(mValueTextAlignBuffer);
        mValuePaint.setColor(Color.parseColor(mChart.getSheet().getTextFormatter().getTextColor(cell, column, columns)));
    }


    @Override
    public void drawValues(Canvas c) {

    }

    @Override
    public void drawTitle(Canvas c) {
        transformer = mChart.getTransformer();
        if (transformer == null) return;
        //优化onDraw执行时间，只在initBuffer中做一次feed
        mTitleBuffer.feed(mChart.getColumnList());
        transformer.pointValuesToPixel(mTitleBuffer.buffer);

        Column column;
        int clipCount = 0;
        mFixedRect.set(mViewPortHandler.getContentRect());

        for (int i = 0; i < mTitleBuffer.size(); i += 4) {

            float left = mTitleBuffer.buffer[i];
            float top = mTitleBuffer.buffer[i + 1];
            float right = mTitleBuffer.buffer[i + 2];
            float bottom = mTitleBuffer.buffer[i + 3];
            float height = bottom - top;


            if (mChart.isTitleFixed()) {
                if (top < 0) {
                    top = 0;
                    bottom = height;
                }

            }

            column = mChart.getColumnList().get(i/4);


            if(column != null && column.isFixed()){

                if(left < mFixedRect.left){
                    left = mFixedRect.left;
                    right = left + mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i];
                    mFixedRect.left += right - left;
                    clipCount++;
                }
//                else if(right > mFixedRect.right){//只支持左侧固定
//                    right = mFixedRect.right;
//                    left = right - (mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i]);
//                    mFixedRect.right -= right - left;
//
//                    clipCount++;
//                }

            }

            if ((left > mViewPortHandler.contentRight()) || (right < mViewPortHandler.contentLeft())) {
                continue;
            }

            c.drawRect(left, top, right, bottom, mGridPaint);
            mTitleValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getContentFontSize() * mViewPortHandler.getScaleX()));
            Utils.drawSingleText(c, mTitleValuePaint,
                    Utils.getTextCenterX(left, right, mValuePaint),
                    Utils.getTextCenterY((top + bottom) / 2, mValuePaint),
                    mTitleBuffer.columnNames[i / 4]);
            if(clipCount > 0){
                c.save();
                c.clipRect(mFixedRect);
            }


        }

        for(int i = 0; i < clipCount; i++){
            c.restore();
        }


    }

    @Override
    public void drawExtras(Canvas c) {

    }

    @Override
    public void drawHighlighted(Canvas c, Highlight highlight) {

        if (highlight != null) {
            transformer = mChart.getTransformer();
            if (transformer == null) return;
            RectF hRect = highlight.getRect();
            int clipRestoreCount = c.save();
            if (highlight.isTitle() && mChart.isTitleFixed()) {
                c.clipRect(mChart.getViewPortHandler().getContentRect());
            } else {
                mValuesRect.set(mViewPortHandler.getContentRect());
                mValuesRect.top += mChart.getTitleHeight() * mViewPortHandler.getScaleY();
                c.clipRect(mValuesRect);
            }

            float halfBorderWidth = mChart.getHighlightBorderWidth() / 2;

            transformer.rectValueToPixel(hRect);
            c.drawRect(hRect.left + halfBorderWidth,
                    hRect.top + halfBorderWidth,
                    hRect.right - halfBorderWidth,
                    hRect.bottom - halfBorderWidth,
                    mHighlightPaint);
            c.restoreToCount(clipRestoreCount);
        }
    }
}
