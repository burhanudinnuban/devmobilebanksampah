package com.demo.user.banksampah.NotificationActivities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import es.dmoral.toasty.Toasty;

public class RiwayatOrder extends AppCompatActivity {

    //Session Class
    protected PrefManager session;
    protected String strIDUser;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    private CustomProgress customProgress;

    protected ListView lvListRiwayatOrder;
    protected ListView lvgetDetailRiwayatList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout linear_listRiwayatOrder;
    protected ConnectivityManager conMgr;

    protected LazyAdapter adapter;

    protected View include_FormOrderList;
    protected CardView cd_NoData, cd_NoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_order);

        session = new PrefManager(RiwayatOrder.this);
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        customProgress = CustomProgress.getInstance();

        lvListRiwayatOrder = findViewById(R.id.listView_RiwayatOrder);
        lvgetDetailRiwayatList = findViewById(R.id.listView_OrderDetails);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linear_listRiwayatOrder = findViewById(R.id.linearLayout_ListRiwayatOrder);
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getListRiwayatOrder(strIDUser);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_listRiwayatOrder.setVisibility(View.GONE);

        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        getListRiwayatOrder(strIDUser);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getListRiwayatOrder(strIDUser);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_listRiwayatOrder.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getListRiwayatOrder(final String strIDUser) {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        list_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.riwayat_order_user";
        params.put("id_user", strIDUser);

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
                    displayOrder(resp_content);
                } catch (Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();

                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
//                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void displayOrder(String resp_content) {
        String[] field_name = {"message", "berat_total", "total_point", "id_user", "name", "order_status", "image", "alamat"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            String message = jsonObject.getString(field_name[0]);

            if (!message.equalsIgnoreCase("Invalid")) {
                ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
                JSONArray cast = jsonObject.getJSONArray(field_name[0]);
                Log.e("tag", String.valueOf(cast.length()));

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);

                    String beratTotal_Result = c.getString(field_name[1]);
                    String totalPoint_Result = c.getString(field_name[2]);
                    String idUser_Result = c.getString(field_name[3]);
                    String orderName_Result = c.getString(field_name[4]);
                    String status_Result = c.getString(field_name[5]);
                    String image_Result = c.getString(field_name[6]);
                    String alamat_Result = c.getString(field_name[7]);

                    Log.e("tag_order", orderName_Result);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(field_name[1], beratTotal_Result);
                    map.put(field_name[2], totalPoint_Result);
                    map.put(field_name[3], idUser_Result);
                    map.put(field_name[4], orderName_Result);
                    map.put(field_name[5], status_Result);
                    map.put(field_name[6], image_Result);
                    map.put(field_name[7], alamat_Result);

                    allOrder.add(map);
                }

                Log.d("tag", allOrder.toString());

                adapter = new LazyAdapter(this, allOrder, 5);
                lvListRiwayatOrder.setAdapter(adapter);

                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.GONE);
                linear_listRiwayatOrder.setVisibility(View.VISIBLE);

            } else {
                //include_FormOrderList.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_listRiwayatOrder.setVisibility(View.GONE);
                cd_NoConnection.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();

            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }
}
