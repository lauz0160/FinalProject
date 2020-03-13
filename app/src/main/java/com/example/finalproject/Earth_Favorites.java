package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Earth_Favorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String date;
    String lat;
    String lon;
    String imageUrl;

    private ArrayList<AnImage> imagesList = new ArrayList<>(Arrays.asList());
    private MyListAdapter myAdapter;
    SQLiteDatabase db;

    final static String DATABASE_NAME = "imagesDB";
    final static String TABLE_NAME = "Images";
    final static String COL_Date = "Date";
    final static String COL_Longitude = "Longitude";
    final static String COL_Latitude = "Latitude";
    final static String COL_Image = "ImageUrl";
    public final static String COL_ID = "_id";
    final static int VERSION_NUM = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth__favorites);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        loadDataFromDatabase();
        ListView myList = findViewById(R.id.listOfImages);
        myList.setAdapter(myAdapter = new MyListAdapter());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        Intent data = getIntent();
        if (data.hasExtra("image")) {
            imageUrl = data.getStringExtra("image");
            date = data.getStringExtra("date");
            lon = data.getStringExtra("lon");
            lat = data.getStringExtra("lat");

            ContentValues newRow = new ContentValues();
            newRow.put(COL_Date, date);
            newRow.put(COL_Image, imageUrl);
            newRow.put(COL_Latitude, lat);
            newRow.put(COL_Longitude, lon);

            long newID = db.insert(TABLE_NAME, null, newRow);
            imagesList.add(new AnImage(newID, date, imageUrl, lat, lon));
            Toast.makeText(this, "Image has been Saved", Toast.LENGTH_LONG).show();
        }

        if (data.hasExtra("position")) {
            db.delete(TABLE_NAME, COL_ID + "= ?", new String[]{Long.toString(imagesList.get(data.getIntExtra("position",0)).getId())});
            imagesList.remove(data.getIntExtra("position",0));
            myAdapter.notifyDataSetChanged();
        }

        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString("date", imagesList.get(position).getDate());
            dataToPass.putString("file", imageUrl);
            dataToPass.putString("coordinates", imagesList.get(position).getLat() + ", " + imagesList.get(position).getLon());
            dataToPass.putInt("position", position);

            Intent next = new Intent(Earth_Favorites.this, Phone_fragment.class);
            next.putExtras(dataToPass);
            startActivity(next);

            return true;
        });


        myAdapter.notifyDataSetChanged();
    }


    private class AnImage extends Object {

        private String imageUrl;
        private String date;
        private String lat;
        private String lon;


        private long id;

        private AnImage(long id, String d, String i, String la, String lo) {
            setId(id);
            setDate(d);
            setImageUrl(i);
            setLat(la);
            setLon(lo);
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        String getImageUrl() {
            return imageUrl;
        }

        void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        String getDate() {
            return date;
        }

        void setDate(String date) {
            this.date = date;
        }

        String getLat() {
            return lat;
        }

        void setLat(String lat) {
            this.lat = lat;
        }

        String getLon() {
            return lon;
        }

        void setLon(String lon) {
            this.lon = lon;
        }
    }


    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return imagesList.size();
        }

        public AnImage getItem(int position) {
            return imagesList.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            AnImage anImage = this.getItem(position);

            old = inflater.inflate(R.layout.image_layout, parent, false);
            TextView date = old.findViewById(R.id.imageLayoutDate);
            TextView lat = old.findViewById(R.id.imageLayoutLat);
            TextView lon = old.findViewById(R.id.imageLayoutLong);
            ImageView image = old.findViewById(R.id.imageLayoutImage);

            FileInputStream fis = null;
            try {
                fis = openFileInput(anImage.getImageUrl());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap pic = BitmapFactory.decodeStream(fis);

            image.setImageBitmap(pic);

            lat.setText("Latitude:     " + anImage.getLat());
            lon.setText("Longitude:  " + anImage.getLon());
            date.setText("Date:           " + anImage.getDate());
            return old;
        }
    }


    private class MyOpener extends SQLiteOpenHelper {


        MyOpener(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_Date + " text,"
                    + COL_Image + " text,"
                    + COL_Latitude + " text,"
                    + COL_Longitude + " text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }
    }


    private void loadDataFromDatabase() {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {COL_ID, COL_Date, COL_Image, COL_Latitude, COL_Longitude};
        Cursor results = db.query(false, TABLE_NAME, columns, null, null, null, null, null, null);

        int DateColumnIndex = results.getColumnIndex(COL_Date);
        int ImageColIndex = results.getColumnIndex(COL_Image);
        int LatColIndex = results.getColumnIndex(COL_Latitude);
        int LonColIndex = results.getColumnIndex(COL_Longitude);
        int idColIndex = results.getColumnIndex(COL_ID);

        while (results.moveToNext()) {
            long id = results.getLong(idColIndex);
            String date = results.getString(DateColumnIndex);
            String image = results.getString(ImageColIndex);
            String lat = results.getString(LatColIndex);
            String lon = results.getString(LonColIndex);
            imagesList.add(new AnImage(id, date, image, lat, lon));
        }
        results.close();
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
        startActivity(new Intent(Earth_Favorites.this, activity));
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
                startActivity(new Intent(Earth_Favorites.this, NASA_Earth_Imagery.class));
                break;
            case R.id.bbcNewsIcon:

                break;
            case R.id.helpIcon:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Favorites Help");
                alertDialogBuilder.setMessage("This screen displays a list of images that have been saved. " +
                        "\n\nClick on on of the items in the list to get details about that image or to remove it from the list" +
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
