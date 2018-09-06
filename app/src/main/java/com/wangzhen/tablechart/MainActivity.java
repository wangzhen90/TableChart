package com.wangzhen.tablechart;

import android.content.Intent;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wangzhen.tablechart.others.ChartItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ChartItem> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        initData();

        listView.setAdapter(new DataAdapter());
    }

    void initData(){


        items.add(new ChartItem("简单表格（测试大量数据）",SimpleTableActivity.class));
        items.add(new ChartItem("测试格式化",FormatterActivity.class));
        items.add(new ChartItem("测试合并单元格",MergedActivity.class));
        items.add(new ChartItem("测试列固定",FixedColumnActivity.class));
        items.add(new ChartItem("测试排序",SortActivity.class));


    }


    class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ChartItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View contextView, ViewGroup viewGroup) {

            if(contextView == null){
                contextView = View.inflate(MainActivity.this,R.layout.layout_chart_item,null);
            }
            final ChartItem item = getItem(i);
            ((TextView)contextView.findViewById(R.id.textView)).setText(item.name);
            contextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,item.clazz);
                    startActivity(intent);
                }
            });

            return contextView;
        }
    }

}
