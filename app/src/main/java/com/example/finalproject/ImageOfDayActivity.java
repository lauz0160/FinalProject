package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ImageOfDayActivity extends AppCompatActivity {

    //Initialise the shared preferences, database, and query async
    private SharedPreferences imgSharedPrefs;
    private ImageOpener imgOpen;
    private SQLiteDatabase imagedb;
    private ArrayList<NASAImage> imageList = new ArrayList<>(Arrays.asList());
    private static String apiQueryString;

    //Global values for db table and column reference
    final static String TABLE_NAME = "IMAGEDB";
    final static String FILENAME = "ImageDB";
    final static String COL_ID = "_id";
    final static String COL_URL = "URL";
    final static String COL_DATE = "DATE";

    /**
     * Activity creation method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Open main view
        setContentView(R.layout.activity_image_of_day);

        //Set values for the sharedprefs and database
        imgSharedPrefs = getSharedPreferences("imgSavedPrefs", Context.MODE_PRIVATE);
        imgOpen = new ImageOpener(this);
        imagedb = imgOpen.getWritableDatabase();

        //Initialise ListView and Adapter
        ListView imageListView = (ListView) findViewById(R.id.image_favs_list);
        ImageAdapter imageAdapter = new ImageAdapter();
        imageListView.setAdapter(imageAdapter);

        //Set the listener to prompt view/delete snackbar
        imageListView.setOnItemLongClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Select an option")
                        .setCancelable(false)
                        .setPositiveButton("View", (click, arg) -> {

                        })
                        .setNegativeButton("Delete", (click, arg) -> {
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                            deleteBuilder.setMessage(R.string.list_alert_confirm_delete)
                                            .setPositiveButton(R.string.confirm, (click2, arg2) -> {

                                            })
                                            .setNegativeButton(R.string.deny, (click2, arg2) -> {
                                                Toast.makeText(this, R.string.list_alert_canceled,Toast.LENGTH_SHORT).show();
                                            });
                        })
                        .setNeutralButton(R.string.list_alert_neutral, (click, arg) -> {
                            Toast.makeText(this, R.string.list_alert_canceled, Toast.LENGTH_SHORT).show();
                        })
                .create().show();
                return true;});

        //Initialise Toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.nav_toolbar);
        setSupportActionBar(myToolbar);


        //Initialize Find Images button and resulting fragment date picker
        Button findBtn = findViewById(R.id.find_images_button);
        findBtn.setOnClickListener( click -> {DatePickerFragment tempDialog = new DatePickerFragment();
                                                showDatePickerDialog(findBtn);
                                                //Put the resulting date into the query
                                                ImageDataQuery tempHolder = new ImageDataQuery();
                                                tempHolder.execute("https://api.nasa.gov/planetary/apod?api_key=Nygu8miwu5Wrqr8DycTtyarzSN1Zmso2SmisiMFv&date=" + apiQueryString);
                                                //Create the content values
                                                ContentValues cv = new ContentValues();
                                                cv.put(COL_URL, tempHolder.imageURL);
                                                cv.put(COL_DATE, tempHolder.date);
                                                //Insert the content values to the db
                                                long tempID = imagedb.insert(TABLE_NAME, null, cv);
                                                //Create the NASAImage object and store it in the list
                                                NASAImage temp = new NASAImage(tempID, tempHolder.imageURL, tempHolder.date);
                                                imageList.add(temp);
                                                //Update the ListView
                                                imageAdapter.notifyDataSetChanged();
                                    });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_of_day_menu, menu);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //ImageAdapter to populate the ListView
    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public NASAImage getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position){
            return getItem(position).getID();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent){
            //Declare the inflater and NASAImage object
            LayoutInflater imageInflater = getLayoutInflater();
            NASAImage tmpImage = this.getItem(position);
            //Create the view
            View imageView = imageInflater.inflate(R.layout.image_listview_layout, parent, false);
            //Fill the appropriate fields
            ImageView picture = imageView.findViewById(R.id.listview_image);
            TextView url = imageView.findViewById(R.id.listview_url);
            TextView date = imageView.findViewById(R.id.listview_date);
            //Find the stored image and use it in the ImageView
            FileInputStream fileInput = null;
            try {
                fileInput = openFileInput(tmpImage.getURL());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap tempBit = BitmapFactory.decodeStream(fileInput);
            tmpImage.setImg(tempBit);
            return imageView;
        }
    }

    //Inner class for DB querying
    public class ImageDataQuery extends AsyncTask<String, Integer, String>{

        private String imageURL;
        private String date;
        private Bitmap image;

        //Basic hollow constructor
        public ImageDataQuery() {

        }

        @Override
        protected String doInBackground(String... strings) {

                //Try-catch for exceptions
                try {
                    //Get the URL and open the connection to the website
                    URL url = new URL(strings[0]);
                    HttpURLConnection Connection = (HttpURLConnection) url.openConnection();
                    Connection.connect();
                    InputStream Response = Connection.getInputStream();

                    //Create the reader of the input as well as the string builder for holding the string values
                    BufferedReader reader = new BufferedReader(new InputStreamReader(Response, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    //Save the string builder composition and convert to a JSON
                    String result = sb.toString();
                    JSONObject Report = new JSONObject(result);
                    //Get the key value and save it to the imageURL
                    imageURL = Report.getString("URL") + "";

                    //If the image does exists, use it. Otherwise, download the image and save it to the drive
                    if (getBaseContext().getFileStreamPath(imageURL).exists())
                    {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = openFileInput(imageURL);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        image = BitmapFactory.decodeStream(inputStream);
                    } else {
                        URL imgurl = new URL(imageURL);
                        HttpURLConnection connection = (HttpURLConnection) imgurl.openConnection();
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            image = BitmapFactory.decodeStream(connection.getInputStream());
                        }

                        FileOutputStream outputStream = openFileOutput(imageURL, Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }


                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return null;
            }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //Update the screen as it progresses
            /*
            ProgressBar progBar = findViewById(R.id.progressBar);
            progBar.setVisibility(View.VISIBLE);
            progBar.setProgress(values[0]);
            */
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //Update the screen on execute
            super.onPostExecute(s);
        }
    }

        //Inner class for DB opener
        public class ImageOpener extends SQLiteOpenHelper {

            public ImageOpener (Context context) {
                super(context, "ImageDB", null, 1);
            }


            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE_NAME +  "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                    + COL_URL + " text, " + COL_DATE + " text)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }

        }

        //Date Picker class, as gotten from the Android Development website given in instructions
        public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Set the API string for use by ImageDataQuery in onCreate
            apiQueryString = year + "-" + month + "-" + day;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //Method for saving the DB entities to the list
    private void loadDataFromDatabase() {
        ImageOpener dbOpener = new ImageOpener(this);
        imagedb = dbOpener.getWritableDatabase();

        String[] columns = {COL_ID, COL_URL, COL_DATE};
        Cursor results = imagedb.query(false, TABLE_NAME, columns, null, null, null, null, null, null);

        int urlIndex = results.getColumnIndex(COL_URL);
        int dateIndex = results.getColumnIndex(COL_DATE);
        int idIndex = results.getColumnIndex(COL_ID);

        while (results.moveToNext()) {
            long id = results.getLong(idIndex);
            String date = results.getString(dateIndex);
            String url = results.getString(urlIndex);
            imageList.add(new NASAImage(id, url, date));
        }
        results.close();
    }

    //NASAImage object for populating the ListView
    private class NASAImage extends Object {

        private long id;
        private String url;
        private String date;
        private Bitmap img;

        NASAImage( long id, String url, String date){
            setID(id);
            setURL(url);
            setDate(date);
        }

        public void setID(long id){
            this.id = id;
        }

        public long getID(){
            return id;
        }

        public void setURL(String url) {
            this.url = url;
        }

        public String getURL(){
            return url;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setImg(Bitmap img) {
            this.img = img;
        }

        public Bitmap getImg() {
            return img;
        }
    }



}


