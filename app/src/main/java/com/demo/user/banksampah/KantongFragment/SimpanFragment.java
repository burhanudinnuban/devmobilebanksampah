package com.demo.user.banksampah.KantongFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Activities.SummaryOrderActivity;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SimpanFragment extends Fragment {

    protected String getIdUser_Result = null, getNameOrder_Result = null, getMessage_Result = null;
    protected String getTotalPoints;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /*API process and dialog*/
    private RestProcess rest_class;
    protected HashMap<String,String> apiData;

    private CustomProgress customProgress;

    // <------- List Data Sampah ------>
    //View List Incoming Order
    private CardView cd_WishlistData, cd_NoData, cd_NoConnection;
    private ListView lvIncomingOrder_Data;
    //Image Trash in ListView
    protected ImageView imgDeleteIncomingOrder_Data;
    // <------- List Data Sampah ------>

    protected Button btnSummary, btnCancelOrder;
    private TextView tvTotalPoints;

    protected ConnectivityManager conMgr;

    //Cek Data
    ArrayList<HashMap<String, String>> arrCheckOrder = new ArrayList<>();

    protected LazyAdapter adapter3;
    protected View rootView;

    private Dialog myDialog;

    DecimalFormat decimalFormat, decimalFormat_Point;

    public static void newInstance() {
        new SimpanFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_simpan, container, false);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        decimalFormat = new DecimalFormat("0.##");
        decimalFormat_Point = new DecimalFormat(",###.##");

        if (getContext() != null) {
            myDialog = new Dialog(getContext());
        }

        cd_WishlistData = rootView.findViewById(R.id.cardView_Wishlist);
        cd_NoData = rootView.findViewById(R.id.cardView_NoData);
        cd_NoConnection = rootView.findViewById(R.id.cardView_NoInternet);

        lvIncomingOrder_Data = rootView.findViewById(R.id.listView_IncomingOrder);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        imgDeleteIncomingOrder_Data = rootView.findViewById(R.id.imgDelete_IncomingOrder);

        btnSummary = rootView.findViewById(R.id.btnSummary);
        btnCancelOrder = rootView.findViewById(R.id.btnCancel_IncomingOrder);

        tvTotalPoints = rootView.findViewById(R.id.tvGrandPoints_IncomingOrder);

        if(getActivity()!=null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        /*if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            CheckOrder();
        } else {
            Toast.makeText(getContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }*/

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(getNameOrder_Result!=null)
                    showPopupDeleteIncomingOrder();
            }
        });

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getNameOrder_Result!=null && getTotalPoints!=null) {
                        Intent a = new Intent(getContext(), SummaryOrderActivity.class);
                        a.putExtra("inc_order", getNameOrder_Result);
                        a.putExtra("total_points", decimalFormat_Point.format(Double.valueOf(getTotalPoints)));
                        startActivity(a);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getActivity()!=null) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        CheckOrder();
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
                    CheckOrder();
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    cd_NoConnection.setVisibility(View.VISIBLE);
                    cd_NoData.setVisibility(View.GONE);
                    cd_WishlistData.setVisibility(View.GONE);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        Log.e("tag", "onResume");
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            CheckOrder();
        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            cd_NoConnection.setVisibility(View.VISIBLE);
            cd_NoData.setVisibility(View.GONE);
            cd_WishlistData.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void CheckOrder() {
        /*dialog.setMessage("Mengambil Data, Harap Menunggu...");
        dialog.show();*/

        //customProgress.showProgress(getContext(), "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.check_incoming_order";
        params.put("id_user", MainActivity.strID);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    viewFromDB(resp_content);
                } catch (Throwable t) {
                    if (getContext()!= null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //customProgress.hideProgress();
                if (getContext()!= null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });
    }

    private void viewFromDB(String resp_content) {
        String[] field_name = {"id_user", "name", "message"};
        try {
            arrCheckOrder = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);

            getMessage_Result = jsonPost.getString(field_name[2]);

            Log.e("tag", getIdUser_Result + " " + getNameOrder_Result);

            //Model Benarnya...
            if (getMessage_Result.equals("False")) {
                cd_NoData.setVisibility(View.VISIBLE);
                cd_NoConnection.setVisibility(View.GONE);
                cd_WishlistData.setVisibility(View.GONE);

                /*if (getContext() != null) {
                    Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
                }*/

            } else if (getMessage_Result.equals("True")) {
                getIdUser_Result = jsonPost.getString(field_name[0]);
                getNameOrder_Result = jsonPost.getString(field_name[1]);

                cd_NoData.setVisibility(View.GONE);
                cd_NoConnection.setVisibility(View.GONE);
                cd_WishlistData.setVisibility(View.VISIBLE);

                getIncomingOrder();
            }

        } catch (JSONException e) {
            if (getContext()!= null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2: " + String.valueOf(e));
        }
    }

    private void getIncomingOrder() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String incoming_order_url;

        incoming_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.get_incoming_order";
        params.put("inc_order", getNameOrder_Result);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(incoming_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displayKantongSampah(resp_content);
                } catch (Throwable t) {
                    if (getContext()!= null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 2: " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
               // customProgress.hideProgress();
                if (getContext()!= null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 3 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 3: " + String.valueOf(error));
            }
        });
    }

    private void displayKantongSampah(String resp_content) {
        String[] field_name = {"name", "parent", "point", "creation", "tipe_sampah",
                "jenis_sampah", "jumlah_kg", "created_date"};

        try {
            JSONObject jsonPost = new JSONObject(resp_content);

            ArrayList<HashMap<String, String>> allFields = new ArrayList<>();

            //getTotalPoints = jsonPost.getString("total_point");
            JSONArray tot_Point = jsonPost.getJSONArray("total_point");
            JSONArray cast = jsonPost.getJSONArray("message");

            if (cast.length() == 0){
                cd_NoData.setVisibility(View.VISIBLE);
                cd_WishlistData.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < cast.length(); i++) {
                    JSONObject c = cast.getJSONObject(i);

                    // Post result field to string
                    String id_line_result = c.getString(field_name[0]);
                    String id_incoming_order_result = c.getString(field_name[1]);
                    String point_result = c.getString(field_name[2]);
                    String date_result = c.getString(field_name[7]);
                    String tipe_sampah_result = c.getString(field_name[4]);
                    String jenis_sampah_result = c.getString(field_name[5]);
                    String jumlah_kg_result = c.getString(field_name[6]);

                    // Make HashMap string for put string above
                    HashMap<String, String> map = new HashMap<>();

                    map.put(field_name[0], id_line_result);
                    map.put(field_name[1], id_incoming_order_result);
                    map.put(field_name[2], point_result);
                    map.put(field_name[7], date_result);
                    map.put(field_name[4], tipe_sampah_result);
                    map.put(field_name[5], jenis_sampah_result);
                    map.put(field_name[6], jumlah_kg_result);

                    allFields.add(map);
                }

                // Call Lazy Adapter for Listview
                adapter3 = new LazyAdapter(getActivity(), allFields, 3);
                lvIncomingOrder_Data.setAdapter(adapter3);
            }

            JSONObject total_Point = tot_Point.getJSONObject(0);
            getTotalPoints = total_Point.getString("total_point");

            if (!getTotalPoints.equals("")) {
                try {
                    tvTotalPoints.setText(decimalFormat_Point.format(Double.valueOf(getTotalPoints)) + " Pts");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            if (getContext()!= null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 4 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 4: " + String.valueOf(e));
        }
    }

    private void showPopupDeleteIncomingOrder() {
        myDialog.setContentView(R.layout.pop_up_delete_incoming_order_line);

        Button btnDelete_Confirmation = myDialog.findViewById(R.id.btnDelete_Confirmation);
        Button btnCancel_Confirmation = myDialog.findViewById(R.id.btnCancel_Confirmation);
        TextView tvTitle_Delete = myDialog.findViewById(R.id.tvTitle_Confirmation);

        tvTitle_Delete.setText(getString(R.string.MSG_DELETE_INCOMING));

        btnDelete_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIncomingOrder();
            }
        });

        btnCancel_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void deleteIncomingOrder() {
        /*dialog.setMessage("Menghapus Data, Harap Menunggu...");
        dialog.show();*/

        customProgress.showProgress(getContext(), "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String delete_incoming_order_url;

        delete_incoming_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.delete_incoming_order";
        params.put("inc_order", getNameOrder_Result);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(delete_incoming_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    if (getContext()!= null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Berhasil Menghapus Kantong Limbah Sampah!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (getActivity() != null) {
                                            Intent a = new Intent(getContext(), MainActivity.class);
                                            startActivity(a);
                                            getActivity().finish();
                                        }
                                    /*Intent a = new Intent(getContext(), MainActivity.class);
                                    startActivity(a);
                                    getActivity().finish();*/
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (Throwable t) {
                    if (getContext()!= null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 3 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 3: " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if (getContext()!= null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 5 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 5: " + String.valueOf(error));
            }
        });
    }
}
