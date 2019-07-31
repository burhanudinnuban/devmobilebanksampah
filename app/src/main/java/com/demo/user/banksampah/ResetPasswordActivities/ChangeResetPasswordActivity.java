package com.demo.user.banksampah.ResetPasswordActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.user.banksampah.Activities.LoginActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ChangeResetPasswordActivity extends AppCompatActivity {

    protected EditText etNewPwd, etConfirmPwd;
    protected String strNewPwd, strConfirmPwd;
    protected Button btKofirmasi_Reset;

    private CustomProgress customProgress;
    private RestProcess rest_class;
    private HashMap<String,String> apiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_reset_password);

        customProgress = CustomProgress.getInstance();
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        etNewPwd = findViewById(R.id.etNewPwd_Reset);
        etConfirmPwd = findViewById(R.id.etConfirmPwd_Reset);

        Intent intent = getIntent();
        final String id_user = intent.getStringExtra("id_user");
        final String phone = intent.getStringExtra("no_telepon");

        btKofirmasi_Reset = findViewById(R.id.btKonfirmasi_Reset);

        btKofirmasi_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(id_user);
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

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String update_url;

        update_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.lupa_pass_user";
        params.put("new_password", strNewPwd);
        params.put("id_user", userID);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(update_url, params, new AsyncHttpResponseHandler() {
            //If Success...
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content ="";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeResetPasswordActivity.this);
                    builder.setMessage("Berhasil Melakukan Reset Password.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(login);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + "1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("Tag", " 1:" + String.valueOf(t));
                }
            }

            //If Fail...
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + "1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag","1: " + String.valueOf(error));
            }
        });
    }
}
