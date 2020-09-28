package com.kazimir.pedometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.kazimir.pedometer.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * TYPE_STEP_COUNTER documentation: https://developer.android.com/reference/android/hardware/Sensor.html#TYPE_STEP_COUNTER
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    DatabaseHandler database;
    SharedPreferences preferences;
    ActivityMainBinding binding;

    private int todayOffset;
    private int todaySteps;
    private String todaysDate;

    private int stepLength;


    final int DAY_MIN = 10000;
    final int STEP_LENGTH_DEFAULT = 70;
    final int DAYS_IN_STATISTICS = 7;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("settings", 0);
        stepLength = preferences.getInt("stepLength", STEP_LENGTH_DEFAULT);

        if (stepLength != STEP_LENGTH_DEFAULT){
            SharedPreferences.Editor edit= preferences.edit();
            edit.putInt("stepLength", stepLength);
            edit.commit();
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        database = DatabaseHandler.getInstance(this);
        todaySteps = database.getStepCountForDay(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        createGraph();
        binding.textViewSteps.setText(String.valueOf(todaySteps));

        binding.textViewDistance.setText(String.format("%.1f km", getDistance(todaySteps)));

        binding.buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        binding.buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStats();
            }
        });


    }

    private void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openStats(){
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    /**
     * Method for setting all graph parameters and values
     */
    private void createGraph() {
        BarChart barChart = (BarChart) findViewById(R.id.graph);
        addDataToGraph(barChart);
        formatGraph(barChart);
    }

    private void addDataToGraph(BarChart barChart){

        List<BarEntry> barEntries = new ArrayList<>();

        //remove this if You want to run with real values
        insertDummyValuesToDb();
        //

        List<Integer> colors = new ArrayList<>();
        List<String> days = new ArrayList<>();
        for (int i = 0; i < DAYS_IN_STATISTICS; i++) {

            int steps = database.getStepCountForDay(getDateForDayMinusN(i + 1, "yyyyMMdd"));
            BarEntry barEntry = new BarEntry(i, steps);

            barEntries.add(barEntry);
            days.add(getDateForDayMinusN(DAYS_IN_STATISTICS - i, "dd.MM"));

            colors.add(steps >= DAY_MIN ? Color.GREEN : Color.RED);
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Steps");
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(10);
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        barChart.setData(barData);
    }

    private void formatGraph(BarChart barChart){
        XAxis xAxis = barChart.getXAxis();
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
        for (int i = 1; i <= DAYS_IN_STATISTICS; i++) {
            Random random = new Random();
            DayData dayData = new DayData(getDateForDayMinusN(i, "yyyyMMdd"), random.nextInt(20000));
            database.updateDayDataOrCreate(dayData);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        int since_boot = (int) event.values[0];
        todaySteps = database.getStepCountForDay(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        if (todaySteps == 0) {
            todayOffset = since_boot;
            todaysDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            todaySteps++;
            database.updateDayDataOrCreate(new DayData(todaysDate, todaySteps));
        } else {
            todaySteps = since_boot - todayOffset;
            binding.textViewSteps.setText(String.valueOf(todaySteps));
            binding.textViewDistance.setText(String.format("%.1f km", getDistance(todaySteps)));
        }
        database.updateDayData(new DayData(todaysDate, todaySteps));
    }

    private float getDistance(int steps) {
        float distance = steps * stepLength;
        return (distance / 100000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
