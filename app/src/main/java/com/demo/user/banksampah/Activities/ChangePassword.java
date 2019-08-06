package com.demo.user.banksampah.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ChangePassword extends AppCompatActivity {

    //Session Class testing
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

    private void updatePwd(final String oldPwd, final String newPwd){
        /*progressDialog.setMessage("Memperbarui Kata Sandi");
        progressDialog.show();*/

        customProgress.showProgress(this, "", false);

        final String[]field_name = {"no_telepon", "old_pass", "new_pass", "message"};
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String validate_url;

        validate_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.change_pass_user";
        params.put(field_name[0], strPhone);
        params.put(field_name[1], oldPwd);
        params.put(field_name[2], newPwd);

        //Authorization based on POST
        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(validate_url, params, new AsyncHttpResponseHandler() {
            //If Success...
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();

                String resp_content = "";
                //progressDialog.dismiss();
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    arrayResult = rest_class.getJsonData(field_name, resp_content);
                    JSONObject jsonObject = new JSONObject(resp_content);

                    String result = jsonObject.getString(field_name[3]);
                    if(result.equalsIgnoreCase("Password Changed")){
                        Toasty.success(getApplicationContext(), "Berhasil Memperbarui Kata Sandi.", Toast.LENGTH_LONG).show();
                    }else{
                        Toasty.error(getApplicationContext(), "Kata Sandi Lama yang Anda Masukkan Salah!\nMohon Periksa Data Kembali.", Toast.LENGTH_LONG).show();
                    }

                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + "1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_409) + "1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("Tag", " 1:" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();

                //progressDialog.dismiss();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + "1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_500) + "1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag","1: " + String.valueOf(error));
            }
        });
    }
}
