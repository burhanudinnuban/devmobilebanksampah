package com.demo.user.banksampah.DataRekeningBank;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.ProfileBankSampah.ProfileActivity;
import com.demo.user.banksampah.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class UpdatedDataBank extends AppCompatActivity {

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    protected CustomProgress customProgress;
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
    protected RelativeLayout parent_layout;
    Context ctx;
    protected View rootView;
    protected LazyAdapter adapter;
    private ArrayList<String> Items = new ArrayList<String>(  );
    protected Spinner sBank;

    protected Button btnUpdate;
    protected EditText etNoRek, etNamaBank, etPemilik, etCabang, etAccountBank, etIdUser;
    protected static String getNama = "", getId = "", getBank="", getCabangBank="", getNoRek="", getUnitDefault="", getBankAccount="", getNoTelp="", getPemilik="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_view_rekening_bank );

        etAccountBank = findViewById( R.id.etAccountBank );
        etCabang = findViewById( R.id.etCabang );
        etIdUser = findViewById( R.id.etIdUser );
        etNoRek = findViewById( R.id.etNoRek );
        etPemilik = findViewById( R.id.etPemilik );
        sBank = findViewById( R.id.sNamaBank );
        btnUpdate = findViewById( R.id.btnUpdataDataBank );
        ctx = UpdatedDataBank.this;
        session = new PrefManager( this );
        rest_class = new RestProcess();

        apiData = rest_class.apiErecycle();
        parent_layout = findViewById( R.id.parent );
        final HashMap<String, String> user = session.getUserDetails();

        getNama = user.get( PrefManager.KEY_NAMA );
        getId = user.get( PrefManager.KEY_ID );
        getBank = user.get( PrefManager.KEY_BANK );
        getCabangBank = user.get( PrefManager.KEY_CABANG );
        getNoRek = user.get( PrefManager.KEY_NO_REKENING );
        getUnitDefault = user.get( PrefManager.KEY_UNIT_DEFAULT );
        getBankAccount = user.get( PrefManager.KEY_BANK_ACCOUNT );
        getNoTelp = user.get( PrefManager.KEY_NO_HP );
        getPemilik = user.get( PrefManager.KEY_NAMA_PEMILIK );

        etAccountBank.setText( getBankAccount );
        etPemilik.setText( getPemilik );
        etNoRek.setText( getNoRek );
        etIdUser.setText( getId );
        etCabang.setText( getCabangBank );

        btnUpdate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strSBank = sBank.getSelectedItem().toString().trim();
                String strCabang = etCabang.getText().toString();
                String strPemilik = etPemilik.getText().toString();
                String strAccountBank = etAccountBank.getText().toString();
                String strNoRek = etNoRek.getText().toString();
                updateDataBank(strSBank, strNoRek, strPemilik, strCabang, strAccountBank  );
            }
        } );
    }

    private void updateDataBank(String strNamaBank, String strNoRek, String strPemilik, String strCabang, String strBankAccount) {
        String[] field_name = {"id_user", "no_telepon", "nama_bank", "no_rekening", "pemilik", "cabang", "bank_account"};
        String base_url = apiData.get( "str_url_address" ) + (".update_rekening");
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "debug", "Check Login Response: " + response );
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( UpdatedDataBank.this );
                    builder.setMessage( "Selamat Data Bank Berhasil Dirubah." )
                            .setCancelable( false )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    displayLogin( response );
                                    finish();
                                }
                            } );
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.e( "tag", "sukses" );
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
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "debug", "Volley Error: " + error.toString() );
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( field_name[0], getId );
                params.put( field_name[1], getNoTelp );
                params.put( field_name[2], strNamaBank );
                params.put( field_name[3], strNoRek );
                params.put( field_name[4], strPemilik );
                params.put( field_name[5], strCabang );
                params.put( field_name[6], strBankAccount );
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
        String[] field_name = {"message", "jam_operasional", "alamat", "email", "foto", "no_sk", "penerbit_sk"};
        try {
            arrayList = rest_class.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );
            String message = jsonPost.getString( field_name[0] );
            if (message.equals( "Updated" )) {
                String jamOperasional = jsonPost.getString( field_name[1] );
                String alamat = jsonPost.getString( field_name[2] );
                String email = jsonPost.getString( field_name[3] );
                String foto = jsonPost.getString( field_name[4] );
                String noSK = jsonPost.getString( field_name[5] );
                String penerbitSk = jsonPost.getString( field_name[6] );
                session.updateProfil( email, alamat, jamOperasional, foto, noSK, penerbitSk );
            } else {
                Toasty.error( getApplicationContext(), message, Toast.LENGTH_LONG ).show();
            }
        } catch (JSONException e) {
            Toasty.error( getApplicationContext(), getString( R.string.MSG_CODE_500 ) + " 2 : " + getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
        }
    }


    private String modifNumber(String num) {
        if (num.startsWith( "08" )) {
            num = num.replaceFirst( "08", "08" );
        } else if (num.startsWith( "628" )) {
            num = num.replaceFirst( "628", "08" );
        }
        return num;
    }

    private void getDataRekening() {
        String base_url = apiData.get( "str_url_address" ) + (".check_rekening");

        StringRequest stringRequest = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    viewDataRekening( response );
                    Log.d( "debug", "onResponse: " + response );
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar.make( parent_layout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make( parent_layout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                snackbar.show();
            }
        } ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_user", getId );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue( stringRequest, apiData.get( "str_json_obj" ) );
    }

    protected void viewDataRekening(String response) {
        String[] field_name = {"message", "data", "name", "no_rekening", "bank_account", "logo", "pemilik", "cabang", "nama_bank"};

        try {
            JSONObject jsonObject = new JSONObject( response );
            String message = jsonObject.getString( field_name[0] );
            if (message.equals( "True" )) {
                JSONObject jsonObject1 = jsonObject.getJSONObject( field_name[1] );
                String namaBank = jsonObject1.getString( field_name[8] );
                String noRekeningBank = jsonObject1.getString( field_name[3] );
                String bankAccount = jsonObject1.getString( field_name[4] );
                String logoBank = jsonObject1.getString( field_name[5] );
                String pemilik = jsonObject1.getString( field_name[6] );
                String cabang = jsonObject1.getString( field_name[7] );
                session.checkBankAkun( namaBank, noRekeningBank, pemilik, cabang, bankAccount );
                etNoRek.setText( noRekeningBank );
                etPemilik.setText( pemilik );
                etAccountBank.setText( bankAccount );

//                Picasso.get()
//                        .load( apiData.get( "str_url_main" ) + logoBank )
//                        .error( R.drawable.ic_navigation_profil )
//                        .into( imgBank );
            } else if (message.equals( "Not Found" )) {
                popupYesOrNo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void popupYesOrNo() {
        Dialog dialog = new Dialog( ctx );
        dialog.setContentView( R.layout.ok_dan_tidak );
        Button ok = dialog.findViewById( R.id.btnOk );
        Button cancel = dialog.findViewById( R.id.btnCancel );
        dialog.setCanceledOnTouchOutside( false );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( UpdatedDataBank.this, ProfileActivity.class );
                startActivity( intent );
            }
        } );
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( UpdatedDataBank.this, DataRekeningBank.class );
                startActivity( intent );
            }
        } );
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            dialog.show();
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
        sBank.setAdapter(new ArrayAdapter<String>( UpdatedDataBank.this, android.R.layout.simple_spinner_dropdown_item, Items));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getData();
        getDataRekening();
    }
}
