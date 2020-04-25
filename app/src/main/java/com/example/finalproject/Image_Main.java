package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * Launches the main menu of the Earth image search activity
 * launches a screen with 3 buttons, a toolbar and a navigation menu, different screens will be launches depending on which buttons are clicked
 */
public class Image_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_main);

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

        //Launches the search page if the user clicks the search button
        Button btnSearch = findViewById(R.id.btnNewSearch);
        btnSearch.setOnClickListener(btn -> startActivity(new Intent(Image_Main.this, Earth_Search.class)));

        //Launches the favorites page if the user clicks on the favorites button
        Button btnFavorites = findViewById(R.id.btnViewFav);
        btnFavorites.setOnClickListener(btn -> startActivity(new Intent(Image_Main.this, Earth_Favorites.class)));

        //Launches the last image search
        Button btnLastSearch = findViewById(R.id.btnLastSearch);
        btnLastSearch.setOnClickListener(btn -> {
            Intent lastImage = new Intent(Image_Main.this, Earth_Image.class);
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
                startActivity(new Intent(Image_Main.this, Earth_Favorites.class));
                break;
            case R.id.SearchIcon:
                startActivity(new Intent(Image_Main.this, Earth_Search.class));
                break;
            case R.id.HomeIcon:
                startActivity(new Intent(Image_Main.this, Image_Main.class));
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
                startActivity(new Intent(Image_Main.this, Image_Main.class));
                break;
            case R.id.bbcNewsIcon:

                break;
            case R.id.helpIcon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("NASA Image Help");
                alertDialogBuilder.setMessage("To search for a new image click the first button" +
                        "\n\nTo see the list of favorites click the last button" +
                        "\n\nTo see the last image searched click on the last button" +
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
