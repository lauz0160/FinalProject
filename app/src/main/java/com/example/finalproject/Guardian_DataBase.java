package com.example.finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLITE //saves news articles
 * */
public class Guardian_DataBase extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "ContactsDB";
    protected final static int VERSION_NUM = 1;
    public final static String COL_ID = "id";
    private final static String TABLE_NAME = "articles";
    private final static String COL_WEB_TITLE = "webTitle";
    private final static String COL_SECTION_NAME = "sectionName";
    private final static String COL_WEB_URL = "webUrl";



    public Guardian_DataBase(Context context) {
        super(context, "guardian_DB", null, 1);
    }

    //https://stackoverflow.com/questions/49352632/how-to-create-a-new-table-in-sqlite-database-from-android-app
    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.

    /**
     * @param db creates an table object with all columns
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + TABLE_NAME +
                "(" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_WEB_TITLE + " TEXT, " +
                COL_SECTION_NAME + " TEXT, " +
                COL_WEB_URL + " TEXT); ");
    }

    /**
     * @param db object if updated the old table is dropped
     */
    //https://stackoverflow.com/questions/49352632/how-to-create-a-new-table-in-sqlite-database-from-android-app
    //MyOpener classexample wk5
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    /**
     * @param db this is called when the database is higher than 1 as if manually set higher
     *           causes fatal error
     * @param oldVersion
     * @param newVersion
     */
    //https://stackoverflow.com/questions/34544205/how-to-resolve-cant-downgrade-database-from-version-2-to-1-in-activeandroid
    //MyOpener classexample wk5
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

//    private class SaveDelete extends SQLiteOpenHelper {
//
//    }

    /**
     * @param news puts all information into the table
     */
    //Save a ContentValues object to a database row:
    //Database example.java week5
    public void saveDetails(News news) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COL_ID, news.getId());
        updatedValues.put(COL_WEB_TITLE, news.getWebTitle());
        updatedValues.put(COL_SECTION_NAME, news.getSectionName());
        updatedValues.put(COL_WEB_URL, news.getWebUrl());
        //newRow.put(VERSION_NUMBER, news.getVersionNumber());
        //call the insert function
        db.insert(TABLE_NAME, null, updatedValues);
    }


    /**
     * @return loads all of the news articles from the table
     * cursor starts at beginning and moves until end -> saving to the list
     */
    // https://stackoverflow.com/questions/1243199/how-to-perform-an-sqlite-query-within-an-android-application
    public List<News> loadDataFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        // get all of the columns
        //String [] columns = {MyOpener.COL_ID, MyOpener.COL_WEB_TITLE, MyOpener.COL_WEB_URL, MyOpener.COL_DATE, MyOpener.COL_SECTION_NAME,};
        Cursor results = db.query(TABLE_NAME, new String[]{COL_ID, COL_WEB_TITLE, COL_SECTION_NAME, COL_WEB_URL }, null, null,
                null, null, null);

        //https://www.codota.com/code/java/methods/android.database.Cursor/isAfterLast
        List<News> storeSaved = new ArrayList<>();
        results.moveToFirst();
        while (!results.isAfterLast()) {
            News news = new News();
            String id = results.getString(results.getColumnIndex(COL_ID));
            String title = results.getString(results.getColumnIndex(COL_WEB_TITLE));
            String section = results.getString(results.getColumnIndex(COL_SECTION_NAME));
            String url = results.getString(results.getColumnIndex(COL_WEB_URL));
            news.setId(id);
            news.setWebTitle(title);
            news.setSectionName(section);
            news.setWebUrl(url);
            storeSaved.add(news);
            results.moveToNext();
        }
        results.close();
        results = null;
        return storeSaved;
    }

    /**
     * @param id deletes selected news article
     */
    public void delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

}