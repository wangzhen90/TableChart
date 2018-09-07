package com.wangzhen.tablechartlib.buffer;

import java.util.List;

/**
 * Created by wangzhen on 2018/4/30.
 *
 * 提高绘制性能的缓冲类，概念：替换而不是重新创建
 */

public abstract class AbstractBuffer<T>  {

    protected int index = 0;
    /**
     * 包含所有要绘制的points，比如：x,y,x,y...
     */
    public final float[] buffer;

    /**
     * x-axis的动画阶段
     */
    protected float phaseX = 1f;
    /**
     * y-axis的动画阶段
     */
    protected float phaseY = 1f;
    /**
     * x-axis 开始绘制的index
     */
    protected int mFrom = 0;

    protected int mTo =0;

    public AbstractBuffer(int size){

        index = 0;
        buffer = new float[size];
    }

    /** limits the drawing on the x-axis */
    public void limitFrom(int from) {
        if (from < 0)
            from = 0;
        mFrom = from;
    }

    /** limits the drawing on the x-axis */
    public void limitTo(int to) {
        if (to < 0)
            to = 0;
        mTo = to;
    }

    /**
     * Resets the buffer index to 0 and makes the buffer reusable.
     */
    public void reset() {
        index = 0;
    }

    /**
     * Returns the size (length) of the buffer array.
     *
     * @return
     */
    public int size() {
        return buffer.length;
    }


    /**
     * Set the phases used for animations.
     * 设置动画进行的阶段
     *
     * @param phaseX
     * @param phaseY
     */
    public void setPhases(float phaseX, float phaseY) {
        this.phaseX = phaseX;
        this.phaseY = phaseY;
    }

    /**
     * Builds up the buffer with the provided data and resets the buffer-index
     * after feed-completion. This needs to run FAST.
     *
     使用提供的数据构建缓冲区，并在完成反馈后重置缓冲区索引为0。这需要运行的很快
     *
     * @param data
     */
    public abstract void feed(T data);
    public  void feed(T data, List<T> dataList){

    }

    public  void feed(T data, boolean sorted){

    }


}
