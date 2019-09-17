package com.demo.user.banksampah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.user.banksampah.Services.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WebViewTest extends AppCompatActivity {

    private WebView webview, webview_getdata;
    Button test;
    ListView lv_Sampah;

    //Untuk List View
    String url = "https://dev-erpnext.pracicointiutama.id/api/resource/Waste%20Classification";
    ArrayList<String> ListSampah;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        //tambahkan kode di bawah ini
        webview = (WebView) this.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyBrowser());
        webview.loadUrl("https://dev-erpnext.pracicointiutama.id/api/method/login?usr=Administrator&pwd=1");

        webview_getdata = (WebView) this.findViewById(R.id.webView_Test);
        webview_getdata.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyBrowser());
        webview_getdata.loadUrl("https://dev-erpnext.pracicointiutama.id/api/resource/Waste%20Classification");

        //Untuk ListView
        spinner = findViewById(R.id.spinner_list);
        ListSampah = new ArrayList<>();

        test = findViewById(R.id.button_test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent (WebViewTest.this, MainActivity.class);
                startActivity(a);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sampah = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                Toast.makeText(getApplicationContext(),sampah,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });


        loadSpinnerData(url);


    }

    private void loadSpinnerData(String url) {
        Log.e("tag", "cek");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag", "sukses");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("tag", "json");
                    JSONArray jsonArray;
                    jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String datanya = jsonObject1.getString("name");
                        ListSampah.add(datanya);
                        Toast.makeText(getApplicationContext(), datanya, Toast.LENGTH_LONG).show();
                    }spinner.setAdapter(new ArrayAdapter<String>(WebViewTest.this, android.R.layout.simple_dropdown_item_1line, ListSampah));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("tag", "gagal");
                error.printStackTrace();
            }

        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private class MyBrowser extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url){
        view.loadUrl(url);
        return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //ketika disentuh tombol back
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
