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

public class MergedActivity extends AppCompatActivity {

    TableChart tableChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merged);
        tableChart = findViewById(R.id.tableView);

        initData();
    }

    void initData() {

        final int divideIndex = 1;
        final List<Column> columns = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i);
            column.setTitleTextAlign(i < divideIndex ? Paint.Align.LEFT : Paint.Align.RIGHT);
            columns.add(column);
        }

        List<ICell> sumCells = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sumCells.add(new Cell(-1, i, (i + 1000) + (i == 1 ? "哈哈哈哈hahah" : "")));
        }


        for (int i = 0; i < columns.size(); i++) {
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < 20; j++) {

                Cell cell = new Cell(j, i, (j * 200) + (i == 0 ? "哈哈哈哈hahah" : ""));

                cells.add(cell);
            }

            columns.get(i).setData(cells);
        }

//        Sheet<Cell> sheet = new Sheet<>(columns, sumCells);
        Sheet<Cell> sheet = new Sheet<>(columns, null);

        sheet.merge(0, 0, 2, 2);
        sheet.merge(5, 0, 5, 1);
        sheet.merge(6, 2, 7, 3);

        tableChart.setHighlightColor(Color.parseColor("#4558C9"));
        tableChart.setShowSum(false);

        tableChart.setSheet(sheet);

    }
}
