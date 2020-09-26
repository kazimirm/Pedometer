package com.kazimir.pedometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * TYPE_STEP_COUNTER documentation: https://developer.android.com/reference/android/hardware/Sensor.html#TYPE_STEP_COUNTER
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    TextView tv_steps, tv_distance;
    DatabaseHandler db;
    BarChart barChart;

    int stepCounter, since_boot, todayOffset, todaySteps;
    String todaysDate;

    final int DAY_MIN = 10000;
    final int STEP_SIZE = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCounter = 0;
        tv_steps = (TextView) findViewById(R.id.tv_steps);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        db = DatabaseHandler.getInstance(this);

        createGraph();

    }

    /**
     * Method for setting all graph parameters and values
     */
    private void createGraph() {
        barChart = (BarChart) findViewById(R.id.graph);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        //remove this if You want to run with real values
        insertDummyValuesToDb();

        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<String> days = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {

            int steps = db.getStepCountForDay(getDateForDayMinusN(i + 1, "yyyyMMdd"));
            BarEntry barEntry = new BarEntry(i, steps);

            barEntries.add(barEntry);
            days.add(getDateForDayMinusN(i + 1, "dd.MM"));

            colors.add(steps >= DAY_MIN ? Color.GREEN : Color.RED);
        }

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

    private String getDateForDayMinusN(int n, String format) {
        String date = new SimpleDateFormat(format).format(dayMinusN(n));
        return date;
    }

    private Date dayMinusN(int n) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -n);
        return cal.getTime();
    }

    /**
     * Method for testing purposes - inserts random values for past days to demonstrate beauty of graph
     */
    private void insertDummyValuesToDb() {
        for (int i = 1; i <= 7; i++) {
            Random random = new Random();
            DayData dayData = new DayData(getDateForDayMinusN(i, "yyyyMMdd"), random.nextInt(20000));
            db.updateDayDataOrCreate(dayData);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found!¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.updateDayDataOrCreate(new DayData(todaysDate, todaySteps));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        since_boot = (int) event.values[0];
        todaySteps = db.getStepCountForDay(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        if (todaySteps == 0) {
            todayOffset = since_boot;
            todaysDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            todaySteps++;
        } else {
            todaySteps = since_boot - todayOffset;
            tv_steps.setText(String.valueOf(todaySteps));
            tv_distance.setText(String.format("%.1f km", getDistance(todaySteps)));
        }
    }

    private float getDistance(int steps) {
        float distance = steps * STEP_SIZE;
        return (distance / 100000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
