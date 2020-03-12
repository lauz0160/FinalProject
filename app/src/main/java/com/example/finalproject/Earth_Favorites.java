package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Earth_Favorites extends AppCompatActivity {

    String date;
    String lat;
    String lon;
    String imageUrl;

    private ArrayList<AnImage> imagesList = new ArrayList<>(Arrays.asList());
    private MyListAdapter myAdapter;
    SQLiteDatabase db;

    final static String DATABASE_NAME = "imagesDB";
    final static String TABLE_NAME = "Images";
    final static String COL_Date= "Date";
    final static String COL_Longitude = "Longitude";
    final static String COL_Latitude = "Latitude";
    final static String COL_Image = "ImageUrl";
    public final static String COL_ID = "_id";
    final static int VERSION_NUM = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth__favorites);


        loadDataFromDatabase();
        ListView myList = findViewById(R.id.listOfImages);
        myList.setAdapter(myAdapter = new MyListAdapter());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        Intent data = getIntent();
        if(data.hasExtra("image")){
            imageUrl = data.getStringExtra("image");
            date = data.getStringExtra("date");
            lon = data.getStringExtra("lon");
            lat = data.getStringExtra("lat");

            ContentValues newRow = new ContentValues();
            newRow.put(COL_Date,date);
            newRow.put(COL_Image,imageUrl);
            newRow.put(COL_Latitude,lat);
            newRow.put(COL_Longitude,lon);

            long newID = db.insert(TABLE_NAME, null, newRow);
            imagesList.add(new AnImage(newID, date,imageUrl,lat, lon));
            Toast.makeText(this, "Image has been Saved", Toast.LENGTH_LONG).show();
        }

        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            alertDialogBuilder.setTitle("Remove this item from favorites?");
            //alertDialogBuilder.setView(view.);
            alertDialogBuilder.setMessage("Date: "+date+"\nLatitude: "+lat+"\nLongitude: "+lon);
            alertDialogBuilder.setPositiveButton("Yes", (click, arg) -> {
                db.delete(TABLE_NAME, COL_ID + "= ?", new String[]{Long.toString(imagesList.get(position).getId())});
                imagesList.remove(position);
                try {
                    deleteFile(imageUrl);
                }catch(Exception e){
                    Log.e(e.getMessage(),"");
                }
                myAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Image has been deleted", Toast.LENGTH_LONG).show();
            });
            alertDialogBuilder.setNegativeButton("No", (click, arg) -> {
            });
            alertDialogBuilder.create().show();

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

        private AnImage (long id,String d, String i, String la, String lo ){
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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }
    }






    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return imagesList.size();
        }

        public AnImage getItem(int position) {return imagesList.get(position); }

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
            try {    fis = openFileInput(anImage.getImageUrl());   }
            catch (FileNotFoundException e) {    e.printStackTrace();  }
            Bitmap pic = BitmapFactory.decodeStream(fis);

            image.setImageBitmap(pic);

            lat.setText( "Latitude:     "+anImage.getLat());
            lon.setText( "Longitude:  "+anImage.getLon());
            date.setText("Date:           "+anImage.getDate());
            return old;
        }
    }






    private class MyOpener extends SQLiteOpenHelper {


        public MyOpener(Context ctx){ super(ctx, DATABASE_NAME, null, VERSION_NUM); }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_Date + " text,"
                    + COL_Image  + " text,"
                    +COL_Latitude + " text,"
                    +COL_Longitude + " text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {   //Drop the old table:
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

            //Create the new table:
            onCreate(db);
        }
    }





    private void loadDataFromDatabase()
    {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {COL_ID, COL_Date, COL_Image, COL_Latitude, COL_Longitude};
        Cursor results = db.query(false,TABLE_NAME, columns, null, null, null, null, null, null);

        int DateColumnIndex = results.getColumnIndex(COL_Date);
        int ImageColIndex = results.getColumnIndex(COL_Image);
        int LatColIndex = results.getColumnIndex(COL_Latitude);
        int LonColIndex = results.getColumnIndex(COL_Longitude);
        int idColIndex = results.getColumnIndex(COL_ID);

        while(results.moveToNext()) {
            long id = results.getLong(idColIndex);
            String date = results.getString(DateColumnIndex);
            String image =results.getString(ImageColIndex);
            String lat = results.getString(LatColIndex);
            String lon = results.getString(LonColIndex);
            imagesList.add(new AnImage(id, date, image, lat, lon));
        }
    }
}
