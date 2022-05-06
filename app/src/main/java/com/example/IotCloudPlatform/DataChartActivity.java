package com.example.IotCloudPlatform;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.IotCloudPlatform.tools.DataBaseHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DataChartActivity extends Activity {
    DataBaseHelper dataBaseHelper;
    private LineChart lineChart;
    protected Typeface typeface;
    private ArrayList<Entry> sensorData = new ArrayList<>();
    private String dataType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Intent intent = getIntent();
        dataType = intent.getStringExtra("type");
        dataBaseHelper = new DataBaseHelper(this);
        lineChart = findViewById(R.id.lineChart);
        Description description = new Description();
        description.setText("折线统计图");
        lineChart.setDescription(description);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setData(getLineData());
        lineChart.setScaleEnabled(false); //Xy 轴禁止缩放
        Legend legend = lineChart.getLegend(); //设置图例样式
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTypeface(typeface);
        legend.setTextSize(11f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        lineChart.animateX(3000);//数据显示动画
    }

    private LineData getLineData() {
        getChartData(20);
        LineDataSet dataSet = new LineDataSet(sensorData, dataType);
        dataSet.setColor(Color.BLUE);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setDrawCircleHole(true);
        LineData data = new LineData(dataSet);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);
        return data;
    }

    private void getChartData(int count) {
        List<Float> lists;
        lists = dataBaseHelper.search(DataChartActivity.this, dataType);
        if (count > lists.size()) {
            for (int i = 0; i < lists.size(); i++) {
                Entry tempEntry = new Entry(i, lists.get(i));
                sensorData.add(tempEntry);
            }
        } else {
            for (int i = 0; i < count; i++) {
                Entry tempEntry = new Entry(i, lists.get(i));
                sensorData.add(tempEntry);
            }
        }
    }
}