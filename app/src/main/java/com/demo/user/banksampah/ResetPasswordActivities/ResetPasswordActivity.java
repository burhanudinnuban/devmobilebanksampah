package com.demo.user.banksampah.ResetPasswordActivities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    //API Process and Dialog
    private RestProcess rest_class;
    private HashMap<String,String> apiData;
    protected ArrayList<HashMap<String, String>> arrayResult = new ArrayList<>();
    protected ArrayList<HashMap<String, String>> arrayValidateOTP = new ArrayList<>();

    //Dialog Message
    private CustomProgress customProgress;
    private Dialog myDialog;

    //Initiate Data XML and Variable
    private EditText etHP_Reset;
    protected String strHP_Reset = "";
    protected Button btKonfirmasi_Reset;
    protected String getMessage_Result;

    protected PrefManager session;
    protected RelativeLayout parent_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        customProgress = CustomProgress.getInstance();
        myDialog = new Dialog(ResetPasswordActivity.this);

        session = new PrefManager( getApplicationContext() );
        HashMap<String, String> user = session.getUserDetails();
        final String strPhone = user.get( PrefManager.KEY_NO_HP );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        parent_layout = findViewById( R.id.layout_parent );
        etHP_Reset = findViewById(R.id.etHp_Reset);
        btKonfirmasi_Reset = findViewById(R.id.btKonfirmasi_Reset);

        btKonfirmasi_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData(){
        strHP_Reset = etHP_Reset.getText().toString();
        String resp_content = "";
        if(strHP_Reset.length() == 0){
            etHP_Reset.setError(getString(R.string.MSG_CELLPHONE_EMPTY));
            etHP_Reset.requestFocus();
        }else if (strHP_Reset.length() <= 9){
            etHP_Reset.setError(getString(R.string.MSG_NO_HP_EMPTY));
            etHP_Reset.requestFocus();
        }else{
            strHP_Reset = modifNumber(strHP_Reset);
            ValidatePhoneNumber(strHP_Reset);
            Log.e("tag", strHP_Reset);
        }
    }

    private String modifNumber(String num){
        if(num.startsWith("08")){
            num = num.replaceFirst("08", "08");
        }else if(num.startsWith("628")){
            num = num.replaceFirst("628", "08");
        }
        return num;
    }

    private void ValidatePhoneNumber(final String phone){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon"};

        String base_url = apiData.get("str_url_address") + (".check_lupa_pass");

        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( ResetPasswordActivity.this);
                    builder.setMessage("Sukses")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    viewStatus(response, phone );
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
                params.put(field_name[0], phone);
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

    private void viewStatus(String resp_content, String phone){
        String[]field_name = {"message", "no_telepon", "id_user"};
        try{
            arrayResult = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonObject = new JSONObject( resp_content );

            String message = jsonObject.getString(field_name[0]);
            String strPhone = jsonObject.getString( field_name[1] );
            String idUser = jsonObject.getString( field_name[2] );
            session.lupaPassword( strPhone, idUser );

            if(message.equals( "OTP Send" )){
                Toasty.success(ResetPasswordActivity.this,"Kode Aktivasi Telah Dikirimkan ke Nomor Telepon Anda. Silakan Masukkan Kode Aktivasi Tersebut", Toast.LENGTH_LONG).show();
                showOTPInput(phone);
            }
            else {
               AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                    builder.setMessage("Nomor yang Anda Masukkan Tidak Terdaftar. Harap Periksa Nomor Telepon Kembali.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
        } catch (JSONException e){
            Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_501) + "1 : " + getString(R.string.MSG_TRY_AGAIN), Toast.LENGTH_LONG).show();
            Log.e("tag", " 1: " + String.valueOf(e));
        }
    }

    private void showOTPInput(String phone){
        myDialog.setContentView(R.layout.activity_otp);
        myDialog.setCanceledOnTouchOutside(false);

        Button btnKonfirmasi_PopUp = myDialog.findViewById(R.id.btAktivasi_Regist);
        TextView tvSendOTP = myDialog.findViewById(R.id.tvSendOTP);
        final EditText etKodeAktivasi_PopUp = myDialog.findViewById(R.id.etPinOTP_Regist);

        tvSendOTP.setVisibility(View.GONE);
        btnKonfirmasi_PopUp.setText("Konfirmasi");
        btnKonfirmasi_PopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKodeAktivasi(etKodeAktivasi_PopUp, phone);
            }
        });

        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            myDialog.show();
        }
    }

    private void checkKodeAktivasi(final EditText etAktivasi, final String phone){
        String KodeAktivasi = etAktivasi.getText().toString();
        session = new PrefManager( getApplicationContext() );
        HashMap<String, String> user = session.getUserDetails();
        String strPhone = user.get( PrefManager.KEY_NO_HP );
        if(KodeAktivasi.isEmpty()){
            etAktivasi.setError("Harap Masukkan Kode Aktivasi");
            etAktivasi.requestFocus();
        }else{
            ValidateOTP(KodeAktivasi, phone);
        }
    }

    private void ValidateOTP(final String kode, final String phone){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon", "otp"};

        String base_url = apiData.get("str_url_address") + (".validate_otp");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( ResetPasswordActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), ChangeResetPasswordActivity.class);
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
                params.put(field_name[0], strHP_Reset);
                params.put(field_name[1], kode);
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
