package com.demo.user.banksampah.Pin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.demo.user.banksampah.PencairanSaldo.Nasabah.PencairanNasabah;
import com.demo.user.banksampah.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CheckPin extends AppCompatActivity {

    //Session Class
    protected PrefManager session;

    private ArrayList listPin = new ArrayList();

    ArrayList<HashMap<String, String>> arrayLogin = new ArrayList<>();

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected String strNo_Telepon, strPin;
    protected CustomProgress customProgress;

    //Pin
    protected TextView tvNum1, tvNum2, tvNum3, tvNum4, tvNum5, tvNum6, tvNum7, tvNum8, tvNum9, tvNum0, tvDel, tvLupaPin;
    protected ImageView img1, img2, img3, img4, img5, img6, imgExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_check_pin );

        session = new PrefManager( CheckPin.this );
        HashMap<String, String> user = session.getUserDetails();
        strNo_Telepon = user.get( PrefManager.KEY_NO_HP );

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        tvNum0 = findViewById( R.id.tvNum0 );
        tvNum1 = findViewById( R.id.tvNum1 );
        tvNum2 = findViewById( R.id.tvNum2 );
        tvNum3 = findViewById( R.id.tvNum3 );
        tvNum4 = findViewById( R.id.tvNum4 );
        tvNum5 = findViewById( R.id.tvNum5 );
        tvNum6 = findViewById( R.id.tvNum6 );
        tvNum7 = findViewById( R.id.tvNum7 );
        tvNum8 = findViewById( R.id.tvNum8 );
        tvNum9 = findViewById( R.id.tvNum9 );
        tvNum0 = findViewById( R.id.tvNum0 );
        tvDel = findViewById( R.id.tvDel );
        tvLupaPin = findViewById( R.id.etLupaPin );
        img1 = findViewById( R.id.img1 );
        img2 = findViewById( R.id.img2 );
        img3 = findViewById( R.id.img3 );
        img4 = findViewById( R.id.img4 );
        img5 = findViewById( R.id.img5 );
        img6 = findViewById( R.id.img6 );
        imgExit = findViewById( R.id.imgExit );

        imgExit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );

        tvLupaPin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lupaPin = new Intent( CheckPin.this, LupaPin.class );
                startActivity( lupaPin );
            }
        } );

        tvNum0.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum0.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum1.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum2.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum3.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum4.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum5.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum5.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum6.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum6.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum7.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum7.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum8.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum8.getText().toString() );
                    checkPinSize();
                } else {
                    checkPinSize();
                }
            }
        } );

        tvNum9.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPin.size() <= 5) {
                    listPin.add( tvNum9.getText().toString() );
                    checkPinSize();
                } else {
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

//        btnSubmit = findViewById( R.id.btnsubmit );
//        // if button is clicked, close the custom dialog
//        btnSubmit.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listPin.size() == 6) {
//
//                }
//            }
//        } );
    }

    protected void SaveToDB(final String Pin) {
        customProgress.showProgress( this, "", false );
        final String[] field_name = {"no_telepon", "pin"};
        String base_url = apiData.get( "str_url_address" ) + (".check_pin");

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
                params.put( field_name[0], strNo_Telepon );
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

    private void displayLogin(String resp_content) {
        String[] field_name = {"message", "no_telepon", "pin"};
        try {
            arrayLogin = rest_class.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );
            JSONObject message = jsonPost.getJSONObject( field_name[0] );
            if (message != null) {
                strNo_Telepon = message.getString( field_name[1] );
                strPin = message.getString( field_name[2]);
                session.CheckPin( strNo_Telepon, strPin );
                Intent main_intent = new Intent( CheckPin.this, PencairanNasabah.class );
                startActivity( main_intent );
                finish();
            } else{
                Toasty.error( getApplicationContext(), getString( R.string.MSG_FALSE_LOGIN ) + "\n" + getString( R.string.MSG_CHECK_DATA ), Toast.LENGTH_LONG ).show();
            }

        } catch (JSONException e) {
            Log.d( "tag", e.toString() );
            Toasty.error( getApplicationContext(), getString( R.string.MSG_CODE_500 ) + " 2 : " + getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
        }

    }

//    @Override
//    public void onStart() {
//        if (session.isPinIn()) {
////            HashMap<String, String> user = session.getUserDetails();
////            strNo_Telepon = user.get( PrefManager.KEY_NO_HP );
//            Intent intent_session = new Intent( CheckPin.this, PencairanNasabah.class );
//            startActivity( intent_session );
//            finish();
//        }
//        super.onStart();
//    }

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
            String pin = "";
            for (int i = 0; i < listPin.size(); i++) {
                pin = pin + listPin.get( i );
            }
            Log.d( "tag", pin );
            SaveToDB( pin );
        } else
            {
        }
        Log.d( "tag", String.valueOf( listPin ) );
    }
}
