package com.demo.user.banksampah.DataPengurus;

import android.app.AlertDialog;
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
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class DataPengurus extends AppCompatActivity {

    protected EditText etNamaPengurus, etJabatanPengurus;
    protected TextView tvNamaBankSampah;
    protected Button btnTambahPengurus;
    protected RelativeLayout parent_layout;

    //Proccess API
    protected RestProcess restClass;
    protected HashMap<String, String> apiData;

    //API dialog progress loading
    protected CustomProgress customProgress;

    //Session Class
    protected PrefManager session;
    protected HashMap<String,String> user;

    //Get Data From Login Process
    protected static String getNama = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_data_pengurus );

        //Session Instance
        session = new PrefManager(getApplicationContext());
        user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);

        etNamaPengurus = findViewById( R.id.etNamaPengurus );
        etJabatanPengurus = findViewById( R.id.etjabatanPengurus );
        btnTambahPengurus = findViewById( R.id.btTambahPengurus );
        tvNamaBankSampah = findViewById( R.id.tvBankSampahPengurus );

        customProgress = CustomProgress.getInstance();
        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        parent_layout = findViewById( R.id.ParentDataPengurus );

        tvNamaBankSampah.setText( getNama );

        btnTambahPengurus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        } );
    }

    private void validateData(){
        final String strNamaBankSampah = tvNamaBankSampah.getText().toString().trim();
        final String strNamaPengurus = etNamaPengurus.getText().toString().trim();
        final String strJabatan = etJabatanPengurus.getText().toString().trim();

        if (strNamaPengurus.isEmpty()) {
            etNamaPengurus.setError("Nama Pengurus Diperlukan");
            etNamaPengurus.requestFocus();
        }else if (strJabatan.isEmpty()) {
            etJabatanPengurus.setError("Jabatan Pengurus Diperlukan");
            etJabatanPengurus.requestFocus();
        }else{
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( DataPengurus.this);
            builder.setMessage("Apakah Anda Yakin Ingin Menambah Harga Item?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveToDB(strNamaBankSampah, strNamaPengurus, strJabatan);
                        }
                    })
                    .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }

    protected void SaveToDB(final String Namabanksampah, final String NamaPengurus, final String Jabatan) {
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"id_bank_sampah", "nama_pengurus", "jabatan"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_tambah_pengurus");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder( DataPengurus.this);
                    builder.setMessage(R.string.MSG_TAMBAH_PENGURUS)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent add = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(add);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Register Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], Namabanksampah);
                params.put(field_name[1], NamaPengurus);
                params.put(field_name[2], Jabatan);
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
}
