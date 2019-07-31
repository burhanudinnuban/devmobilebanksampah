package com.demo.user.banksampah.RegisterActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Adapter.RestProcess;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = OtpActivity.class.getSimpleName();

    private RelativeLayout parent_layout;
    private EditText etPinOTP_Regist;
    protected String strPinOTP_Regist;
    protected String strPhone_Extra;
    protected Button btAktivasi_Regist;
    protected TextView tvSendOTP;

    protected String strGetMessage_Result;
    protected String strGetPhone_Result;
    protected String strGetIdUser_Result;

    private RestProcess rest_class;
    protected HashMap<String,String> apiData;
    private CustomProgress customProgress;

    protected ProgressDialog dialog;

    protected ArrayList<HashMap<String, String>> arrayValidateOTP = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        customProgress = CustomProgress.getInstance();

        Intent intent_phone = getIntent();
        if (getIntent().getExtras() != null)
            strPhone_Extra = intent_phone.getStringExtra("no_telepon");

        parent_layout = findViewById(R.id.parent);
        etPinOTP_Regist = findViewById(R.id.etPinOTP_Regist);
        btAktivasi_Regist = findViewById(R.id.btAktivasi_Regist);
        tvSendOTP = findViewById(R.id.tvSendOTP);

        CountDownTimer();

        btAktivasi_Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiInput();
            }
        });

        tvSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOTP();
                CountDownTimer();
            }
        });
    }

    private void CountDownTimer(){
        new CountDownTimer(120000, 100){
            public void onTick(long millisUntilFinished){
                tvSendOTP.setEnabled(false);
                NumberFormat nf = new DecimalFormat("00");
                long min = (millisUntilFinished/60000) % 60;
                long sec = (millisUntilFinished/1000) % 60;
                tvSendOTP.setText("Kirim Ulang Kode Aktivasi " + "(" + nf.format(min) + ":" + nf.format(sec) + ")");
            }
            public void onFinish(){
                tvSendOTP.setText("Kirim Ulang Kode Aktivasi..");
                tvSendOTP.setEnabled(true);
            }
        }.start();
    }

    private void validasiInput(){
        strPinOTP_Regist = etPinOTP_Regist.getText().toString();

        if (TextUtils.isEmpty(strPinOTP_Regist)){
            etPinOTP_Regist.setError("Harap Masukkan Kode OTP yang Telah Dikirimkan");
            etPinOTP_Regist.requestFocus();
        } else{
            validasiOTP(strPhone_Extra, strPinOTP_Regist);
        }
    }

    private void validasiOTP(final String strPhone_Extra, final String strPinOTP_Regist){
        customProgress.showProgress(this, "", false);

        final String[]field_name = {"no_telepon", "otp"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_validate_otp");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Phone Response: " + response);
                try {
                    viewStatusOTP(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Validate OTP Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + "1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
                Log.d(TAG, "Volley Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strPhone_Extra);
                params.put(field_name[1], strPinOTP_Regist);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void viewStatusOTP(String resp_content){
        String[]field_name = {"message", "no_telepon", "id_user"};

        try{
            arrayValidateOTP = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonObject = new JSONObject(resp_content);

            strGetMessage_Result = jsonObject.getString(field_name[0]);

            if(strGetMessage_Result.equals("True")){
                strGetPhone_Result = jsonObject.getString(field_name[1]);
                strGetIdUser_Result = jsonObject.getString(field_name[2]);

                Intent intent_regist = new Intent(OtpActivity.this, RegisterActivity.class);
                intent_regist.putExtra("no_telepon", strGetPhone_Result);
                intent_regist.putExtra("id_user", strGetIdUser_Result);
                startActivity(intent_regist);
                finish();
            }
            else if (strGetMessage_Result.equals("False")){
                Toasty.error(getApplicationContext(), "Nomor Token yang Anda Masukkan Salah, Mohon Periksa Kembali", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e){
            customProgress.hideProgress();
            Snackbar snackbar = Snackbar
                    .make(parent_layout, getString(R.string.MSG_CODE_501) + " 1: " + getString(R.string.MSG_TRY_AGAIN), Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.d(TAG, "Error JSONException Validate OTP: " + e.toString());
        }
    }

    private void sendOTP(){
        final String[]field_name = {"no_telepon"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_send_otp");
        customProgress.showProgress(this, "", false);

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Phone Response: " + response);
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
                    builder.setMessage("Kode Aktivasi Telah Dikirimkan ke Nomor Anda. Silakan Masukkan Kode Aktivasi.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Check Phone Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            //jika error
            public void onErrorResponse(VolleyError error) {
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + "1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
                Log.d(TAG, "Volley Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strPhone_Extra);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }
}
