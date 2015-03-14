package com.example.rajeshkhandelwal.g;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;


public class Circles extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        ResultCallback<People.LoadPeopleResult>, OnConnectionFailedListener,
        DialogInterface.OnCancelListener {
    TextView text;
    public static com.google.api.services.plus.Plus plus;



        private static final String TAG = "ListConnectedPeople";

        private static final String STATE_RESOLVING_ERROR = "resolving_error";


        private static final int REQUEST_CODE_SIGN_IN = 1;
        private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

        private ArrayAdapter mListAdapter;
        private ListView mPersonListView;
        private ArrayList<String> mListItems;
        private GoogleApiClient mGoogleApiClient;
        private boolean mResolvingError;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();


        }



        @Override
        public void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        }

        @Override
        public void onStop() {
            super.onStop();
            mGoogleApiClient.disconnect();
        }



    @Override
    public void onConnected(Bundle connectionHint) {
        mPersonListView.setAdapter(mListAdapter);
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mPersonListView.setAdapter(null);
        mGoogleApiClient.connect();
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Log.e(TAG, "Unable to sign the user in.");
    }


    public static Circles getInstance(String position)
    {
        Log.i("pos", "pos" + position);
        Circles circle=new Circles();
        Bundle args=new Bundle();
        args.putString("position", position);
        circle.setArguments(args);
        return circle;

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_circles, container, false);
        mPersonListView = (ListView)layout.findViewById(R.id.person_list);

        mListItems = new ArrayList<String>();
        mListAdapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , mListItems);

        mPersonListView.setAdapter(mListAdapter);


        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        text= (TextView) layout.findViewById(R.id.textView);
        Bundle bundle = getArguments();
        if(bundle!=null) {


            text.setText("the page is"+bundle.getString("position"));
        }
 //       String UserId = bundle.getString("UserId");
        // Inflate the layout for this fragment

        return layout;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
       /* if (mResolvingError) {
            return;
        }

        mPersonListView.setAdapter(null);
        try {
            result.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
            mResolvingError = true;
        } catch (IntentSender.SendIntentException e) {
            // Get another pending intent to run.
            mGoogleApiClient.connect();
        }
        */
    }



        @Override
        public void onResult(People.LoadPeopleResult peopleData) {
            if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                PersonBuffer personBuffer = peopleData.getPersonBuffer();
                try {
                    int count = personBuffer.getCount();
                    for (int i = 0; i < count; i++) {
                        Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                    }
                } finally {
                    personBuffer.close();
                }
            } else {
                Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
            }
        }



}

