package com.demo.user.banksampah.Activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;
import android.content.Context;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class TrashDetail extends AppCompatActivity {

    private GridView gridView;

    //private ProgressDialog progressDialog;
    protected CustomProgress customProgress;

    private RestProcess rest_class;
    protected HashMap<String,String> apiData;

    private HashMap<String, String> sysMsg = new HashMap<>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ConnectivityManager conMgr;

    LazyAdapter adapter2;

    //LazyAdapter adapter;
    private HashMap<String, String> var_trash_detail = new HashMap<>();

    public static String getSampah = "";

    private String[] field_name = {"deskripsi", "berat_bersih", "name", "image", "harga_per_pcs",
            "point_per_kilo", "point_per_pcs", "harga_per_kilo"};

    DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_detail);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }*/

        decimalFormat = new DecimalFormat("0.#");
        gridView = findViewById(R.id.gridView_Trash);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        Intent intent = getIntent();
        getSampah = intent.getStringExtra("detailSampah");
        Log.e("tag", getSampah);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getDetailSampah(getSampah);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    getDetailSampah(getSampah);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                var_trash_detail = (HashMap<String,String>) adapter2.getItem(i);

                String deskripsiSampah = var_trash_detail.get(field_name[0]);
                String beratBersih = var_trash_detail.get(field_name[1]);
                String name = var_trash_detail.get(field_name[2]);
                String hargaPcs = var_trash_detail.get(field_name[4]);
                String pointKilo = var_trash_detail.get(field_name[5]);
                String pointPcs = var_trash_detail.get(field_name[6]);
                String hargaKilo = var_trash_detail.get(field_name[7]);

                //Start Activity
                Intent intent = new Intent(getApplicationContext(), TrashDetailOrder.class);
                Bundle extras = new Bundle();
                extras.putString("name_Sampah", name);
                extras.putString("beratBersih_Sampah", beratBersih);
                extras.putString("deskripsi_Sampah", deskripsiSampah);
                extras.putString("hargaPcs_Sampah", hargaPcs);
                if (pointKilo != null) {
                    extras.putString("pointKilo_Sampah", decimalFormat.format(Double.valueOf(pointKilo)));
                }
                extras.putString("pointPcs_Sampah", pointPcs);
                extras.putString("hargaKilo_Sampah", hargaKilo);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    private void getDetailSampah(final String getSampah) {
        customProgress = CustomProgress.getInstance();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.jenis_sampah";
        params.put("tipe", getSampah);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
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
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });

    }

    private void displayTrash(String resp_content) {
        try {
            JSONObject jsonPost = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allNames = new ArrayList<>();

            JSONArray cast = jsonPost.getJSONArray("message");
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                // Post result field to string
                String deskripsi_result = c.getString(field_name[0]);
                String beratBersih_result = c.getString(field_name[1]);
                String name_result = c.getString(field_name[2]);
                String image_result = c.getString(field_name[3]);
                String hargaPcs_result = c.getString(field_name[4]);
                String pointKilo_result = c.getString(field_name[5]);
                String pointPcs_result = c.getString(field_name[6]);
                String hargaKilo_result = c.getString(field_name[7]);

                // Make HashMap string for put string above
                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[0], deskripsi_result);
                map.put(field_name[1], beratBersih_result);
                map.put(field_name[2], name_result);
                map.put(field_name[3], image_result);
                map.put(field_name[4], hargaPcs_result);
                map.put(field_name[5], pointKilo_result);
                map.put(field_name[6], pointPcs_result);
                map.put(field_name[7], hargaKilo_result);

                allNames.add(map);
            }

            // Call Lazy Adapter for Listview
            adapter2 = new LazyAdapter(TrashDetail.this, allNames, 2);
            gridView.setAdapter(adapter2);

        } catch (JSONException e) {
            e.printStackTrace();
            Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 4: " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            Log.e("tag", " 4: " + String.valueOf(e));
        }
    }
}
