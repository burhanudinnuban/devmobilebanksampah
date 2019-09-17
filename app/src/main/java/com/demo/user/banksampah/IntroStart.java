package com.demo.user.banksampah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.RegisterActivities.RegisterPhoneActivity;
import com.demo.user.banksampah.Services.LoginActivity;
import com.demo.user.banksampah.Services.MainActivity;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class IntroStart extends AppCompatActivity {
    protected Button btnLogin, btnDaftar;
    protected RelativeLayout rlParent;
    protected PrefManager session;
    protected String var_noHP, var_password, var_imei;
    protected String pref_getName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intro_start );
        btnLogin = findViewById( R.id.btnLogin );
        btnDaftar = findViewById( R.id.btnDaftar );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent( IntroStart.this, LoginActivity.class );
                startActivity( login );
            }
        } );
        btnDaftar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent daftar = new Intent( IntroStart.this, RegisterPhoneActivity.class );
                startActivity( daftar );
            }
        } );
    }

    @Override
    public void onStart(){
        //PrefManager
        session = new PrefManager(getApplicationContext());

        //For Save Phone Number in EditText...
        SharedPreferences mPrefs = getSharedPreferences("myprofile",0);
        var_noHP = mPrefs.getString("member_id", "");
        var_imei = mPrefs.getString("session_id", "");

        if(session.isLoggedIn()){
            HashMap<String,String> user = session.getUserDetails();
            pref_getName = user.get(PrefManager.KEY_NAMA);
            Toasty.info(getApplicationContext(), "Selamat Datang " + pref_getName + ",\n" + "Semoga Harimu Menyenangkan!", Toast.LENGTH_LONG).show();
            Intent intent_session = new Intent(IntroStart.this, MainActivity.class);
            startActivity(intent_session);
            finish();
        }
        super.onStart();
    }
}
