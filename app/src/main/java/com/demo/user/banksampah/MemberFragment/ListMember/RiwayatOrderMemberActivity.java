package com.demo.user.banksampah.MemberFragment.ListMember;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;

import java.util.HashMap;

public class RiwayatOrderMemberActivity extends AppCompatActivity {

    //Session Class
    protected PrefManager session;
    protected LinearLayout parent_layout;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected LazyAdapter adapter;

    protected CustomProgress customProgress;

    protected ListView lvListRiwayatOrderMember;
    protected ListView lvgetDetailList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout linear_listRiwayatOrderMember;
    protected ConnectivityManager conMgr;

    protected CardView cd_NoData, cd_NoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_order_member);

        customProgress = CustomProgress.getInstance();

        lvListRiwayatOrderMember = findViewById(R.id.listView_Order);
        lvgetDetailList = findViewById(R.id.listView_OrderDetails);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        linear_listRiwayatOrderMember = findViewById(R.id.linearLayout_ListOrder);

        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        //include_FormOrderList = rootView.findViewById(R.id.include_FormOrder);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            //Jalanin API
        } else {
            Snackbar snackbar = Snackbar
                    .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
            snackbar.show();
            linear_listRiwayatOrderMember.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                        //Jalanin API
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, "Tidak Ada Koneksi Internet", Snackbar.LENGTH_LONG);
                        snackbar.show();                    }
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
                    linear_listRiwayatOrderMember.setVisibility(View.GONE);
                }
            }
        });
    }
}
