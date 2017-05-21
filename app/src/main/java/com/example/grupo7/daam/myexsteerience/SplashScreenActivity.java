package com.example.grupo7.daam.myexsteerience;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /****** Create Thread that will sleep for 2 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                   if( verifyInternet()){
                       if (mAuth.getCurrentUser() != null){
                           mAuth.removeAuthStateListener(mAuthListener);
                           startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                       }

                       else {
                           sleep(1500);
                           startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                       }
                   }else {
                       sleep(1500);
                       startActivity(new Intent(SplashScreenActivity.this, NoInternetActivity.class));

                   }
                    finish();

                } catch (Exception e) {

                }
            }
        };

        background.start();
    }

    private boolean verifyInternet() {
        try {
            if (isConnected() )
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        if(isWifiConn || isMobileConn){
            return true;
        }else{
            return  false;
        }

    }
}
