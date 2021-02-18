package com.latentsoft.advocatenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ActionBar actionBar;
    SharedPreferences sp;
    boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        actionBar = getSupportActionBar();
        actionBar.hide();
        sp = getSharedPreferences("advocate_memory", MODE_PRIVATE);
        isLogged = sp.getBoolean("isLogged", false);

        new Thread(){

            @Override
            public void run() {
                 super.run();
                try {
                    sleep(3000);
                    if(isLogged){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this, LogInActivity.class));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
