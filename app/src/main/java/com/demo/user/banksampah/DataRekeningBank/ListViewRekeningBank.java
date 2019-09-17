package com.demo.user.banksampah.DataRekeningBank;

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
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ListViewRekeningBank extends AppCompatActivity {

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
    protected ConnectivityManager conMgr;
    Context ctx;
    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;

    protected FloatingActionButton addRekBank;
    protected ListView lvRekBank;
    protected RelativeLayout llListRelative;

    //Get Data From Login Process
    protected static String getNama = "";
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_view_rekening_bank );

        ctx = ListViewRekeningBank.this;
        session = new PrefManager( this );
        final HashMap<String, String> user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        mShimmerViewContainer = findViewById( R.id.shimmer_view_container );
        cd_NoData = findViewById( R.id.cd_noData );
        cd_NoConnection = findViewById( R.id.cd_noInternet );
        parent_layout = findViewById( R.id.parent );
        customProgress = CustomProgress.getInstance();
        addRekBank = findViewById( R.id.fbaddRekBank );
        lvRekBank = findViewById( R.id.listViewRekBank );
        llListRelative = findViewById( R.id.linearLayout_ListMember );
        lvListMember = findViewById( R.id.listViewPengurus );
        mSwipeRefreshLayout = findViewById( R.id.swipeToRefresh );

        if (ctx != null) {
            conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            allOrder.clear();
            getListRekening();
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
                    getListRekening();
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
                    getListRekening();
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

        addRekBank.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddRekBank = new Intent( ListViewRekeningBank.this, DataRekeningBank.class );
                startActivity( AddRekBank );
            }
        } );
    }

    private void getListRekening() {
        String base_url = apiData.get( "str_url_address" ) + (".list_rekening");
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "debug", "Check Login Response: " + response );
                try {
                    viewDataPengurus( response );
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility( View.GONE );
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make( parent_layout, getString( R.string.MSG_CODE_409 ) + "1: " + getString( R.string.MSG_CHECK_DATA ), Snackbar.LENGTH_SHORT );
                    snackbar.show();
                    Log.d( "debug", "Error Check Login Response: " + t.toString() );
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "debug", "Volley Error: " + error.toString() );
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_bank_sampah", getNama );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
    }

    protected void viewDataPengurus(String resp_content) {
        String[] field_name = {"message", "no_rekening", "nama_bank", "cabang", "pemilik"};
        try {
            JSONObject jsonObject = new JSONObject( resp_content );

            //if (!message.equalsIgnoreCase("Invalid")) {

            JSONArray cast = jsonObject.getJSONArray( field_name[0] );
            Log.e( "tag_cast", String.valueOf( cast.length() ) );

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject( i );
                String no_rekening = c.getString( field_name[1] );
                String nama_bank = c.getString( field_name[2] );
                String cabang = c.getString( field_name[3] );
                String pemilik = c.getString( field_name[4] );

                HashMap<String, String> map = new HashMap<>();
                map.put( field_name[1], no_rekening );
                map.put( field_name[2], nama_bank );
                map.put( field_name[3], cabang );
                map.put( field_name[4], pemilik );

                allOrder.add( map );
            }

            Log.d( "tag_allorder", allOrder.toString() );

            adapter = new LazyAdapter( ListViewRekeningBank.this, allOrder, 16 );
            lvRekBank.setAdapter( adapter );

        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Toasty.error( getApplicationContext(), getString( R.string.MSG_CODE_409 ) + " 2: " + getString( R.string.MSG_CHECK_DATA ), Toast.LENGTH_LONG ).show();
            }
            Log.e( "tag", " 2 :" + String.valueOf( e ) );
            e.printStackTrace();
            /*include_FormOrderList.setVisibility(View.GONE);
            linear_NoData.setVisibility(View.VISIBLE);
            if(getContext()!=null) {
                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
            }*/
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
