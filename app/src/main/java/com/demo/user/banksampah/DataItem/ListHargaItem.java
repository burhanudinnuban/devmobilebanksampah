package com.demo.user.banksampah.DataItem;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.ProfileBankSampah.ProfileActivity;
import com.demo.user.banksampah.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ListHargaItem extends AppCompatActivity {

    private TextView tvJenisItem, tvHargaItem;
    private LinearLayout llParent, llList;
    protected RelativeLayout llListRelative;
    private ListView lvHargaItem;

    //if (!message.equalsIgnoreCase("Invalid")) {
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

    //Session Class
    protected PrefManager session;

    protected View rootView;

    /*API process and dialog*/
    private RestProcess rest_class;
    private HashMap<String, String> apiData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LazyAdapter adapter;
    protected CardView cd_NoData, cd_NoConnection;

    protected ConnectivityManager conMgr;
    private ShimmerFrameLayout mShimmerViewContainer;
    protected String strIDUser;

    protected FloatingActionButton fbaddItem;
    protected Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_harga_item);

        ctx = ListHargaItem.this;
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_NAMA);
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        tvHargaItem =findViewById(R.id.tvHargaItem);
        tvJenisItem = findViewById(R.id.tvJenisItem);
        llParent = findViewById(R.id.parentItem);
        lvHargaItem = findViewById(R.id.lvHargaItem);
        fbaddItem = findViewById( R.id.fbaddItem );
        llList = findViewById( R.id.llList );
        llListRelative = findViewById( R.id.linearLayout_ListItem );

        fbaddItem.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent additem = new Intent( ListHargaItem.this, TambahkanDaftar.class );
                startActivity( additem );
            }
        } );

        if (ctx != null) {
            conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            allOrder.clear();
            getListItem( strIDUser );
            cd_NoConnection.setVisibility( View.GONE );
            cd_NoData.setVisibility( View.GONE );
            llListRelative.setVisibility( View.VISIBLE );
        }else {
            Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            llListRelative.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListItem( strIDUser );
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.GONE );
                    llListRelative.setVisibility( View.VISIBLE );
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    llListRelative.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListItem( strIDUser );
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.GONE );
                    llListRelative.setVisibility( View.VISIBLE );
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    llListRelative.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getListItem(final String strIDUser){
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_list_daftar");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("debug", "Check Login Response: " + response);
                try {
                    viewDataMember(response);
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(llParent, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("debug", "Error Check Login Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                timerDelayRemoveDialog();
                Snackbar snackbar = Snackbar
                        .make(llParent, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
                Log.d("debug", "Volley Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_bank_sampah", strIDUser);
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
        String[] field_name = {"message", "id_item", "id_bank_sampah", "jenis_item","harga_per_kilo"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            Log.e("tag", String.valueOf(cast.length()));

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String id_item= c.getString(field_name[1]);
                String id_bank_sampah = c.getString(field_name[2]);
                String jenis_item = c.getString(field_name[3]);
                String harga_per_kilo = c.getString(field_name[4]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], id_item);
                map.put(field_name[2], id_bank_sampah);
                map.put(field_name[3], jenis_item);
                map.put(field_name[4], harga_per_kilo);

                allOrder.add(map);
            }

            Log.d("tag", allOrder.toString());

            adapter = new LazyAdapter(ListHargaItem.this, allOrder, 12);
            lvHargaItem.setAdapter(adapter);


        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(ListHargaItem.this, ProfileActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        llList.setVisibility( View.VISIBLE );
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
