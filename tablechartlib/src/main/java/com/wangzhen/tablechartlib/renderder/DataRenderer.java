package com.wangzhen.tablechartlib.renderder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.wangzhen.tablechartlib.formatter.IValueFormatter;

import com.wangzhen.tablechartlib.highlight.Highlight;
import com.wangzhen.tablechartlib.utils.Utils;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

/**
 * Created by wangzhen on 2018/6/28.
 */

public abstract class DataRenderer extends Renderer {


    protected Paint mValuePaint;

    protected Paint mGridPaint;

    protected Paint mTitleValuePaint;

    protected Paint mHighlightPaint;

    protected Paint mBgPaint;

    protected Paint mSumValuePaint;



    public DataRenderer(ViewPortHandler viewPortHandler) {


        super(viewPortHandler);

        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setTextAlign(Paint.Align.CENTER);
        mValuePaint.setTextSize(Utils.convertDpToPixel(9f));
        mValuePaint.setColor(Color.parseColor("#4D4D4D"));

        mTitleValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitleValuePaint.setTextAlign(Paint.Align.CENTER);
        mTitleValuePaint.setTextSize(Utils.convertDpToPixel(9f));
        mTitleValuePaint.setColor(Color.parseColor("#4D4D4D"));

        mSumValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSumValuePaint.setTextAlign(Paint.Align.CENTER);
        mSumValuePaint.setTextSize(Utils.convertDpToPixel(9f));
        mSumValuePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        mSumValuePaint.setColor(Color.parseColor("#4D4D4D"));


        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStrokeWidth(1);
        mGridPaint.setColor(Color.parseColor("#B3B3B3"));
        mGridPaint.setStyle(Paint.Style.STROKE);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(3f));
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setColor(Color.parseColor("#2ca9e1"));

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mBgPaint.setColor(Color.parseColor("transparent"));
        mBgPaint.setStyle(Paint.Style.FILL);
    }


    public abstract void initBuffers();

    public abstract void drawData(Canvas c);
    public abstract void drawValues(Canvas c);
    public abstract void drawTitle(Canvas c);
    public abstract void drawSum(Canvas c);


    public abstract void drawExtras(Canvas c);


//    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
//
//    }

    public abstract void drawHighlighted(Canvas c, Highlight highlight);


}
