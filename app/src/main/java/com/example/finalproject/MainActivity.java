package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Launcher activity for the Final Project application
 * {@link AppCompatActivity} subclass.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button that triggers the NASA image of the day activity
        Button btnImage = findViewById(R.id.btnImageOfDay);
        btnImage.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, ImageOfDayActivity.class)));

        //button that triggers the Guardians news article search activity
        Button btnGuardian = findViewById(R.id.btnGuardian);
        btnGuardian.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, GuardianMainActivity.class)));

        //button that triggers the NASA earth image search
        Button btnEarth = findViewById(R.id.btnEarthImage);
        btnEarth.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, Earth_Main.class)));

        //button that triggers the BBC news article search
        Button btnBBC = findViewById(R.id.btnBBC);
        btnBBC.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, BbcNewsReader.class)));

    }

}