package com.wangzhen.tablechart;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.Sheet;
import com.wangzhen.tablechartlib.formatter.BiColorFormatSet;
import com.wangzhen.tablechartlib.formatter.BiFormatSet;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;
import com.wangzhen.tablechartlib.formatter.IValueFormatter;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.MultiColorFormatUtils;
import com.wangzhen.tablechartlib.utils.MultiNumberFormatUtils;
import com.wangzhen.tablechartlib.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FormatterActivity extends AppCompatActivity {
    TableChart tableChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formatter);

        tableChart = findViewById(R.id.tableView);
        initData();

    }


    void initData() {

        final int divideIndex = 1;

        final List<Column> columns = new ArrayList<>();

        //数字格式化
        final BiFormatSet biFormatSet = new BiFormatSet();

        biFormatSet.unitSet = "K";

        biFormatSet.decimalDigitsNum = 2;

        biFormatSet.formatType = "commonValue";

        biFormatSet.positiveSign = 1;

        biFormatSet.thousandsSeparator = 1;


        final MultiNumberFormatUtils multiNumberFormatter = new MultiNumberFormatUtils(biFormatSet);

        for (int i = 0; i < 10; i++) {
            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i);
            //设置标题的TextAlign
            column.setTitleTextAlign(i < divideIndex ? Paint.Align.LEFT : Paint.Align.RIGHT);
            columns.add(column);
        }

        Random random = new Random();

        List<ICell> sumCells = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sumCells.add(new Cell(-1, i, (i ) + (i == 1 ? "哈哈哈哈hahah" : "")));
        }


        for (int i = 0; i < columns.size(); i++) {
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < 200; j++) {

                Cell cell = new Cell(j, i, (j+ random.nextInt(10000)) + "");
                /**
                 * 如果cell的内容格式化后变化比较大，需要先将数据在后台格式化好，否则可能影响列宽的计算
                 */
                cell.setFormatValue(multiNumberFormatter.getFormattedValue(cell, null));

                cells.add(cell);
            }

            columns.get(i).setData(cells);
        }

        Sheet<Cell> sheet = new Sheet<>(columns, Utils.measureViewWidth(tableChart), sumCells);

        //设置单元格的字体颜色和背景，可以添加多项，后面的优先级高于前面
        List<BiColorFormatSet> colorSets = new ArrayList<>();

        colorSets.add(new BiColorFormatSet(MultiColorFormatUtils.OPERATOR_GREAER_THAN, Color.parseColor("blue"),
                Color.parseColor("#E15E62"), 9000, 0));

        colorSets.add(new BiColorFormatSet(MultiColorFormatUtils.OPERATOR_RANGE, Color.parseColor("red"),
                Color.parseColor("#FF8106"), 500, 800));


        colorSets.add(new BiColorFormatSet(MultiColorFormatUtils.OPERATOR_LESS_THAN_OR_EQUAL, Color.parseColor("cyan"),
                Color.parseColor("#4558C9"), 600, 0));


        final MultiColorFormatUtils colorFormatUtils = new MultiColorFormatUtils(colorSets, Color.parseColor("#4D4D4D"), Color.TRANSPARENT);

        //设置TextFormatter
        sheet.setTextFormatter(new ITextFormatter() {
            @Override
            public int getTextSize(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                return 9;
            }

            @Override
            public TextPaint.Align getTextAlign(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
                if (cell.getColumn() >= 1) {
                    return TextPaint.Align.RIGHT;
                } else {
                    return TextPaint.Align.LEFT;
                }
            }

            @Override
            public int getTextColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                return colorFormatUtils.getTextColor(cell, column);
            }
        });

        //设置背景颜色
        sheet.setBgFormatter(new IBgFormatter() {
            @Override
            public int getContentBackgroundColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                if (column.columnIndex >= 1)
                    return colorFormatUtils.getTextBgColor(cell, column);

                return Color.parseColor("#00ffffff");
            }

            @Override
            public int getTitleBackgroundColor() {
                return Color.parseColor("#aaF0F0F0");
            }
        });


        //设置数字格式化
        sheet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                if (column.columnIndex >= divideIndex) {
                    return multiNumberFormatter.getFormattedValue(cell, column);
                } else {
                    return cell.getContents();
                }
            }
        });


        tableChart.setHighlightColor(Color.parseColor("#4558C9"));
        tableChart.setShowSum(true);

        tableChart.setSheet(sheet);

    }
}
