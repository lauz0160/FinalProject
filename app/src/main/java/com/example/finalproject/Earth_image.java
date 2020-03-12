package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Earth_image extends AppCompatActivity {

    Bitmap image;
    String date;
    String imageUrl;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_image);

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
                fileName=lat+lon+date+".png";
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.flush();
                outputStream.close();
            }catch(Exception e){}

            Intent next = new Intent(Earth_image.this, Earth_Favorites.class );
            next.putExtra("lat",lat);
            next.putExtra("lon",lon);
            next.putExtra("image",fileName );
            next.putExtra("date",date);
            startActivity(next);
        });

        GetData req = new GetData();
        req.execute("https://api.nasa.gov/planetary/earth/imagery/?lon="+lon+"&lat="+lat+"&date=2014-02-01&api_key=DEMO_KEY");
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
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject Report = new JSONObject(result);

                imageUrl = Report.getString("url");
                date=Report.getString("date").substring(0,10);
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

                    Snackbar sBar = Snackbar.make(findViewById(R.id.imageView),"",Snackbar.LENGTH_INDEFINITE);
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

            ImageView pic  = findViewById(R.id.imageView);
            pic.setImageBitmap(image);
            pic.setVisibility(View.VISIBLE);
            TextView dateText = findViewById(R.id.dateValue);
            dateText.setText(date);
            super.onPostExecute(s);
        }
    }
}