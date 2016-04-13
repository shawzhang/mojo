package com.example.mghlcs.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;

public class RootActivity extends AppCompatActivity {
    private UserSubAppTask mAuthTask = null;
    private ArrayList<String> subapps;
    private ArrayAdapter<String> adapter;
    private MojoConnection mojoConnection;
    private String userName;
    private Boolean verifiedUser = false;

 //   private final String MOJO_SERVICE_HOST = "https://oncallweb.partners.org/javatest2/mojo";
    private final String MOJO_SERVICE_HOST = "https://oncallweb.partners.org/javaprod2/mojo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

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
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }


        subapps = new ArrayList<>();
        adapter = new ArrayAdapter<String>(
                this,
                //android.R.layout.simple_list_item_1, android.R.id.text1,
                R.layout.root_apps,
                R.id.the_item_text,
                subapps
        );


        ListView definitionList = (ListView) findViewById(R.id.defination_list);
        definitionList.setAdapter(adapter);
        definitionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView definitionList = (ListView) findViewById(R.id.defination_list);
                String text = definitionList.getItemAtPosition(position).toString();
                if (text.equalsIgnoreCase("sub_name")) {
                    Toast.makeText(RootActivity.this, "You got it!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RootActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
//        AsyncHttpClient myClient = MojoConnection.myClient; //true to ignore ssl checking
//        PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
//        myClient.setCookieStore(myCookieStore);
//        RequestParams params = new RequestParams();
//        params.put("PartnersUsername", "");
//        //params.put("PartnersPassword","");
//        myClient.post("https://lcsmdc-nsvip-p-14.partners.org/default.asp", params, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.v("responseString", responseString);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Log.v("responseString", responseString);
//            }
//        });
        getUserSubApps();
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
            //String urlString = "https://oncallweb.partners.org/javaprod2/mojo/json/user";
            //String urlString = "https://oncallweb.partners.org";
            //String urlString = "https://lcs124.partners.org:8280/mojo/json/user?login=xz020";
            String urlString = "https://oncallweb.partners.org/javaprod2/mojo/json/user?login="+ userName;

            try {
                url = new URL(urlString);

                connection = (HttpsURLConnection) url.openConnection();

                //CookieManager cookieManager = mojoConnection.getCookieManager();


//                if(cookieManager.getCookieStore().getCookies().size() > 0)
//                {
//                    //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
//                    connection.setRequestProperty("Cookie",
//                            TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
//                    Log.v("Cookie", "Set Cookie Root: " + TextUtils.join(";", cookieManager.getCookieStore().getCookies()));
//                }

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
                                for (int i = 0; i < subappJsonArray.length(); i++) {
                                    JSONObject item = (JSONObject) subappJsonArray.get(i);
                                    String brandName = item.getString("description");
                                    String brandIcon = item.getString("iconPath");
                                    subapps.add(brandName);
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
                return false;

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Log.v("Display", subapps.toString());
                //adapter.notifyDataSetChanged();
                //finish();
            } else {
                Log.v("Display", "Failed");
            }
        }
    }
}
