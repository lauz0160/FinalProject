package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.widget.ProgressBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * this fragment communicates with API to exchange data
 */
public class Guardian_Search extends Fragment {

    private ProgressBar progressBar;
    private EditText seditText;

    private GuardianListAdapter listAdapter;
    private List<News> newsList;

    /**
     * @param inflater inflate for this corresponding layout
     * @param savedInstanceState is called when the activity first starts up.
     * @return
     */
    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // super.onCreateView(savedInstanceState);
        View inflate = inflater.inflate(R.layout.guardian_get_data, viewGroup, false);
        progressBar = inflate.findViewById(R.id.progressBar);
        ImageButton imageButton = inflate.findViewById(R.id.imageButton);
        seditText = inflate.findViewById(R.id.editText);
        imageButton.setOnClickListener(v -> {
            String query = seditText.getText().toString().trim();
            new MyHTTPRequest(query).execute();
        });

        //        getSupportFragmentManager().beginTransaction().replace(
//                id , fragment  ).   addToBackStack  (“any string here”).commit();
        ListView listView = inflate.findViewById(R.id.listView);
        newsList = new ArrayList<>();
        listAdapter = new GuardianListAdapter(getActivity(), newsList);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), GuardianNewsList.class);
            intent.putExtra("article", newsList.get(position));
            startActivity(intent);
        });

        //save last query and show
        //week6_http
        String lastSearch = returnSharedPreferences();
        MyHTTPRequest req = new MyHTTPRequest(lastSearch);
        req.execute();
        return inflate;
    }


    /**
     * @param findLastList save the last search term
     * private file can only be accessed using calling application
     */
    private void saveSharedPreferences(String findLastList){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GN", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("findLastList",findLastList).commit();
    }


    /**
     * @return the last search term
     *  private file can only be accessed using calling application
     */
    private String returnSharedPreferences(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GN", Context.MODE_PRIVATE);
        String findLastList = sharedPreferences.getString("findLastList","");
        return findLastList;
    }


    //week6_http
    private class MyHTTPRequest extends AsyncTask<String, String, String> {
        //https://open-platform.theguardian.com/documentation/search
        String url = "https://content.guardianapis.com/search?api-key=7ede6108-368d-4fca-981c-8de9ed102ce9&q=ref=";
        String q;
        // https://stackoverflow.com/questions/3286067/url-encoding-in-android

        /**
         * @param q query comes in and is endoded
         * utility for HTML form encoding
         */
        MyHTTPRequest(String q) {
            this.q = q;
            try {
                //   FIX ME:    if (q == ""||null ) {
                //        Snackbar.make(findViewById(R.id.viewView), "", Snackbar.LENGTH_SHORT).setText("No search found").setAction
                //        ("Please attempt search again", click -> finish()).show();
                //     }
                this.url = url+ URLEncoder.encode(q,"UTF-8");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * start progressbar at beginning of search
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility (View.VISIBLE);
        }


        //week6_http
        //https://stackoverflow.com/questions/32958164/post-request-with-httpurlconnetion-and-read-response-from-server-android
        @Override
        protected String doInBackground(String... args) {
            //initialize empty
            String data = " ";
            //get a new connection to the url and set it
            try {
                //url object of the guardian server
                URL thisUrl = new URL(url);
                //open connection
                HttpURLConnection connection = (HttpURLConnection) thisUrl.openConnection();

                //wait for data
                // FIXME: InputStream response = thisUrl.getInputStream();
                data = readStream(connection.getInputStream());
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
            } catch (Exception e) {
                // Snackbar.make(findViewById(R.id.textView), "", Snackbar.LENGTH_SHORT).setText("no data found").setAction
                //     ("Please try again", click ->return()).show();
                e.printStackTrace();
            }
            return data;
        }

        //https://android.developreference.com/article/16189496/Android+6.0+HtppURLConnection+Crash+Error

        /**
         * @param in
         * @return reads the input stream from the HTTP search
         */
        private String readStream(InputStream in) {
            BufferedReader br = null;
            String line = null;
            InputStreamReader inStr = new InputStreamReader(in);
            br = new BufferedReader(inStr);
            StringBuilder sb = new StringBuilder();
            try {
                if ((line = br.readLine()) == null);
                br.reset();
            } catch (IOException e) { e.printStackTrace(); }

            return sb.append(line).toString();
        }

        //updates for  gui
        @Override
        protected void onProgressUpdate(String... args) {
            super.onProgressUpdate(args);
        }

        //https://stackoverflow.com/questions/47996330/getting-a-string-out-of-a-jsonobject
        //https://stackoverflow.com/questions/51546424/issue-extracting-json-response-data
        /**
         * @param fromDoInBackground invoked on the UI thread after the background computation finishes.
         *                          The result of the background computation is passed to this step as a parameter
         */
        @Override
        protected void onPostExecute(String fromDoInBackground) {
            super.onPostExecute(fromDoInBackground);
            Log.i("HTTP", fromDoInBackground);
            saveSharedPreferences(q);

            try {
                JSONObject mainJson = new JSONObject(fromDoInBackground);
                JSONObject webReply = mainJson.getJSONObject("response");
                String status = webReply.getString("status");

                if (status == null || status.length() == 0) {
                    Toast.makeText(getActivity(), "try again", Toast.LENGTH_SHORT);
                } else {
                    //clear for new search
                    newsList.clear();

                    //https://stackoverflow.com/questions/48237294/json-parsing-error-data-not-displaying/48237402
                    JSONArray results = webReply.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {

                        Log.d("SAMPLE", results.get(i).toString());

                        JSONObject news = results.getJSONObject(i);
                        String id = news.getString("id");
                        String title = news.getString("webTitle");
                        String section = news.getString("sectionName");
                        String webUrl = news.getString("webUrl");
                        News nNews = new News();
                        nNews.setId(id);
                        nNews.setWebTitle(title);
                        nNews.setSectionName(section);
                        nNews.setWebUrl(webUrl);
                        newsList.add(nNews);
                        //  Log.i("main",article.getString("main"));
                        // Log.i("description",article.getString("description"));
                    }
                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.e("QueryError", "Problem parsing the news JSON results", e);
                e.printStackTrace();
            }
        }
    }
    private boolean findViewById(int textView) {
        return true;
    }

}
