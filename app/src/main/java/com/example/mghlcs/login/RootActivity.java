package com.example.mghlcs.login;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mghlcs.login.Objects.SubApp;
import com.example.mghlcs.login.Root.CustomListAdapter;
import com.example.mghlcs.login.utility.Constants;
import com.example.mghlcs.login.utility.MojoConnection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;

public class RootActivity extends AppCompatActivity {

    private static final int MENU_ITEM_LOGOUT = 1001;
    private static final String STATE_ITEMS = "items";

    private UserSubAppTask mAuthTask = null;
    private ArrayList<String> subapp_names;
    private ArrayList<Integer> subapp_images;
    private MojoConnection mojoConnection;
    private String userName;
    private Boolean verifiedUser = false;
    private ListView subAppList;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        //enable search
        //onSearchRequested();

        mojoConnection = MojoConnection.getInstance(getApplicationContext());
        mojoConnection.setConnection();
        //find username from cookie, check if user has credentials already

        List<HttpCookie> cookies = mojoConnection.getCookies();
        for (HttpCookie cookie : cookies) {
            Log.v("Cookie saved", "name: " + cookie.getName() + " value=" + cookie.getValue());
            if (cookie.getName().equalsIgnoreCase("OncallPartnersUsername")) {
                userName = cookie.getValue();
                verifiedUser = true;
            }
        }
        Log.v("Cookie userName", userName + " verified user? " + verifiedUser);
        if (!verifiedUser) {
            backToLogin();
            return;
        }

        subapp_names = new ArrayList<>();
        subapp_images = new ArrayList<>();

        adapter=new CustomListAdapter(this, subapp_names, subapp_images);

        subAppList = (ListView) findViewById(R.id.sub_app_list);
        subAppList.setAdapter(adapter);
        subAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = subAppList.getItemAtPosition(position).toString();
                if (text.equalsIgnoreCase(getResources().getString(R.string.subapp_name_transplant))) {
                    //Toast.makeText(RootActivity.this, "You got it!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RootActivity.this, TransplantActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(RootActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getUserSubApps();
    }

    private void backToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
    //Display menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //add one by code
        menu.add(0, MENU_ITEM_LOGOUT,102, R.string.logout);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                //Snackbar.make(coodinatorLayout, )
                  return true;
            case R.id.action_about:
                //
                return true;
            case MENU_ITEM_LOGOUT:
                //do something here, delete all cookies
                mojoConnection.removeCookies();
                //reload the page?
                Intent intent = new Intent(RootActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUserSubApps() {
        if (mAuthTask != null) {
            return;
        }
        //showProgress(true);
        mAuthTask = new UserSubAppTask();
        mAuthTask.execute((Void) null);

    }

    public class UserSubAppTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {


            URL url;
            HttpsURLConnection connection;
            String urlString = Constants.PROFILE_URL + "?login=" + userName;

            try {
                url = new URL(urlString);

                connection = (HttpsURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");


                int status = connection.getResponseCode();
                Log.v("Root Response Status:", ""+status);

                if (connection.getResponseCode() != 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                   Log.v("request error", sb.toString());
                    connection.disconnect();
                    return false;

                }
                String responseOutput =  mojoConnection.printResponse(connection);

/*
{"user":{"subapp":[{"description":"Partners Paging","iconPath":"page.png","id":"17","name":"Page","sortOrder":"40"},{"description":"Your Schedule","iconPath":"schedule.png","id":"14","name":"Schedule","sortOrder":"10"},{"description":"Synapse MVP","iconPath":"synapse.png","id":"26","name":"Synapse","sortOrder":"100"},{"description":"Messages","iconPath":"messages.png","id":"27","name":"Messaging","sortOrder":"100"},{"description":"Program Settings","iconPath":"settings.png","id":"18","name":"Settings","sortOrder":"50"},{"description":"Transplant list info","iconPath":"transplant.png","id":"19","name":"Transplant","sortOrder":"60"},{"description":"Your Rolodex","iconPath":"rolodex.png","id":"15","name":"Rolodex","sortOrder":"20"},{"description":"Apprentice","iconPath":"rounds.png","id":"16","name":"Apprentice","sortOrder":"30"}],"brandName":"HEPATOLOGY","email":"xzhang14@partners.org","group":[{"groupId":"12","name":"transplant"},{"groupId":"10","name":"core"},{"groupId":"18","name":"messaging"},{"groupId":"17","name":"synapse"}],"hasNoteWriteAccess":"true","id":"34","login":"xz020","mghProviderNumber":"","minimalNoteStatus":"prelim","name":{"first":"Xiaofeng","last":"Zhang","middleInitial":"","display":"Xiaofeng Zhang"},"noteBrand":"HEPATOLOGY","oncallId":"002125","status":"ACTIVE"}}
 */
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK ) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseOutput);
                        Iterator<?> keys = jsonObject.keys();
                        while(keys.hasNext() ) {
                            String key = (String)keys.next();
                            if ( jsonObject.get(key) instanceof JSONObject ) {
                                JSONObject subappJson = new JSONObject(jsonObject.get(key).toString());
                                String subAppString = subappJson.get("subapp").toString();
                                JSONArray subappJsonArray = new JSONArray(subAppString);

                                List<SubApp> subapplist= new ArrayList<SubApp>();

                                SubApp subApp;
                                for (int i = 0; i < subappJsonArray.length(); i++) {
                                    JSONObject item = (JSONObject) subappJsonArray.get(i);
                                    subApp = new SubApp(item);
                                    subapplist.add(subApp);
                                }
                                Collections.sort(subapplist, new Comparator <SubApp>() {
                                    @Override
                                    public int compare(SubApp subApp2, SubApp subApp1) {
                                        return subApp2.getBrandSortOrder().compareTo(subApp1.getBrandSortOrder());
                                    }
                                });
                                for (SubApp subapp:subapplist) {
                                    subapp_names.add(subapp.getBrandName());
                                    int imgId = getResources().getIdentifier(subapp.getBrandIconName().toLowerCase(), "drawable", getPackageName());

                                    subapp_images.add(imgId);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }  catch(MalformedURLException ex){
                Log.v("Authentication", "MalformedURLException " + ex.toString());
                return false;

            } catch (IOException e) {
                Log.v("Authentication", "IOException " + e.toString());
                //Go back to Login page
                backToLogin();
                return false;

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Log.v("Display", subapp_names.toString());
                Log.v("Display", subapp_images.toString());
                adapter.notifyDataSetChanged();
                //finish();
            } else {
                Log.v("Display", "Failed");
            }
        }

    }
}
