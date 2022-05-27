package fcu.app.unknownfooddelivery;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopHistoryFragment extends Fragment {

  LineChart lineChart;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
    lineChart = view.findViewById(R.id.line_chart);

    ArrayList<Entry> values = new ArrayList<>();
    values.add(new Entry(1, 30.65f));
    values.add(new Entry(1.3f, 30.69f));
    values.add(new Entry(5.8f, 30.58f));
    values.add(new Entry(6, 30.58f));

    ArrayList<Entry> values_end = new ArrayList<>();
    values_end.add(new Entry(6, 30.58f));

    // Inflate the layout for this fragment
    return view;
  }

  private void initDataSet(final ArrayList<Entry> values, ArrayList<Entry> values1, ArrayList<Entry> values_end, ArrayList<Entry> values1_end) {
    final LineDataSet set,  set_end;
    // greenLine
    set = new LineDataSet(values, "");
    set.setMode(LineDataSet.Mode.LINEAR);//類型為折線
    //   set.setColor(getResources().getColor(R.color.green));//線的顏色
    set.setLineWidth(1.5f);//線寬
    set.setDrawCircles(false); //不顯示相應座標點的小圓圈(預設顯示)
    set.setDrawValues(false);//不顯示座標點對應Y軸的數字(預設顯示)

//greenLine最後的圓點
    set_end = new LineDataSet(values_end, "");
    set_end.setCircleColor(ContextCompat.getColor(getContext(), R.color.black));//圓點顏色
    set_end.setColor(ContextCompat.getColor(getContext(), R.color.black));//線的顏色
    set_end.setCircleRadius(4);//圓點大小
    set_end.setDrawCircleHole(false);//圓點為實心(預設空心)
    set_end.setDrawValues(false);//不顯示座標點對應Y軸的數字(預設顯示)

    /**
     * yellowLine及其最後的圓點設定可比照如上greenLine設定，不再列示
     */

//理解爲多條線的集合
    LineData data = new LineData(set, set_end);
    lineChart.setData(data);//一定要放在最後
    lineChart.invalidate();//繪製圖表
  }

  private void initX() {
    XAxis xAxis = lineChart.getXAxis();

    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X軸標籤顯示位置(預設顯示在上方，分為上方內/外側、下方內/外側及上下同時顯示)
    xAxis.setTextColor(Color.GRAY);//X軸標籤顏色
    xAxis.setTextSize(12);//X軸標籤大小

    xAxis.setLabelCount(6);//X軸標籤個數
    xAxis.setSpaceMin(0.5f);//折線起點距離左側Y軸距離
    xAxis.setSpaceMax(0.5f);//折線終點距離右側Y軸距離

    xAxis.setDrawGridLines(false);//不顯示每個座標點對應X軸的線 (預設顯示)

    //設定所需特定標籤資料
    String[] xValue = new String[]{"", "1/3", "1/10", "1/17", "1/24", "1/31", "2/7"};
    List<String> xList = new ArrayList<>();
    for (int i = 0; i < xValue.length; i++) {
      xList.add(xValue[i]);
//            xList.add(String.valueOf(i +1).concat("月"));
    }

    xAxis.setValueFormatter(new IndexAxisValueFormatter(xList));
  }
  private void initY() {
    YAxis rightAxis = lineChart.getAxisRight();//獲取右側的軸線
    rightAxis.setEnabled(false);//不顯示右側Y軸
    YAxis leftAxis = lineChart.getAxisLeft();//獲取左側的軸線

    leftAxis.setLabelCount(4);//Y軸標籤個數
    leftAxis.setTextColor(Color.GRAY);//Y軸標籤顏色
    leftAxis.setTextSize(12);//Y軸標籤大小

    leftAxis.setAxisMinimum(30.5f);//Y軸標籤最小值
    leftAxis.setAxisMaximum(30.9f);//Y軸標籤最大值

    String[] yValue = new String[]{"", "1/3", "1/10", "1/17", "1/24", "1/31", "2/7"};
    List<String> yList = new ArrayList<>();
    for (int i = 0; i < yValue.length; i++) {
      yList.add(yValue[i]);
//            xList.add(String.valueOf(i +1).concat("月"));
    }

    leftAxis.setValueFormatter(new IndexAxisValueFormatter(yList));
  }

  private void initChartFormat() {
    //右下方description label：設置圖表資訊
    Description description = lineChart.getDescription();
    description.setEnabled(false);//不顯示Description Label (預設顯示)

    //左下方Legend：圖例數據資料
    Legend legend = lineChart.getLegend();
    legend.setEnabled(false);//不顯示圖例 (預設顯示)

    lineChart.setBackgroundColor(Color.WHITE);//顯示整個圖表背景顏色 (預設灰底)

    //設定沒資料時顯示的內容
    lineChart.setNoDataText("暫時沒有數據");
    lineChart.setNoDataTextColor(Color.BLUE);//文字顏色
  }
}