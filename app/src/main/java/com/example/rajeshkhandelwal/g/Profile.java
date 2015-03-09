package com.example.rajeshkhandelwal.g;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Profile extends Fragment {
    TextView textView;

    public static Profile newInstance(int position) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        Log.i("pos","position at"+position);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //Log.i("pos","position at"+position);


        View layout= inflater.inflate(R.layout.fragment_profile, container, false);
        textView= (TextView) layout.findViewById(R.id.position);

        Bundle bundle=getArguments();
        if(bundle!=null)
        {
         textView.setText("the page is"+bundle.getInt("position"));
        }
        return layout;
    }

}
