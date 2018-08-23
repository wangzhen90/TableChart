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
        mHighlightPaint.setColor(mChart.getHighlightColor());
    }

    @Override
    public void initBuffers() {

        List<Column<ICell>> columns = mChart.getColumnList();
        mBuffers = new ColumnBuffer[mChart.getColumnCount()];
        mTitleBuffer = new TitleBuffer(mChart.getColumnCount() * 4, mChart.getColumnCount());
        mSumBuffer = new SumBuffer(mChart.getColumnCount() * 4,mChart.getHeight());
//        mTitleBuffer.feed(mChart.getColumnList());
        for (int i = 0; i < mChart.getColumnCount(); i++) {
            mBuffers[i] = new ColumnBuffer(columns.get(i).getData().size() * 4);
//            mBuffers[i].feed(columns.get(i));
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
        long startTime = System.currentTimeMillis();

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

//    float left, right;

    //    float[] checkBuffer = new float[]{0,0,0,0};
    RectF checkRect = new RectF();
    private String bgColorBuffer;
    int contentClipCount = 0;


    private void drawColumn(Canvas c, Column<ICell> column, int index, RectF visibleRect, List<Column<ICell>> columns) {

        if (transformer == null) {
            transformer = mChart.getTransformer();
        }
        if (transformer == null) return;

        float left = 0, top, right, bottom;

        boolean isCliped = false;

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
                left = mContentFixedRect.left;
                mContentFixedRect.left += checkRect.width();
                isCliped = true;

            } else {
                left = checkRect.left;
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

            if (mChart.getBgFormatter() != null) {
                bgColorBuffer = mChart.getBgFormatter().getContentBackgroundColor(column.getData().get(i / 4).getRealCell(), column, columns);

                if (bgColorBuffer != null) {
                    mBgPaint.setColor(Color.parseColor(bgColorBuffer));
                    c.drawRect(left, top, right, bottom, mBgPaint);
                }
            }


            fillValuePaint(column.getData().get(i / 4).getRealCell(), column, columns);

            Utils.drawSingleText(c, mValuePaint,
                    Utils.getTextCenterX(
                            left + column.getLeftOffset() * mChart.getViewPortHandler().getScaleX(),
                            right - column.getRightOffset() * mChart.getViewPortHandler().getScaleX(),
                            mValuePaint),
                    Utils.getTextCenterY((top + bottom) / 2, mValuePaint),
                    column.getData().get(i / 4).getContents()
            );


        }

        if (isCliped) {
            c.save();
            contentClipCount++;
            c.clipRect(mContentFixedRect);
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



    private void fillTitlePaint(Column column){

        String bgColor = mChart.getBgFormatter().getTitleBackgroundColor();
        if(mChart.getHighlight() != null && mChart.getHighlight().isTitle() && mChart.getHighlight().getColumnIndex() == column.columnIndex){
            mBgPaint.setColor(mChart.getHighlightColor());
            mTitleValuePaint.setColor(Color.parseColor("white"));
        }else if(!TextUtils.isEmpty(bgColor)){
            mBgPaint.setColor(Color.parseColor(bgColor));
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        }else{
            mBgPaint.setColor(Color.TRANSPARENT);
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        }
    }


    private void fillSumPaint(){
        String bgColor = mChart.getBgFormatter().getTitleBackgroundColor();
        if(!TextUtils.isEmpty(bgColor)){
            mBgPaint.setColor(Color.parseColor(bgColor));
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        }else{
            mBgPaint.setColor(Color.TRANSPARENT);
            mTitleValuePaint.setColor(mChart.getTitleValueColor());
        }
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

        if(mChart.isShowSum()){
            mSumBuffer.feed(mChart.getColumnList());
            transformer.pointValuesToPixel(mSumBuffer.buffer);
        }



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


                if(mFixedRect.bottom == mViewPortHandler.contentBottom()){
                    mFixedRect.bottom += column.getRowHeight();
                }

                if (left < mFixedRect.left) {
                    left = mFixedRect.left;
                    right = left + mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i];
                    mFixedRect.left += right - left;
                    clipCount++;
                    isCliped = true;
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
            fillTitlePaint(column);

            c.drawRect(left, top, right, bottom, mBgPaint);
            c.drawRect(left, top, right, bottom, mGridPaint);
            mTitleValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getTitleFontSize() * mViewPortHandler.getScaleX()));
            Utils.drawSingleText(c, mTitleValuePaint,
                    Utils.getTextCenterX(left, right, mValuePaint),
                    Utils.getTextCenterY((top + bottom) / 2, mValuePaint),
                    mTitleBuffer.columnNames[i / 4]);

            if(mChart.isShowSum()){

                float sumTop,sumBottom,sumHeight;
                sumHeight = mSumBuffer.buffer[i+3] - mSumBuffer.buffer[i+1];
                sumBottom = mSumBuffer.buffer[i+3];


                if(sumBottom != mChart.getHeight()){
                    sumBottom = mChart.getHeight();
                }
                sumTop = sumBottom - sumHeight;


                fillSumPaint();
                c.drawRect(left, sumTop, right, sumBottom, mBgPaint);
                c.drawRect(left, sumTop, right, sumBottom, mGridPaint);

                mSumValuePaint.setTextSize(Utils.convertDpToPixel(mChart.getTitleFontSize() * mViewPortHandler.getScaleY()));

                Utils.drawSingleText(c, mSumValuePaint,
                        Utils.getTextCenterX(left, right, mValuePaint),
                        Utils.getTextCenterY((sumTop + sumBottom) / 2, mValuePaint),
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
//        if(mChart.isShowSum()){
//            mSumBuffer.feed(mChart.getColumnList());
//
//            transformer = mChart.getTransformer();
//            if (transformer == null) return;
//            mSumBuffer.feed(mChart.getColumnList());
//            ICell sumCell;
//            Column<ICell> column;
//            for(int i = 0; i < mSumBuffer.buffer.length; i+=4){
//
//                float left = mTitleBuffer.buffer[i];
//                float top = mTitleBuffer.buffer[i + 1];
//                float right = mTitleBuffer.buffer[i + 2];
//                float bottom = mTitleBuffer.buffer[i + 3];
//                float height = bottom - top;
//                boolean isCliped = false;
//
//                column = mChart.getColumnList().get(i / 4);
//
//                if (column != null && column.isFixed()) {
//
//                    if (left < mFixedRect.left) {
//                        left = mFixedRect.left;
//                        right = left + mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i];
//                        mFixedRect.left += right - left;
//                        clipCount++;
//                        isCliped = true;
//                    }
////                else if(right > mFixedRect.right){//只支持左侧固定
////                    right = mFixedRect.right;
////                    left = right - (mTitleBuffer.buffer[i + 2] - mTitleBuffer.buffer[i]);
////                    mFixedRect.right -= right - left;
////
////                    clipCount++;
////                }
//
//                }
//
//                if(mSumBuffer.buffer[i] > mViewPortHandler.contentRight()){
//                    continue;
//                }
//
//
//
//            }
//
//
//        }
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




}
