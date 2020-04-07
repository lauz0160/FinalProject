package com.example.finalproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import java.util.List;
import android.view.View;
import android.widget.ListView;
import android.view.ViewGroup;


/**
 this fragment displays saved articles
 * */

public class Guardian_Fav extends Fragment {

    private ListView dbList;
    private List<News> newsList;

    /**
     * @param inflater inflates the layout
     * @param savedInstanceState
     * is called when the activity first starts up.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO  https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
        View inflate = inflater.inflate(R.layout.guardian_fav_frag, viewGroup, false);
        dbList = inflate.findViewById(R.id.listView);
        dbList.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent ( getActivity(), GuardianNewsList.class);
            intent.putExtra ("article", newsList.get(position));

            startActivity(intent);
        });

        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        Guardian_DataBase db = new Guardian_DataBase(getActivity());
        newsList = db.loadDataFromDatabase();
        GuardianListAdapter listAdapter = new GuardianListAdapter(getActivity(),  newsList);
        dbList.setAdapter(listAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();

    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
}