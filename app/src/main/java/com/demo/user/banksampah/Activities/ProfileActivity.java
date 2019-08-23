package com.demo.user.banksampah.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.DataPengurus.ListViewDataPengurus;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    protected TextView tvNama, tvIDUser;
    protected ImageView imgProfile, imgArrowBack;
    protected TextView tvDataProfil, tvPassword, tvCallUs, tvLogOut, tvDaftarHarga, tvListHarga, tvAddPengurus, tvAddRekBank, tvPencairanSaldo;

    //Pin
    protected TextView tvNum1, tvNum2, tvNum3, tvNum4, tvNum5, tvNum6, tvNum7, tvNum8, tvNum9, tvNum0, tvDel;
    protected ImageView img1, img2, img3, img4, img5, img6, imgCancel;
    protected Button btnSubmit;

    private String url_foto;
    private ArrayList listPin = new ArrayList();

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
        imgArrowBack = findViewById( R.id.imgArrowBack );
        tvDataProfil = findViewById( R.id.tvDataSampah );
        tvPassword = findViewById( R.id.tvGantiKataSandi );
        tvLogOut = findViewById( R.id.tvLogOut );
        tvListHarga = findViewById( R.id.tvListHarga );
        tvAddPengurus = findViewById( R.id.tvAddPengurus );
        tvAddRekBank = findViewById( R.id.tvAddRekBank );
        tvPencairanSaldo = findViewById( R.id.tvPencairanLestari );

        tvPencairanSaldo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetPin();
            }
        } );


        tvAddPengurus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        } );

        tvAddRekBank.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        tvListHarga.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent list_harga = new Intent( getApplicationContext(), ListHargaItem.class );
                startActivity( list_harga );
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

        imgArrowBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        tvAddPengurus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent( getApplicationContext(), ListViewDataPengurus.class );
                startActivity( add );
            }
        } );

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


    public void SetPin() {
        // custom dialog
        final Dialog dialog = new Dialog( ProfileActivity.this );
        dialog.setContentView( R.layout.activity_set_pin );
        dialog.setTitle( "Enter Pin" );

        //Pin
        tvNum0 = dialog.findViewById( R.id.tvNum0 );
        tvNum1 = dialog.findViewById( R.id.tvNum1 );
        tvNum2 = dialog.findViewById( R.id.tvNum2 );
        tvNum3 = dialog.findViewById( R.id.tvNum3 );
        tvNum4 = dialog.findViewById( R.id.tvNum4 );
        tvNum5 = dialog.findViewById( R.id.tvNum5 );
        tvNum6 = dialog.findViewById( R.id.tvNum6 );
        tvNum7 = dialog.findViewById( R.id.tvNum7 );
        tvNum8 = dialog.findViewById( R.id.tvNum8 );
        tvNum9 = dialog.findViewById( R.id.tvNum9 );
        tvNum0 = dialog.findViewById( R.id.tvNum0 );
        tvDel = dialog.findViewById( R.id.tvDel );
        imgCancel = dialog.findViewById( R.id.tvCancel );
        img1 = dialog.findViewById( R.id.img1 );
        img2 = dialog.findViewById( R.id.img2 );
        img3 = dialog.findViewById( R.id.img3 );
        img4 = dialog.findViewById( R.id.img4 );
        img5 = dialog.findViewById( R.id.img5 );
        img6 = dialog.findViewById( R.id.img6 );

        imgCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        } );

        tvNum0.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum0.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum1.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum2.getText().toString() );
                    checkPinSize();
                }else{
                    checkPinSize();
                }
            }
        } );

        tvNum3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum3.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum4.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum5.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum5.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum6.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum6.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum7.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum7.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum8.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum8.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvNum9.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size()<=5) {
                    listPin.add( tvNum9.getText().toString() );
                    checkPinSize();
                }
                else{
                    checkPinSize();
                }
            }
        } );

        tvDel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() != 0) {
                    listPin.remove( listPin.size() - 1 );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        btnSubmit = dialog.findViewById( R.id.btnsubmit );
        // if button is clicked, close the custom dialog
        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() == 6) {
                    String pin = "";
                    for (int i = 0; i < listPin.size(); i++) {
                        pin = pin + listPin.get( i );
                    }
                    Log.d( "tag", pin );
                    SaveToDB( pin );
                    dialog.dismiss();
                }
            }
        } );

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            dialog.show();
        }
    }

    private void checkPinSize() {
        img1.setBackgroundResource( R.drawable.border_rectangle );
        img2.setBackgroundResource( R.drawable.border_rectangle );
        img3.setBackgroundResource( R.drawable.border_rectangle );
        img4.setBackgroundResource( R.drawable.border_rectangle );
        img5.setBackgroundResource( R.drawable.border_rectangle );
        img6.setBackgroundResource( R.drawable.border_rectangle );
        if (listPin.size() == 0) {
        }
        if (listPin.size() >= 1) {
            img1.setBackgroundResource( R.drawable.border_rectangle_dark );
        }

        if (listPin.size() >= 2) {
            img2.setBackgroundResource( R.drawable.border_rectangle_dark );
        }

        if (listPin.size() >= 3) {
            img3.setBackgroundResource( R.drawable.border_rectangle_dark );
        }

        if (listPin.size() >= 4) {
            img4.setBackgroundResource( R.drawable.border_rectangle_dark );
        }

        if (listPin.size() >= 5) {
            img5.setBackgroundResource( R.drawable.border_rectangle_dark );
        }

        if (listPin.size() == 6) {
            img6.setBackgroundResource( R.drawable.border_rectangle_dark );
        }else{

        }
        Log.d( "tag", String.valueOf( listPin ) );
    }

    protected void SaveToDB(final String Pin) {
        customProgress.showProgress( this, "", false );
        final String[] field_name = {"no_telepon", "pin"};
        String base_url = apiData.get( "str_url_address" ) + (".set_pin");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d( "DEBUG", "Register Response: " + response );
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder( ProfileActivity.this );
                    builder.setMessage( R.string.MSG_TAMBAH_PENGURUS )
                            .setCancelable( false )
                            .setPositiveButton( "ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent add = new Intent( getApplicationContext(), MainActivity.class );
                                    startActivity( add );
                                    finish();
                                }
                            } );
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.e( "tag", "sukses" );
                } catch (Throwable t) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d( "DEBUG", "Volley Error: " + error.getMessage() );
                customProgress.hideProgress();
            }
        } ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( field_name[0], getNoHp );
                params.put( field_name[1], Pin );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
    }
}
