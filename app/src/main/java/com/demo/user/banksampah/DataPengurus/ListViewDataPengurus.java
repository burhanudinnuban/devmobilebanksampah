package com.demo.user.banksampah.DataPengurus;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListViewDataPengurus extends AppCompatActivity {

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected String strIDUser;

    protected CustomProgress customProgress;
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
    protected LinearLayout parent_layout;
    protected ListView lvListMember;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout linear_ListMember;
    protected ConnectivityManager conMgr;
    protected Context ctx;

    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;
    protected FloatingActionButton btnAddPengurus;
    private ShimmerFrameLayout mShimmerViewContainer;
    //Get Data From Login Process
    protected static String getNama = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_view_data_pengurus );

        session = new PrefManager(this);
        final HashMap<String, String> user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);
        ctx = ListViewDataPengurus.this;

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        linear_ListMember = findViewById( R.id.linearLayout_ListItem );
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        parent_layout = findViewById(R.id.parent);
        customProgress = CustomProgress.getInstance();
        lvListMember = findViewById(R.id.listViewPengurus);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        btnAddPengurus = findViewById( R.id.fbaddPengurus );

        btnAddPengurus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent( ListViewDataPengurus.this, DataPengurus.class );
                startActivity( add );
            }
        } );

        if (ctx != null) {
            conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            allOrder.clear();
            getListPengurus();
        }else {
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_ListMember.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListPengurus();
                } else {
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_ListMember.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListPengurus();
                } else {
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_ListMember.setVisibility(View.GONE);
                }
            }
        });

        cd_NoData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListPengurus();
                } else {
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.VISIBLE );
                    linear_ListMember.setVisibility( View.GONE );
                }
            }
        } );
    }

    private void getListPengurus(){
        customProgress.showProgress( ListViewDataPengurus.this, "Loading", false );
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_list_pengurus");
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("debug", "Check Login Response: " + response);
                try {
                    viewDataPengurus(response);
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
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
                params.put("id_bank_sampah", getNama);
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

    protected void viewDataPengurus(String resp_content){
        String[] field_name = {"message", "id_bank_sampah", "nama_pengurus", "jabatan","name","data"};
        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            JSONArray cast = jsonObject.getJSONArray(field_name[5]);
            Log.e("tag_cast", String.valueOf(cast.length()));
            String message = jsonObject.getString( field_name[0] );

            if (message.equals( "True" )){
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String nama_pengurus = c.getString(field_name[2]);
                String jabatan = c.getString(field_name[3]);
                String id_pengurus = c.getString(field_name[4]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[2], nama_pengurus);
                map.put(field_name[3], jabatan);
                map.put(field_name[4], id_pengurus);

                allOrder.add(map);
            }

            Log.d("tag_allorder", allOrder.toString());

            cd_NoConnection.setVisibility( View.GONE );
            cd_NoData.setVisibility( View.GONE );
            linear_ListMember.setVisibility( View.VISIBLE );
            adapter = new LazyAdapter(ListViewDataPengurus.this, allOrder, 13);
            lvListMember.setAdapter(adapter);
            }
            else if (message.equals( "Not Found" ))
            {
                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_ListMember.setVisibility(View.GONE);
            }else{
                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_ListMember.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            cd_NoConnection.setVisibility(View.GONE);
            cd_NoData.setVisibility(View.VISIBLE);
            linear_ListMember.setVisibility(View.GONE);
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        timerDelayRemoveDialog();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    public void timerDelayRemoveDialog(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        }, 3000);
    }
}
