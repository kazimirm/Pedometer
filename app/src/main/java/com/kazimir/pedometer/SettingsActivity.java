package com.kazimir.pedometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.kazimir.pedometer.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivity main = new MainActivity();
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String stepLength = String.valueOf(preferences.getInt("stepLength", main.STEP_LENGTH_DEFAULT));
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

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveSettings() {
        int stepLength = Integer.parseInt(binding.inputStepLength.getText().toString());
        SharedPreferences.Editor edit = preferences.edit();

        edit.putInt("stepLength", stepLength);
        edit.commit();
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
    }


}