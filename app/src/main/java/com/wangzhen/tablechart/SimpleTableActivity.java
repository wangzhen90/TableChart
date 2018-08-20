package com.wangzhen.tablechart;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Toast;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.Sheet;
import com.wangzhen.tablechartlib.formatter.IBgFormatter;
import com.wangzhen.tablechartlib.formatter.ITextFormatter;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.interfaces.ITableOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class SimpleTableActivity extends AppCompatActivity {



    TableChart tableChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_table);

        tableChart = findViewById(R.id.tableView);
        tableChart.setOnClickListener(new ITableOnClickListener() {
            @Override
            public void onColumnClick(Column column) {
                Toast.makeText(SimpleTableActivity.this, "点击了" + column.columnName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCellClick(ICell cell) {
                Toast.makeText(SimpleTableActivity.this, "点击了" + cell.getContents(), Toast.LENGTH_SHORT).show();
            }
        });


        initData();
    }


    void initData() {

        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
//            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i,(i==1 || i==10) ? true : false);
            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i);

            columns.add(column);
        }

        List<ICell> sumCells = new ArrayList<>();
        for (int i = 0; i < 20; i++) {

            sumCells.add(new Cell(-1,i,(i+1000)+""));

        }


        for (int i = 0; i < columns.size(); i++) {
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                cells.add(new Cell(j, i, "内容" + i + "-" + j));
            }

            columns.get(i).setData(cells);
        }

//        Sheet<Cell> sheet = new Sheet<>(columns, null);
        Sheet<Cell> sheet = new Sheet<>(columns, null,sumCells);

//        sheet.merge(0, 0, 2, 2);
//        sheet.merge(5, 0, 5, 1);
//        sheet.merge(6, 2, 7, 3);

        sheet.setTextFormatter(new ITextFormatter() {
            @Override
            public int getTextSize(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                return 9;
            }

            @Override
            public TextPaint.Align getTextAlign(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
                if (cell.getColumn() == 1) {
                    return TextPaint.Align.RIGHT;
                } else {
                    return TextPaint.Align.CENTER;
                }
            }

            @Override
            public String getTextColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {

                if(cell.getContents().equals("内容1-3")){
                    return "red";
                }

                return "#4D4D4D";
            }
        });


        sheet.setBgFormatter(new IBgFormatter() {
            @Override
            public String getContentBackgroundColor(ICell cell, Column<ICell> column, List<Column<ICell>> columns) {
                return null;
            }

            @Override
            public String getTitleBackgroundColor() {
                return "#F0F0F0";
            }
        });


        tableChart.setHighlightColor(Color.parseColor("#4558C9"));
        tableChart.setShowSum(true);

        tableChart.setSheet(sheet);

    }
}
