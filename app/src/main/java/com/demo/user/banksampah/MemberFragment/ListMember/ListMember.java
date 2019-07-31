package com.demo.user.banksampah.MemberFragment.ListMember;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

public class ListMember extends Fragment {

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected String strIDUser;


    protected CustomProgress customProgress;

    protected LinearLayout parent_layout;
    protected ListView lvListMember;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout linear_ListMember;
    protected ConnectivityManager conMgr;

    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;

    String strNamaMember, strIdMember, strPointMember;

    public static ListMember newInstance() {
        return new ListMember();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_member, container, false);
        session = new PrefManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        parent_layout = rootView.findViewById(R.id.parent);
        customProgress = CustomProgress.getInstance();
        lvListMember = rootView.findViewById(R.id.listView_Member);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        linear_ListMember = rootView.findViewById(R.id.linearLayout_ListMember);
        cd_NoData = rootView.findViewById(R.id.cd_noData);
        cd_NoConnection = rootView.findViewById(R.id.cd_noInternet);

        if (getActivity() != null)
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getListMember(strIDUser);
            //Jalanin API
        } else {
            Snackbar snackbar = Snackbar
                    .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() != null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        //Jalanin API
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
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getListMember(strIDUser);
        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_ListMember.setVisibility(View.GONE);

        }
        return rootView;
    }

    protected void getListMember(String strIDUser) {
        customProgress.showProgress(getContext(), "", false);
        final String[] field_name = {"id_member","nama_member","point"};

        String base_url = apiData.get("str_url_address") + apiData.get(".list_member_bank_sampah");

        StringRequest strReq = new StringRequest(Request.Method.GET, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Register Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strIdMember);
                params.put(field_name[1], strNamaMember);
                params.put(field_name[2], strPointMember);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }


}
