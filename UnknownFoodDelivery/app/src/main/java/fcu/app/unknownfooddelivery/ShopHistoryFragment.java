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
  LineChartData lineChartData;
  LineChart lineChart;
  ArrayList<String> xData = new ArrayList<>();
  ArrayList<Entry> yData = new ArrayList<>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_shop_history, container, false);
    lineChart = (LineChart) view.findViewById(R.id.line_chart);
    lineChartData = new LineChartData(lineChart, getActivity());

    for(int i = 0; i < 10; i++){
      xData.add("第" + i + "筆");
      yData.add(new Entry(i-1, i));
    }
    lineChartData.initX(xData);
    lineChartData.initY(0F,10F);
    lineChartData.initDataSet(yData);
    // Inflate the layout for this fragment
    return view;
  }


}