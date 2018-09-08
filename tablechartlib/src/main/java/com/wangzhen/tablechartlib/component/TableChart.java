package com.wangzhen.tablechartlib.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;


import com.wangzhen.tablechartlib.data.CellType;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.EmptyCell;
import com.wangzhen.tablechartlib.data.Sheet;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;
import com.wangzhen.tablechartlib.formatter.IValueFormatter;
import com.wangzhen.tablechartlib.highlight.Highlight;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.interfaces.ISheet;
import com.wangzhen.tablechartlib.interfaces.ITableOnClickListener;
import com.wangzhen.tablechartlib.listener.ChartTouchListener;
import com.wangzhen.tablechartlib.renderder.DataRenderer;
import com.wangzhen.tablechartlib.renderder.SimpleRenderer;
import com.wangzhen.tablechartlib.utils.Transformer;
import com.wangzhen.tablechartlib.utils.Utils;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by wangzhen on 2018/6/28.
 */

public class TableChart extends ViewGroup {

    public static final int SORT_DISORDER = 0;
    public static final int SORT_DES = 1;
    public static final int SORT_ASC = 2;

    private ISheet sheet;

    protected DataRenderer mDataRenderer;

    protected ViewPortHandler mViewPortHandler = new ViewPortHandler();

    protected Transformer mTransformer;

    private boolean dragEnable = true;
    private boolean scaleXEnable = true;
    private boolean scaleYEnable = true;

    private boolean mDragXEnabled = true;
    private boolean mDragYEnabled = true;

    private boolean mDragOnlySigleDirection = true;

    protected boolean mPinchZoomEnabled = true;

    protected ChartTouchListener mChartTouchListener;

    protected boolean mTouchEnable = true;
    //惯性减速系数
    private float mDragDecelerationFrictionCoef = 0.9f;

    private boolean isTitleFixed = true;

    private int titleFontSize = 9;

    private float highlightBorderWidth = Utils.convertDpToPixel(5f);


    private Highlight mHighlight;

    private ITableOnClickListener onClickListener;

    private int highlightColor = Color.parseColor("#4558C9");

    private int titleValueColor = Color.parseColor("#4D4D4D");

    private boolean sortable = false;
    private boolean sorting = false;
    private boolean sorted = false;
    private int sortMode = SORT_DISORDER;

    private boolean drawColumnHighlightBg = true;


    public TableChart(Context context) {
        super(context);
        init();
    }

    public TableChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TableChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setWillNotDraw(false);

        Utils.init(this.getContext());
        mDataRenderer = new SimpleRenderer(mViewPortHandler, this);
        mTransformer = new Transformer(mViewPortHandler);


        mChartTouchListener = new ChartTouchListener(this, mViewPortHandler.getMatrixTouch(), 5f);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        if (sheet == null)
            return;

        if (mDataRenderer != null) {

            mDataRenderer.drawTitle(canvas);
            mDataRenderer.drawData(canvas);
            mDataRenderer.drawHighlighted(canvas, mHighlight);

        }
    }

    public void setSheet(ISheet sheet) {
        this.sheet = sheet;
        this.sheet.setChart(this);
        notifyDataSetChanged();
    }

    public ISheet getSheet() {
        return sheet;
    }


    public void notifyDataSetChanged() {

        if (mDataRenderer != null) {
            mDataRenderer.initBuffers();
        }
        sheet.setViewWidth(getMeasuredWidth());
        sheet.calculate();

        calcMinMax();

        calculateOffsets();

        invalidate();
    }

    protected void calcMinMax() {

        prepareOffsetMatrix();
    }


    private boolean showSum;

    public void calculateOffsets() {

        mViewPortHandler.setMaxTransY(sheet.getHeight());
        mViewPortHandler.setMaxTransX(sheet.getWidth());

//        float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;
//
//        if(showSum){
//            offsetBottom = sheet.getRowHeight();
//        }
//
//        mViewPortHandler.restrainViewPort(offsetLeft,offsetTop,offsetRight,offsetBottom);

    }

    protected void prepareOffsetMatrix() {

        //TODO 表格的默认scaleX = 1, scaleY = 1,这个地方只需要做偏移量的处理
    }
//
//
//    protected void prepareValuePxMatrix() {
//
//
//    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {

            mViewPortHandler.setChartDimens(w, h);
        }

        notifyDataSetChanged();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) Utils.convertDpToPixel(50f);
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(size,
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(size,
                                heightMeasureSpec)));
    }

    public Transformer getTransformer() {
        return mTransformer;
    }

    public int getColumnCount() {

        return sheet.getColumns();
    }

    public List<Column<ICell>> getColumnList() {

        return sheet.getColumnList();
    }

    public List<ICell> getSumCells() {

        return sheet.getSumCells();
    }


    public boolean isScaleXEnabled() {
        return scaleXEnable;
    }

    public boolean isScaleYEnabled() {
        return scaleYEnable;
    }

    public void disableScroll() {

        ViewParent parent = getParent();

        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }

    }

    public void setPinchZoom(boolean enabled) {
        mPinchZoomEnabled = enabled;
    }

    public boolean isPinchZoomEnabled() {
        return mPinchZoomEnabled;
    }

    public void setDragEnabled(boolean enabled) {
        this.mDragXEnabled = enabled;
        this.mDragYEnabled = enabled;
    }

    public boolean isDragEnabled() {
        return mDragXEnabled || mDragYEnabled;
    }

    public void setDragXEnabled(boolean enabled) {
        this.mDragXEnabled = enabled;
    }

    public boolean isDragXEnabled() {
        return mDragXEnabled;
    }

    public void setDragYEnabled(boolean enabled) {
        this.mDragYEnabled = enabled;
    }

    public boolean isDragYEnabled() {
        return mDragYEnabled;
    }

    public void enableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(false);
    }

    public void setTouchEnable(boolean touchEnable) {
        this.mTouchEnable = touchEnable;
    }

    public ViewPortHandler getViewPortHandler() {
        return mViewPortHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (mChartTouchListener == null && sheet == null) {
            return false;
        }

        if (!mTouchEnable) {
            return false;
        } else {
            return mChartTouchListener.onTouch(this, event);
        }
    }

    public float getDragDecelerationFrictionCoef() {
        return mDragDecelerationFrictionCoef;
    }

    public void setDragDecelerationFrictionCoef(float newValue) {

        if (newValue < 0.f)
            newValue = 0.f;

        if (newValue >= 1f)
            newValue = 0.999f;

        mDragDecelerationFrictionCoef = newValue;
    }

    @Override
    public void computeScroll() {
        if (mChartTouchListener != null) {
            mChartTouchListener.computeScroll();
        }
    }

    public int getTitleHeight() {
        return ((Sheet) sheet).getTitleHeight();
    }

    public boolean isTitleFixed() {
        return isTitleFixed;
    }

    public void setTitleFixed(boolean titleFixed) {
        isTitleFixed = titleFixed;
    }

    public boolean hasNoDragOffset() {
        return mViewPortHandler.hasNoDragOffset();
    }

    public boolean isDragOnlySigleDirection() {
        return mDragOnlySigleDirection;
    }

    public void setDragOnlySigleDirection(boolean mDragOnlySigleDirection) {
        this.mDragOnlySigleDirection = mDragOnlySigleDirection;
    }


    public Column getColumnByXValue(double xValue) {

        return sheet.getColumnByXValue(xValue);
    }


    public ICell getCellByTouchPoint(double xValue, double yValue) {

        ICell virtualCell = sheet.getCellByTouchPoint(xValue, yValue);

        if (virtualCell != null) {
            if (virtualCell.getType() == CellType.EMPTY) {
                return ((EmptyCell) virtualCell).getRealCell();
            } else {
                return virtualCell;
            }
        }


        return sheet.getCellByTouchPoint(xValue, yValue);
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int fontSize) {
        this.titleFontSize = fontSize;
    }


    public void highlightValue(Highlight h, boolean callListener) {

        mHighlight = h;

        if (h == null) return;

        if (callListener && onClickListener != null) {
            if (h.isTitle())
                onClickListener.onColumnClick(h.getColumnData());
            else
                onClickListener.onCellClick(h.getCell());
        }


        invalidate();

    }


    public Highlight getHighlight() {
        return mHighlight;
    }

    public int getContentHeight() {

        return sheet.getHeight();
    }

    public float getHighlightBorderWidth() {
        return highlightBorderWidth;
    }

    public void setHighlightBorderWidth(float highlightBorderWidth) {
        this.highlightBorderWidth = highlightBorderWidth;
    }

    public ITableOnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ITableOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public boolean hasMergedCell() {
        return sheet.hasMergedCell();
    }


    public IBgFormatter getBgFormatter() {
        if (sheet != null) {
            return sheet.getBgFormatter();
        }
        return null;
    }

    public int getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }


    public int getTitleValueColor() {
        return titleValueColor;
    }

    public void setTitleValueColor(int titleValueColor) {
        this.titleValueColor = titleValueColor;
    }

    public boolean isShowSum() {
        return showSum;
    }

    public void setShowSum(boolean showSum) {
        this.showSum = showSum;
    }


    public int getRowHeight() {

        return sheet.getRowHeight();

    }

    public void setRowHeight(int rowHeight) {

        sheet.setRowHeight(rowHeight);
    }

    public IValueFormatter getValueFormatter() {

        return sheet.getValueFormatter();
    }

    public ITextFormatter getTextFormatter() {
        return sheet.getTextFormatter();
    }


    public void sort(final Column<ICell> targetColumn) {

        if (!sortable || sorting || targetColumn.getData() == null || targetColumn.getData().isEmpty())
            return;
        sorting = true;

        if (targetColumn.getSortMode() == SORT_DISORDER) {
            targetColumn.setSortMode(SORT_DES);
            Toast.makeText(getContext(), "降序", Toast.LENGTH_SHORT).show();
        } else if (targetColumn.getSortMode() == SORT_DES) {
            targetColumn.setSortMode(SORT_ASC);
            Toast.makeText(getContext(), "升序", Toast.LENGTH_SHORT).show();

        } else if (targetColumn.getSortMode() == SORT_ASC) {
            targetColumn.setSortMode(SORT_DISORDER);
            Toast.makeText(getContext(), "无序", Toast.LENGTH_SHORT).show();
        }

        if (targetColumn.getSortMode() == SORT_DISORDER) {
            sorting = false;
            resetDatas();
            sortMode = targetColumn.getSortMode();
            return;
        }

        Collections.sort(targetColumn.getSortDatas(), new Comparator<ICell>() {
            @Override
            public int compare(ICell o1, ICell o2) {
                try {
                    if (targetColumn.columnIndex == 2) {
                        return sortNumber(o1, o2, targetColumn.getSortMode() == SORT_ASC);
                    } else {
                        return sortString(o1, o2, targetColumn.getSortMode() == SORT_ASC);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        List<ICell> targetList = targetColumn.getSortDatas();
        List<Column<ICell>> columns = sheet.getColumnList();

        for (int i = 0; i < targetList.size(); i++) {
            ICell cell = targetList.get(i);

            ICell changeCell;
            int rawRowIndex = cell.getRawRow();
            cell.setRow(i);

            for (int j = 0; j < columns.size(); j++) {
                if (j == targetColumn.columnIndex) continue;
                Column<ICell> thisColumn = columns.get(j);
                changeCell = thisColumn.getData().get(rawRowIndex);
                changeCell.setRow(i);
                thisColumn.getSortDatas().set(i, changeCell);
            }
        }
        sortMode = targetColumn.getSortMode();
        sorting = false;

    }


    void resetDatas() {

        List<Column<ICell>> columns = sheet.getColumnList();
        for (int i = 0; i < columns.size(); i++) {
            List<ICell> cells = columns.get(i).getData();
            for (int j = 0; j < cells.size(); j++) {
                cells.get(j).setRow(j);
            }
        }

    }

    static Pattern numberPattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public static boolean isNumber(String str) {

        return numberPattern.matcher(str).matches();
    }


    private int sortNumber(ICell o1, ICell o2, boolean isAsc) {

        if (TextUtils.isEmpty(o1.getContents())) {
            return isAsc ? -1 : 1;
        } else if (TextUtils.isEmpty(o2.getContents())) {
            return isAsc ? 1 : -1;
        } else if (!isNumber(o1.getContents()) && !isNumber(o2.getContents())) {
            return sortString(o1, o2, isAsc);
        } else if (!isNumber(o1.getContents())) {
            return isAsc ? -1 : 1;
        } else if (!isNumber(o1.getContents())) {
            return isAsc ? 1 : -1;
        }

        return isAsc ? (int) (Float.parseFloat(o2.getContents()) - Float.parseFloat(o1.getContents())) : (int) (Float.parseFloat(o1.getContents()) - Float.parseFloat(o2.getContents()));
    }


    private int sortString(ICell o1, ICell o2, boolean isAsc) {

        Collator collator = Collator.getInstance(Locale.CHINA);

        if (isAsc) {
            return -collator.compare(o1.getContents(), o2.getContents());
        } else {
            return collator.compare(o1.getContents(), o2.getContents());
        }

    }

    //合并单元格不允许排序
    public boolean isSortable() {
        return sortable && !hasMergedCell();
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isSorting() {
        return sorting;
    }

    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }

    public boolean isSorted() {
        return sortMode != SORT_DISORDER;
    }


    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    public int getColumnPaddingRight(){
        return  ((Sheet)sheet).columnRightOffset;
    }

    public boolean isDrawColumnHighlightBg() {
        return drawColumnHighlightBg;
    }

    public void setDrawColumnHighlightBg(boolean drawColumnHighlightBg) {
        this.drawColumnHighlightBg = drawColumnHighlightBg;
    }
}
