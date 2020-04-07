package com.example.finalproject;

import java.io.Serializable;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.List;

//https://github.com/prafulnayak/NewsUltimate/blob/master/app/src/main/java/org/sairaa/newsultimate/News.java

/**
 * getters and setters for a new artical
 * contains all the necessary info to be used
 * ---------------------------------
 * contains inner listAdapter - > customize's list view layout for the rows in the list
 */
public class News implements Serializable {

    private String id;
    private String webTitle;
    private String sectionName;
    private String webUrl;

    //setters getters
    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getWebTitle() {
        return webTitle;
    }

    void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    String getSectionName() {
        return sectionName;
    }

    void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    String getWebUrl() {
        return webUrl;
    }

    void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

}

//https://stackoverflow.com/questions/20994728/what-is-the-correct-way-to-get-layout-inflater-in-android

 class GuardianListAdapter extends BaseAdapter {

    List<News> newsList;
    LayoutInflater inflater;
     //LayoutInflater inflater = getLayoutInflater();

    GuardianListAdapter(Context context, List<News> newsList) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public View getView(int position, View old, ViewGroup parent) {
        //inflate layout and set fields
        old = inflater.inflate(R.layout.guardian_article, parent, false);
        News article = newsList.get(position);
        String title = article.getWebTitle();
        String section = article.getSectionName();
        TextView sectionName = old.findViewById(R.id.textView4);
        TextView newsTitle = old.findViewById(R.id.textView1);
        sectionName.setText(section);
        newsTitle.setText(title);
        return old;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

}


