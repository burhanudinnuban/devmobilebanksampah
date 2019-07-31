package com.demo.user.banksampah.NotificationActivities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
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
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class PickStatus extends AppCompatActivity {

    //Session Class
    PrefManager session;
    String strIDUser;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected CustomProgress customProgress;

    protected ListView lvStatusPick;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ConnectivityManager conMgr;

    protected LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_status);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);

        rest_class = new RestProcess();
        lvStatusPick = findViewById(R.id.lv_StatusPick);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getStatusPick();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getStatusPick();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    private void getStatusPick() {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        /*final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Mengambil Data, Harap Menunggu...");
        dialog.show();*/

        list_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_all_picker_information";
        params.put("id_user", strIDUser);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(list_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = null;

                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displayStatusPick(resp_content);
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });
    }

    private void displayStatusPick(String resp_content) {
        String[] field_name = {"message", "name", "no_kendaraan", "tanggal_penjemputan", "waktu_penjemputan",
                "nama_picker", "tipe_kendaraan", "foto_diri"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);

            ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String idOrder_Result = c.getString(field_name[1]);
                String nopol_Result = c.getString(field_name[2]);
                String tglJemput_Result = c.getString(field_name[3]);
                String dateTime_Result = c.getString(field_name[4]);
                String namaDriver_Result = c.getString(field_name[5]);
                String tipeKendaraan_Result = c.getString(field_name[6]);
                String fotoDriver_Result = c.getString(field_name[7]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], idOrder_Result);
                map.put(field_name[2], nopol_Result);
                map.put(field_name[3], tglJemput_Result);
                map.put(field_name[4], dateTime_Result);
                map.put(field_name[5], namaDriver_Result);
                map.put(field_name[6], tipeKendaraan_Result);
                map.put(field_name[7], fotoDriver_Result);

                allOrder.add(map);
            }

            adapter = new LazyAdapter(this, allOrder, 7);
            lvStatusPick.setAdapter(adapter);

        } catch (JSONException e) {
            //Toast.makeText(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }
}
