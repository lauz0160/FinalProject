package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * Launches the search activity of the nasa image search
 * Displays a screen displaying the search fields for the image and a search button and a button to display the last search done
 */
public class Earth_Search extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_search);

        //Sets the toolbar for the activity with the in-activity icons
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //sets the drawers layout of this activity
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Sets the navigation pane to contain the icons to launch other activities and the help icon
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //Makes the edit text boxes into variables that can be altered
        EditText lat = findViewById(R.id.editLatitude);
        EditText lon = findViewById(R.id.editLong);

        //Sets the search button to go to the image page with the variables attached to the intent
        Button search = findViewById(R.id.btnSearchDatabase);
        search.setOnClickListener(btn -> {
            Intent goToImage = new Intent(Earth_Search.this, Earth_Image.class);
            goToImage.putExtra("lat", lat.getText().toString());
            goToImage.putExtra("lon", lon.getText().toString());
            startActivity(goToImage);
        });

        //Sets the last search button to open up the image page with no variables, just a string saying this intent is from this button
        Button btnLastSearch = findViewById(R.id.btnLastSearch);
        btnLastSearch.setOnClickListener(btn -> {
            Intent lastImage = new Intent(Earth_Search.this, Earth_Image.class);
            lastImage.putExtra("last", "yes");
            startActivity(lastImage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //set the toolbar with menu options
        getMenuInflater().inflate(R.menu.earth_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //launch one of the in-activity pages based on which toolbar button is clicked
        switch (item.getItemId()) {
            case R.id.FavIcon:
                startActivity(new Intent(Earth_Search.this, Earth_Favorites.class));
                break;
            case R.id.SearchIcon:
                startActivity(new Intent(Earth_Search.this, Earth_Search.class));
                break;
            case R.id.HomeIcon:
                startActivity(new Intent(Earth_Search.this, Earth_Main.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Launch one of the other activites or the help dialog based on the navigation menu buttons is clicked
        switch (item.getItemId()) {
            case R.id.guardianIcon:

                break;
            case R.id.nasaImageIcon:

                break;
            case R.id.nasaEarthIcon:
                startActivity(new Intent(Earth_Search.this, Earth_Main.class));
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

