package com.demo.user.banksampah.Services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.demo.user.banksampah.Activities.IntroSliderActivity;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;

public class SplashScreen extends Activity {
    Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        checkFirstRun();
    }

    private void checkFirstRun(){
        final String PREFS_NAME = "myPrefsIntro";
        final String PREF_VERSION_CODE = "version_code";
        final int DOESNT_EXIST = -1;

        //Get Current Version
        int currentVersionCode = BuildConfig.VERSION_CODE;

        //Get Saved version code
        SharedPreferences prefs_check = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs_check.getInt(PREF_VERSION_CODE, DOESNT_EXIST);

        //Check first run or upgrade
        //Normal run...
        if (currentVersionCode == savedVersionCode){
            intent = new Intent(this, LoginActivity.class);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }

        //Jika baru install ulang atau session dihapus
        else if (savedVersionCode == DOESNT_EXIST){
            //intent = new Intent(this, LoginActivity.class);
            intent = new Intent(this, IntroSliderActivity.class);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
        //Jika diupdate
        else if (currentVersionCode > savedVersionCode){

        }

        prefs_check.edit().putInt(PREF_VERSION_CODE, currentVersionCode).apply();
    }
}
