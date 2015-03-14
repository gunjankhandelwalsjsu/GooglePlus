package com.example.rajeshkhandelwal.g;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

import Tabs.SlidingTabLayout;



public class User extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    GoogleApiClient mGoogleApiClient;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private Button SignOutBtn, RevokeAccessbtn;
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    private LinearLayout llProfileLayout;
    public  Person currentPerson;
    public String userId1;
    String SCOPE = "oauth2:" + "https://www.googleapis.com/auth/plus.me" + "https://www.googleapis.com/auth/plus.circles.read" + "https://www.googleapis.com/auth/plus.circles.write" + "";
    String userID3;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setLogoDescription(getResources().getString(R.string.GooglePlus));
        toolbar.setLogo(R.drawable.download);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        SignOutBtn = (Button) findViewById(R.id.btn_sign_out);
        RevokeAccessbtn = (Button) findViewById(R.id.btn_revoke_access);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        SignOutBtn.setOnClickListener(this);
        RevokeAccessbtn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mPager);


    }

    /**
     * Button on click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGooglePlus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGooglePlusAccess();
                break;
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGooglePlus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }


    /**
     * Revoking access from google
     */
    private void revokeGooglePlusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e("inOn Rsult", "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }

                    });
        }
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            SignOutBtn.setVisibility(View.VISIBLE);
            RevokeAccessbtn.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);


        } else {
            SignOutBtn.setVisibility(View.GONE);
            RevokeAccessbtn.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private String getProfileInformation() {

        try {
            if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String userID = currentPerson.getId();
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("UserActivity", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + "UserID>>>>" + userID);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
                return userID;

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
        NavUtils.navigateUpFromSameTask(this);
        return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

            Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

            // Get user's information
             userID3=getProfileInformation();
        setUserId(userID3);
            // Update the UI after signin
            updateUI(true);


    }
    public void setUserId(String userId) {
        this.userId = userId;
        Log.i("tag","in setData"+userId);


    }

    public String getMyData() {
        Log.i("tag","in getdata"+userId);

        return userId;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        updateUI(false);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    class MyPagerAdapter extends FragmentPagerAdapter {
      String[] tabs;
      public MyPagerAdapter(FragmentManager fm) {
          super(fm);
          tabs = getResources().getStringArray(R.array.tabs);
      }


      @Override
      public Fragment getItem(int position) {
          Log.i("Im Here", "pos" + position);

          if (position == 0) // if the position is 0 we are returning the First tab
          {
              Profile profile = new Profile();

              return profile;
          } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
          {

              Circles frag = Circles.getInstance(getMyData());

              return frag;
          }

      }



        public CharSequence getPageTitle(int position)
        {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.i("image","image");
            bmImage.setImageBitmap(result);
        }
    }

}
