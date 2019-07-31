package com.demo.user.banksampah.NotificationActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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

public class PointGetStatus extends AppCompatActivity {

    //Session Class
    PrefManager session;
    String strIDUser;

    /*API process and dialog*/
    private RestProcess rest_class;

    protected ListView lvPointGet;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ConnectivityManager conMgr;

    private LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_get_status);

        session = new PrefManager(getApplicationContext());
        HashMap<String,String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_ID);

        rest_class = new RestProcess();
        lvPointGet = (ListView)findViewById(R.id.lv_PointGet);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                getPointOrder();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getPointOrder(){
        HashMap<String,String> apiData = new HashMap<String,String>();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String list_order_url;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Mengambil Data, Harap Menunggu...");
        dialog.show();

        list_order_url = "http://dev-erpnext.pracicointiutama.id/api/method/digitalwaste.digital_waste.custom_api.get_payment_point_user";
        params.put("id_user", strIDUser);
        client.addHeader("Authorization", "token dd42bf276446682:753232865403c96");
        client.post(list_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String resp_content = null;

                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    dialog.dismiss();
                    displayPointOrder(resp_content);
                } catch (Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag"," 1: " + String.valueOf(error));
            }
        });
    }

    private void displayPointOrder(String resp_content){
        String[]field_name = {"message", "name", "total_point"};

        try{
            JSONObject jsonObject = new JSONObject(resp_content.toString());

            ArrayList<HashMap<String,String>> allOrder = new ArrayList<>();
            ArrayList<HashMap<String,String>> allOrder_Line = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i = 0; i<cast.length(); i++){
                JSONObject c = cast.getJSONObject(i);

                String namaOrder_Result = c.getString(field_name[1]);
                String totalPoint_Result = c.getString(field_name[2]);

                HashMap<String,String>map = new HashMap<String,String>();

                map.put(field_name[1], namaOrder_Result);
                map.put(field_name[2], totalPoint_Result);

                allOrder.add(map);

            }

            adapter = new LazyAdapter(this, allOrder, 8);
            lvPointGet.setAdapter(adapter);

        } catch (JSONException e){
            //Toast.makeText(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
    }
}
