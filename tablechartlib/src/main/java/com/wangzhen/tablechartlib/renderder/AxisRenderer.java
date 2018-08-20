package com.wangzhen.tablechartlib.renderder;


import android.graphics.Paint;

import com.wangzhen.tablechartlib.utils.ViewPortHandler;

/**
 * Created by wangzhen on 2018/6/28.
 */

public class AxisRenderer extends Renderer {
    protected Paint mValuePaint;

    protected Paint mGridPaint;

    public AxisRenderer(ViewPortHandler viewPortHandler) {
        super(viewPortHandler);
    }
}
