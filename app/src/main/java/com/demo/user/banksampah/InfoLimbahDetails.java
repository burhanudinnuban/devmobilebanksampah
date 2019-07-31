package com.demo.user.banksampah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.RecyclerViewAdapter;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class InfoLimbahDetails extends AppCompatActivity {

    private ArrayList<String> arr_TipeSampah = new ArrayList<>();
    private ArrayList<String> arr_Penjelasan = new ArrayList<>();
    private ArrayList<String> arr_Image = new ArrayList<>();
    private ArrayList<String> arr_ImageParent = new ArrayList<>();
    private ArrayList<String> arr_JenisSampah = new ArrayList<>();

    private HashMap<String, String> sysMsg = new HashMap<>();
    RecyclerView recyclerView;

    //Get Extra from intent
    protected String strJenisSampah, strImageSampah;

    ProgressDialog progressDialog;

    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected ConnectivityManager conMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_limbah_details);

        Intent intent = getIntent();

        recyclerView = findViewById(R.id.rv_InfoDetailSampah);

        strJenisSampah = intent.getStringExtra("EXTRA_JENIS_SAMPAH");
        strImageSampah = intent.getStringExtra("EXTRA_JENIS_IMAGE_SAMPAH");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menampilkan Deskripsi Limbah, Harap Menunggu...");

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getDetailSampah();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getDetailSampah() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        progressDialog.show();

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_info_limbah";
        params.put("tipe", strJenisSampah);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                String str = null;
                try {
                    str = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    String resp_content;
                    resp_content = rest_class.extractJson(str, "txlist", sysMsg);
                    displayTrash(resp_content);
                    Log.e("tag", "test_success");

                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });

    }

    private void displayTrash(String resp_content) {
        String[] field_name = {"message", "penjelasan", "name", "image"};

        try {
            JSONObject jsonPost = new JSONObject(resp_content);
            //ArrayList<HashMap<String, String>> allNames = new ArrayList<>();

            JSONArray cast = jsonPost.getJSONArray(field_name[0]);
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                // Post result field to string
                String deskripsi_result = c.getString(field_name[1]);
                String name_result = c.getString(field_name[2]);
                String image_result = c.getString(field_name[3]);

                // Make HashMap string for put string above
//                HashMap<String, String> map = new HashMap<String, String>();
//
//                map.put(field_name[1], deskripsi_result);
//                map.put(field_name[2], name_result);
//                map.put(field_name[3], image_result);

                arr_Image.add(image_result);
                arr_Penjelasan.add(deskripsi_result);
                arr_TipeSampah.add(name_result);
                arr_JenisSampah.add(strJenisSampah);
                arr_ImageParent.add(strImageSampah);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                RecyclerView recyclerView = findViewById(R.id.rv_InfoDetailSampah);
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, arr_TipeSampah, arr_Penjelasan, arr_Image, arr_ImageParent, arr_JenisSampah);
                recyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 4 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            Log.e("tag", " 4: " + String.valueOf(e));
        }
    }
}
