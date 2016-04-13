package com.example.mghlcs.login.utility;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by mghlcs on 3/29/16.
 */
/*
public class ClassicSingleton {
   private static ClassicSingleton instance = null;
   protected ClassicSingleton() {
      // Exists only to defeat instantiation.
   }
   public static ClassicSingleton getInstance() {
      if(instance == null) {
         instance = new ClassicSingleton();
      }
      return instance;
   }
}
 */
public class MojoConnection {
    private static CookieManager cookieManager;
    //public static AsyncHttpClient myClient = new AsyncHttpClient(true,80, 443); //true to ignore ssl cheking
    //    public static void setCookieStore(PersistentCookieStore cookieStore) {
    //        myClient.setCookieStore(cookieStore);
    //    }

    private static MojoConnection instance = null;
    protected MojoConnection() {
        //protection purpose
    }

    public static MojoConnection getInstance(Context ctxContext){
        if (instance == null) {
            instance = new MojoConnection();
            instance.cookieManager = new CookieManager(MojoPersistentCookieStore.getInstance(ctxContext), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            CookieHandler.setDefault(instance.cookieManager);
        }
        return instance;
    }

    public List<HttpCookie> getCookies() {
        List<HttpCookie> cookies = instance.cookieManager.getCookieStore().getCookies();

        for (HttpCookie cookie : cookies)
        {
            Log.v("Cookie", "Previous Saved cookie : " + cookies);
        }
        return cookies;
    }
    public void setConnection() {
        trustEveryone();
    }


    public String printResponse(HttpsURLConnection conn) {
        try {
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            int status = conn.getResponseCode();
            System.out.println("Status = " + status);
            String key;
            System.out.println("Headers-------start-----");
            for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
                System.out.println(key + ":" + conn.getHeaderField(i));
            }
            System.out.println("Headers-------end-----");
            System.out.println("Content-------start-----");
            String inputLine;
            StringBuilder responseOutput = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
                responseOutput.append(inputLine);
            }
            System.out.println("Content-------end-----");
            in.close();
            return responseOutput.toString();
        }  catch (Exception e) {
            System.out.println(e);
            return (e.toString());
        }
    }

    //Somehow the server SSL cert is not set right, we need to ignore it.
    public void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }


}
