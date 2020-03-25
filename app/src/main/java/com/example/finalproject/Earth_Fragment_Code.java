package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;


/**
 * This fragment contains the info about a certain image from the list
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Earth_Fragment_Code.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Earth_Fragment_Code extends Fragment {

    //private OnFragmentInteractionListener mListener;
    private AppCompatActivity parentActivity;

    public Earth_Fragment_Code() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //local variables
        Bundle dataFromActivity = getArguments();

        //inflates the fragment
        View result = inflater.inflate(R.layout.activity_fragment_code, container, false);

        //pulls the image file name from the data that was passed to the fragment and decodes it to a bitmap
        if (dataFromActivity != null) {
            try {
                //set the image view to the bitmap image from the file
                ImageView image = result.findViewById(R.id.viewImage);
                image.setImageBitmap(BitmapFactory.decodeStream(parentActivity.openFileInput(dataFromActivity.getString("file"))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //set the text of the date box to the date data passed into the fragment
            TextView dateBox = result.findViewById(R.id.date);
            dateBox.setText(dataFromActivity.getString("date"));

            //set the text of the coordinates box to the coordinate data passed into the fragment
            TextView coordinates = result.findViewById(R.id.coordinates);
            coordinates.setText(dataFromActivity.getString("coordinates"));

            //set the hide button to close the fragment and the delete button to delete the item then close the fragment
            result.findViewById(R.id.hide).setOnClickListener(btn -> parentActivity.finish());
            result.findViewById(R.id.delete).setOnClickListener(btn -> startActivity(new Intent(parentActivity, Earth_Favorites.class).putExtra("position", dataFromActivity.getInt("position"))));
        }
        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href="http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
