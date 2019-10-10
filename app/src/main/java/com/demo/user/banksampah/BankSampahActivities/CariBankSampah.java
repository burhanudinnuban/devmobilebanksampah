package com.demo.user.banksampah.BankSampahActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
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

public class CariBankSampah extends AppCompatActivity {
    private ShimmerFrameLayout mShimmerViewContainer;
    //Session Class
    protected PrefManager session;
    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected CustomProgress customProgress;
    ArrayList<HashMap<String, String>> arrayBankSampah = new ArrayList<>();
    protected LinearLayout parent_layout;
    protected ListView lvListBankSampah;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected RelativeLayout linear_ListBankSampah;
    protected ConnectivityManager conMgr;
    protected LazyAdapter adapter;
    protected AppCompatAutoCompleteTextView etSearch;
    protected Context ctx;
    protected CardView cd_NoData, cd_NoConnection;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_bank_sampah);
        ctx = CariBankSampah.this;
        session = new PrefManager( ctx );
        final HashMap<String, String> user = session.getUserDetails();
        String strBankSampah = user.get( PrefManager.KEY_NAMA );
        String strlatLong = user.get( PrefManager.KEY_LATLONG );
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        parent_layout = findViewById( R.id.parent );
        customProgress = CustomProgress.getInstance();
        lvListBankSampah = findViewById( R.id.listView_bankSampah );
        mSwipeRefreshLayout = findViewById( R.id.swipeToRefresh );
        linear_ListBankSampah = findViewById( R.id.linearLayout_ListBankSampah );
        cd_NoData = findViewById( R.id.cd_noData );
        cd_NoConnection = findViewById( R.id.cd_noInternet );

        etSearch = findViewById( R.id.etSearch );

        etSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String[] field_name = {"message", "id_bank_sampah", "no_telepon", "foto", "email", "alamat", "jam_operasional", "latlong"};

                ArrayList<HashMap<String, String>> allOrderSearch = new ArrayList<>();

                Log.e( "tag1", allOrderSearch.toString() );

                try {
                    for (int x = 0; x < arrayBankSampah.size(); x++) {
                        JSONObject c = new JSONObject( arrayBankSampah.get( x ) );
                        String id_bankSampahInduk = c.getString( field_name[1] );
                        String no_telepon = c.getString( field_name[2] );
                        String foto = c.getString( field_name[3] );
                        String email = c.getString( field_name[4] );
                        String alamat = c.getString( field_name[5] );
                        String jam_operasional = c.getString( field_name[6] );
                        String latlong = c.getString( field_name[7] );

                        HashMap<String, String> map = new HashMap<>();
                        if (id_bankSampahInduk.toLowerCase().contains( etSearch.getText().toString().toLowerCase() )) {
                            map.put( field_name[1], id_bankSampahInduk );
                            map.put( field_name[2], no_telepon );
                            map.put( field_name[3], foto );
                            map.put( field_name[4], email );
                            map.put( field_name[5], alamat );
                            map.put( field_name[6], jam_operasional );
                            map.put( field_name[7], latlong );
                            allOrderSearch.add( map );
                        } else if (alamat.toLowerCase().contains( etSearch.getText().toString().toLowerCase() )){
                            map.put( field_name[1], id_bankSampahInduk );
                            map.put( field_name[2], no_telepon );
                            map.put( field_name[3], foto );
                            map.put( field_name[4], email );
                            map.put( field_name[5], alamat );
                            map.put( field_name[6], jam_operasional );
                            map.put( field_name[7], latlong );
                            allOrderSearch.add( map );
                        }
                    }

                    Log.d( "tag1", allOrderSearch.toString() );

                    adapter = new LazyAdapter( (Activity) ctx, allOrderSearch, 17 );
                    lvListBankSampah.setAdapter( adapter );
                } catch (JSONException e) {
                    Log.d( "tag1", "error" );
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );

        if (ctx != null) {
            conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            arrayBankSampah.clear();
            getListBankSampah(strBankSampah, strlatLong);
        }else {
            Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility( View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_ListBankSampah.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    arrayBankSampah.clear();
                    getListBankSampah(strBankSampah, strlatLong);
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_ListBankSampah.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    arrayBankSampah.clear();
                    getListBankSampah(strBankSampah, strlatLong);
                } else {
                    Toast.makeText(ctx, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_ListBankSampah.setVisibility(View.GONE);
                }
            }
        });

        cd_NoData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    arrayBankSampah.clear();
                    getListBankSampah(strBankSampah, strlatLong);
                } else {
                    Toast.makeText( ctx, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.VISIBLE );
                    linear_ListBankSampah.setVisibility( View.GONE );
                }
            }
        } );
    }

    public void getListBankSampah(String strBankSampah, String lat_long){
        customProgress.showProgress( ctx,"",true );
        String base_url = apiData.get( "str_url_address" ) + ( ".get_nearby_bank" );
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "debug", "Check ListMember Response: " + response );
                try {
                    viewDataMember( response );
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

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
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "debug", "Volley Error: " + error.toString() );
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_bank_sampah", strBankSampah );
                params.put( "latlong", lat_long );
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

    protected void viewDataMember(String resp_content) {
        String[] field_name = {"message", "id_bank_sampah", "no_telepon", "foto", "email", "alamat", "jam_operasional", "latlong"};

        try {
            customProgress.hideProgress();
            JSONObject jsonObject = new JSONObject( resp_content );
            JSONArray cast = jsonObject.getJSONArray( field_name[0] );


            if (cast!=null) {
                Log.e( "tag_cast", String.valueOf( cast.length() ) );
                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject( i );

                    String id_bankSampahInduk = c.getString( field_name[1] );
                    String no_telepon = c.getString( field_name[2] );
                    String foto = c.getString( field_name[3] );
                    String email = c.getString( field_name[4] );
                    String alamat = c.getString( field_name[5] );
                    String jam_operasional = c.getString( field_name[6] );
                    String latlong = c.getString( field_name[7] );

                    HashMap<String, String> map = new HashMap<>();

                    map.put( field_name[1], id_bankSampahInduk );
                    map.put( field_name[2], no_telepon );
                    map.put( field_name[3], foto );
                    map.put( field_name[4], email );
                    map.put( field_name[5], alamat );
                    map.put( field_name[6], jam_operasional );
                    map.put( field_name[7], latlong );
                    arrayBankSampah.add( map );
                }
                cd_NoConnection.setVisibility( View.GONE );
                cd_NoData.setVisibility( View.GONE );
                linear_ListBankSampah.setVisibility( View.VISIBLE );
                Log.d( "tag_allorder", arrayBankSampah.toString() );

                adapter = new LazyAdapter( (Activity) ctx, arrayBankSampah, 17 );
                lvListBankSampah.setAdapter( adapter );
            }
            else {
                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_ListBankSampah.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            cd_NoConnection.setVisibility(View.GONE);
            cd_NoData.setVisibility(View.VISIBLE);
            linear_ListBankSampah.setVisibility(View.GONE);
            Log.e( "tag", " 2 :" + String.valueOf( e ) );
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.setVisibility( View.VISIBLE );
        mShimmerViewContainer.startShimmerAnimation();
        timerDelayRemoveDialog();
    }

    @Override
    public void onPause() {
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
