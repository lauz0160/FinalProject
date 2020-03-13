package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Earth_search extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_search);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

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
        btnLastSearch.setOnClickListener(btn -> startActivity(new Intent(Earth_search.this, Earth_image.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class activity = MainActivity.class;
        switch (item.getItemId()) {
            case R.id.FavIcon:
                activity = Earth_Favorites.class;
                break;
            case R.id.SearchIcon:
                activity = Earth_search.class;
                break;
            case R.id.HomeIcon:
                activity = NASA_Earth_Imagery.class;
                break;
        }
        startActivity(new Intent(Earth_search.this, activity));
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guardianIcon:

                break;
            case R.id.nasaImageIcon:

                break;
            case R.id.nasaEarthIcon:
                startActivity(new Intent(Earth_search.this, NASA_Earth_Imagery.class));
                break;
            case R.id.bbcNewsIcon:

                break;
            case R.id.helpIcon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Search Help");
                alertDialogBuilder.setMessage("To search for a new image enter the coordinates in the spaces provided then click on the search button" +
                        "\n\nTo see the last image searched click on the second button" +
                        "\n\nThe toolbar along the top of the screen will take you to any of the three main screens of this activity: the home screen, the search screen, and the list of favorites" +
                        "\n\nThe icons in the navigation menu along the side of the screen can take you to any of the other activities in this application");
                alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {

                });
                alertDialogBuilder.create().show();
                break;
        }

        return false;
    }
}

