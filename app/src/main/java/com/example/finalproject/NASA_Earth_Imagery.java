package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class NASA_Earth_Imagery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasa__earth__imagery);

        Button btnSearch = findViewById(R.id.btnNewSearch);
        btnSearch.setOnClickListener(btn -> startActivity(new Intent(NASA_Earth_Imagery.this, Earth_search.class )));

        Button btnFavorites = findViewById(R.id.btnViewFav);
        btnFavorites.setOnClickListener(btn -> startActivity(new Intent(NASA_Earth_Imagery.this, Earth_Favorites.class )));

        Button btnLastSearch = findViewById(R.id.btnLastSearch);
        btnLastSearch.setOnClickListener(btn -> startActivity(new Intent(NASA_Earth_Imagery.this, Earth_image.class )));
    }
}
