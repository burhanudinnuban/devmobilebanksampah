package com.demo.user.banksampah.MemberFragment.RequestMember;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestMember extends Fragment {

    //Session Class
    protected PrefManager session;
    private ShimmerFrameLayout mShimmerViewContainer;
    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    private CustomProgress customProgress;

    //if (!message.equalsIgnoreCase("Invalid")) {
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

    protected LinearLayout parent_layout;
    protected ListView lvListRequestMember;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout linear_ListRequestMember;
    protected ConnectivityManager conMgr;
    protected String strIDUser;
    protected ListView listView;
    protected View rootView;
    protected LazyAdapter adapter;
    protected Context ctx;

    protected CardView cd_NoData, cd_NoConnection;

    protected AutoCompleteTextView editsearch;

    public static RequestMember newInstance() {
        return new RequestMember();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate( R.layout.fragment_request_member, container, false );
        mShimmerViewContainer = rootView.findViewById( R.id.shimmer_view_container );
        session = new PrefManager( getContext() );
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get( PrefManager.KEY_NAMA );
        rest_class = new RestProcess();
        listView = rootView.findViewById( R.id.listView_RequestMember );
        apiData = rest_class.apiErecycle();

        ctx = getContext();
        parent_layout = rootView.findViewById( R.id.parent );
        customProgress = CustomProgress.getInstance();
        lvListRequestMember = rootView.findViewById( R.id.listView_RequestMember );
        mSwipeRefreshLayout = rootView.findViewById( R.id.swipeToRefresh );
        linear_ListRequestMember = rootView.findViewById( R.id.linearLayout_ListRequestMember );
        cd_NoData = rootView.findViewById( R.id.cd_noData );
        cd_NoConnection = rootView.findViewById( R.id.cd_noInternet );
        editsearch = rootView.findViewById( R.id.etSearchReq );

        editsearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String[] field_name = {"message", "id_member", "alamat", "id", "nama_member", "creation", "foto", "id_bank_sampah"};
                /*try {*/

                ArrayList<HashMap<String, String>> allOrderSearch = new ArrayList<>();
                Log.e( "tag1", allOrderSearch.toString() );

                try {
                    for (int x = 0; x < allOrder.size(); x++) {
                        JSONObject c = new JSONObject( allOrder.get( x ) );
                        String idReqMember = c.getString( field_name[1] );
                        String Alamat = c.getString( field_name[2] );
                        String idBankSampah = c.getString( field_name[3] );
                        String namaReqMember = c.getString( field_name[4] );
                        String tanggalReqMember = c.getString( field_name[5] );
                        String foto = c.getString( field_name[6] );
                        String id_bankSampah = c.getString( field_name[7] );

                        if (namaReqMember.toLowerCase().contains( editsearch.getText().toString().toLowerCase() )) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put( field_name[1], idReqMember );
                            map.put( field_name[2], Alamat );
                            map.put( field_name[3], idBankSampah );
                            map.put( field_name[4], namaReqMember );
                            map.put( field_name[5], tanggalReqMember );
                            map.put( field_name[6], foto );
                            map.put( field_name[7], id_bankSampah );
                            allOrderSearch.add( map );
                        } else if (idReqMember.toLowerCase().contains( editsearch.getText().toString().toLowerCase() )) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put( field_name[1], idReqMember );
                            map.put( field_name[2], Alamat );
                            map.put( field_name[3], idBankSampah );
                            map.put( field_name[4], namaReqMember );
                            map.put( field_name[5], tanggalReqMember );
                            map.put( field_name[6], foto );
                            map.put( field_name[7], id_bankSampah );
                            allOrderSearch.add( map );
                        }
                    }

                    Log.d( "tag1", allOrderSearch.toString() );

                    adapter = new LazyAdapter( getActivity(), allOrderSearch, 10 );
                    lvListRequestMember.setAdapter( adapter );
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
            conMgr = (ConnectivityManager) ctx.getSystemService( Context.CONNECTIVITY_SERVICE );
        }
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            allOrder.clear();
            getListReqMember( strIDUser );
        } else {
            Toast.makeText( ctx, "No Internet Connection", Toast.LENGTH_SHORT ).show();
            cd_NoConnection.setVisibility( View.VISIBLE );
            cd_NoData.setVisibility( View.GONE );
            linear_ListRequestMember.setVisibility( View.GONE );
        }

        mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListReqMember( strIDUser );
                } else {
                    Toast.makeText( ctx, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                    cd_NoConnection.setVisibility( View.VISIBLE );
                    cd_NoData.setVisibility( View.GONE );
                    linear_ListRequestMember.setVisibility( View.GONE );
                }
                mSwipeRefreshLayout.setRefreshing( false );
            }
        } );

        cd_NoConnection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListReqMember( strIDUser );
                    timerDelayProgressDialog();
                } else {
                    Toast.makeText( ctx, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                    cd_NoConnection.setVisibility( View.VISIBLE );
                    cd_NoData.setVisibility( View.GONE );
                    linear_ListRequestMember.setVisibility( View.GONE );
                }
            }
        } );

        cd_NoData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    allOrder.clear();
                    getListReqMember( strIDUser );
                    timerDelayProgressDialog();
                } else {
                    Toast.makeText( ctx, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.VISIBLE );
                    linear_ListRequestMember.setVisibility( View.GONE );
                }
            }
        } );

        return rootView;
    }

    private void getListReqMember(final String strIDUser) {
        String base_url = apiData.get( "str_url_address" ) + apiData.get( "str_api_list_request_member" );
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d( "debug", "Check List Request Member Response: " + response );
                try {
                    viewDataMember( response );
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
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
                Log.d( "debug", "Volley Error: " + error.toString() );
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_bank_sampah", strIDUser );
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
        String[] field_name = {"message", "id_member", "alamat", "id", "nama_member", "creation", "foto", "id_bank_sampah","request"};

        try {
            JSONObject jsonObject = new JSONObject( resp_content );
            JSONArray cast = jsonObject.getJSONArray( field_name[8] );
            String message = jsonObject.getString( field_name[0] );
                if (message.equals( "True" )){
                    for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject( i );

                    String idReqMember = c.getString( field_name[1] );
                    String Alamat = c.getString( field_name[2] );
                    String idBankSampah = c.getString( field_name[3] );
                    String namaReqMember = c.getString( field_name[4] );
                    String tanggalReqMember = c.getString( field_name[5] );
                    String foto = c.getString( field_name[6] );
                    String id_bankSampah = c.getString( field_name[7] );

                    Log.d( "DEBUG", "viewDataMember: " + Alamat );
                    HashMap<String, String> map = new HashMap<>();

                    map.put( field_name[1], idReqMember );
                    map.put( field_name[2], Alamat );
                    map.put( field_name[3], idBankSampah );
                    map.put( field_name[4], namaReqMember );
                    map.put( field_name[5], tanggalReqMember );
                    map.put( field_name[6], foto );
                    map.put( field_name[7], id_bankSampah );
                    allOrder.add( map );
                }
                cd_NoConnection.setVisibility( View.GONE );
                cd_NoData.setVisibility( View.GONE );
                linear_ListRequestMember.setVisibility( View.VISIBLE );
                Log.d( "tag", allOrder.toString() );
                adapter = new LazyAdapter( getActivity(), allOrder, 10 );
                listView.setAdapter( adapter );}
                else if (message.equals( "False" )){
                    cd_NoConnection.setVisibility( View.GONE );
                    cd_NoData.setVisibility( View.VISIBLE );
                    linear_ListRequestMember.setVisibility( View.GONE );
                }
        } catch (JSONException e) {
            cd_NoConnection.setVisibility( View.GONE );
            cd_NoData.setVisibility( View.VISIBLE );
            linear_ListRequestMember.setVisibility( View.GONE );
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

    public void timerDelayRemoveDialog() {
        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility( View.GONE );
            }
        }, 3000 );
    }

    public void timerDelayProgressDialog(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                customProgress.showProgress( getContext(), "Loading", false );
            }
        }, 2000);
        customProgress.hideProgress();
    }
}
