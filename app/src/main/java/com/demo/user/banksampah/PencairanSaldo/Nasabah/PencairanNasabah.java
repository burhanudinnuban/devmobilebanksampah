package com.demo.user.banksampah.PencairanSaldo.Nasabah;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.demo.user.banksampah.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PencairanNasabah extends AppCompatActivity {

    //Proccess API
    protected RestProcess restClass;
    protected HashMap<String, String> apiData;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>(  );

    //API dialog progress loading
    protected CustomProgress customProgress;

    //Session Class
    protected PrefManager session;
    protected HashMap<String, String> user;

    //Get Data From Login Process
    protected static String getNama = "", getNoHp = "", getId="", getPoint="", getIdMember;

    protected RelativeLayout parent_layout;
    protected EditText etWithdraw;
    protected Button btnYes, btnNo;
    protected TextView tvPoint;
    protected DecimalFormat decimalFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pencairan_nasabah );
        decimalFormat = new DecimalFormat( ".###" );
        //Session Instance
        session = new PrefManager( PencairanNasabah.this );
        user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getNoHp = user.get( PrefManager.KEY_NO_HP );
        getId = user.get( PrefManager.KEY_ID );
        getPoint = user.get( PrefManager.KEY_SALDO );
        getIdMember = user.get( PrefManager.KEY_ID_NASABAH );

        Log.d( "Saldo", "point: "+getPoint );
        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        etWithdraw = findViewById( R.id.etWithdraw );
        parent_layout = findViewById( R.id.flNasabah );
        btnYes = findViewById( R.id.btnYes );
        btnNo = findViewById( R.id.btnNo );
        tvPoint = findViewById( R.id.tvPointNasabah );

        tvPoint.setText( decimalFormat.format( Double.valueOf( getPoint ) ) );

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
    String base_url = apiData.get( "str_url_address" ) + ( ".pencairan_saldo_member" );
    StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d( "DEBUG", "Register Response: " + response );
            try {
                getDataPencairanSaldo( response );
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
            params.put( field_name[2], getNama );
            params.put( field_name[1], strJumalhWithdraw );
            params.put( field_name[0], getIdMember );
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

    public void getDataPencairanSaldo(String resp_content){
        String[] field_name = {"message","data","withdraw","point_tersedia","jumlah_point","status"};
        Dialog dialog = new Dialog( PencairanNasabah.this );
        try {
            arrayList = restClass.getJsonData( field_name, resp_content );
            JSONObject a = new JSONObject( resp_content );
            String message = a.getString( field_name[0] );

            if (message.equals( "True" )){
                JSONObject b = a.getJSONObject( field_name[2] );
                String point_tersedia = b.getString( field_name[3] );
                String jumlah_point = b.getString( field_name[4] );
                String status = b.getString( field_name[5] );
                String data = a.getString( field_name[1] );
                Toast.makeText( getApplicationContext(), data, Toast.LENGTH_LONG ).show();
                finish();
            }else if (message.equals( "Failed" )){
                String data = a.getString( field_name[1] );
                Snackbar snackbar = Snackbar
                        .make( parent_layout, data, Snackbar.LENGTH_SHORT );
                snackbar.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
