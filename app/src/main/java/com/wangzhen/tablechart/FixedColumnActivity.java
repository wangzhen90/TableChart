package com.wangzhen.tablechart;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.Sheet;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FixedColumnActivity extends AppCompatActivity {
    TableChart tableChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_column);
        tableChart = findViewById(R.id.tableView);
        initData();
    }

    void initData() {

        final int divideIndex = 1;

        final List<Column> columns = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i);
            //设置列是否固定
            if(i == 2 || i == 6) column.setFixed(true);

            column.setTitleTextAlign(Paint.Align.CENTER);

            columns.add(column);
        }

        List<ICell> sumCells = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i == 0) {
                sumCells.add(new Cell(-1, i, "总计"));
            }
            sumCells.add(new Cell(-1, i, "客户" + i));
        }

        for (int i = 0; i < columns.size(); i++) {
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < 300; j++) {
                Cell cell = new Cell(j, i, "客户" + i + "-" + j);
                cells.add(cell);
            }

            columns.get(i).setData(cells);
        }

        Sheet<Cell> sheet = new Sheet<>(columns, Utils.measureViewWidth(tableChart), sumCells);


        tableChart.setHighlightColor(Color.parseColor("#4558C9"));
        tableChart.setShowSum(true);

        tableChart.setSheet(sheet);

    }
}
