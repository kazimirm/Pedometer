package com.kazimir.pedometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    TextView tv_steps;
    DatabaseHandler db;
    boolean moving = false;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_steps = (TextView) findViewById(R.id.tv_steps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        db = DatabaseHandler.getInstance(this);

        createGraph();

    }

    private void createGraph(){
        barChart = (BarChart) findViewById(R.id.graph);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        insertDummyValuesToDb();
        for (int i = 0; i <= 6; i++){
            barEntries.add(new BarEntry(i,db.getStepCountForDay(getDateForDayMinusN(i + 1, "yyyyMMdd"))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Steps");
        barDataSet.setValueTextColor(getResources().getColor(android.R.color.white));

        ArrayList<String> days = new ArrayList<>();
        days.add(getDateForDayMinusN(7,"dd.MM"));
        days.add(getDateForDayMinusN(6,"dd.MM"));
        days.add(getDateForDayMinusN(5,"dd.MM"));
        days.add(getDateForDayMinusN(4,"dd.MM"));
        days.add(getDateForDayMinusN(3,"dd.MM"));
        days.add(getDateForDayMinusN(2,"dd.MM"));
        days.add(getDateForDayMinusN(1,"dd.MM"));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);

        barChart.setTouchEnabled(true);
        barChart.setClickable(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barChart.setDrawBorders(false);
        barChart.setDrawGridBackground(false);


        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisLeft().setDrawAxisLine(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getXAxis().setTextColor(getResources().getColor(android.R.color.white));

        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawAxisLine(false);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

    }


    private String getDateForDayMinusN(int n, String format){
        String date = new SimpleDateFormat(format).format(dayMinusN(n));
        return date;
    }

    private Date dayMinusN(int n) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -n);
        return cal.getTime();
    }

    private void insertDummyValuesToDb(){
        for(int i = 1; i <= 7; i++){
            Random random = new Random();
            DayData dayData = new DayData(getDateForDayMinusN(i, "yyyyMMdd"), random.nextInt(30000));
            db.updateDayDataOrCreate(dayData);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        moving = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        moving = false;
        //this will stop detecting steps
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (moving){
            int dailySteps = (int) event.values[0];
            tv_steps.setText(String.valueOf(dailySteps));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
