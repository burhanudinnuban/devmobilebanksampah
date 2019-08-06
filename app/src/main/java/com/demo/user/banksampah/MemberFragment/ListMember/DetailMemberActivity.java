package com.demo.user.banksampah.MemberFragment.ListMember;

import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DetailMemberActivity extends AppCompatActivity {
    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected String strIDUser;

    protected CustomProgress customProgress;

    protected LinearLayout parent_layout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ConnectivityManager conMgr;

    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cd_NoConnection;

    protected ImageView imgDetailMember, imgPencairan, imgHubungiMember, imgRiwayatOrder, imgHapusMember;
    protected TextView tvNamaMemberDetail, tvPointMemberDetail, tvIdMemberDetail, tvAlamatMemberDetail, tvNoHpMemberDetail, tvStatusMemberDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_member);
        session = new PrefManager(DetailMemberActivity.this);
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get(PrefManager.KEY_NAMA);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        //        Deklarasi dari layout ke aktivity
        imgDetailMember =findViewById(R.id.imgPicture_DetailMember);
        imgPencairan =findViewById(R.id.imgPencairan);
        imgHubungiMember =findViewById(R.id.imgHubungiMember);
        imgHapusMember =findViewById(R.id.imgHapusMember);
        imgRiwayatOrder =findViewById(R.id.imgRiwayatOrder);
        tvNamaMemberDetail =findViewById(R.id.tvNamaMember_DetailMember);
        tvPointMemberDetail =findViewById(R.id.tvPointMember_DetailMember);
        tvIdMemberDetail =findViewById(R.id.tvIDMember_DetailMember);
        tvAlamatMemberDetail =findViewById(R.id.tvAlamatMember_DetailMember);
        tvNoHpMemberDetail =findViewById(R.id.tvNoHPMember_DetailMember);
        tvStatusMemberDetail =findViewById(R.id.tvStatusMember_DetailMember);

//        Deklarasi String ke Rest
        String imgDetailMember1 = getIntent().getStringExtra("base_url");
        String tvNamaMemberDetail1 = getIntent().getStringExtra("id_order");
        String tvPointMemberDetail1 = getIntent().getStringExtra("id_order");
        String tvIdMemberDetail1 = getIntent().getStringExtra("id_order");
        String tvAlamatMemberDetail1 = getIntent().getStringExtra("id_order");
        String tvNoHpMemberDetail1 = getIntent().getStringExtra("id_order");
        String tvStatusMemberDetail1 = getIntent().getStringExtra("id_order");

        tvNamaMemberDetail.setText(""+tvNamaMemberDetail1);
        tvPointMemberDetail.setText(""+tvPointMemberDetail1);
        tvIdMemberDetail.setText(""+tvIdMemberDetail1);
        tvAlamatMemberDetail.setText(""+tvAlamatMemberDetail1);
        tvNoHpMemberDetail.setText(""+tvNoHpMemberDetail1);
        tvStatusMemberDetail.setText(""+tvStatusMemberDetail1);

        Picasso.get().load((imgDetailMember1)).into(imgDetailMember);

        final ImageView image = new ImageView(this);
        Picasso.get().load((imgDetailMember1)).into(image);


    }

}
