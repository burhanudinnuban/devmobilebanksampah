package com.demo.user.banksampah.MainFragment;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ReceiveFragment extends Fragment {
    private final String TAG = ReceiveFragment.class.getSimpleName();
    protected LinearLayout parent_layout;

    //Session Class
    protected PrefManager session;
    protected String strIDUser, strNamaUser;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    private CustomProgress customProgress;

    protected ListView lvListOrder;
    protected ListView lvgetDetailList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout linear_listOrder;
    protected ConnectivityManager conMgr;

    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;

    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_order, container, false);

        session = new PrefManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);
        strNamaUser = user.get(PrefManager.KEY_NAMA);

        parent_layout = rootView.findViewById(R.id.parent);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        customProgress = CustomProgress.getInstance();

        lvListOrder = rootView.findViewById(R.id.listView_Order);
        lvgetDetailList = rootView.findViewById(R.id.listView_OrderDetails);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linear_listOrder = rootView.findViewById(R.id.linearLayout_ListOrder);

        cd_NoData = rootView.findViewById(R.id.cd_noData);
        cd_NoConnection = rootView.findViewById(R.id.cd_noInternet);
        //include_FormOrderList = rootView.findViewById(R.id.include_FormOrder);

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getListOrder(strNamaUser);
        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            linear_listOrder.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() != null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        getListOrder(strNamaUser);
                    } else {
                        Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        cd_NoConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getListOrder(strNamaUser);
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    linear_listOrder.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }

//        Button btnScan = rootView.findViewById(R.id.btnScan);
//        btnScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), QrCodeScannerActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        return rootView;


    @Override
    public void onResume() {
        if (getActivity() != null) {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                getListOrder(strNamaUser);
            } else {
                Toast.makeText(getContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        Log.e("tag", "OnResume");
        super.onResume();
    }

    private void getListOrder(final String strIDUser) {
        String base_url = apiData.get("str_url_address") + (".get_member_order");
        final String[] field_name = {"id_bank_sampah"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Get List Order Response: " + response);
                try {
                    displayOrder(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get List Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strIDUser);
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

    private void displayOrder(String resp_content) {
        String[] field_name = {"message", "nama_bank_sampah", "berat_total", "nama", "status_order", "point_total", "name", "creation"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content.toString());
            if (!jsonObject.toString().equals("{}")) {
                ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
                Log.e("tag status", String.valueOf(jsonObject));

                JSONArray cast = jsonObject.getJSONArray("message");
                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);

                    String namabanksampahresult = c.getString(field_name[1]);
                    String berat_total = c.getString(field_name[2]);
                    String nama = c.getString(field_name[3]);
                    String status_order = c.getString(field_name[4]);
                    String point_total = c.getString(field_name[5]);
                    String name = c.getString(field_name[6]);
                    String creation = c.getString(field_name[7]);



                    HashMap<String, String> map = new HashMap<>();
                    map.put(field_name[1], namabanksampahresult);
                    map.put(field_name[2], berat_total);
                    map.put(field_name[3], nama);
                    map.put(field_name[4], status_order);
                    map.put(field_name[5], point_total);
                    map.put(field_name[6], name);
                    map.put(field_name[7], creation);

                    allOrder.add(map);
                }

//


//                    //Detail Picker
//                    String namaPicker_Result = c.getString(field_name[9]);
//                    String noKendaraan_Result = c.getString(field_name[10]);
//                    String fotoPicker_Result = c.getString(field_name[11]);
//                    String tipeKendaraan_Result = c.getString(field_name[12]);

                adapter = new LazyAdapter(getActivity(), allOrder, 11);
                lvListOrder.setAdapter(adapter);

                cd_NoConnection.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.GONE);
                linear_listOrder.setVisibility(View.VISIBLE);

            } else {
                //include_FormOrderList.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_listOrder.setVisibility(View.GONE);
                cd_NoConnection.setVisibility(View.GONE);
                /*if (getContext() != null) {
                    Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
                }*/
            }

        } catch (JSONException e) {
            if (getContext() != null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2 :" + e);
            e.printStackTrace();
            /*include_FormOrderList.setVisibility(View.GONE);
            linear_NoData.setVisibility(View.VISIBLE);
            if(getContext()!=null) {
                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
            }*/
        }
    }
}
