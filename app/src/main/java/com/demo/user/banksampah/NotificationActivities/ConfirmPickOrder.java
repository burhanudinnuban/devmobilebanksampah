package com.demo.user.banksampah.NotificationActivities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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

public class ConfirmPickOrder extends AppCompatActivity {

    //Session Class
    PrefManager session;
    String strIDUser;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected CustomProgress customProgress;

    protected ListView lvConfirmPick;

    protected LinearLayout linear_noData;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private CardView cd_NoData, cd_NoConnection;
    protected ConnectivityManager conMgr;

    protected LazyAdapter adapter;

    //PopUp Image Dialog
    Dialog myDialog;
    TextView tvIDOrder_Pop, tvTanggal_Pop, tvHari_Pop, tvJam_Pop;

    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pick_order);

        //LocalBroadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.demo.user.erecycle.notification"));
        myDialog = new Dialog(this);

        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

       // dialog = new ProgressDialog(this);

        lvConfirmPick =  findViewById(R.id.lv_ConfirmPick);
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linear_noData = findViewById(R.id.linear_NoData);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getConfirmPick();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection",
                            Toast.LENGTH_SHORT).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getConfirmPick();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                }
            }
        });

        /*if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getConfirmPick();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }*/


       /* //Test
        if(getIntent().getExtras() != null){
            for(String key:getIntent().getExtras().keySet()){
                if (key.equals("EXTRA_BODY")){
                }
                if (key.equals("EXTRA_TITLE")){
                }
            }
            PopUpConfirmPick();
                    tvIDOrder_Pop.setText("ID-000012");
                    tvHari_Pop.setText("Senin");
                    tvJam_Pop.setText("12.12");
                    tvTanggal_Pop.setText("21 Mei 2019");

        }
    */
    }

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null) {
                if (intent.getAction().equals("com.demo.user.erecycle.notification")) {
                    String message = intent.getStringExtra("message");
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    protected void onResume() {
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getConfirmPick();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.demo.user.erecycle.notification"));
        super.onResume();
    }

    private void PopUpConfirmPick() {
        myDialog.setContentView(R.layout.lv_konfirmasi_penjemputan);

        tvIDOrder_Pop = myDialog.findViewById(R.id.tvIDOrder_Penjemputan);
        tvHari_Pop = myDialog.findViewById(R.id.tvHari_Penjemputan);
        tvTanggal_Pop = myDialog.findViewById(R.id.tvTanggal_Penjemputan);
        tvJam_Pop = myDialog.findViewById(R.id.tvJam_Penjemputan);

        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void getConfirmPick() {
        /*dialog.setMessage("Mengambil Data, Harap Menunggu...");
        dialog.show();*/

        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        list_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.validation_pickuptime";
        params.put("id_user", strIDUser);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(list_order_url, params, new AsyncHttpResponseHandler() {
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
                    displayPickOrder(resp_content);
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //dialog.dismiss();
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_SHORT).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
            }
        });
    }

    private void displayPickOrder(String resp_content) {
        String[] field_name = {"message", "name", "tanggal_penjemputan", "id_order", "waktu_penjemputan", "id_user", "order_status"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);

            ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
//            ArrayList<HashMap<String, String>> allOrder_Line = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String IdPicker_Result = c.getString(field_name[1]);
                String tglJemput_Result = c.getString(field_name[2]);
                String IdOrder_Result = c.getString(field_name[3]);
                String waktuJemput_Result = c.getString(field_name[4]);
                String IdUser_Result = c.getString(field_name[5]);
                String orderStatus_Result = c.getString(field_name[6]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], IdPicker_Result);
                map.put(field_name[2], tglJemput_Result);
                map.put(field_name[3], IdOrder_Result);
                map.put(field_name[4], waktuJemput_Result);
                map.put(field_name[5], IdUser_Result);
                map.put(field_name[6], orderStatus_Result);

                allOrder.add(map);
            }

            adapter = new LazyAdapter(this, allOrder, 6);
            lvConfirmPick.setAdapter(adapter);

            cd_NoConnection.setVisibility(View.GONE);
            cd_NoData.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            //Toast.makeText(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }
}
