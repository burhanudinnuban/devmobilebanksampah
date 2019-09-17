package com.demo.user.banksampah.ResetPasswordActivities;

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
import com.demo.user.banksampah.Services.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ChangeResetPasswordActivity extends AppCompatActivity {

    protected EditText etNewPwd, etConfirmPwd;
    protected String strNewPwd, strConfirmPwd;
    protected Button btKofirmasi_Reset;

    private CustomProgress customProgress;
    private RestProcess rest_class;
    private HashMap<String,String> apiData;
    private RelativeLayout parent_layout;
    private PrefManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_reset_password);

        customProgress = CustomProgress.getInstance();
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        session = new PrefManager( getApplicationContext() );
        HashMap<String, String> user = session.getUserDetails();
        final String strUserId = user.get( PrefManager.KEY_ID );

        etNewPwd = findViewById(R.id.etNewPwd_Reset);
        etConfirmPwd = findViewById(R.id.etConfirmPwd_Reset);
        parent_layout = findViewById( R.id.layoutParent );


        btKofirmasi_Reset = findViewById(R.id.btKonfirmasi_Reset);

        btKofirmasi_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(strUserId);
            }
        });
    }

    private void validateData(final String userID){
        strNewPwd = etNewPwd.getText().toString().trim();
        strConfirmPwd = etConfirmPwd.getText().toString().trim();

        if(strNewPwd.isEmpty()){
            etNewPwd.setError(getString(R.string.MSG_PASSWORD_EMPTY));
            etNewPwd.requestFocus();
        } else if(strConfirmPwd.isEmpty()){
            etConfirmPwd.setError(getString(R.string.MSG_PASSWORD_EMPTY));
            etConfirmPwd.requestFocus();
        } else if (strNewPwd.length() <= 7){
            etNewPwd.setError("Minimal Kata Sandi 8 Karakter");
            etNewPwd.requestFocus();
        } else if(!strNewPwd.equals(strConfirmPwd)){
            Toasty.warning(getApplicationContext(), getString(R.string.MSG_PASSWORD_CHECK), Toast.LENGTH_LONG).show();
        } else{
            updatePassword(strNewPwd, userID);
        }
    }

    private void updatePassword(final String strNewPwd, final String userID){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"new_pass", "id_user"};

        String base_url = apiData.get("str_url_address") + (".lupa_pass");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( ChangeResetPasswordActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), LoginActivity.class);
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
                params.put(field_name[0], strNewPwd);
                params.put(field_name[1], userID);
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
