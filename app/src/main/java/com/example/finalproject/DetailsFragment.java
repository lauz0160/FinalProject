package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private AppCompatActivity parentActivity;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle dataFromActivity;
        dataFromActivity = getArguments();

        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        FileInputStream fis = null;
        String file=dataFromActivity.getString("file");
        if( file !=null) {
            try {
                fis = parentActivity.openFileInput(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Bitmap pic = BitmapFactory.decodeStream(fis);

        ImageView image = result.findViewById(R.id.viewImage);
        image.setImageBitmap(pic);

        TextView dateBox = result.findViewById(R.id.date);
        dateBox.setText(dataFromActivity.getString("date"));

        TextView coordinates = result.findViewById(R.id.coordinates);
        coordinates.setText(dataFromActivity.getString("coordinates"));

        Button hide = result.findViewById(R.id.hide);
        hide.setOnClickListener(btn -> parentActivity.finish());

        Button delete = result.findViewById(R.id.delete);
        delete.setOnClickListener(btn -> {
            Intent a =new Intent(parentActivity , Earth_Favorites.class);
            a.putExtra("position",dataFromActivity.getInt("position"));
            startActivity(a);
        });

        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
