package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.finalproject.MyDatabaseHelper.Companion.KEY_FAVOURITE
import com.example.finalproject.MyDatabaseHelper.Companion.KEY_ID
import com.example.finalproject.R.layout.row_layout_article
import kotlinx.android.synthetic.main.activity_bbc_news_reader.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class BbcNewsReader : AppCompatActivity() {

    var elements = java.util.ArrayList<Article>()
    var isPhone : Boolean? = null
    var df : BbcNewsDetailsFragment? = null
    val adapter = BbcArticleAdapter(this, elements)
    var dbHelper : MyDatabaseHelper? = null
    var db : SQLiteDatabase? = null
    val TITLE = "TITLE"
    val ARTICLE_ID = "ARTICLE_ID"
    val DESCRIPTION = "DESCRIPTION"
    val PUBDATE = "PUBDATE"
    val LINK = "LINK"
    val FAVOURITE = "FAVOURITE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbc_news_reader)
        progressBarBBC.visibility = INVISIBLE
        isPhone = (findViewById<FrameLayout>(R.id.frameLayout) == null)

        dbHelper = MyDatabaseHelper(this)
        db = dbHelper!!.writableDatabase

        val articleList = findViewById<ListView>(R.id.bbcNewsList)
        articleList.adapter = adapter

        // Uncomment this block if you want to add all existing articles in db to the list on each load.
        // The db articles would load above the articles from the rss feed as it currently is,
        // due to the id numbering from the db, and the call to loadArticlesFromXml being below.
        //
        /*val cursorOfMessages = db!!.rawQuery("SELECT * FROM ArticlesTable", null)
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

                elements.add(tempArticle)
                adapter.notifyDataSetChanged()
            }
        }
        cursorOfMessages.close()*/

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
                    .setPositiveButton("Favourite") { dialog, _ ->
                        val article = elements[position]
                        article.isFavourite = true

                        db = dbHelper!!.writableDatabase
                        var updateString = "UPDATE ArticlesTable SET $KEY_FAVOURITE = 1 WHERE $KEY_ID ="+ elements[position].id
                        db!!.execSQL(updateString)
                    }
                    .setNeutralButton("Unfavourite") { dialog, _ ->
                        val article = elements[position]
                        article.isFavourite = false

                        db = dbHelper!!.writableDatabase
                        var updateString = "UPDATE ArticlesTable SET $KEY_FAVOURITE = 0 WHERE $KEY_ID ="+ elements[position].id
                        db!!.execSQL(updateString)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel()}
            val alert = builder.create()
            alert.setTitle("Do you want to favourite this?")
            alert.show()
            return@setOnItemLongClickListener true
        }
        mainMenuButton.setOnClickListener { finish()}
        favouritesButton.setOnClickListener{ startActivity(Intent(this, BbcFavouritesMenu::class.java))}

        loadArticlesFromXml()
    }

    fun loadArticlesFromXml(){

        ArticleQuery().execute()
        Toast.makeText(this, "All articles loaded from BBC", Toast.LENGTH_SHORT).show()

    }

    fun createArticle(title: String, description: String, pubDate: String, link: String) : Article{
        val article = Article(title = title, description =  description,link =  link, pubDate =  pubDate, isFavourite = false)
        article.id = dbHelper!!.addArticle(article)

        elements.add(article)
        adapter.notifyDataSetChanged()

        return article
    }

        inner class ArticleQuery : AsyncTask<String, Int, String>() {
            var title : String = ""
            var description : String = ""
            var pubDate : String = ""
            var link : String = ""
            override fun doInBackground(vararg params: String?): String {
                val bbcUrl = "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml"

                var ret = ""

                try {
                    val url = URL(bbcUrl)
                    val urlConnection = url.openConnection() as HttpURLConnection
                    val inStream = urlConnection.inputStream

                    //Set up the XML parser:
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = false
                    val xpp = factory.newPullParser()
                    xpp.setInput(inStream, "UTF-8")


                    var eventType: Int = xpp.eventType      //While not the end of the document:
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        eventType = xpp.eventType
                        when (eventType) {
                            XmlPullParser.START_TAG         //This is a start tag < ... >
                            -> {
                                if(xpp.name == "item")
                                    Log.i("ArticleFound","New Article Found")
                                Thread.sleep(1)
                                when (xpp.name) {
                                    "title" -> {
                                        xpp.next()
                                        title = xpp.text
                                        publishProgress(25)
                                    }
                                    "description" -> {
                                        xpp.next()
                                        description = xpp.text
                                        publishProgress(50)
                                    }
                                    "link" -> {
                                        xpp.next()
                                        link = xpp.text
                                        publishProgress(75)
                                    }
                                    "pubDate" -> {
                                        xpp.next()
                                        pubDate = xpp.text
                                        publishProgress(100)
                                    }
                                    null -> {

                                    }
                                }
                            }
                            XmlPullParser.END_TAG           //This is an end tag: </ ... >
                            -> {
                            }
                            XmlPullParser.TEXT              //This is text between tags < ... > Hello world </ ... >
                            -> {
                            }
                        }
                        xpp.next() // move the pointer to next XML element
                    }
                }
                catch(ex: MalformedURLException){
                    ret = "MalformedUrlException"
                }
                catch(ex : IOException ){
                    ret = "IO Exception"
                }
                catch(ex : XmlPullParserException){
                    ret = "XML Pull Exception. The XML is not properly formed"
                }
                return ret
            }

            override fun onProgressUpdate(vararg progress: Int?){
                super.onProgressUpdate(progress[0])
                //progressBarBBC.visibility = View.VISIBLE
                //progressBarBBC.progress = progress
                if(progress[0] == 100)
                    createArticle(title, description, pubDate, link)
            }
        }
    }


class BbcArticleAdapter(private val context: Context, private val dataSource: ArrayList<Article>) : BaseAdapter(){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val currentArticle = getItem(position) as Article
        val newView : View

        //make a new row:
        newView = inflater.inflate(row_layout_article, parent, false)

        //set what the text should be for this row:
        val tViewTitle = newView.findViewById<TextView>(R.id.articleTitle)
        tViewTitle.text = currentArticle.toString()

        //return it to be put in the table
        return newView
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


class MyDatabaseHelper(context: Context) :  SQLiteOpenHelper(context,
        DATABASE_NAME,null,
        DATABASE_VERSION
) {

    companion object {
        val DATABASE_VERSION = 1
        const val DATABASE_NAME = "ArticleDatabase"
        const val TABLE_ARTICLES = "ArticlesTable"
        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_PUBDATE = "pubDate"
        const val KEY_LINK = "link"
        const val KEY_FAVOURITE = "favourite"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ARTICLES_TABLE = ("CREATE TABLE $TABLE_ARTICLES ( $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_TITLE TEXT, $KEY_DESCRIPTION INTEGER, $KEY_PUBDATE TEXT, $KEY_LINK TEXT, $KEY_FAVOURITE INTEGER)")
        db?.execSQL(CREATE_ARTICLES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ARTICLES")
        onCreate(db)
    }

    fun addArticle(art : Article): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        //contentValues.put(KEY_ID, msg.id)
        contentValues.put(KEY_TITLE, art.title)
        contentValues.put(KEY_DESCRIPTION, art.description)
        contentValues.put(KEY_PUBDATE, art.pubDate)
        contentValues.put(KEY_LINK, art.link)
        contentValues.put(KEY_FAVOURITE, art.isFavourite)

        val success = db.insert(TABLE_ARTICLES, null, contentValues)

        db.close()
        return success
    }

    fun viewArticles():List<Article>{
        val artList:ArrayList<Article> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_ARTICLES"
        val db = this.readableDatabase
        var cursor: Cursor
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var artId: Long
        var artTitle: String
        var artDescription: String
        var artLink: String
        var artPubDate: String
        var artFavourite: Boolean = false

        Log.d("testing", cursor.getColumnName(0)  )
        if (cursor.moveToFirst()) {
            do {
                cursor.moveToNext()
                artId = cursor.getInt(cursor.getColumnIndex("Id")).toLong()
                artTitle = cursor.getString(cursor.getColumnIndex("Title"))
                artDescription = cursor.getString(cursor.getColumnIndex("Description"))
                artLink = cursor.getString(cursor.getColumnIndex("Link"))
                artPubDate = cursor.getString(cursor.getColumnIndex("PubDate"))
                artFavourite = cursor.getInt(cursor.getColumnIndex("Favourite")) == 1

                val art= Article(
                        id = artId,
                        title = artTitle,
                        description = artDescription,
                        link = artLink,
                        pubDate = artPubDate,
                        isFavourite = artFavourite
                )
                artList.add(art)
            } while (cursor.moveToNext())
        }
        return artList
    }

    fun deleteArticle(art: Article):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, art.id)
        // Deleting Row
        val success = db.delete(TABLE_ARTICLES,"id="+art.id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}


class Article(val title: String, val description: String, var id: Long? = 0, var link: String, var pubDate: String, var isFavourite: Boolean = false) {
    override fun toString(): String { return title }
}