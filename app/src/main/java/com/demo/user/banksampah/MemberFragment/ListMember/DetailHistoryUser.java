package com.demo.user.banksampah.MemberFragment.ListMember;

import android.app.Dialog;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailHistoryUser extends AppCompatActivity {

    //Session Class
    protected PrefManager session;
    private ArrayList listPin = new ArrayList();
    ArrayList<HashMap<String, String>> allOrder1 = new ArrayList<>();
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected String strNo_Telepon, strIdUser, strNamaBankSampah;
    protected CustomProgress customProgress;
    protected LinearLayout parent_layout;
    protected ListView  lvListDetailOrderUser;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected LinearLayout linear_ListMember;
    protected ConnectivityManager conMgr;
    protected LazyAdapter adapter, adapter1;
    protected CardView cd_NoData, cd_NoConnection;
    protected String tvIdMemberDetail1;
    protected Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.list_order_detail);

        lvListDetailOrderUser = findViewById( R.id.listView_OrderUserDetail );
        session = new PrefManager( DetailHistoryUser.this );
        HashMap<String, String> user = session.getUserDetails();
        strNo_Telepon = user.get( PrefManager.KEY_NO_HP );
        strIdUser = user.get( PrefManager.KEY_ID );
        strNamaBankSampah = user.get( PrefManager.KEY_NAMA );
        tvIdMemberDetail1 = getIntent().getStringExtra( "id_member" );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();
        parent_layout = findViewById(R.id.parent);
        cd_NoData = findViewById(R.id.cd_noData);
        cd_NoConnection = findViewById(R.id.cd_noInternet);
        linear_ListMember = findViewById( R.id.parent );
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);


    }
}
