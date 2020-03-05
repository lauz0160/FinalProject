package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnImage = findViewById(R.id.btnImageOfDay);
      //  btnImage.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, MainActivity.class)));

        Button btnGuardian = findViewById(R.id.btnGuardian);
      //  btnImage.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, MainActivity.class)));

        Button btnEarth = findViewById(R.id.btnEarthImage);
     //   btnImage.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, MainActivity.class)));

        Button btnBBC = findViewById(R.id.btnBBC);
     //   btnImage.setOnClickListener(btn -> startActivity(new Intent(MainActivity.this, MainActivity.class)));

    }
}
