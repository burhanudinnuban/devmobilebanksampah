package com.demo.user.banksampah.MemberFragment.ListMember;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ListOrderUser extends AppCompatActivity {

    //Session Class
    protected PrefManager session;
    private ArrayList listPin = new ArrayList();
    ArrayList<HashMap<String, String>> allOrder1 = new ArrayList<>();
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected String strNo_Telepon, strIdUser, strNamaBankSampah;
    protected CustomProgress customProgress;
    protected LinearLayout parent_layout;
    protected LinearLayout parent_layout1;
    protected ListView lvListOrderUser, lvListDetailOrderUser;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout linear_ListMember;
    protected ConnectivityManager conMgr;
    protected LazyAdapter adapter, adapter1;
    protected CardView cd_NoData, cd_NoConnection;
    protected String tvIdMemberDetail1;
    protected Dialog myDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order_user);

        tvIdMemberDetail1 = getIntent().getStringExtra( "id_member" );

        session = new PrefManager( ListOrderUser.this );
        HashMap<String, String> user = session.getUserDetails();
        strNo_Telepon = user.get( PrefManager.KEY_NO_HP );
        strIdUser = user.get( PrefManager.KEY_ID );
        strNamaBankSampah = user.get( PrefManager.KEY_NAMA );

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();
        parent_layout = findViewById(R.id.parent);
        lvListOrderUser = findViewById( R.id.listView_OrderUser );
        lvListDetailOrderUser = findViewById( R.id.listView_OrderUserDetail );
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        linear_ListMember = findViewById( R.id.parent );
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        //Run Method
        getListOrderUser();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ListOrderUser.this != null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        //Jalanin API
                        allOrder.clear();
                        getListOrderUser();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    //Jalanin API
                    getListOrderUser();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        if (ListOrderUser.this != null) {
            conMgr = (ConnectivityManager) ListOrderUser.this.getSystemService( Context.CONNECTIVITY_SERVICE);
        }

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {

        } else {
            Toast.makeText(ListOrderUser.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_ListMember.setVisibility(View.GONE);

        }
    }

    private void getListOrderUser(){
        customProgress.showProgress(ListOrderUser.this, "", false);

        String base_url = apiData.get("str_url_address") + (".get_history_user");
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("debug", "Check Login Response: " + response);
                try {
                    viewDataMember(response);

                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("debug", "Error Check Login Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
                Log.d("debug", "Volley Error: " + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_bank_sampah", strNamaBankSampah);
                params.put("id_user", tvIdMemberDetail1);
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

    protected void viewDataMember(String resp_content){
        String[] field_name = {"message", "creation", "point_total", "berat_total", "id_user"};
        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            Log.e("tag_cast", String.valueOf(cast.length()));

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String tanggalOrder= c.getString(field_name[1]);
                String totalPoint = c.getString(field_name[2]);
                String totalBerat = c.getString(field_name[3]);
                String id_user = c.getString(field_name[4]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], tanggalOrder);
                map.put(field_name[2], totalPoint);
                map.put(field_name[3], totalBerat);
                map.put(field_name[4], id_user);

                allOrder.add(map);
            }

            Log.d("tag_allorder", allOrder.toString());
            adapter = new LazyAdapter(ListOrderUser.this, allOrder, 14);
            lvListOrderUser.setAdapter(adapter);

        } catch (JSONException e) {
            if (ListOrderUser.this != null) {
                Toasty.error(ListOrderUser.this, getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
            /*include_FormOrderList.setVisibility(View.GONE);
            linear_NoData.setVisibility(View.VISIBLE);
            if(getContext()!=null) {
                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
            }*/
        }
    }



}
