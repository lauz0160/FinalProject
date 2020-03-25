package com.example.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class creates the fragment and places it into the frame of the empty layout
 */
public class Earth_Fragment_Frame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_frame);

        //creates new fragment using the arguments passed from the favorites screen based on which item was clicked and passes that info to the fragment
        Earth_Fragment_Code dFragment = new Earth_Fragment_Code();
        dFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.frame1, dFragment).commit();
    }
}
