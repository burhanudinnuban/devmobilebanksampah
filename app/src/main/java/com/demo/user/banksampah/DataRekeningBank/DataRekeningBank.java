package com.demo.user.banksampah.DataRekeningBank;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DataRekeningBank extends AppCompatActivity {
    //Proccess API
    protected RestProcess restClass;
    protected HashMap<String, String> apiData;
    protected ArrayList<HashMap<String, String>> arrayPin = new ArrayList<>();

    //API dialog progress loading
    protected CustomProgress customProgress;

    //An ArrayList for Spinner Items
    private ArrayList<String> Items;

    //Session Class
    protected PrefManager session;
    protected HashMap<String,String> user;

    //Get Data From Login Process
    protected static String getNama = "", getNoTelp = "", getIdBankSampah = "";

    //Declaration to Layout
    protected EditText etNoTel, etNoRkBank, etNamaPemilikRekBank, etCabangBank;
    protected Spinner sBank;
    protected Button btnAddRekBank;
    protected RelativeLayout parent_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_data_rekening_bank );

        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        Items = new ArrayList<String>();
        //Session Instance
        session = new PrefManager(getApplicationContext());
        user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);
        getNoTelp = user.get( PrefManager.KEY_NO_HP );
        getIdBankSampah = user.get( PrefManager.KEY_ID );
        getNoTelp = modifNumber( getNoTelp );

        etCabangBank = findViewById( R.id.etCabangBank );
        etNamaPemilikRekBank = findViewById( R.id.etNamaPemilikRekBank );
        etNoRkBank = findViewById( R.id.etNoRekBank );
        sBank = findViewById( R.id.sBank );
        btnAddRekBank = findViewById( R.id.btnAddRekBank );
        parent_layout = findViewById( R.id.parent_layout );

        btnAddRekBank.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        } );
        getData();
    }

    protected void  SaveToDB(String strNamaBank, String strNoRek, String strPemilik, String strCabang){
        customProgress.showProgress( this, "Silahkan Tunggu", false );
        final String[] field_name = {"id_user", "no_telepon", "nama_bank", "no_rekening", "pemilik", "cabang" };
        String base_url = apiData.get( "str_url_address" ) + (".add_rekening");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG", "Response: " + response);
                customProgress.hideProgress();
                try {
                    ValidateDB(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make( parent_layout,"Data Rekening Gagal Ditambahkan." , Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customProgress.hideProgress();
                Toast.makeText( DataRekeningBank.this, "Data Rekening Bank Gagal Ditambahkan.", Toast.LENGTH_LONG ).show();
            }
        } ){
            protected Map<String , String>getParams(){
                Map<String, String> params = new HashMap<>(  );
                params.put( field_name[0],getIdBankSampah );
                params.put( field_name[1],getNoTelp );
                params.put( field_name[2],strNamaBank );
                params.put( field_name[3],strNoRek );
                params.put( field_name[4],strPemilik );
                params.put( field_name[5],strCabang );
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };
        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    protected void ValidateDB(String resp_content){
        String[] field_name = {"message"};
        try {
            arrayPin = restClass.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );
            String message = jsonPost.getString( field_name[0] );
            if (message.equals( "success" )) {
                Toast.makeText( DataRekeningBank.this, "Rekening Bank Berhasil Ditambahkan",Toast.LENGTH_LONG ).show();
                Intent intent = new Intent( DataRekeningBank.this, PencairanSaldoBankSampah.class );
                startActivity( intent );
                finish();
            } else if (message.equals( "Failed" )){
                Snackbar snackbar = Snackbar
                        .make( parent_layout,"Data Rekening Gagal Ditambahkan." , Snackbar.LENGTH_SHORT);
                snackbar.show();
                 }

        } catch (JSONException e) {
            Log.d( "tag", e.toString() );
            Toasty.error( getApplicationContext(), getString( R.string.MSG_CODE_500 ) + " 2 : " + getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
        }
    }

    protected void ValidateData(){
        final String strNoRekBank = etNoRkBank.getText().toString().trim();
        final String strNamaPemilik = etNamaPemilikRekBank.getText().toString().trim();
        final String strCabang = etCabangBank.getText().toString().trim();
        final String strSBank = sBank.getSelectedItem().toString().trim();
        if (strNoRekBank.isEmpty()){
            etNoRkBank.setError( "No Rekening Bank Diperlukan" );
            etNoRkBank.requestFocus();
        }else if (strNamaPemilik.isEmpty()){
            etNamaPemilikRekBank.setError( "Nama Pemilik Rekening Diperlukan" );
            etNamaPemilikRekBank.requestFocus();
        }else if (strCabang.isEmpty()){
            etCabangBank.setError( "Cabang Bank Diperlukan" );
            etCabangBank.requestFocus();
        }else {
            SaveToDB( strSBank, strNoRekBank, strNamaPemilik, strCabang );
        }
    }

    protected void getData(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST,apiData.get("str_url_address")+(".list_bank"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag", response);
                        getItem(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void getItem(String response){
        String[]field_name = {"message", "name"};

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            Log.e("tag", String.valueOf(cast.length()));

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String bankName= c.getString(field_name[1]);

                Items.add(bankName);
            }
        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
        //Setting adapter to show the items in the spinner
        sBank.setAdapter(new ArrayAdapter<String>( DataRekeningBank.this, android.R.layout.simple_spinner_dropdown_item, Items));
    }

    private String modifNumber(String num){
        if (num.startsWith("08")){
            num = num.replaceFirst("08", "08");
        } else if (num.startsWith("628")){
            num = num.replaceFirst("628", "08");
        }
        return num;
    }
}
