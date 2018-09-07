package com.wangzhen.tablechart;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wangzhen.tablechartlib.component.TableChart;
import com.wangzhen.tablechartlib.data.Cell;
import com.wangzhen.tablechartlib.data.Column;
import com.wangzhen.tablechartlib.data.Sheet;
import com.wangzhen.tablechartlib.interfaces.ICell;
import com.wangzhen.tablechartlib.interfaces.ITableOnClickListener;
import com.wangzhen.tablechartlib.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SortActivity extends AppCompatActivity {

    TableChart tableChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);


        tableChart = findViewById(R.id.tableView);

        tableChart.setOnClickListener(new ITableOnClickListener() {
            @Override
            public void onColumnClick(Column column) {
//                Toast.makeText(SortActivity.this, "点击了" + column.columnName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCellClick(ICell cell) {
//                Toast.makeText(SortActivity.this, "点击了" + cell.getContents(), Toast.LENGTH_SHORT).show();
            }
        });

        initData();

    }


    void initData() {


        final List<Column> columns = new ArrayList<>();

        int columnCounts = 20;
        int rowCounts = 300;

        String[][] rawDataArray = new String [columnCounts][rowCounts];


        for (int i = 0; i < columnCounts; i++) {
            Column column = new Column(i == 1 ? "标题比较长比较长比较长" + i : "标题" + i);
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

        Random random = new Random();

        for (int i = 0; i < columns.size(); i++) {
            List<Cell> cells = new ArrayList<>();
            for (int j = 0; j < rowCounts; j++) {

                String cellString;

                if(i == 0){
                    if(i % 2 == 0){
//                        cellString = "aa" + i + "-" + j;
                        cellString = "aa"  + "-" + j;
                    }else{
//                        cellString = "mm" + i + "-" + j;
                        cellString = "mm"  + "-" + j;
                    }
                }else if(i == 2){
//                    cellString = random.nextInt(10000000)+"";
                    cellString = j+"";
                }else{
//                    cellString = "客户" + i + "-" + j;
                    cellString = "客户" + "-" + j;
                }


                cells.add(new Cell(j,i,cellString));

            }

            columns.get(i).setData(cells);
        }

        Sheet<Cell> sheet = new Sheet<>(columns, Utils.measureViewWidth(tableChart), sumCells);


        tableChart.setHighlightColor(Color.parseColor("#4558C9"));
        tableChart.setShowSum(true);
        tableChart.setSortable(true);

        tableChart.setSheet(sheet);



//        Arrays.sort();
//
//        Collections.sort();


        //一个列排序之后，联动其他的列，列宽等不需要再计算，可以重用Cell

        //或者就是原来的columns数据不变，某一列数据改变之后会创建一个正序columns  和 一个倒叙columns，不行，这样会影响共用的Cells


        //可行方案：某一列排序，然后循环此列，然后修改Cell的column，其他的List不停的对调cell，或者干脆每列新建一个List，再把原来的List清空
        //可以弄一个ListBuffer，List清空之后不销毁

        //需要维护一个AsyncTask，然后排序的时候loading

    }


}
