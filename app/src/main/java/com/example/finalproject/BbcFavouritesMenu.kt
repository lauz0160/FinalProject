package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_bbc_favourites_menu.*
import kotlinx.android.synthetic.main.activity_bbc_news_reader.*

class BbcFavouritesMenu : AppCompatActivity() {

    var elements = java.util.ArrayList<Article>()
    var isPhone: Boolean? = null
    var df: BbcNewsDetailsFragment? = null
    private val adapter = BbcArticleFavouritesAdapter(this, elements)
    var dbHelper: MyDatabaseHelper? = null
    var db: SQLiteDatabase? = null
    val TITLE = "TITLE"
    val ARTICLE_ID = "ARTICLE_ID"
    val DESCRIPTION = "DESCRIPTION"
    val PUBDATE = "PUBDATE"
    val LINK = "LINK"
    val FAVOURITE = "FAVOURITE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbc_favourites_menu)

        bbcBackToListButton.setOnClickListener { finish() }
        isPhone = (findViewById<FrameLayout>(R.id.frameLayout) == null)

        dbHelper = MyDatabaseHelper(this)
        db = dbHelper!!.writableDatabase
        val articleList = findViewById<ListView>(R.id.bbcFavouritesListView)
        articleList.adapter = adapter

        val cursorOfMessages = db!!.rawQuery("SELECT * FROM ArticlesTable", null)
        cursorOfMessages.moveToFirst()
        while (cursorOfMessages.moveToNext()) {
            if (!cursorOfMessages.isAfterLast) {
                val id = cursorOfMessages.getInt(0).toLong()
                val title = cursorOfMessages.getString(1)
                val description = cursorOfMessages.getString(2)
                val pubDate = cursorOfMessages.getString(3)
                val link = cursorOfMessages.getString(4)
                val isFavourite: Boolean = cursorOfMessages.getInt(5) == 1
                val tempArticle = Article(title, description, id, link, pubDate, isFavourite)

                if(tempArticle.isFavourite)
                    elements.add(tempArticle)
                adapter.notifyDataSetChanged()
            }
        }
        cursorOfMessages.close()

        adapter.notifyDataSetChanged()

        articleList.setOnItemClickListener { list, view, position, id ->
            run {
                val articleBundle = Bundle()
                articleBundle.putString(TITLE, elements[position].title)
                articleBundle.putLong(ARTICLE_ID, id)
                articleBundle.putString(DESCRIPTION, elements[position].description)
                articleBundle.putString(PUBDATE, elements[position].pubDate)
                articleBundle.putString(LINK, elements[position].link)
                articleBundle.putBoolean(FAVOURITE, elements[position].isFavourite)

                articleBundle.putBoolean("isPhone", isPhone!!)
                if(!isPhone!!) {
                    val fragTransaction = supportFragmentManager.beginTransaction()
                    df = BbcNewsDetailsFragment()
                    df!!.arguments = articleBundle
                    fragTransaction.replace(R.id.frameLayout, df!!)
                    fragTransaction.commit()
                }
                else{
                    val intent = Intent(this, BbcNewsDetails::class.java)
                    intent.putExtra("bundle", articleBundle)
                    startActivity(intent)
                }
            }
        }
        articleList.setOnItemLongClickListener { parent, view, position, id ->

            val builder = AlertDialog.Builder(this)

            builder.setMessage("The article selected is:\n"+elements[position].title+" \n")

                    .setCancelable(false)
                    .setPositiveButton("Unfavourite") { dialog, _ ->
                        val article = elements[position]
                        article.isFavourite = false
                        db = dbHelper!!.writableDatabase
                        var updateString = "UPDATE ArticlesTable SET ${MyDatabaseHelper.KEY_FAVOURITE} = 0 WHERE ${MyDatabaseHelper.KEY_ID} ="+ elements[position].id
                        db!!.execSQL(updateString)
                        elements.remove(elements[position])
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel()}
            val alert = builder.create()
            alert.setTitle("Do you want to remove this from favourites?")
            alert.show()
            return@setOnItemLongClickListener true
        }
    }
}

class BbcArticleFavouritesAdapter(private val context: Context, private val dataSource: ArrayList<Article>) : BaseAdapter(){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val currentArticle = getItem(position) as Article
        val newView : View

        if(currentArticle.isFavourite) {
            //make a new row:
            newView = inflater.inflate(R.layout.row_layout_article, parent, false)

            //set what the text should be for this row:
            val tViewTitle = newView.findViewById<TextView>(R.id.articleTitle)
            tViewTitle.text = currentArticle.toString()

            //return it to be put in the table
            return newView
        }
        return null
    }

    override fun getItem(position: Int): Any {
        //return "This is row $position"
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return dataSource[position].id!!
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}