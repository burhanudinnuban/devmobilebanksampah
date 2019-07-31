package com.demo.user.banksampah.KantongFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ListOrderFragment extends Fragment {

    //Session Class
    protected PrefManager session;
    protected String strIDUser;

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

    protected View include_FormOrderList;
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
            getListOrder(strIDUser);
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
                        getListOrder(strIDUser);
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
                    getListOrder(strIDUser);
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
        /*if (getActivity() != null) {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                getListOrder(strIDUser);
            } else {
                Toast.makeText(getContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        Log.e("tag", "OnResume");*/
        super.onResume();
    }

    private void getListOrder(final String strIDUser) {
        /*dialog.setMessage("Mengambil Data, Harap Menunggu");
        dialog.show();*/
        customProgress.showProgress(getContext(), "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        list_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_order";
        params.put("id_user", strIDUser);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(list_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displayOrder(resp_content);
                } catch (Throwable t) {
                    if (getContext() != null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
//                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayOrder(String resp_content) {
        String[] field_name = {"message", "berat_total", "total_point", "id_user", "name", "order_status", "image", "alamat", "latlong",
                "nama_picker", "no_kendaraan", "foto_picker", "tipe_kendaraan"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            String message = jsonObject.getString(field_name[0]);

            if (!message.equalsIgnoreCase("Invalid")) {
                ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
                JSONArray cast = jsonObject.getJSONArray(field_name[0]);
                Log.e("tag", String.valueOf(cast.length()));

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);

                    String beratTotal_Result = c.getString(field_name[1]);
                    String totalPoint_Result = c.getString(field_name[2]);
                    String idUser_Result = c.getString(field_name[3]);
                    String orderName_Result = c.getString(field_name[4]);
                    String status_Result = c.getString(field_name[5]);
                    String image_Result = c.getString(field_name[6]);
                    String alamat_Result = c.getString(field_name[7]);
                    String latlong_Result = c.getString(field_name[8]);

                    //Detail Picker
                    String namaPicker_Result = c.getString(field_name[9]);
                    String noKendaraan_Result = c.getString(field_name[10]);
                    String fotoPicker_Result = c.getString(field_name[11]);
                    String tipeKendaraan_Result = c.getString(field_name[12]);

                    Log.e("tag_order", orderName_Result);

                    HashMap<String, String> map = new HashMap<>();

                    map.put(field_name[1], beratTotal_Result);
                    map.put(field_name[2], totalPoint_Result);
                    map.put(field_name[3], idUser_Result);
                    map.put(field_name[4], orderName_Result);
                    map.put(field_name[5], status_Result);
                    map.put(field_name[6], image_Result);
                    map.put(field_name[7], alamat_Result);

                    //Detail Picker
                    map.put(field_name[8], latlong_Result);
                    map.put(field_name[9], namaPicker_Result);
                    map.put(field_name[10], noKendaraan_Result);
                    map.put(field_name[11], fotoPicker_Result);
                    map.put(field_name[12], tipeKendaraan_Result);

                    allOrder.add(map);
                }

                Log.d("tag", allOrder.toString());

                adapter = new LazyAdapter(getActivity(), allOrder, 5);
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
