package com.example.mghlcs.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mghlcs.login.Objects.KidneyHotList;
import com.example.mghlcs.login.utility.Constants;
import com.example.mghlcs.login.utility.KidneyListAdapter;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static com.loopj.android.http.AsyncHttpClient.log;

public class TransplantActivity extends AppCompatActivity {

    private static String webUrl = "http://www.google.com";
    private static String email = "info@test.com";
    private ArrayList<String> definations;
    private KidneyListAdapter adapter;

    private ListView mListView;

    private ArrayList<KidneyHotList> hotListArrayList= new ArrayList<KidneyHotList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transplant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        adapter = new ArrayAdapter<String>(
                TransplantActivity.this,
                R.layout.content_transplant,
                R.id.patientCell,
                hotListArrayList
        );
*/


        //Get data
        String hot_list_url = Constants.TX_HOT_LIST_URL + "?organ=Kidney&abo=o";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(hot_list_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseBody = response.body().string();
                    Log.v("list",responseBody);
                    processData(responseBody);
                }
            }
        });

        mListView = (ListView)findViewById(R.id.hot_list_view);
        Log.v("list length", Integer.toString(hotListArrayList.size()));
        adapter = new KidneyListAdapter(this,hotListArrayList);
        mListView.setAdapter(adapter);
     }

    private void processData(String data) {
        //Json
        try {
            JSONObject jsonRootObject = new JSONObject(data);

                        JSONObject patientsRoot = jsonRootObject.getJSONObject("patients");

                        //Get the instance of JSONArray that contains JSONObjects
                        JSONArray jsonArray = patientsRoot.optJSONArray("patient");

                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            KidneyHotList kidneyHotList = new KidneyHotList(jsonObject);
                            hotListArrayList.add(kidneyHotList);
                            /*
                            String mrn = jsonObject.optString("mrn").toString();
                            String dob = jsonObject.optString("dob").toString();
                            String name = jsonObject.optString("name").toString();
                            */
                            Log.v("list length", Integer.toString(hotListArrayList.size()));
                            //Log.v("list","mrn=" + kidneyHotList.getMrn() + " dob=" + kidneyHotList.getAbo() + " name=" + kidneyHotList.getPatientName());
                            //data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
                        }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui//
                     adapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {e.printStackTrace();}
        //definations.add(response.body().string());

    }
    //Display menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            /*
            case R.id.action_web:
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
                return true;
            case R.id.action_cart:
                return true;
                */
        }
        return super.onOptionsItemSelected(item);
    }
}
