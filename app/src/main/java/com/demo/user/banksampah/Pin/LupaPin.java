package com.demo.user.banksampah.Pin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LupaPin extends AppCompatActivity {

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

    protected EditText etPassword;
    protected Button btnSubmit;
    protected ImageView imgExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lupa_pin );
        session = new PrefManager( getApplicationContext() );
        user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getNoHp = user.get( PrefManager.KEY_NO_HP );

        etPassword = findViewById( R.id.etPassword );
        btnSubmit = findViewById( R.id.btnSubmitPassword );

        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        imgExit = findViewById( R.id.imgExit );

        imgExit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );

        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPassword = etPassword.getText().toString();
                SaveToDB(strPassword);
            }
        } );

    }
    protected void SaveToDB(String strPassword) {
        customProgress.showProgress( this, "", false );
        final String[] field_name = {"no_telepon","password"};
        String base_url = apiData.get( "str_url_address" ) + (".forgot_pin");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d( "DEBUG", "Register Response: " + response );
                try {
                    displayLogin( response );
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
                params.put( field_name[1], strPassword );
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

    private void displayLogin(String resp_content) {
        String[] field_name = {"message"};
        try {
            arrayPin = restClass.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );
            String message = jsonPost.getString( field_name[0] );
            if (message.equals( "True" )) {
                Intent main_intent = new Intent( LupaPin.this, CreatePin.class );
                startActivity( main_intent );
                finish();
            } else {
            Toast.makeText( LupaPin.this, "Password yang Anda Masukkan Salah, Harap Masukkan Password yang Benar.",Toast.LENGTH_SHORT ).show();
            }

        } catch (JSONException e) {
            Log.d( "tag", e.toString() );
            Toasty.error( getApplicationContext(), getString( R.string.MSG_CODE_500 ) + " 2 : " + getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
        }
    }
}
