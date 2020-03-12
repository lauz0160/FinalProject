package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Earth_search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_search);

        EditText lat = findViewById(R.id.editLatitude);
        EditText lon = findViewById(R.id.editLong);

        Button search = findViewById(R.id.btnSearchDatabase);
        search.setOnClickListener(btn -> {
            Intent goToImage = new Intent(Earth_search.this, Earth_image.class);
            goToImage.putExtra("lat", lat.getText().toString());
            goToImage.putExtra("lon", lon.getText().toString());
            startActivity(goToImage);
        });

        Button btnLastSearch = findViewById(R.id.btnLastSearch);
        btnLastSearch.setOnClickListener(btn -> startActivity(new Intent(Earth_search.this, Earth_image.class )));
    }
}
