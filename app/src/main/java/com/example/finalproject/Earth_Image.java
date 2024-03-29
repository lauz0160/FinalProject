package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Launches the Image display activity
 * This launches a screen with the results of the search, including the image date, coordinates and the image itself
 */
public class Earth_Image extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //global variables relating to image data
    private Bitmap image;
    private String fileName;
    private String lat;
    private String lon;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_image);

        prefs = getSharedPreferences("file", Context.MODE_PRIVATE);


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

        //set the progress bars initial value
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(25);

        //set the text boxes to variables that can be used
        TextView latitude = findViewById(R.id.latValue);
        TextView longitude = findViewById(R.id.longValue);

        //get the intent with its data from the previous activity
        Intent fromActivity = getIntent();
        //if the activity was launched using the button for the last search, pull the data from the shared preferences
        if (fromActivity.hasExtra("last")) {
            lat = prefs.getString("lat", "");
            lon = prefs.getString("lon", "");
        }
        //otherwise pull the data from the intent which was the data from the search boxes
        else {
            lat = fromActivity.getStringExtra("lat");
            lon = fromActivity.getStringExtra("lon");
        }
        //set the text boxes to display the appropriate data
        latitude.setText(lat);
        longitude.setText(lon);

        //this button finishes this activity and sends the user back to the previous activity
        Button backToSearch = findViewById(R.id.btnBackToSearch);
        backToSearch.setOnClickListener(btn -> this.finish());

        //this buttons saves the current image to a file then creates an intent with all the images data and passes it all to the favorites list activity
        Button save = findViewById(R.id.btnSaveFav);
        save.setOnClickListener(btn -> {
            try {
                fileName = lat + lon + ".png";
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                if (e.getMessage() != null)
                    Log.e("Error", e.getMessage());
            }

            startActivity(new Intent(Earth_Image.this, Earth_Favorites.class).putExtra("lat", lat).putExtra("lon", lon).putExtra("image", fileName));
        });
        //this send the url to the AsyncTask
        new GetData().execute("http://dev.virtualearth.net/REST/V1/Imagery/Map/Birdseye/" + lat + "," + lon + "/20?dir=180&ms=500,500&key=AkYKOT4zFh9RFk8QFu4M7wkYSQHRo5HaD1PRiFMo3TVnz7e2sfAGWXa_roMIQXD3");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when this screen is left, the current values of the latitude and longitude will be saved to shared preferences as the last image searched
        prefs.edit().putString("lat", lat).putString("lon", lon).commit();
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
                startActivity(new Intent(Earth_Image.this, Earth_Favorites.class));
                break;
            case R.id.SearchIcon:
                startActivity(new Intent(Earth_Image.this, Earth_Search.class));
                break;
            case R.id.HomeIcon:
                startActivity(new Intent(Earth_Image.this, Earth_Main.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Launch one of the other activites or the help dialog based on the navigation menu buttons is clicked
        switch (item.getItemId()) {
            case R.id.MainIcon:
                startActivity(new Intent(Earth_Image.this, MainActivity.class));
                break;
            case R.id.guardianIcon:
                startActivity(new Intent(Earth_Image.this, GuardianMainActivity.class));
                break;
            case R.id.nasaImageIcon:
                startActivity(new Intent(Earth_Image.this, ImageOfDayActivity.class));
                break;
            case R.id.nasaEarthIcon:
                startActivity(new Intent(Earth_Image.this, Earth_Main.class));
                break;
            case R.id.bbcNewsIcon:
                startActivity(new Intent(Earth_Image.this, BbcNewsReader.class));
                break;
            case R.id.helpIcon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Image Help");
                alertDialogBuilder.setMessage("This screen has the image results from the previous screens search" +
                        "\n\nThe return to search button will take you back to the search screen where you can put in new coordinates, the second button will add these image details to your list of favorite items" +
                        "\n\nThe toolbar along the top of the screen will take you to any of the three main screens of this activity: the home screen, the search screen, and the list of favorites" +
                        "\n\nThe icons in the navigation menu along the side of the screen can take you to any of the other activities in this application");
                alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {

                });
                alertDialogBuilder.create().show();
                break;
        }
        return false;
    }

    class GetData extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... strings) {
            //advance progress bar to 50% while connection is being made to the url
            publishProgress(50);
            try {
                //get a new connection to the url of the image and save the image to the bitmap variable
                HttpURLConnection imgconnection = (HttpURLConnection) new URL(strings[0]).openConnection();
                imgconnection.connect();
                int responseCode = imgconnection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(imgconnection.getInputStream());
                    publishProgress(100);
                }
                else{
                    Snackbar.make(findViewById(R.id.imageView), "", Snackbar.LENGTH_INDEFINITE).setText("Image not found").setAction("Return to search", click -> finish()).show();
                }
            } catch (Exception e) {
                //if the data cant be found make a toast saying the image cant be found
                Snackbar.make(findViewById(R.id.imageView), "", Snackbar.LENGTH_INDEFINITE).setText("Image not found").setAction("Return to search", click -> finish()).show();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            //every time the progress is updated, set the progress bar to that value
            ProgressBar progBar = findViewById(R.id.progressBar);
            progBar.setProgress(values[0]);

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            //once all the data has been found set the progress bar to invisible and put all the data into their respective fields to display
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

            ImageView pic = findViewById(R.id.imageView);
            pic.setImageBitmap(image);
            pic.setVisibility(View.VISIBLE);

            super.onPostExecute(s);
        }
    }
}