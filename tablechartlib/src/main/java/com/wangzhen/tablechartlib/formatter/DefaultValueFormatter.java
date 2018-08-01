
package com.wangzhen.tablechartlib.formatter;


import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 *
 * @author Philipp Jahoda
 */
public class DefaultValueFormatter implements IValueFormatter
{

    /**
     * DecimalFormat for formatting
     */
    protected DecimalFormat mFormat;

    protected int mDecimalDigits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public DefaultValueFormatter(int digits) {
        setup(digits);
    }

    /**
     * Sets up the formatter with a given number of decimal digits.
     *
     * @param digits
     */
    public void setup(int digits) {

        this.mDecimalDigits = digits;

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }


    /**
     * Returns the number of decimal digits this formatter uses.
     *
     * @return
     */
    public int getDecimalDigits() {
        return mDecimalDigits;
    }

    @Override
    public String getFormattedValue(float value, Cell entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return null;
    }
}