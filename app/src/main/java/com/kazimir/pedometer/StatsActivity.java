package com.kazimir.pedometer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.kazimir.pedometer.databinding.ActivityStatsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    ActivityStatsBinding binding;
    DatabaseHandler database;
    MainActivity main;
    SharedPreferences preferences;

    private int stepLength;
    private final int DAYS_IN_STATISTICS = 7;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = DatabaseHandler.getInstance(this);
        main = new MainActivity();

        preferences = getSharedPreferences("settings", 0);
        stepLength = preferences.getInt("stepLength", main.STEP_LENGTH_DEFAULT);


        List<DayData> dayDataList = getLastNDays(DAYS_IN_STATISTICS);
        Collections.sort(dayDataList, Comparator.comparing(DayData::getSteps));
        int max = dayDataList.size() - 1;
        displayData(dayDataList.get(max), dayDataList.get(0));

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });


    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private List<DayData> getLastNDays(int n) {
        List<DayData> dayDataList = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            DayData dayData = database.getDayData(main.getDateForDayMinusN(i, "yyyyMMdd"));
            dayDataList.add(dayData);
        }
        return dayDataList;
    }

    private void displayData(DayData max, DayData min) {
        binding.textViewMax.setText(String.format("Max distance in km for last %d days = %s with date %s", DAYS_IN_STATISTICS, max.getDistance(stepLength), max.getDate()));
        binding.textViewMin.setText(String.format("Min distance in km for last %d days = %s with date %s", DAYS_IN_STATISTICS, min.getDistance(stepLength), min.getDate()));
    }
}