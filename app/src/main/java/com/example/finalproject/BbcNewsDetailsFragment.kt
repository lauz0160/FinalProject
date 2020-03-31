package com.example.finalproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BBC_news_details_fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BBC_news_details_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BbcNewsDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }*/

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bbc_news_details_fragment, container, false)

        val titleView: TextView = view.findViewById(R.id.titleTextDetails)
        val descriptionView: TextView = view.findViewById(R.id.descriptionTextDetails)
        val pubDateView: TextView = view.findViewById(R.id.pubDateTextDetails)
        val linkView: TextView = view.findViewById(R.id.linkTextDetails)
        val isFavouriteView: TextView = view.findViewById(R.id.isFavouriteTextDetails)

        val dataFromActivity = arguments
        if (dataFromActivity != null) {
            titleView.text = dataFromActivity.getString(BbcNewsReader().TITLE)
            descriptionView.text = dataFromActivity.getString(BbcNewsReader().DESCRIPTION)
            pubDateView.text = dataFromActivity.getString(BbcNewsReader().PUBDATE)
            linkView.text = dataFromActivity.getString(BbcNewsReader().LINK)
            if(dataFromActivity.getBoolean(BbcNewsReader().FAVOURITE))
                isFavouriteView.text = getString(R.string.ifFavourited) else isFavouriteView.text = ""
        }
        linkView.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(dataFromActivity!!.getString(BbcNewsReader().LINK)))
            startActivity(browserIntent)
        }
        val backButton : Button = view.findViewById(R.id.backButton)
        backButton.setOnClickListener { clk ->
            //For tablet:
            if (!dataFromActivity!!.getBoolean("isPhone")) { //both the list and details are on the screen:
                val parent = activity as BbcNewsDetails
                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.supportFragmentManager.beginTransaction().remove(this).commit()
            }

            //For phone:
            else
            //You are only looking at the details, you need to go back to the previous list page
            {
                val parent = activity as BbcNewsDetails?
                val backToFragmentExample = Intent()
                backToFragmentExample.putExtra("bundle", arguments )

                parent!!.setResult(
                        Activity.RESULT_OK,
                        backToFragmentExample
                ) //send data back to FragmentExample in onActivityResult()
                parent.finish() //go back
            }
        }


        return view
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BBC_news_details_fragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BbcNewsDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
