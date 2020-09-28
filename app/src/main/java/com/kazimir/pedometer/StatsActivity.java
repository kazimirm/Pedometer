package com.kazimir.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kazimir.pedometer.databinding.ActivitySettingsBinding;
import com.kazimir.pedometer.databinding.ActivityStatsBinding;

public class StatsActivity extends AppCompatActivity {

    ActivityStatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });
    }



    private void openMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}