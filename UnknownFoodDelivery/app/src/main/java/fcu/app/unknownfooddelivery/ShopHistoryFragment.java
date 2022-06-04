package fcu.app.unknownfooddelivery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ShopHistoryFragment extends Fragment {
  BarChartData lineChartData;
  BarChart lineChart;
  ArrayList<String> xData = new ArrayList<>();
  ArrayList<BarEntry> yData = new ArrayList<>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_shop_history, container, false);
    lineChart = (BarChart) view.findViewById(R.id.bar_chart);
    lineChartData = new BarChartData(lineChart, getActivity());

    for(int i = 0; i < 10; i++){
      xData.add("第" + i + "筆");
      yData.add(new BarEntry(i-1, i));
    }
    lineChartData.initX(xData);
    lineChartData.initY(0F,10F);
    lineChartData.initDataSet(yData);
    // Inflate the layout for this fragment
    return view;
  }


}