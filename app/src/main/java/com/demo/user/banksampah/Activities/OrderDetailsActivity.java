package com.demo.user.banksampah.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends AppCompatActivity {

    protected String strOrderID_Detail = "", strStatusOrder_Detail = "", strTotalKg_Detail = "",
            strTotalPoints_Detail = "", strImage_Detail = "", strAlamat_Detail = "";

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String,String> apiData;

    protected ListView lvListDetailOrder;
    protected LazyAdapter adapter;

    protected TextView tvOrderID, tvStatusOrder, tvTotalKg, tvTotalPoints, tvAlamat;

//    protected ProgressDialog dialog;
    protected CustomProgress customProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //dialog = new ProgressDialog(this);

        tvOrderID = findViewById(R.id.tvOrderID_Detail);
        tvStatusOrder = findViewById(R.id.tvStatusOrder_Detail);
        tvTotalKg = findViewById(R.id.tvTotalKg_Detail);
        tvTotalPoints = findViewById(R.id.tvTotalPoints_Detail);
        tvAlamat = findViewById(R.id.tvAlamat_Detail);

        lvListDetailOrder = findViewById(R.id.listView_OrderDetails);
    }

    protected void onStart(){
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        customProgress = CustomProgress.getInstance();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            strOrderID_Detail = extras.getString("EXTRA_ORDER_ID");
            strStatusOrder_Detail = extras.getString("EXTRA_STATUS_ORDER");
            strTotalKg_Detail = extras.getString("EXTRA_TOTAL_KG");
            strTotalPoints_Detail = extras.getString("EXTRA_TOTAL_POINTS");
            strImage_Detail = extras.getString("EXTRA_IMAGE");
            strAlamat_Detail = extras.getString("EXTRA_ALAMAT");
        }

        tvOrderID.setText(strOrderID_Detail);
        tvStatusOrder.setText(strStatusOrder_Detail);
        tvTotalKg.setText(strTotalKg_Detail + getString(R.string.placeholder_kgs));
        tvTotalPoints.setText(strTotalPoints_Detail + getString(R.string.placeholder_point));
        tvAlamat.setText(strAlamat_Detail);
        super.onStart();
    }

    protected void onResume(){
        getDetailListOrder(strOrderID_Detail);
        super.onResume();
    }

    private void getDetailListOrder(final String strOrderID_Detail) {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_detail_order_url;

        list_detail_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_order_line";
        params.put("id_order", strOrderID_Detail);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(list_detail_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //dialog.dismiss();
                customProgress.hideProgress();
                String resp_content = null;
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    if (resp_content != null) {
                        displayOrderDetail(resp_content);
                    }
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), "Periksa Koneksi Anda 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderDetail(String resp_content){
        String[]field_name = {"message", "name", "parent", "point", "creation", "tipe_sampah",
                "jenis_sampah", "jumlah_kg"};

        try{
            JSONObject jsonObject = new JSONObject(resp_content);
            ArrayList<HashMap<String,String>> allOrderDetail = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i =0; i< cast.length(); i++){
                JSONObject c = cast.getJSONObject(i);

                String idOrderLine_Result = c.getString(field_name[1]);
                String idOrder_Result = c.getString(field_name[2]);
                String point_Result = c.getString(field_name[3]);
                String dateCreation_Result = c.getString(field_name[4]);
                String tipeSampah_Result = c.getString(field_name[5]);
                String jenisSampah_Result = c.getString(field_name[6]);
                String jumlahKg_Result = c.getString(field_name[7]);

                HashMap<String,String>map = new HashMap<>();

                map.put(field_name[1], idOrderLine_Result);
                map.put(field_name[2], idOrder_Result);
                map.put(field_name[3], point_Result);
                map.put(field_name[4], dateCreation_Result);
                map.put(field_name[5], tipeSampah_Result);
                map.put(field_name[6], jenisSampah_Result);
                map.put(field_name[7], jumlahKg_Result);

                allOrderDetail.add(map);

                //#Loop ke-2
                /*try{
                    JSONObject jsonObject1 = new JSONObject(resp_content. toString());
                    ArrayList<HashMap<String,String>> detail = new ArrayList<>();

                    JSONArray cast2 = jsonObject1.getJSONArray(field_name[0]);

                    for (int a = 0; i <cast2.length(); i++){

                    }
                    adapter = new LazyAdapter(this, detail, 7);
                    lvListDetailOrder.setAdapter(adapter);
                }catch (JSONException e){

                }*/
            }
            adapter = new LazyAdapter(this, allOrderDetail, 6);
            lvListDetailOrder.setAdapter(adapter);

        } catch (JSONException e){
            Toasty.error(getApplicationContext(), "Terjadi Kesalahan 1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
