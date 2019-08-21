package com.demo.user.banksampah.DataPengurus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditPengurus extends AppCompatActivity {
    protected String strIDUser;

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    private RestProcess rest_class;
    private HashMap<String, String> apiData;
    protected CustomProgress customProgress;
    protected EditText etNamaPengurus, etJabatanPengurus;
    protected Button btnEdit;
    protected TextView tvNamaBankSampah, tvIdPengurus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_pengurus );

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        //progressDialog = new ProgressDialog(this);
        customProgress = CustomProgress.getInstance();

        etNamaPengurus = findViewById( R.id.etNamaPengurus );
        etJabatanPengurus = findViewById( R.id.etjabatanPengurus );
        btnEdit = findViewById( R.id.btEditPengurus );
        tvNamaBankSampah = findViewById( R.id.tvBankSampahPengurus );
        tvIdPengurus = findViewById( R.id.tvIdPengurus );

        String strNamaPengurus = getIntent().getStringExtra("nama_pengurus");
        String strJabatanPengurus = getIntent().getStringExtra("jabatan");
        String strNamaBankSampah = getIntent().getStringExtra("id_bank_sampah");
        String strIdPengurus = getIntent().getStringExtra("name");

        tvIdPengurus.setText( strIdPengurus );
        tvNamaBankSampah.setText( strNamaBankSampah );
        etNamaPengurus.setText( strNamaPengurus );
        etJabatanPengurus.setText( strJabatanPengurus );

        btnEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        } );

    }

    private void EditPengurus(final String strIdPengurus, final String strJabatan, final String strNamaPengurus){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditPengurus.this);
        builder.setMessage("Data Berhasil Diubah")
                .setCancelable(false)
                .setTitle("Konfirmasi Edit")
                .setMessage("Apakah Anda yakin?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String[] field_name = {"nama_pengurus","jabatan","id_pengurus"};
                        String base_url = apiData.get("str_url_address") + (".update_pengurus");
                        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("DEBUG", "Register Response: " + response);
                                try {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditPengurus.this);
                                    builder.setMessage("Data Berhasil Dirubah.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent hapusPengurus = new Intent(EditPengurus.this, MainActivity.class);
                                                    startActivity(hapusPengurus);
                                                    finish();
                                                }
                                            });
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                    Log.e("tag", "sukses");
                                } catch (Throwable t) {
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put(field_name[0],strNamaPengurus);
                                params.put(field_name[1],strJabatan);
                                params.put(field_name[2],strIdPengurus);
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
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
        Log.e("tag", "sukses");
    }

    private void validateData() {
        final String strNamaPengurus = etNamaPengurus.getText().toString();
        final String strJabatanPengurus = etJabatanPengurus.getText().toString();
//        final String strNamaBankSampah = tvNamaBankSampah.getText().toString();
        final String strIdPengurus = tvIdPengurus.getText().toString();

        if (strNamaPengurus.isEmpty()) {
            etNamaPengurus.setError("Nama Pengurus Diperlukan");
            etNamaPengurus.requestFocus();
        }
        else if (strJabatanPengurus.isEmpty()) {
            etJabatanPengurus.setError("Jabatan Pengurus Diperlukan");
            etJabatanPengurus.requestFocus();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder( EditPengurus.this);
            builder.setMessage("Apakah Anda Yakin Memperbarui Data Pengurus?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditPengurus(strIdPengurus, strJabatanPengurus, strNamaPengurus);
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
