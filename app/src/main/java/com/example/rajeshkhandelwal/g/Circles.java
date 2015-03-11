package com.example.rajeshkhandelwal.g;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;


public class Circles extends Fragment {
    TextView text;
    private GoogleApiClient mGoogleApiClient;
    public static com.google.api.services.plus.Plus plus;
    public Boolean isAuthenticated;
    private final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/plus_sample");
    private FileDataStoreFactory dataStoreFactory;
    private HttpTransport httpTransport;


    public Circles() {
        try
        {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            Credential credential = authorize();
            plus = new Plus.Builder(httpTransport,Constants.JSON_FACTORY, credential).setApplicationName(Constants.APP_NAME).build();
            isAuthenticated = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            isAuthenticated = false;
        }
    }
    private Credential authorize() throws Exception
    {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Constants.JSON_FACTORY, new InputStreamReader(Authenticator.class.getResourceAsStream("/com/example/plustests/client_secrets.json")));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,  Constants.JSON_FACTORY, clientSecrets, Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(dataStoreFactory).build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public void printFriendsList() throws IOException
    {
        Plus.People.List listPeople = plus.people().list("me", "visible");
        PeopleFeed peopleFeed = listPeople.execute();
        java.util.List<Person> people = peopleFeed.getItems();
        while(people != null)
        {
            for(Person person : people)
            {
                com.example.rajeshkhandelwal.g.View.printString(person.getDisplayName(), "Friend:");
            }
            if(peopleFeed.getNextPageToken() == null) break;
            listPeople.setPageToken(peopleFeed.getNextPageToken());
            peopleFeed = listPeople.execute();
            people = peopleFeed.getItems();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public static Circles newInstance(int position) {
        Circles Cfragment = new Circles();
        Bundle args = new Bundle();
        Log.i("pos", "position at" + position);
        args.putInt("position", position);
        Cfragment.setArguments(args);
        return Cfragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            printFriendsList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        View layout=inflater.inflate(R.layout.fragment_circles, container, false);
        // Inflate the layout for this fragment

        return layout;
    }

}

