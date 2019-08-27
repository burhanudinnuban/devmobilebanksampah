package com.demo.user.banksampah.PencairanSaldo.Nasabah;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PencairanNasabah extends AppCompatActivity {

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
    protected static String getNama = "", getNoHp = "", getId="";

    protected RelativeLayout parent_layout;
    protected EditText etWithdraw;
    protected Button btnYes, btnNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pencairan_nasabah );

        //Session Instance
        session = new PrefManager( getApplicationContext() );
        user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getNoHp = user.get( PrefManager.KEY_NO_HP );
        getId = user.get( PrefManager.KEY_ID );

        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        etWithdraw = findViewById( R.id.etWithdraw );
        parent_layout = findViewById( R.id.flNasabah );
        btnYes = findViewById( R.id.btnYes );
        btnNo = findViewById( R.id.btnNo );

        btnNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );
        btnYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strWithdraw = etWithdraw.getText().toString();
                PencairanNasabah(strWithdraw);
            }
        } );
    }

    public void PencairanNasabah(String strJumalhWithdraw){
    final String[] field_name = {"id_member", "jumlah_withdraw", "id_bank_sampah"};
    String base_url = apiData.get( "str_url_address" ) + apiData.get( "str_api_pencairan_saldo" );
    StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d( "DEBUG", "Register Response: " + response );
            try {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( PencairanNasabah.this );
                builder.setMessage( R.string.MS_PENCAIRAN_SALDO )
                        .setCancelable( false )
                        .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent pencairan = new Intent( getApplicationContext(), MainActivity.class );
                                startActivity( pencairan );
                                finish();
                            }
                        } );
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.e( "tag", "sukses" );
            } catch (Throwable t) {
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_409 ) + " 1: " + getString( R.string.MSG_CHECK_DATA ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "DEBUG", "Error Validate Change Password Response: " + t.toString() );
            }
        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d( "DEBUG", "Volley Error: " + error.getMessage() );
        }
    } ) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();
            params.put( field_name[0], getNama );
            params.put( field_name[1], strJumalhWithdraw );
            params.put( field_name[2], getId );
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
    VolleyController.getInstance().addToRequestQueue(strReq, apiData.get( "str_json_obj" ) );
    }
}
