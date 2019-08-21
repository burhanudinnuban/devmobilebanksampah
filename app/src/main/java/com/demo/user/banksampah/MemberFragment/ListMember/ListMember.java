package com.demo.user.banksampah.MemberFragment.ListMember;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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

public class ListMember extends Fragment implements SearchView.OnQueryTextListener {

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
    protected RelativeLayout linear_ListMember;
    protected ConnectivityManager conMgr;
    protected Button btDetailListMember;

    Context ctx;
    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;

    protected ImageView imgSearch;
    private AppCompatAutoCompleteTextView etSearch;
    public static ListMember newInstance() {
        return new ListMember();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list_member, container, false);
        session = new PrefManager(getContext());
        final HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_NAMA);

        this.ctx = ctx;
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        parent_layout = rootView.findViewById(R.id.parent);
        customProgress = CustomProgress.getInstance();
        lvListMember = rootView.findViewById(R.id.listView_Member);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        linear_ListMember = rootView.findViewById(R.id.linearLayout_ListMember);
        cd_NoData = rootView.findViewById(R.id.cd_noData);
        cd_NoConnection = rootView.findViewById(R.id.cd_noInternet);
        btDetailListMember = rootView.findViewById(R.id.btnDetailListMember);

        etSearch = rootView.findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String[] field_name = {"message", "id_member", "nama_member", "point","no_telepon","email","foto","alamat"};

                    ArrayList<HashMap<String, String>> allOrderSearch = new ArrayList<>();

                    Log.e("tag1", allOrderSearch.toString());

                try {
                    for (int x = 0; x < allOrder.size(); x++) {
                        JSONObject c = new JSONObject(allOrder.get(x));

                        String id_member = c.getString(field_name[1]);
                        String nama_member = c.getString(field_name[2]);
                        String point = c.getString(field_name[3]);
                        String no_telepon = c.getString(field_name[4]);
                        String email = c.getString(field_name[5]);
                        String foto = c.getString(field_name[6]);
                        String alamat = c.getString(field_name[7]);

                        if(nama_member.toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {

                            HashMap<String, String> map = new HashMap<>();

                            map.put(field_name[1], id_member);
                            map.put(field_name[2], nama_member);
                            map.put(field_name[3], point);
                            map.put(field_name[4], no_telepon);
                            map.put(field_name[5], email);
                            map.put(field_name[6], foto);
                            map.put(field_name[7], alamat);
                            allOrderSearch.add(map);
                        }
                    }

                    Log.d("tag1", allOrderSearch.toString());

                    adapter = new LazyAdapter(getActivity(), allOrderSearch, 9);
                    lvListMember.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.d("tag1", "error");
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*//Intent Ke Detail Member Activity
        if (getActivity() != null)
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected())
        {
            //Jalanin API
            getListMember(strIDUser);

        } else {
            Snackbar snackbar = Snackbar
                    .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
            snackbar.show();
        }*/

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() != null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        //Jalanin API
                        allOrder.clear();
                        getListMember(strIDUser);
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

    private void getListMember(final String strIDUser){
//        customProgress.showProgress(getContext(), "", false);
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_list_member");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                customProgress.hideProgress();
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
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
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
        String[] field_name = {"message", "id_member", "nama_member", "point","no_telepon","email","foto","alamat"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);

            //if (!message.equalsIgnoreCase("Invalid")) {

                JSONArray cast = jsonObject.getJSONArray(field_name[0]);
                Log.e("tag_cast", String.valueOf(cast.length()));

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);

                    String id_member= c.getString(field_name[1]);
                    String nama_member = c.getString(field_name[2]);
                    String point = c.getString(field_name[3]);
                    String no_telepon = c.getString(field_name[4]);
                    String email = c.getString(field_name[5]);
                    String foto = c.getString(field_name[6]);
                    String alamat = c.getString(field_name[7]);


                    HashMap<String, String> map = new HashMap<>();

                    map.put(field_name[1], id_member);
                    map.put(field_name[2], nama_member);
                    map.put(field_name[3], point);
                    map.put(field_name[4], no_telepon);
                    map.put(field_name[5], email);
                    map.put(field_name[6], foto);
                    map.put(field_name[7], alamat);
                    allOrder.add(map);
                }

                Log.d("tag_allorder", allOrder.toString());

                adapter = new LazyAdapter(getActivity(), allOrder, 9);
                lvListMember.setAdapter(adapter);

        } catch (JSONException e) {
            if (getContext() != null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        String text = newText;
        return false;
    }

    private void initView() {
        registerForContextMenu(lvListMember);
    }

//    private void loadData() {
//        PrefManager prefManager = new PrefManager(getContext());
//        List<> pre
//        if (strIDUser != null) {
//            lvListMember.setAdapter(new LazyAdapter(getContext(), ));
//        }
//    }
}
