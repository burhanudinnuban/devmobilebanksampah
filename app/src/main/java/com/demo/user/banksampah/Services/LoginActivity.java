package com.demo.user.banksampah.Services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.RegisterActivities.RegisterPhoneActivity;
import com.demo.user.banksampah.ResetPasswordActivities.ResetPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout parent_layout;

    private Activity activity;
    final Context context = this;

    private EditText etNoHP, etPassword;
    protected String var_noHP, var_password, var_imei;

    private CustomProgress customProgress;

    protected HashMap<String,String> apiData;
    private RestProcess rest_class;

    ArrayList<HashMap<String, String>> arrayLogin = new ArrayList<>();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    //Session Class
    protected PrefManager session;
    protected ConnectivityManager conMgr;

    protected String pref_strNama, pref_strFoto, pref_strNoHp, pref_strPoint, pref_strLatlong,
            pref_strAlamat, pref_strEmail, pref_strID, pref_role_user, pref_token;
    protected String pref_getName;

    //Get Token Data
    private String refreshedToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }
        parent_layout = findViewById(R.id.parent);

        session = new PrefManager( LoginActivity.this );
        HashMap<String, String> user = session.getUserDetails();
        String strNoHp = user.get( PrefManager.KEY_NO_HP );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        activity = (Activity) context;
        customProgress = CustomProgress.getInstance();

        Button btnLogin = this.findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPass = findViewById(R.id.tvForgotPass);

        //Inisiasi No HP dan Password
        etNoHP = findViewById(R.id.etNoHP);
        etNoHP.setText( strNoHp );
        etPassword = findViewById(R.id.etPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                var_noHP = etNoHP.getText().toString().trim();
                var_password = etPassword.getText().toString().trim();

                if(var_noHP.isEmpty()){
                    etNoHP.setError(getString(R.string.MSG_CELLPHONE_EMPTY));
                    etNoHP.requestFocus();
                } else if(var_noHP.length()<=9) {
                    etNoHP.setError(getString(R.string.MSG_NO_HP_EMPTY));
                    etNoHP.requestFocus();
                } else if(var_password.isEmpty()) {
                    etPassword.setError(getString(R.string.MSG_PASSWORD_EMPTY));
                    etPassword.requestFocus();
                } else {
                    var_noHP = modifNumber(var_noHP);
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        loginProcess(var_noHP, var_password);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_register = new Intent(activity, RegisterPhoneActivity.class);
                startActivity(intent_register);
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tncIntent = new Intent(activity, ResetPasswordActivity.class);
                startActivity(tncIntent);
            }
        });
    }

    @Override
    public void onStart(){
        //PrefManager
        session = new PrefManager(getApplicationContext());

        //For Save Phone Number in EditText...
        SharedPreferences mPrefs = getSharedPreferences("myprofile",0);
        var_noHP = mPrefs.getString("member_id", "");
        var_imei = mPrefs.getString("session_id", "");
        etNoHP.setText(var_noHP);

        if(session.isLoggedIn()){
            HashMap<String,String> user = session.getUserDetails();
            pref_getName = user.get(PrefManager.KEY_NAMA);
            Toasty.info(getApplicationContext(), "Selamat Datang " + pref_getName + ",\n" + "Semoga Harimu Menyenangkan!", Toast.LENGTH_LONG).show();
            Intent intent_session = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent_session);
            finish();
        }
        super.onStart();
    }

    @Override
    public void onResume(){
        onTokenRefresh();
        super.onResume();
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

    private void loginProcess(final String var_noHP, final String var_password){
        customProgress.showProgress(this, "", false);

        final String[]field_name = {"no_telepon", "password", "token"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_login");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Login Response: " + response);
                try {
                    displayLogin(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Check Login Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
                Log.d(TAG, "Volley Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], var_noHP);
                params.put(field_name[1], var_password);
                params.put(field_name[2], refreshedToken);
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

    private void displayLogin(String resp_content){
        String[] field_name = {"message", "nama", "foto", "role_user", "latlong", "token",
                "no_telepon", "alamat", "id", "email","jam_operasional","point"};

        try {
            arrayLogin = rest_class.getJsonData(field_name,resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);
            String message = jsonPost.getString(field_name[0]);

            if(message.equalsIgnoreCase("Logged In")){
                pref_strNama = jsonPost.getString(field_name[1]);
                pref_strFoto = jsonPost.getString(field_name[2]);
                pref_role_user = jsonPost.getString(field_name[3]);
                pref_strLatlong = jsonPost.getString(field_name[4]);
                pref_token = jsonPost.getString(field_name[5]);
                pref_strNoHp = jsonPost.getString(field_name[6]);
                pref_strAlamat = jsonPost.getString(field_name[7]);
                pref_strID = jsonPost.getString(field_name[8]);
                pref_strEmail = jsonPost.getString(field_name[9]);
                String jamOperasional = jsonPost.getString(field_name[10]);
                String point = jsonPost.getString(field_name[11]);

                session.createLoginSession(pref_strNoHp, pref_strNama, pref_strLatlong,
                        pref_strAlamat, pref_strEmail, pref_strFoto, pref_strID, pref_role_user, jamOperasional, point);

                session.createTokenSession(pref_token);

                Toasty.success(getApplicationContext(), "Selamat Datang " + pref_strNama + ",\n" + "Semoga Harimu Menyenangkan!", Toast.LENGTH_LONG).show();
                Intent main_intent = new Intent(activity, MainActivity.class);
                startActivity(main_intent);
                finish();
            }else{
                Toasty.error(getApplicationContext(), getString(R.string.MSG_FALSE_LOGIN) + "\n" + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
        }

    }

    public void onTokenRefresh() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    refreshedToken = task.getResult().getToken();
                    Log.e(TAG, "Firebase TOKEN ID : " + refreshedToken);
                }else{
                    Log.w(TAG, "getInstanceId Failed", task.getException());
                }
            }
        });
    }

    private void checkAndRequestPermissions() {
        int phone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int write_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int read_storage = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            read_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (write_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[0]),REQUEST_ID_MULTIPLE_PERMISSIONS);
        }
    }
}
