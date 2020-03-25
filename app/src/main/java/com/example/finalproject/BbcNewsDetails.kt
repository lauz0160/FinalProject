package com.example.finalproject

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BbcNewsDetails : AppCompatActivity(), BbcNewsDetailsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbc_news_details)

        val bundleArgs = intent.getBundleExtra("bundle")
        val fragTransaction = supportFragmentManager.beginTransaction()
        val df = BbcNewsDetailsFragment()
        df.arguments = bundleArgs
        fragTransaction.replace(R.id.frameLayout, df)
        fragTransaction.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
