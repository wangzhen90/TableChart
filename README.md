# TableChart
类似Excel的自定义表格控件，继承ViewGroup，代码思路借鉴于MPAndroidChart，支持缩放，横竖向滑动，合并单元格，列固定，标题固定，底部总计，自定义样式，列升序/降序，自定义点击事件等，现在只是一个demo版本，后续优化后可用于项目开发。
主要功能点如下,放了几张gif图，有点模糊，将就着看看：
#### 1.大数据量测试
以小米6，3000 * 20 的数据量测试，滑动流畅:

![image.png](https://github.com/wangzhen90/TableChart/blob/master/demoGif/%E5%A4%A7%E9%87%8F%E5%88%97.gif)
#### 2.列固定

![image.png](https://github.com/wangzhen90/TableChart/blob/master/demoGif/%E5%88%97%E5%9B%BA%E5%AE%9A.gif)

#### 3.格式化
支持单位，千分位，小数点等数字格式化，支持阈值设置。

![image.png](https://github.com/wangzhen90/TableChart/blob/master/demoGif/%E6%A0%BC%E5%BC%8F%E5%8C%96.gif)
#### 4.列排序
点击某一列标题，第一次点击为升序，第二次点击为降序，第三次点击恢复无序状态。

![image.png](https://github.com/wangzhen90/TableChart/blob/master/demoGif/%E6%8E%92%E5%BA%8F.gif)
#### 5.合并单元格
现在只做了内容的合并单元格，标题栏的合并单元格还未添加，以后有时间会添加上。

#### 6.列宽的设置
现在的demo版本只支持列宽自适应，未做文字长度过长的换行处理，以后会加上。
