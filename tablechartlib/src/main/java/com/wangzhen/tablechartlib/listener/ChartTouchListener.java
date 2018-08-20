package com.wangzhen.tablechartlib.listener;

import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.highlight.Highlight;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.MPPointD;
import com.wangzhen.tablechartlib.utils.MPPointF;
import com.wangzhen.tablechartlib.utils.Transformer;
import com.wangzhen.tablechartlib.utils.Utils;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

/**
 * Created by wangzhen on 2018/7/8.
 */

public class ChartTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {


    public enum ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    protected ChartGesture mLastGesture = ChartGesture.NONE;

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    //chart原有的matrix
    private Matrix mOriginMatrix = new Matrix();
    //记录chart原有的matrix的matrix
    private Matrix mSavedMatrix = new Matrix();

    //开始的点
    private MPPointF mTouchStartPoint = MPPointF.getInstance(0, 0);
    //双指间的中心点
    private MPPointF mTouchPointCenter = MPPointF.getInstance(0, 0);

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;


    protected int mCurrentTouchMode = NONE;

    //速度跟踪器
    private VelocityTracker mVelocityTracker;

    //减速时间
    private long mDecelerationLastTime = 0;
    private MPPointF mDecelerationCurrentPoint = MPPointF.getInstance(0, 0);
    private MPPointF mDecelerationVelocity = MPPointF.getInstance(0, 0);

    protected TableChart mChart;

    //拖拽的触发位移
    private float mDragTriggerDist;
    //缩放的触发位移
    private float mMinScalePointerDistance;

    //上一次的highlight
    private Highlight mLastHighlight = null;
    //本次的highlight
    private Highlight mHighlight = null;

    protected GestureDetector mGestureDetector;


    public ChartTouchListener(TableChart chart, Matrix touchMatrix, float dragTiggerDistance) {

        this.mChart = chart;
        this.mOriginMatrix = touchMatrix;
        this.mDragTriggerDist = dragTiggerDistance;
        mGestureDetector = new GestureDetector(mChart.getContext(),this);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //初始化速度跟踪器
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }


        if (mCurrentTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
            return true;


        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                stopDeceleration();
                saveTouchStart(event);

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {

                    mChart.disableScroll();

                    saveTouchStart(event);

                    mSavedXDist = getXDist(event);
                    mSavedYDist = getYDist(event);

                    mSavedDist = spacing(event);

                    if (mSavedDist > 10f) {
                        if (mChart.isPinchZoomEnabled()) {
                            mCurrentTouchMode = PINCH_ZOOM;
                        }
                        else {
                            if (mChart.isScaleXEnabled() != mChart.isScaleYEnabled()) {
                                mCurrentTouchMode = mChart.isScaleXEnabled() ? X_ZOOM : Y_ZOOM;
                            } else {
                                mCurrentTouchMode = mSavedXDist > mSavedYDist ? X_ZOOM : Y_ZOOM;
                            }
                        }
                    }
                    midPoint(mTouchPointCenter, event);
                }

                break;


            case MotionEvent.ACTION_MOVE:

                if (mCurrentTouchMode == DRAG) {
                    mChart.disableScroll();

                    float x = mChart.isDragXEnabled() ? event.getX() - mTouchStartPoint.x : 0;
                    float y = mChart.isDragYEnabled() ? event.getY() - mTouchStartPoint.y : 0;

                    if(mChart.isDragOnlySigleDirection()){
                        if(Math.abs(x) > Math.abs(y)){
                            y = 0;
                        }else{
                            x = 0;
                        }
                    }


                    performDrag(event, x, y);
                } else if ( mCurrentTouchMode == PINCH_ZOOM || mCurrentTouchMode == X_ZOOM || mCurrentTouchMode == Y_ZOOM) {

                    mChart.disableScroll();

                    if(mChart.isPinchZoomEnabled()){
                        performZoom(event);
                    }

                } else if (mCurrentTouchMode == NONE
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(), mTouchStartPoint.y)) > mDragTriggerDist) {

                    if (mChart.isDragEnabled()) {

                        float distanceX = Math.abs(event.getX() - mTouchStartPoint.x);
                        float distanceY = Math.abs(event.getY() - mTouchStartPoint.y);

                        if ((mChart.isDragXEnabled() || distanceY >= distanceX) &&
                                (mChart.isDragYEnabled() || distanceY <= distanceX)) {
                            mLastGesture = ChartGesture.DRAG;
                            mCurrentTouchMode = DRAG;
                        }

                    }

                }

                break;

            case MotionEvent.ACTION_UP:

                //1.惯性滑动的实现
                final VelocityTracker velocityTracker = mVelocityTracker;
                //1.1获取第一个触点
                final int pointerId = event.getPointerId(0);
                //1.2 计算当前的滑动速度
                velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
                //1.3 分别获取x，y轴方向的速度
                final float velocityX = velocityTracker.getXVelocity(pointerId);
                final float velocityY = velocityTracker.getYVelocity(pointerId);

                //1.4如果速度大于惯性滑动的触发速度就执行惯性滑动
                if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() || Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                    if (mCurrentTouchMode == DRAG) {
                        //1.5 先终止惯性滑动
                        stopDeceleration();
                        //1.6获取当前的动画时间，在动画开始前获取
                        mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();

                        //1.7 赋值惯性减速开始的位置和速度
                        mDecelerationCurrentPoint.x = event.getX();
                        mDecelerationCurrentPoint.y = event.getY();
                        mDecelerationVelocity.x = velocityX;
                        mDecelerationVelocity.y = velocityY;


                        //1.8 下面的代码会引起chart的computerScroll方法的调用，而在chart中的该方法会调用当前类的computerScroll方法
                        Utils.postInvalidateOnAnimation(mChart);
                    }
                }

                mCurrentTouchMode = NONE;
                mChart.enableScroll();

                //1.9 释放速度跟踪器
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                break;


            case MotionEvent.ACTION_POINTER_UP:

                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);

                mCurrentTouchMode = POST_ZOOM;
                break;

            case MotionEvent.ACTION_CANCEL:

                mCurrentTouchMode = NONE;
                break;
        }

        mOriginMatrix = mChart.getViewPortHandler().refresh(mOriginMatrix, mChart, true);


        return true;
    }


    public void stopDeceleration() {
        mDecelerationVelocity.x = 0;
        mDecelerationVelocity.y = 0;
    }

    private void saveTouchStart(MotionEvent event) {

        mSavedMatrix.set(mOriginMatrix);

        mTouchStartPoint.x = event.getX();
        mTouchStartPoint.y = event.getY();

        //TODO
//        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    private static float getXDist(MotionEvent e) {
        float x = Math.abs(e.getX(0) - e.getX(1));
        return x;
    }

    private static float getYDist(MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private static void midPoint(MPPointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.x = (x / 2f);
        point.y = (y / 2f);
    }

    private void performDrag(MotionEvent event, float distanceX, float distanceY) {

        mLastGesture = ChartGesture.DRAG;

        mOriginMatrix.set(mSavedMatrix);

//        Log.e("ChartTouchListener", "distanceX:" + distanceX + ",distanceY:" + distanceY);

        if(Math.abs(distanceX) >= Math.abs(distanceY)){
            mOriginMatrix.postTranslate(distanceX, 0);
        }else{
            mOriginMatrix.postTranslate(0, distanceY);
        }

//        mOriginMatrix.postTranslate(distanceX, distanceY);


//        Log.e("ChartTouchListener", "mOriginMatrix:" + mOriginMatrix.toShortString());


    }

    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float lastDragDistanceX,lastDragDistanceY;

    public void computeScroll() {
        //滑动的终止条件
        if (mDecelerationVelocity.x == 0.f && mDecelerationVelocity.y == 0.f) {
            return;
        }
        final long currentTime = AnimationUtils.currentAnimationTimeMillis();

        mDecelerationVelocity.x *= mChart.getDragDecelerationFrictionCoef();
        mDecelerationVelocity.y *= mChart.getDragDecelerationFrictionCoef();

        //1.计算当前时间与point up的时间差，除以1000ms，整个惯性时间是1s,注释要用float类型，不然int直接是0，滑不动了就
        final float timeInterval = (float) (currentTime - mDecelerationLastTime) / 1000.f;

        //2.计算本次移动的距离，每次加上mDecelerationCurrentPoint记录的坐标，就是移动后的坐标，然后手动创建一个MotionEvent
        float distanceX = mDecelerationVelocity.x * timeInterval;
        float distanceY = mDecelerationVelocity.y * timeInterval;

        mDecelerationCurrentPoint.x += distanceX;
        mDecelerationCurrentPoint.y += distanceY;

        MotionEvent event = MotionEvent.obtain(currentTime, currentTime, MotionEvent.ACTION_MOVE,
                mDecelerationCurrentPoint.x + distanceX, mDecelerationCurrentPoint.y + distanceY, 0);

        //计算总共的偏移量，而不是每次的偏移量，因为在performDrag中会每次重置mMatrix到mSavedMatrix
        float dragDistanceX = mChart.isDragXEnabled() ? mDecelerationCurrentPoint.x - mTouchStartPoint.x : 0.f;
        float dragDistanceY = mChart.isDragYEnabled() ? mDecelerationCurrentPoint.y - mTouchStartPoint.y : 0.f;

        if(Math.abs(lastDragDistanceX - dragDistanceX) < 5 && Math.abs(lastDragDistanceY - dragDistanceY) < 5){
            stopDeceleration();
            return;
        }

        if(mChart.isDragOnlySigleDirection()){
            if(Math.abs(dragDistanceX) >= Math.abs(dragDistanceY)){
                dragDistanceY = 0;
            }else{
                dragDistanceX = 0;
            }
        }



        Log.e("6========","dragDistanceX:"+dragDistanceX+",dragDistanceY:"+dragDistanceY);
        performDrag(event, dragDistanceX, dragDistanceY);

        event.recycle();

        // 注意此处不要刷新，因为要用postinvalidate
        mOriginMatrix = mChart.getViewPortHandler().refresh(mOriginMatrix, mChart, false);

        mDecelerationLastTime = currentTime;
        lastDragDistanceX = dragDistanceX;
        lastDragDistanceY = dragDistanceY;

        if (Math.abs(mDecelerationVelocity.x) >= 100 || Math.abs(mDecelerationVelocity.y) >= 100){

            Log.e("6========","mDecelerationVelocity.x:"+mDecelerationVelocity.x+",mDecelerationVelocity.y:"+mDecelerationVelocity.y);
            Utils.postInvalidateOnAnimation(mChart); // This causes computeScroll to fire, recommended for this by Google

        }
        else {
            //滑动之后，y轴可显示的rang的范围可能改变了，这时候需要重新计算

            mChart.calculateOffsets();
            mChart.postInvalidate();

            stopDeceleration();
        }
    }

    public MPPointF getTrans(float x, float y) {

        ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());

        return MPPointF.getInstance(xTrans, yTrans);
    }

    private void performZoom(MotionEvent event) {
        if (event.getPointerCount() >= 2) {

            float totalDist = spacing(event);
            //获得总共的移动
            MPPointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);

            ViewPortHandler h = mChart.getViewPortHandler();

            if (mCurrentTouchMode == PINCH_ZOOM) {
                mLastGesture = ChartGesture.PINCH_ZOOM;
                float scale = totalDist / mSavedDist;
                boolean isZoomingOut = (scale < 1);

                boolean canZoomMoreX = isZoomingOut ? h.canZoomOutMoreX() : h.canZoomInMoreX();
                boolean canZoomMoreY = isZoomingOut ? h.canZoomOutMoreY() : h.canZoomInMoreY();

                float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;
                if (canZoomMoreY || canZoomMoreX) {
                    mOriginMatrix.set(mSavedMatrix);
                    //已中心点缩放
                    mOriginMatrix.postScale(scaleX, scaleY, t.x, t.y);
                }
            }
            MPPointF.recycleInstance(t);
        }
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {


        Log.e("18========","event.x" + e.getX() + ",event.y:"+e.getY());
        Transformer transformer = mChart.getTransformer();


        MPPointD values = transformer.getValuesByTouchPoint(e.getX(),e.getY());

        Log.e("18========","values x:"+values.x + ",y:"+values.y);

        Highlight highlight = null;
        //点击的是标题部分
        if(e.getY() <= mChart.getTitleHeight() * mChart.getViewPortHandler().getScaleY()){

            Column column = mChart.getColumnByXValue(values.x);
            if(column != null){
                highlight = new Highlight(mChart,column.columnIndex,null,true);
            }
            Log.e("18========",column != null ? column.columnName : "empty column");
        }else{

            ICell cell = mChart.getCellByTouchPoint(values.x,values.y);
            if(cell != null){
                highlight = new Highlight(mChart,cell.getColumn(),cell,false);
                Log.e("18========",cell != null ? cell.getContents() : "click cell");

            }
            Log.e("18========",cell != null ? cell.getContents() : "empty cell");
        }

        //点击的是cell部分

        performHighlight(highlight,e);


        //如果出现了重叠，那就以优先fixedColumn

        return super.onSingleTapUp(e);
    }


    protected void performHighlight(Highlight h, MotionEvent e){

        if(h == null || h.equalTo(mHighlight)){
            mHighlight = null;
            mChart.highlightValue(null,true);

        }else{
            mHighlight = h;
            mChart.highlightValue(h,true);
        }
    }
}
