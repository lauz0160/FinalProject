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
import android.view.LayoutInflater;
import android.view.Menu;
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

/**
 * Launches the list of favorites activity
 * Launches a screen that displays a list of all the images that have been saved to the database
 */
public class Earth_Favorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Database constant variables
    private final static String DATABASE_NAME = "imagesDB";
    private final static String TABLE_NAME = "Images";
    private final static String COL_Date = "Date";
    private final static String COL_Longitude = "Longitude";
    private final static String COL_Latitude = "Latitude";
    private final static String COL_Image = "ImageUrl";
    private final static String COL_ID = "_id";
    private final static int VERSION_NUM = 4;

    private ArrayList<AnImage> imagesList = new ArrayList<>(Arrays.asList());
    private MyListAdapter myAdapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth__favorites);

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

        //pulls all the information stored in the database
        loadDataFromDatabase();
        ListView myList = findViewById(R.id.listOfImages);
        myList.setAdapter(myAdapter = new MyListAdapter());

        //get the intent from the previous activity and send the data to their respective variables
        Intent data = getIntent();
        if (data.hasExtra("image")) {
            String imageUrl = data.getStringExtra("image");
            String date = data.getStringExtra("date");
            String lon = data.getStringExtra("lon");
            String lat = data.getStringExtra("lat");

            //create a new row of data to be sent to the database
            ContentValues newRow = new ContentValues();
            newRow.put(COL_Date, date);
            newRow.put(COL_Image, imageUrl);
            newRow.put(COL_Latitude, lat);
            newRow.put(COL_Longitude, lon);

            //insert the new row into the database and save the id of where that row was placed
            long newID = db.insert(TABLE_NAME, null, newRow);
            //create a new item based on that data and add it to the list of images already saved
            imagesList.add(new AnImage(newID, date, imageUrl, lat, lon));
            //display a message saying the image was saved successfully
            Toast.makeText(this, "Image has been Saved", Toast.LENGTH_LONG).show();
        }

        //if the inent from the previous activity contains the position then it came from the button of the fragment and requires the image to be deleted
        if (data.hasExtra("position")) {
            //delete the image from the database and the list and display a message saying it was deleted successfully
            db.delete(TABLE_NAME, COL_ID + "= ?", new String[]{Long.toString(imagesList.get(data.getIntExtra("position", 0)).getId())});
            imagesList.remove(data.getIntExtra("position", 0));
            Toast.makeText(this, "Image has been Deleted", Toast.LENGTH_LONG).show();
        }

        //if an item in the list is clicked, launch the fragment that displays all the details of the image
        myList.setOnItemClickListener((parent, view, position, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString("date", imagesList.get(position).getDate());
            dataToPass.putString("file", imagesList.get(position).getImageUrl());
            dataToPass.putString("coordinates", imagesList.get(position).getLat() + ", " + imagesList.get(position).getLon());
            dataToPass.putInt("position", position);

            startActivity(new Intent(Earth_Favorites.this, Earth_Fragment_Frame.class).putExtras(dataToPass));
        });
        myAdapter.notifyDataSetChanged();
    }

    /**
     * This method pulls all the data out of the database
     * The data will all be displayed on screen in the list view,
     * this method is just to organize the code into more readable blocks
     */
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
    protected void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
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
                startActivity(new Intent(Earth_Favorites.this, Earth_Favorites.class));
                break;
            case R.id.SearchIcon:
                startActivity(new Intent(Earth_Favorites.this, Earth_Search.class));
                break;
            case R.id.HomeIcon:
                startActivity(new Intent(Earth_Favorites.this, Earth_Main.class));
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
                startActivity(new Intent(Earth_Favorites.this, Earth_Main.class));
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

    /**
     * Inner class Image is an extension of an Object
     * Each Image object contains 5 variables, the id of the image, the url of the image, the date the image was taken and the coordinates of the image
     */
    private class AnImage extends Object {

        private String imageUrl;
        private String date;
        private String lat;
        private String lon;

        private long id;

        //Image constructor initializes all variables
        private AnImage(long id, String d, String i, String la, String lo) {
            setId(id);
            setDate(d);
            setImageUrl(i);
            setLat(la);
            setLon(lo);
        }

        //getters and setters
        long getId() {
            return id;
        }

        void setId(long id) {
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

    /**
     * List adapter inner class extends BaseAdapter
     * for however many images there are in the list, pull the images one at a time and display them in the list view
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return imagesList.size(); }

        public AnImage getItem(int position) {
            return imagesList.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            AnImage anImage = this.getItem(position);

            //inflate a layout based on the layout xml file and set all its fields to usable variables
            old = inflater.inflate(R.layout.earth_list_image, parent, false);
            TextView date = old.findViewById(R.id.imageLayoutDate);
            TextView lat = old.findViewById(R.id.imageLayoutLat);
            TextView lon = old.findViewById(R.id.imageLayoutLong);
            ImageView image = old.findViewById(R.id.imageLayoutImage);

            //find the stored image file and decode it, save the image as a bitmap picture and display it in the layout
            FileInputStream fis = null;
            try {
                fis = openFileInput(anImage.getImageUrl());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap pic = BitmapFactory.decodeStream(fis);
            image.setImageBitmap(pic);

            //sets the text of the list view row layout to the images data
            lat.setText("Latitude:     " + anImage.getLat());
            lon.setText("Longitude:  " + anImage.getLon());
            if(anImage.getDate() !=null){
                date.setText("Date:           " + anImage.getDate());
            }

            return old;
        }
    }

    /**
     * MyOpener class extends SQLiteOpenHelper
     * this inner class creates a database, or opens a database, that can be manipulated by this program
     */
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
}
