package com.demo.user.banksampah.BankSampahActivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.GoogleMaps.GoogleMapsMarker;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailBankSampah extends AppCompatActivity {
    protected ImageView imgBankSampahInduk;
    protected TextView tvNamaBankSampahInduk, tvAlamatBankSampahInduk, tvJamBukaBankSampahInduk, tvEmailBankSampahInduk;
    protected CardView cvMapsBankSampah, cvHubungiBankSampah, cvGabungBankSampah;
    protected PrefManager session;
    protected RestProcess restProcess;
    protected HashMap<String, String> apiData;
    protected RelativeLayout parent_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_bank_sampah );

        session = new PrefManager( DetailBankSampah.this );
        HashMap<String, String> user = session.getUserDetails();
        String strIdUser = user.get( PrefManager.KEY_ID );
        restProcess = new RestProcess();
        apiData = restProcess.apiErecycle();
        parent_layout = findViewById( R.id.parent );
        imgBankSampahInduk = findViewById( R.id.imgProfileBankSampah );
        tvNamaBankSampahInduk = findViewById( R.id.tvNamaBankSampah );
        tvAlamatBankSampahInduk = findViewById( R.id.tvAlamatBankSampah );
        tvJamBukaBankSampahInduk = findViewById( R.id.tvJamBukaBankSampah );
        tvEmailBankSampahInduk = findViewById( R.id.tvEmailBankSampah );
        cvMapsBankSampah = findViewById( R.id.cvMapsBankSampah );
        cvGabungBankSampah = findViewById( R.id.cvGabungBankSampah );
        cvHubungiBankSampah = findViewById( R.id.cvHubungiBankSampah );

        String strFotobankSampah = getIntent().getStringExtra( "foto" );
        String strAlamatBankSampah = getIntent().getStringExtra( "alamat" );
        String strBankSampah = getIntent().getStringExtra( "idBankSampah" );
        String strNoTelp = getIntent().getStringExtra( "noTelepon" );
        String strJamOperasional = getIntent().getStringExtra( "jam_operasional" );
        String strEmail = getIntent().getStringExtra( "email" );
        String strLatlong = getIntent().getStringExtra( "latlong" );
        String strActionmaps = getIntent().getStringExtra( "lihat_lokasi" );
        String[] separated = strLatlong.split(",");

        tvEmailBankSampahInduk.setText( strEmail );
        tvJamBukaBankSampahInduk.setText( strJamOperasional );
        tvAlamatBankSampahInduk.setText( strAlamatBankSampah );
        tvNamaBankSampahInduk.setText( strBankSampah );

        String url_foto = apiData.get( "str_url_main" );
        Picasso.get()
                .load( url_foto + strFotobankSampah )
                .error( R.drawable.ic_navigation_profil )
                .into( imgBankSampahInduk );

        cvMapsBankSampah.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( DetailBankSampah.this, GoogleMapsMarker.class );
                intent.putExtra( "lihat_lokasi", strActionmaps );
                intent.putExtra( "latlong", strLatlong );
                startActivity( intent );
            }
        } );

        cvHubungiBankSampah.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlertDialogWithRadioButtonGroup();
            }
        } );

        cvGabungBankSampah.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupBergabungBankSampah(strIdUser, strBankSampah);
            }
        } );
    }
    public void CreateAlertDialogWithRadioButtonGroup() {
        Dialog mydialog = new Dialog( DetailBankSampah.this );
        CardView cvMessage, cvCall;
        TextView imgExit;
        mydialog.setContentView( R.layout.alert_call_n_message );
        cvMessage = mydialog.findViewById( R.id.cvMessage );
        cvCall = mydialog.findViewById( R.id.cvCall );
        imgExit = mydialog.findViewById( R.id.imgExit );
        String strNoTelp = getIntent().getStringExtra( "noTelepon" );
        cvCall.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty( strNoTelp )) {
                    String dial = "tel:" + strNoTelp;
                    startActivity( new Intent( Intent.ACTION_DIAL, Uri.parse( dial ) ) );
                } else {
                    Toast.makeText( DetailBankSampah.this, "Enter a phone number", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        cvMessage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty( strNoTelp ) && !TextUtils.isEmpty( strNoTelp )) {
                    Intent smsIntent = new Intent( Intent.ACTION_SENDTO, Uri.parse( "smsto:" + strNoTelp ) );
                    smsIntent.putExtra( "sms_body", strNoTelp );
                    startActivity( smsIntent );
                }
            }
        } );

        imgExit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        } );

        if (mydialog.getWindow() != null) {
            mydialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            mydialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            mydialog.show();
        }
    }

    public void popupBergabungBankSampah(String strIdUser, String strBankSampah) {
        Dialog mydialog = new Dialog( DetailBankSampah.this );
        mydialog.setContentView( R.layout.popup_bergabung_bank_sampah );
        CardView cvIya, cvTidak;
        cvIya = mydialog.findViewById( R.id.cvIya );
        cvTidak = mydialog.findViewById( R.id.cvTidak );

        cvIya.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinBankSampah( strIdUser, strBankSampah );
                finish();
            }
        } );

        cvTidak.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        } );

        if (mydialog.getWindow() != null) {
            mydialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            mydialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            mydialog.show();
        }
    }

    public void joinBankSampah(String strIdUser, String strBankSampah){
        String base_url = apiData.get( "str_url_address" ) + ( ".add_member_bank" );
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "debug", "Check ListMember Response: " + response );
                try {
                    Intent intent = new Intent( DetailBankSampah.this, CariBankSampah.class );
                    startActivity( intent );
                    Toast.makeText( DetailBankSampah.this,"Sukses Menambahkan Ke Bank Sampah" + strBankSampah, Toast.LENGTH_LONG ).show();
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make( parent_layout, getString( R.string.MSG_CODE_409 ) + "1: " + getString( R.string.MSG_CHECK_DATA ), Snackbar.LENGTH_SHORT );
                    snackbar.show();
                    Log.d( "debug", "Error Check Login Response: " + t.toString() );
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "debug", "Volley Error: " + error.toString() );
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_user",strIdUser  );
                params.put( "id_bank_sampah", strBankSampah );
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
