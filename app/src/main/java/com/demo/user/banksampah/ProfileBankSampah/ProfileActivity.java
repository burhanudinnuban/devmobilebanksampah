package com.demo.user.banksampah.ProfileBankSampah;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.DataPengurus.ListViewDataPengurus;
import com.demo.user.banksampah.DataRekeningBank.UpdatedDataBank;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Services.ChangePassword;
import com.demo.user.banksampah.Services.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    protected TextView tvNama, tvIDUser;
    protected ImageView imgProfile, imgArrowBack;
    protected TextView tvDataProfil, tvPassword,tvLogOut, tvPengurus, tvRekeningBank;
    private String url_foto;

    //Proccess API
    protected RestProcess restClass;
    protected HashMap<String, String> apiData;
    protected String pref_getName;
    ArrayList<HashMap<String, String>> arrayPin = new ArrayList<>();

    //API dialog progress loading
    protected CustomProgress customProgress;

    //Session Class
    protected PrefManager session;
    protected HashMap<String, String> user;

    //Get Data From Login Process
    protected static String getNama = "", getNoHp = "";

    //PopUp Image Dialog
    Dialog myDialog;

    //private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.demo.user.erecycle.notification"));
        setContentView( R.layout.activity_profile );

        //Session Instance
        session = new PrefManager( getApplicationContext() );
        user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getNoHp = user.get( PrefManager.KEY_NO_HP );

        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();

        tvNama = findViewById( R.id.tvNama_Profil );
        tvIDUser = findViewById( R.id.tvIDUser_Profil );
        imgProfile = findViewById( R.id.imgPicture_Profil );
        tvDataProfil = findViewById( R.id.tvDataSampah );
        tvPassword = findViewById( R.id.tvGantiKataSandi );
        tvLogOut = findViewById( R.id.tvLogOut );
        tvPengurus = findViewById( R.id.tvDataPengurus ) ;
        tvRekeningBank = findViewById( R.id.tvDataRekeningBank );

        tvRekeningBank.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RekBank = new Intent( ProfileActivity.this, UpdatedDataBank.class );
                startActivity( RekBank );
            }
        } );

        tvPengurus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pengurus = new Intent( ProfileActivity.this, ListViewDataPengurus.class );
                startActivity( pengurus );
            }
        } );

        //Create myDialog
        myDialog = new Dialog( this );

        imgProfile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePopUp( v );
            }
        } );

        tvDataProfil.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_profil = new Intent( getApplicationContext(), UpdateProfileActivity.class );
                startActivity( intent_profil );
            }
        } );

        tvPassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pwd = new Intent( getApplicationContext(), ChangePassword.class );
                startActivity( pwd );
            }
        } );

        tvLogOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpQuit();
            }
        } );

//        imgArrowBack.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        } );



        //Test
        /*if(getIntent().getExtras() != null){
            for(String key:getIntent().getExtras().keySet()){
                if (key.equals("EXTRA_BODY")){
                    tvNama.setText(getIntent().getExtras().getString(key));
                    Log.e("tag", key);
                }
                if (key.equals("EXTRA_TITLE")){
                    tvIDUser.setText(getIntent().getExtras().getString(key));
                    Log.e("tag", key);
                }
            }
        }*/
    }

//    @Override
//    public void onStart(){
//        rest_class = new RestProcess();
//        apiData = rest_class.apiErecycle();
//
//        /*//Session Instance
//        session = new PrefManager(getApplicationContext());
//
//        tvNama.setText(MainActivity.strNama);
//        tvIDUser.setText(MainActivity.strID);
//        url_foto = apiData.get("str_url_main");
//
//        Picasso.get()
//                .load(url_foto + MainActivity.strFoto)
//                .error(R.drawable.ic_navigation_profil)
//                .into(imgProfile);*/
//        super.onStart();
//    }

    @Override
    public void onResume() {
        //Session Instance
        session = new PrefManager( getApplicationContext() );

        tvNama.setText( MainActivity.strNama );
        tvIDUser.setText( MainActivity.strID );
        url_foto = apiData.get( "str_url_main" );

        Picasso.get()
                .load( url_foto + MainActivity.strFoto )
                .error( R.drawable.ic_navigation_profil )
                .into( imgProfile );
        super.onResume();
    }

//    private BroadcastReceiver mHandler = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals("com.demo.user.erecycle.notification")){
//                String message = intent.getStringExtra("message");
//            }
//
//        }
//    };
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.demo.user.erecycle.notification"));
//    }

    public void PopUpQuit() {
        myDialog.setContentView( R.layout.pop_up_confirm );

        Button btnYes = myDialog.findViewById( R.id.btnYes );
        Button btnNo = myDialog.findViewById( R.id.btnNo );

        btnYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                session.logoutUser();
            }
        } );

        btnNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        } );

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            myDialog.show();
        }
    }
    
    public void ProfilePopUp(View v) {
        //Initiate Variable Image
        ImageView imgProfilCache;

        myDialog.setContentView( R.layout.pop_up_image );

        ImageView imgCancel = myDialog.findViewById( R.id.arrowBack );
        imgCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        } );

        imgProfilCache = myDialog.findViewById( R.id.imgPicture_Cache );

        Picasso.get()
                .load( url_foto + MainActivity.strFoto )
                .error( R.drawable.ic_navigation_profil )
                .resize( 800, 800 )
                .centerCrop()
                .into( imgProfilCache );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull( myDialog.getWindow() ).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        }

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            myDialog.show();
        }
    }
}
