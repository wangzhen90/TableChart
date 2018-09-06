package com.wangzhen.tablechartlib.renderder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.wangzhen.tablechartlib.buffer.ColumnBuffer;
import com.wangzhen.tablechartlib.buffer.SumBuffer;
import com.wangzhen.tablechartlib.buffer.TitleBuffer;
import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.CellType;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;
import com.wangzhen.tablechartlib.formatter.IValueFormatter;
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

    private IValueFormatter valueFormatter;
    private IBgFormatter bgFormatter;
    private ITextFormatter textFormatter;


    public SimpleRenderer(ViewPortHandler viewPortHandler, TableChart chart) {
        super(viewPortHandler);
        this.mChart = chart;
        mHighlightPaint.setStrokeWidth(mChart.getHighlightBorderWidth());
        mHighlightPaint.setColor(mChart.getHighlightColor());

    }

    @Override
    public void initBuffers() {

        List<Column<ICell>> columns = mChart.getColumnList();
        mBuffers = new ColumnBuffer[mChart.getColumnCount()];
        mTitleBuffer = new TitleBuffer(mChart.getColumnCount() * 4, mChart.getColumnCount());
        mSumBuffer = new SumBuffer(mChart.getColumnCount() * 4, mChart.getHeight());
        for (int i = 0; i < mChart.getColumnCount(); i++) {
            mBuffers[i] = new ColumnBuffer(columns.get(i).getData().size() * 4);
        }
    }

    private RectF mValuesRect = new RectF();
    private RectF mFixedRect = new RectF();
    private RectF mContentFixedRect = new RectF();

    @Override
    public void drawData(Canvas c) {
        List<Column<ICell>> columns = mChart.getColumnList();


        mValuesRect.set(mViewPortHandler.getContentRect());
        mValuesRect.top += mChart.getTitleHeight() * mViewPortHandler.getScaleY();

        mValuesRect.bottom -= mChart.isShowSum() ? mChart.getRowHeight() * mViewPortHandler.getScaleY() : 0;
        int clipRestoreCount = c.save();
        c.clipRect(mValuesRect);
        mContentFixedRect.set(mValuesRect);
//        long startTime = System.currentTimeMillis();

        for (int i = 0; i < mChart.getColumnCount(); i++) {
            drawColumn(c, columns.get(i), i, mValuesRect, columns);
        }

//        Log.e("1------绘制所有column的耗费时间：", (System.currentTimeMillis() - startTime) + "");

        for (int i = 0; i < contentClipCount; i++) {
            c.restore();
        }
        contentClipCount = 0;

        c.restoreToCount(clipRestoreCount);

    }

    RectF checkRect = new RectF();
    private int bgColorBuffer;
    int contentClipCount = 0;


    private void drawColumn(Canvas c, Column<ICell> column, int index, RectF visibleRect, List<Column<ICell>> columns) {

        if (transformer == null) {
            transformer = mChart.getTransformer();
        }
        if (transformer == null) return;

        float left = 0, columnLeft = 0,top, right, bottom;

        boolean isCliped = false;
        boolean hasFixedLeft = false;
        ColumnBuffer columnBuffer = mBuffers[index];
        //过滤掉不需要映射column
        if (columnBuffer.size() >= 4) {
            checkRect.set(column.getPreColumnsWidth(), 0, column.getPreColumnsWidth() + column.getWidth(), 0);

            transformer.rectValueToPixel(checkRect);
            if ((checkRect.left - 10 > visibleRect.right)) {
                return;
            }

            if (checkRect.right + 10 < visibleRect.left) {
                if (!column.isFixed()) {
                    return;
                }
            }


            if (column.isFixed() && checkRect.left < mContentFixedRect.left) {
                columnLeft = mContentFixedRect.left;
                mContentFixedRect.left += checkRect.width();
                isCliped = true;

            } else {
                columnLeft = checkRect.left;
            }

        }


//        long startTime = System.currentTimeMillis();

        if (mChart.hasMergedCell()) {
            columnBuffer.feed(column, columns);
        } else {
            columnBuffer.feed(column);
        }
//        Log.e("2------", "column" + index + "的feed耗费时间：" + (System.currentTimeMillis() - startTime) + "");

//        long startTimeTrans = System.currentTimeMillis();
        transformer.pointValuesToPixel(columnBuffer.buffer);
//        Log.e("2------", "column" + index + "的trans耗费时间：" + (System.currentTimeMillis() - startTimeTrans) + "");
        long startTimeDraw = System.currentTimeMillis();
        ICell realCell;
        ICell currentCell;

        for (int i = 0; i < columnBuffer.size(); i += 4) {

            currentCell = column.getData().get(i / 4);
            realCell = currentCell.getRealCell();

            if(currentCell.getType() == CellType.EMPTY){
                left = columnBuffer.buffer[i];
            }else{
                left = columnLeft;
            }

            right = columnBuffer.buffer[i + 2];
            top = columnBuffer.buffer[i + 1];
            bottom = columnBuffer.buffer[i + 3];

            //过滤掉不需要绘制的Cell
            if (left == right) continue;

            if (isCliped) {
                right = left + columnBuffer.buffer[i + 2] - columnBuffer.buffer[i];
            }

            if ((left > mViewPortHandler.contentRight()) || (right < mViewPortHandler.contentLeft())) {
                return;
            }

            if (bottom < (visibleRect.top - (bottom - top)) || top > visibleRect.bottom) {
                continue;
            }


            c.drawRect(left, top, right, bottom, mGridPaint);
            if (bgFormatter != null) {
                bgColorBuffer = bgFormatter.getContentBackgroundColor(realCell, column, columns);
                if (bgColorBuffer != -1) {
                    if (bgColorBuffer != mBgPaint.getColor()) mBgPaint.setColor(bgColorBuffer);
                    c.drawRect(left, top, right, bottom, mBgPaint);
                }
            }


            fillValuePaint(realCell, column, columns);

            Utils.drawSingleText(c, mValuePaint,
                    Utils.getTextCenterX(
                            left + column.getLeftOffset() * mChart.getViewPortHandler().getScaleX(),
                            right - column.getRightOffset() * mChart.getViewPortHandler().getScaleX(),
                            mValuePaint),
                    Utils.getTextCenterY((top + bottom) / 2, mValuePaint),
//                    realCell.getContents()
                    valueFormatter != null ?
                            valueFormatter.getFormattedValue(realCell, column, columns)
                            : realCell.getContents()
            );
            if(column.columnIndex == 3)
            Log.e("2------", column.columnIndex +"_column的第" + i / 4 + "行的draw耗费时间：" + (System.currentTimeMillis() - startTimeDraw) + "");

        }

        if (isCliped) {
            c.save();
            contentClipCount++;
            c.clipRect(mContentFixedRect);
        }

//        Log.e("2------", "column" + index + "的draw耗费时间：" + (System.currentTimeMillis() - startTimeDraw) + "");
//        Log.e("3------", "绘制的column" + index);
    }

    TextPaint.Align mValueTextAlignBuffer;

    private void fillValuePaint(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

        if (textFormatter == null) return;

        try {
            mValueTextAlignBuffer = textFormatter.getTextAlign(cell, column, columns);

            float textSize = Utils.convertDpToPixel(textFormatter.getTextSize(cell, column, columns) * mViewPortHandler.getScaleX());
            int color = textFormatter.getTextColor(cell, column, columns);

            if (mValuePaint.getTextSize() != textSize) {
                mValuePaint.setTextSize(textSize);
            }

            if (mValueTextAlignBuffer != mValuePaint.getTextAlign()) {
                mValuePaint.setTextAlign(mValueTextAlignBuffer);
            }

            if (mValuePaint.getColor() != color) {
                mValuePaint.setColor(color);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillTitlePaint(Column column) {

        int bgColor = bgFormatter.getTitleBackgroundColor();


        if (mChart.getHighlight() != null && mChart.getHighlight().isTitle() && mChart.getHighlight().getColumnIndex() == column.columnIndex) {

            mBgPaint.setColor(mChart.getHighlightColor());
            mTitleValuePaint.setColor(Color.parseColor("white"));

        } else if (bgColor != -1) {
            mBgPaint.setColor(bgColor);
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        } else {
            mBgPaint.setColor(Color.TRANSPARENT);
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        }

        if(mTitleValuePaint.getTextAlign() != column.getTitleTextAlign()){
            mTitleValuePaint.setTextAlign(column.getTitleTextAlign());
        }

    }


    private void fillSumPaint(Column column) {
        int bgColor = bgFormatter.getTitleBackgroundColor();
        if (bgColor != -1) {
            mBgPaint.setColor(bgColor);
            mSumValuePaint.setColor(mChart.getTitleValueColor());
        } else {
            mBgPaint.setColor(Color.TRANSPARENT);
            mSumValuePaint.setColor(mChart.getTitleValueColor());
        }

        if(mSumValuePaint.getTextAlign() != column.getTitleTextAlign()){
            mSumValuePaint.setTextAlign(column.getTitleTextAlign());
        }
    }


    @Override
    public void drawValues(Canvas c) {

    }

    @Override
    public void drawTitle(Canvas c) {
        transformer = mChart.getTransformer();
        if (transformer == null) return;
        mTitleBuffer.feed(mChart.getColumnList());

        transformer.pointValuesToPixel(mTitleBuffer.buffer);

        if (mChart.isShowSum()) {
            mSumBuffer.feed(mChart.getColumnList());
            transformer.pointValuesToPixel(mSumBuffer.buffer);
        }

        if (mChart.getSheet() != null) getFormatter();


        Column column;
        int clipCount = 0;
        mFixedRect.set(mViewPortHandler.getContentRect());

        for (int i = 0; i < mTitleBuffer.size(); i += 4) {

            float left = mTitleBuffer.buffer[i];
            float top = mTitleBuffer.buffer[i + 1];
            float right = mTitleBuffer.buffer[i + 2];
            float bottom = mTitleBuffer.buffer[i + 3];
            float height = bottom - top;

            boolean isCliped = false;

            if (mChart.isTitleFixed()) {
                if (top < 0) {
                    top = 0;
                    bottom = height;
                }

            }

            column = mChart.getColumnList().get(i / 4);


            if (column != null && column.isFixed()) {


                if (mFixedRect.bottom == mViewPortHandler.contentBottom()) {
                    mFixedRect.bottom += column.getRowHeight();
                }

                if (left < mFixedRect.left) {
                    left = mFixedRect.left;
                    right = left + mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i];
                    mFixedRect.left += right - left;
                    clipCount++;
                    isCliped = true;
                }

            }

            if ((left > mViewPortHandler.contentRight()) || (right < mViewPortHandler.contentLeft())) {
                continue;
            }
            fillTitlePaint(column);

            c.drawRect(left, top, right, bottom, mBgPaint);
            c.drawRect(left, top, right, bottom, mGridPaint);

            mTitleValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getTitleFontSize() * mViewPortHandler.getScaleX()));

            Utils.drawSingleText(c, mTitleValuePaint,
                    Utils.getTextCenterX(left + column.getLeftOffset() * mChart.getViewPortHandler().getScaleX(),
                            right - column.getRightOffset() * mChart.getViewPortHandler().getScaleX(), mTitleValuePaint),
                    Utils.getTextCenterY((top + bottom) / 2, mTitleValuePaint),
                    mTitleBuffer.columnNames[i / 4]);

            if (mChart.isShowSum()) {

                float sumTop, sumBottom, sumHeight;
                sumHeight = mSumBuffer.buffer[i + 3] - mSumBuffer.buffer[i + 1];
                sumBottom = mSumBuffer.buffer[i + 3];


                if (sumBottom != mChart.getHeight()) {
                    sumBottom = mChart.getHeight();
                }
                sumTop = sumBottom - sumHeight;


                fillSumPaint(column);
                c.drawRect(left, sumTop, right, sumBottom, mBgPaint);
                c.drawRect(left, sumTop, right, sumBottom, mGridPaint);

                mSumValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getTitleFontSize() * mViewPortHandler.getScaleY()));

                Utils.drawSingleText(c, mSumValuePaint,
                        Utils.getTextCenterX(left + column.getLeftOffset() * mChart.getViewPortHandler().getScaleX(),
                                right - column.getRightOffset() * mChart.getViewPortHandler().getScaleX(), mSumValuePaint),
                        Utils.getTextCenterY((sumTop + sumBottom) / 2, mSumValuePaint),
                        column.getSumCell() != null ? column.getSumCell().getContents() : null);
            }


            if (clipCount > 0 && isCliped) {
                c.save();
                c.clipRect(mFixedRect);
            }
        }

        for (int i = 0; i < clipCount; i++) {
            c.restore();
        }


    }

    private SumBuffer mSumBuffer;

    @Override
    public void drawSum(Canvas c) {
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

            int bottomExtra = mChart.isShowSum() ? mChart.getRowHeight() : 0;

            if (highlight.isTitle() && mChart.isTitleFixed()) {

//                c.clipRect(mChart.getViewPortHandler().getContentRect());
                c.clipRect(mChart.getViewPortHandler().contentLeft(),
                        mChart.getViewPortHandler().contentTop(),
                        mChart.getViewPortHandler().contentRight(),
                        mChart.getViewPortHandler().contentBottom() + bottomExtra);
                hRect.bottom += bottomExtra;

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


    void getFormatter() {

        if (valueFormatter == null) valueFormatter = mChart.getValueFormatter();
        if (bgFormatter == null) bgFormatter = mChart.getBgFormatter();
        if (textFormatter == null) textFormatter = mChart.getTextFormatter();

    }

}
