package com.example.mghlcs.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.mghlcs.login.utility.Constants;
import com.example.mghlcs.login.utility.MojoConnection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextInputEditText userNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private final String COOKIES_HEADER = "Set-Cookie";
    private static MojoConnection mojoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mojoConnection = MojoConnection.getInstance(getApplicationContext());
        mojoConnection.setConnection();

        // Set up the login form.
        userNameView = (TextInputEditText) findViewById(R.id.username);
        //populateAutoComplete();

        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    /*
        private void populateAutoComplete() {
            if (!mayRequestContacts()) {
                return;
            }

            //getLoaderManager().initLoader(0, null, this);
        }

        private boolean mayRequestContacts() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                Snackbar.make(userNameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            }
                        });
            } else {
                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
            }
            return false;
        }

        /**
         * Callback received when a permissions request has been completed.
         */
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
*/

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid mUserName, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = userNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName
        if (TextUtils.isEmpty(userName)) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            userNameView.setError(getString(R.string.error_invalid_email));
            focusView = userNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserNameValid(String userName) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
/*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;
        private boolean taskResult;

        UserLoginTask(String userName, String password) {
            mUserName = userName;
            mPassword = password;
        }



        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.v("Authentication", "Do authentication now " + mUserName + ":" + mPassword);
            String urlString = Constants.LOGIN_URL;

            URL url;
            HttpsURLConnection connection;

            try {

                // Simulate network access.
                //Thread.sleep(2000);

//                AsyncHttpClient myClient = MojoConnection.myClient; //true to ignore ssl checking
//                myClient.setCookieStore(myCookieStore);
//                RequestParams params1 = new RequestParams();
//                params1.put("PartnersUsername", "");
//                params1.put("PartnersPassword","");
//                myClient.post(urlString, params1, new TextHttpResponseHandler() {
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        Log.v("responseString", responseString);
//                        Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
//                        intent.putExtra("userName", mUserName);
//                        intent.putExtra("password", mPassword);
//                        startActivity(intent);
//                        finish();
//                        return;
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        Log.v("responseString", responseString);
//                        return;
//                    }
//                });

                url = new URL(urlString);
                connection = (HttpsURLConnection) url.openConnection();
                //CookieManager cookieManager = mojoConnection.getCookieManager();
                //MojoConnection mojoConnection = MojoConnection.getInstance(getApplicationContext());
                String param="PartnersUsername=" + URLEncoder.encode(mUserName,"UTF-8")+ "&PartnersPassword="+URLEncoder.encode(mPassword,"UTF-8");
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
                    if (responseOutput.contains("ClientRegistrationRequestPending") || responseOutput.contains("AuthenticationSuccess")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                //output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
                //Log.v("Authentication", "Authentication finished now " + mUserName + ":" + mPassword + " Result:" + output);


                //Log.v("Authentication brr", IOUtils.toString(connection.getInputStream()));


            }  catch(MalformedURLException ex){
                Log.v("Authentication", "Authentication error now " + ex.toString());
                return false;

            } catch (IOException e) {
                Log.v("Authentication", "Authentication error now " + e.toString());
                return false;

            }

            // TODO: register the new account here.
            Log.v("Authentication", "Authentication finished now " + mUserName + ":" + mPassword );
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //now load the code verification activity
                Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
                intent.putExtra("userName", mUserName);
                intent.putExtra("password", mPassword);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_login_failed));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

