package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Earth_image extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Bitmap image;
    String date;
    String imageUrl;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_image);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(25);

        TextView latitude = findViewById(R.id.latValue);
        TextView longitude = findViewById(R.id.longValue);

        Intent fromSearch = getIntent();
        String lat = fromSearch.getStringExtra("lat");
        String lon = fromSearch.getStringExtra("lon");
        latitude.setText(lat);
        longitude.setText(lon);

        Button backToSearch = findViewById(R.id.btnBackToSearch);
        Button save = findViewById(R.id.btnSaveFav);

        backToSearch.setOnClickListener(btn -> this.finish());

        save.setOnClickListener(btn -> {
            try {
                fileName = lat + lon + date + ".png";
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                if (e.getMessage() != null)
                    Log.e("Error", e.getMessage());
            }

            Intent next = new Intent(Earth_image.this, Earth_Favorites.class);
            next.putExtra("lat", lat);
            next.putExtra("lon", lon);
            next.putExtra("image", fileName);
            next.putExtra("date", date);
            startActivity(next);
        });

        GetData req = new GetData();
        req.execute("https://api.nasa.gov/planetary/earth/imagery/?lon=" + lon + "&lat=" + lat + "&date=2014-02-01&api_key=DEMO_KEY");
    }

    public class GetData extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... strings) {

            try {
                publishProgress(50);
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

                imageUrl = Report.getString("url");
                date = Report.getString("date").substring(0, 10);
                publishProgress(75);

                URL imgurl = new URL(imageUrl);
                HttpURLConnection imgconnection = (HttpURLConnection) imgurl.openConnection();
                imgconnection.connect();
                int responseCode = imgconnection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(imgconnection.getInputStream());
                    publishProgress(100);
                }


            } catch (Exception e) {

                Snackbar sBar = Snackbar.make(findViewById(R.id.imageView), "", Snackbar.LENGTH_INDEFINITE);
                sBar.setText("Image not found");
                sBar.setAction("Return to search", click -> finish());
                sBar.show();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            ProgressBar progBar = findViewById(R.id.progressBar);
            progBar.setProgress(values[0]);

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            ProgressBar progBar = findViewById(R.id.progressBar);
            progBar.setVisibility(View.INVISIBLE);

            ImageView pic = findViewById(R.id.imageView);
            pic.setImageBitmap(image);
            pic.setVisibility(View.VISIBLE);
            TextView dateText = findViewById(R.id.dateValue);
            dateText.setText(date);
            super.onPostExecute(s);
        }
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
        startActivity(new Intent(Earth_image.this, activity));
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
                startActivity(new Intent(Earth_image.this, NASA_Earth_Imagery.class));
                break;
            case R.id.bbcNewsIcon:

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
}
