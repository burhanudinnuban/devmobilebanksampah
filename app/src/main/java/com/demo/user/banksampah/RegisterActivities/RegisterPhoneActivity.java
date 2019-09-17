package com.demo.user.banksampah.RegisterActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.demo.user.banksampah.Documentation.SyaratKetentuan;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Adapter.RestProcess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegisterPhoneActivity extends AppCompatActivity {

    private static final String TAG = RegisterPhoneActivity.class.getSimpleName();

    //API Process and Dialog
    private RestProcess rest_class;
    private HashMap<String,String> apiData;
    protected ArrayList<HashMap<String, String>> arrayResult = new ArrayList<>();

    //Dialog Message
    protected CustomProgress customProgress;

    //Initiate Data XML and Variable
    private RelativeLayout parent_layout;
    private EditText etHP_Regist;
    private CheckBox chkSK_Regist;
    protected TextView tvReadSK_Regist;
    protected String strHP_Regist = "";
    protected Button btKonfirmasi_Regist;
    protected String getMessage_Result, getInputOTP_Result, getNoTelepon_Result, getIDUser_Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        parent_layout = findViewById(R.id.parent);
        etHP_Regist = findViewById(R.id.etHp_Regist);
        btKonfirmasi_Regist = findViewById(R.id.btKonfirmasi_Regist);
        chkSK_Regist = findViewById(R.id.chkSK_Regist);
        tvReadSK_Regist = findViewById(R.id.tvReadSK_Regist);

        tvReadSK_Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(RegisterPhoneActivity.this, SyaratKetentuan.class);
                if(chkSK_Regist.isChecked()){
                    a.putExtra("EXTRA_CHECK_SK", "true");
                }
                startActivityForResult(a, 1);
            }
        });

        btKonfirmasi_Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == 1){
                btKonfirmasi_Regist.setEnabled(true);
                chkSK_Regist.setChecked(true);
            }
        }
    }

    private void validateData(){
        String PhoneNumber = etHP_Regist.getText().toString();
        if(PhoneNumber.length() == 0){
            etHP_Regist.setError(getString(R.string.MSG_CELLPHONE_EMPTY));
            etHP_Regist.requestFocus();
        } else if (PhoneNumber.length() <= 9){
            etHP_Regist.setError(getString(R.string.MSG_NO_HP_EMPTY));
            etHP_Regist.requestFocus();
        } else if (!chkSK_Regist.isChecked()){
            Toasty.warning(getApplicationContext(), "Harap Menyentang Syarat dan Ketentuan", Toast.LENGTH_LONG).show();
        }else{
            strHP_Regist = modifNumber(PhoneNumber);
            ValidatePhoneNumber(strHP_Regist);
        }
    }

    //Convert Phone Number If Start on 08... to +628....
    private String modifNumber(String num){
        if (num.startsWith("08")){
            num = num.replaceFirst("08", "08");
        } else if (num.startsWith("628")){
            num = num.replaceFirst("628", "08");
        }
        return num;
    }

    private void ValidatePhoneNumber(final String strHP_Regist) {
        final String[]field_name = {"no_telepon"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_check_phonenumber");
        customProgress.showProgress(this, "", false);

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Phone Response: " + response);

                try {
                    viewStatus(response);
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
                params.put(field_name[0], strHP_Regist);
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

    private void viewStatus(String resp_content){
        String[]field_name = {"message", "otp_input", "no_telepon", "id"};

        try{
            arrayResult = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonObject = new JSONObject(resp_content);

            getMessage_Result = jsonObject.getString(field_name[0]);

            if(getMessage_Result.equals("True")){
                sendOTP();
            }
            else if (getMessage_Result.equals("False")){
                getInputOTP_Result = jsonObject.getString(field_name[1]);
                getNoTelepon_Result = jsonObject.getString(field_name[2]);
                getIDUser_Result = jsonObject.getString(field_name[3]);

                if (getInputOTP_Result.equals("0") && getMessage_Result.equals("False")){
                    Toasty.info(getApplicationContext(), "Silakan Masukkan Kode Aktivasi yang Telah Terkirim Sebelumnya.", Toast.LENGTH_SHORT).show();
                    Intent new_Register = new Intent(RegisterPhoneActivity.this, OtpActivity.class);
                    new_Register.putExtra("no_telepon", strHP_Regist);
                    startActivity(new_Register);
                    finish();
                }
                else if (getInputOTP_Result.equals("1") && getMessage_Result.equals("False")){
                    Toasty.info(getApplicationContext(), "Proses Registrasi Hampir Selesai. Ayo, Isi Data Diri Kamu!", Toast.LENGTH_SHORT).show();
                    Intent intent_Register = new Intent (RegisterPhoneActivity.this, RegisterActivity.class);
                    intent_Register.putExtra("no_telepon", strHP_Regist);
                    intent_Register.putExtra("id_user", getIDUser_Result);
                    startActivity(intent_Register);
                    finish();
                } else if (getInputOTP_Result.equals("2") && getMessage_Result.equals("False")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPhoneActivity.this);
                    builder.setMessage("Mohon Maaf, Nomor Anda Telah Terdaftar. Harap Gunakan Nomor Telepon Lain.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        } catch (JSONException e){
            customProgress.hideProgress();
            Snackbar snackbar = Snackbar
                    .make(parent_layout, getString(R.string.MSG_CODE_501) + "1: " + getString(R.string.MSG_TRY_AGAIN), Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.d(TAG, "Error JSONException Check Phone: " + e.toString());
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
                    Toasty.success(getApplicationContext(), getString(R.string.MSG_SUCCESS_SEND_OTP), Toast.LENGTH_LONG).show();
                    Log.e("tag", "Test Masuk");

                    Intent new_Register = new Intent(RegisterPhoneActivity.this, OtpActivity.class);
                    new_Register.putExtra("no_telepon", strHP_Regist);
                    startActivity(new_Register);
                    finish();
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
                params.put(field_name[0], strHP_Regist);
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
