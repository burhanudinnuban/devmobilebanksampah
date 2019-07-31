package com.demo.user.banksampah.BankSampahActivities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
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
import java.util.List;

import com.demo.user.banksampah.Adapter.ExpandableListAdapter;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class DaftarHargaBankSampah extends AppCompatActivity {

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    private CustomProgress customProgress;
    protected ConnectivityManager conMgr;

    private static final String NAME = "Name";
    protected ExpandableListAdapter expandableListAdapter;
    protected ExpandableListView expandableListView;

    protected List<String> listHeader;
    protected HashMap<String, List<String>> listDataChild;
    protected List<String> data_child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_harga_bank_sampah);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        customProgress = CustomProgress.getInstance();
        expandableListView = findViewById(R.id.expandListView_Sampah);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getDaftarHarga();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDaftarHarga() {
        /*dialog.setMessage("Mengambil Data, Harap Menunggu");
        dialog.show();*/
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        list_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_harga_item";
        //params.put("id_user", strIDUser);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(list_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displaySampah(resp_content);
                } catch (Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displaySampah(String resp_content) {
        String[] field_name = {"message", "data", "point_per_kilo", "parent", "jenis_sampah", "name"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            String message = jsonObject.getString(field_name[0]);

            if (!message.isEmpty()) {
                JSONArray cast = jsonObject.getJSONArray(field_name[0]);

                listHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                String tipe_plastik;
                String parent;

                for (int a = 0; a < cast.length(); a++){
                    JSONObject getMessage = cast.getJSONObject(a);

                    tipe_plastik = getMessage.getString(field_name[5]);
                    listHeader.add(tipe_plastik);

                    Log.d("tag Tipe" + a, tipe_plastik);
                    JSONArray getData = getMessage.getJSONArray(field_name[1]);

                    data_child = new ArrayList<>();
                    for (int b = 0; b < getData.length(); b++){
                        JSONObject getChildData = getData.getJSONObject(b);

//                        data_child = new ArrayList<>();

                        String point_per_kg = getChildData.getString(field_name[2]);
                        String jenis_sampah = getChildData.getString(field_name[4]);
                        parent = getChildData.getString(field_name[3]);

                        if (parent.equalsIgnoreCase(tipe_plastik)) {
                            Log.e("tag Data" + b, parent + "," + tipe_plastik);
                            data_child.add(getChildData.getString(field_name[2]) + "," + getChildData.getString(field_name[4]));
                        }else{
                            return;
                        }

                        Log.d("tag Jenis" + b, jenis_sampah);
                        Log.d("tag Point" + b, point_per_kg);
                    }

                    listDataChild.put(listHeader.get(a), data_child);
                    Log.e("tag List:" + a, listDataChild.toString());
                }

                expandableListAdapter = new ExpandableListAdapter(this, listHeader, listDataChild);
                expandableListView.setAdapter(expandableListAdapter);

            } else {
                Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();

            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }
}
