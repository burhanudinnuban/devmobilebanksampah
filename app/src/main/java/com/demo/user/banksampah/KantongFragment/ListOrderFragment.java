package com.demo.user.banksampah.KantongFragment;

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

public class ListOrderFragment extends Fragment {

    private final String TAG = ListOrderFragment.class.getSimpleName();
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

    public static ListOrderFragment newInstance() {
        return new ListOrderFragment();
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

        Toast.makeText(getContext(), strNamaUser, Toast.LENGTH_SHORT).show();

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
        String[] field_name = {"message", "nama_bank_sampah", "role_user", "berat_total", "creation", "latlong", "harga_per_kilo",
                "no_telepon", "alamat", "id_picker", "latlong_picker", "berat_item", "status", "foto", "id_assignment", "point_total",
                "jenis_item", "nama", "id_user", "name", "kecamatan", "created_date", "status_order", "tanggal_penjemputan"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            if (!jsonObject.toString().equals("{}")) {
                ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
                JSONArray cast = jsonObject.getJSONArray(field_name[0]);
                Log.e("tag", String.valueOf(cast.length()));

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);


                    String namabanksampahresult = c.getString(field_name[1]);
                    String role_user = c.getString(field_name[2]);
                    String berat_totalresult = c.getString(field_name[3]);
                    String creation = c.getString(field_name[4]);
                    String latlong = c.getString(field_name[5]);
                    String harga_per_kilo = c.getString(field_name[6]);
                    String no_telepon = c.getString(field_name[7]);
                    String alamat_result = c.getString(field_name[8]);
                    String id_picker = c.getString(field_name[9]);
                    String latlong_picker = c.getString(field_name[10]);
                    String berat_item = c.getString(field_name[11]);
                    String status_result = c.getString(field_name[12]);
                    String fotopiker = c.getString(field_name[13]);
                    String id_assignment = c.getString(field_name[14]);
                    String pointtotal = c.getString(field_name[15]);
                    String jenis_item = c.getString(field_name[16]);
                    String nama = c.getString(field_name[17]);
                    String id_user = c.getString(field_name[18]);
                    String namee = c.getString(field_name[19]);
                    String kecamatan = c.getString(field_name[20]);
                    String created_date = c.getString(field_name[21]);
                    String status_order = c.getString(field_name[22]);
                    String tanggal_penjemputan = c.getString(field_name[23]);


//                    //Detail Picker
//                    String namaPicker_Result = c.getString(field_name[9]);
//                    String noKendaraan_Result = c.getString(field_name[10]);
//                    String fotoPicker_Result = c.getString(field_name[11]);
//                    String tipeKendaraan_Result = c.getString(field_name[12]);



                    HashMap<String, String> map = new HashMap<>();

                    map.put(field_name[1], namabanksampahresult);
                    map.put(field_name[2], role_user);
                    map.put(field_name[3], berat_totalresult);
                    map.put(field_name[4], creation);
                    map.put(field_name[5], latlong);
                    map.put(field_name[6], harga_per_kilo);
                    map.put(field_name[7], no_telepon);
                    map.put(field_name[8], alamat_result);
                    map.put(field_name[9], id_picker);
                    map.put(field_name[10], latlong_picker);
                    map.put(field_name[11], berat_item);
                    map.put(field_name[12], status_result);
                    map.put(field_name[13], fotopiker);
                    map.put(field_name[14], id_assignment);
                    map.put(field_name[15], pointtotal);
                    map.put(field_name[16], jenis_item);
                    map.put(field_name[17], nama);
                    map.put(field_name[18], id_user);
                    map.put(field_name[19], namee);
                    map.put(field_name[20], kecamatan);
                    map.put(field_name[21], created_date);
                    map.put(field_name[22], status_order);
                    map.put(field_name[23], tanggal_penjemputan);


                    allOrder.add(map);
                }

                Log.d("tag", allOrder.toString());

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
