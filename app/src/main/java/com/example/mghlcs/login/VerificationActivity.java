package com.example.mghlcs.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mghlcs.login.utility.Constants;
import com.example.mghlcs.login.utility.MojoConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.CookieManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;


public class VerificationActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private TextView verificationView;
    private View mProgressView;
    private MojoConnection mojoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mojoConnection = MojoConnection.getInstance(getApplicationContext());
        mojoConnection.setConnection();

        //check the code is correct
        verificationView = (TextView) findViewById(R.id.verification_code);

        mProgressView = findViewById(R.id.login_progress);

        verificationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.verification_code || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button verificationButton = (Button) findViewById(R.id.verification_code_sign_in_button);
        verificationButton.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      attemptLogin();
                                                  }
                                              }
        );
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private boolean taskResult;

        //TextView verificationView = (TextView) findViewById(R.id.verification_code);
        final String verificationCode = verificationView.getText().toString();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("userName");
        final String password = intent.getStringExtra("password");

//        UserLoginTask(String userName, String password) {
//            userName = userName;
//            password = password;
//        }



        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v("Authentication", "Verification now: username= " + username + " password: " + password + " ConfirmationKey: " + verificationCode);
            String urlString = Constants.VERIFY_URL;

            URL url;
            HttpsURLConnection connection;

            try {

                url = new URL(urlString);
                connection = (HttpsURLConnection) url.openConnection();
                //CookieManager cookieManager = mojoConnection.getCookieManager();
                String param="PartnersUsername=" + URLEncoder.encode(username,"UTF-8")+ "&PartnersPassword="+URLEncoder.encode(password,"UTF-8") + "&ConfirmationKey="+verificationCode+"&IsPrivateClient=Yes";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(param);
                dStream.flush();
                dStream.close();

                String responseOutput =  mojoConnection.printResponse(connection);


                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK ) {
                    //Log.v("Authentication content:", connection.getContent().toString());
                    if (responseOutput.contains("AuthenticationSuccess")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                //output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                //Log.v("Authentication", "Authentication finished now " + mEmail + ":" + mPassword + " Result:" + output);


                //Log.v("Authentication brr", IOUtils.toString(connection.getInputStream()));


            }  catch(MalformedURLException ex){
                Log.v("Authentication", "Verification error now " + ex.toString());
                return false;

            } catch (IOException e) {
                Log.v("Authentication", "Verification error now " + e.toString());
                return false;

            }


            // TODO: register the new account here.
            Log.v("Authentication", "Verification finished now " );
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(VerificationActivity.this, "You are successfully verified!", Toast.LENGTH_SHORT).show();
                //now load the code verification activity
                Intent intent = new Intent(getApplicationContext(), RootActivity.class);
                startActivity(intent);
                finish();
            } else {
                verificationView.setError(getString(R.string.error_login_failed));
                verificationView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            verificationView.setVisibility(show ? View.GONE : View.VISIBLE);
            verificationView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    verificationView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            verificationView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
