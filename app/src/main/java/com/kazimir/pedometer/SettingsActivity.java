package com.kazimir.pedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.kazimir.pedometer.databinding.ActivityMainBinding;
import com.kazimir.pedometer.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String stepLength = String.valueOf(preferences.getInt("stepLength", 70));
        binding.inputStepLength.setText(stepLength);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

    }

    private void openMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveSettings(){
        int stepLength = Integer.parseInt(binding.inputStepLength.getText().toString());
        SharedPreferences.Editor edit= preferences.edit();

        edit.putInt("stepLength", stepLength);
        edit.commit();
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
    }


}