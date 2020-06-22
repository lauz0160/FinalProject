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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Launches the Image display activity
 * This launches a screen with the results of the search, including the image date, coordinates and the image itself
 */
public class Image_Image extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //global variables relating to image data
    private Bitmap image;
    private String fileName;
    private String date;
    private String title;
    private String desc;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_image);

        prefs = getSharedPreferences("imageFile", Context.MODE_PRIVATE);


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
        TextView dateDisplay = findViewById(R.id.dateValue);

        //get the intent with its data from the previous activity
        Intent fromActivity = getIntent();
        //if the activity was launched using the button for the last search, pull the data from the shared preferences
        if (fromActivity.hasExtra("last")) {
            date = prefs.getString("date", "");
        }
        //otherwise pull the data from the intent which was the data from the search boxes
        else {
            date = fromActivity.getStringExtra("date");
        }
        //set the text boxes to display the appropriate data
        dateDisplay.setText(date);

        //this button finishes this activity and sends the user back to the previous activity
        Button backToSearch = findViewById(R.id.btnBackToSearch);
        backToSearch.setOnClickListener(btn -> this.finish());

        //this buttons saves the current image to a file then creates an intent with all the images data and passes it all to the favorites list activity
        Button save = findViewById(R.id.btnSaveFav);
        save.setOnClickListener(btn -> {
            try {
                fileName = date + ".png";
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                if (e.getMessage() != null)
                    Log.e("Error", e.getMessage());
            }

            startActivity(new Intent(Image_Image.this, Image_Favorites.class).putExtra("date", date).putExtra("image", fileName));
        });
        //this send the url to the AsyncTask
        new GetData().execute("https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date="+date);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when this screen is left, the current values of the latitude and longitude will be saved to shared preferences as the last image searched
        prefs.edit().putString("date", date).commit();
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
                startActivity(new Intent(Image_Image.this, Image_Favorites.class));
                break;
            case R.id.SearchIcon:
                startActivity(new Intent(Image_Image.this, Image_Search.class));
                break;
            case R.id.HomeIcon:
                startActivity(new Intent(Image_Image.this, Image_Main.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Launch one of the other activities or the help dialog based on the navigation menu buttons is clicked
        switch (item.getItemId()) {
            case R.id.lyricIcon:
                startActivity(new Intent(Image_Image.this, Song_Lyric_Main.class));
                break;
            case R.id.nasaImageIcon:
                startActivity(new Intent(Image_Image.this, Image_Main.class));
                break;
            case R.id.nasaEarthIcon:
                startActivity(new Intent(Image_Image.this, Earth_Main.class));
                break;
            case R.id.geoIcon:
                startActivity(new Intent(Image_Image.this, Geo_Main.class));
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

                URL Url = new URL(strings[0]);
                HttpURLConnection Connection = (HttpURLConnection) Url.openConnection();
                Connection.connect();
                InputStream Response = Connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(Response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();
                JSONObject Report = new JSONObject(result);
                desc = Report.getString("explanation");
                title = Report.getString("title");
                String url = Report.getString("hdurl");

                //get a new connection to the url of the image and save the image to the bitmap variable
                HttpURLConnection imgconnection = (HttpURLConnection) new URL(url).openConnection();
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
            TextView titleText = findViewById(R.id.imageTitle);
            titleText.setText(title);
            TextView descText = findViewById(R.id.descValue);
            descText.setText(desc);

            super.onPostExecute(s);
        }
    }
}