package com.kazimir.pedometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

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
    final int DAY_MIN = 10000;

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
        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i <= 6; i++){
            int steps = db.getStepCountForDay(getDateForDayMinusN(i + 1, "yyyyMMdd"));
            BarEntry barEntry = new BarEntry( i, steps);
            barEntries.add(barEntry);
            colors.add(steps >= DAY_MIN ? Color.GREEN : Color.RED);
        }

        ArrayList<String> days = new ArrayList<>();
        days.add(getDateForDayMinusN(7,"dd.MM"));
        days.add(getDateForDayMinusN(6,"dd.MM"));
        days.add(getDateForDayMinusN(5,"dd.MM"));
        days.add(getDateForDayMinusN(4,"dd.MM"));
        days.add(getDateForDayMinusN(3,"dd.MM"));
        days.add(getDateForDayMinusN(2,"dd.MM"));
        days.add(getDateForDayMinusN(1,"dd.MM"));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Steps");
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(10);
        barDataSet.setColors(colors);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LimitLine ll = new LimitLine(DAY_MIN);
        ll.setLineWidth(4f);
        ll.setLineColor(Color.GREEN);
        barChart.getAxisLeft().addLimitLine(ll);
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
        barChart.getXAxis().setTextColor(Color.WHITE);

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
            DayData dayData = new DayData(getDateForDayMinusN(i, "yyyyMMdd"), random.nextInt(20000));
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
