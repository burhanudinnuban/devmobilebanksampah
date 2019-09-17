package com.demo.user.banksampah.Services;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    //Session Class
    PrefManager session;

    //API Process and Dialog
    private RestProcess rest_class;
    protected HashMap<String,String> apiData;
    protected ArrayList<HashMap<String, String>> arrayResult = new ArrayList<>();

    //Dialog Message
    //private ProgressDialog progressDialog;
    private CustomProgress customProgress;

    //Initiate Data XML and Variable
    private EditText etOldPwd, etNewPwd, etConfirmPwd;
    private String strPhone;
    protected Button btUpdate;

    //Parent Layout
    ScrollView parent_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //progressDialog = new ProgressDialog(this);
        customProgress = CustomProgress.getInstance();

        session = new PrefManager(getApplicationContext());
        HashMap<String,String> user = session.getUserDetails();
        strPhone = user.get(PrefManager.KEY_NO_HP);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd = findViewById(R.id.etNewPwd);
        etConfirmPwd = findViewById(R.id.etConfirmPwd);
        btUpdate = findViewById(R.id.btUpdatePwd);
        parent_layout = findViewById(R.id.parentLayout);

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData(){
        final String strOldPwd = etOldPwd.getText().toString().trim();
        final String strNewPwd = etNewPwd.getText().toString().trim();
        String strConfirmPwd = etConfirmPwd.getText().toString().trim();

        if (strOldPwd.isEmpty()){
            etOldPwd.setError("Harap Masukkan Kata Sandi Lama Anda");
            etOldPwd.requestFocus();
        }else if (strNewPwd.isEmpty()){
            etNewPwd.setError("Harap Masukkan Kata Sandi Baru Anda");
            etNewPwd.requestFocus();
        }else if (strNewPwd.length() <= 7){
            etNewPwd.setError("Minimal Kata Sandi 8 Karakter");
            etNewPwd.requestFocus();
        } else if (strConfirmPwd.isEmpty()){
            etConfirmPwd.setError("Harap Konfirmasi Kata Sandi");
            etConfirmPwd.requestFocus();
        } else if (!strNewPwd.equals(strConfirmPwd)){
            etNewPwd.setError(getString(R.string.MSG_PASSWORD_CHECK));
            etNewPwd.requestFocus();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
            builder.setMessage("Apakah Anda Yakin Memperbarui Kata Sandi?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updatePwd(strOldPwd, strNewPwd);
                        }
                    })
                    .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    protected void updatePwd(final String oldPwd, final String newPwd) {
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon", "old_pass", "new_pass","message"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_change_password");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChangePassword.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(changePassword);
                                    finish();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
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
                params.put(field_name[0], strPhone);
                params.put(field_name[1], oldPwd);
                params.put(field_name[2], newPwd);
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
