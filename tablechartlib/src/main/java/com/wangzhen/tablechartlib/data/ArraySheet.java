package com.wangzhen.tablechartlib.data;

import com.wangzhen.tablechartlib.interfaces.ICell;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangzhen on 2018/7/6.
 */

public class ArraySheet<T extends Cell> extends Sheet<T> {
    public ArraySheet(List columns, int viewWidth) {
        super(columns, viewWidth);
    }


//    public ArraySheet(String tableName, List<T> dataList, List columns) {
//        super(columns,dataList);
//
//
//    }




    /**
     * [column][row] => [row][column]
     *
     * @param rowArray
     * @param <T>
     * @return
     */
    public static <T> T[][] transformColumnArray(T[][] rowArray) {
        T[][] columnArray = null;

        T[] row = null;

        if (rowArray != null) {
            int maxLength = 0;

            for (T[] childRow : rowArray) {
                if (childRow != null && childRow.length > maxLength) {
                    maxLength = childRow.length;
                    row = childRow;
                }
            }

            if (row != null) {

                columnArray = (T[][]) Array.newInstance(row.getClass().getComponentType(), maxLength);

                for (int i = 0; i < rowArray.length; i++) {

                    for (int j = 0; j < rowArray[i].length; j++) {
                        if (columnArray[j] == null) {
                            columnArray[j] = (T[]) Array.newInstance(row.getClass().getComponentType(), rowArray.length);
                        }
                        columnArray[j][i] = rowArray[i][j];
                    }
                }
            }
        }
        return columnArray;
    }



    public void setData(T[][] data, boolean needTransform) {

        if (needTransform) {
            setData(transformColumnArray(data));
        } else {
            setData(data);
        }
    }

    //创建有title的data
//    public static <T> ArraySheet<T> createData(String tableName, String[] titleNames, T[][] data) {
//        ArrayList<Column<T>> columns = new ArrayList<>();
//
//        for (int i = 0; i < data.length; i++) {
//            T[] dataArray = data[i];
//            Column<T> column = new Column<>(titleNames[i]);
//            column.setData(Arrays.asList(dataArray));
//            columns.add(column);
//        }
//        ArrayList<T> arrayList = new ArrayList<>(Arrays.asList(data[0]));
//        ArraySheet<T> sheet = new ArraySheet<>(tableName, arrayList);
//        sheet.setData(data);
//
//        return sheet;
//    }

    //创建无title的data
//    public static <T> ArraySheet<T> createData(String tableName, T[][] data) {
//
//        return createData(tableName, null, data);
//    }

    //TODO 2.解析转化后的数据，填充childColumns (参考 TableParser)





}
