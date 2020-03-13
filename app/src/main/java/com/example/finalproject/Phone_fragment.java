package com.example.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Phone_fragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_fragment);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        DetailsFragment dFragment = new DetailsFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame1, dFragment)
                .commit();
    }
}
