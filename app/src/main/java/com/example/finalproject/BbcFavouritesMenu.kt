package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_bbc_favourites_menu.*

class BbcFavouritesMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbc_favourites_menu)

        bbcBackToListButton.setOnClickListener{ finish() }
    }
}
