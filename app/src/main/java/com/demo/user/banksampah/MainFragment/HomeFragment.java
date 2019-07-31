package com.demo.user.banksampah.MainFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.InfoLimbahDetails;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.RegisterActivities.RegisterPhoneActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    protected RelativeLayout parent_layout;
    protected TextView tvNamaBank, tvAlamatBank;
    protected ImageView imgBankSampah;

    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected ConnectivityManager conMgr;

    protected LazyAdapter adapter;
    protected View rootView;

    //Session Class
    PrefManager session;
    protected String strNama, strAlamat, strFoto;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        parent_layout = rootView.findViewById(R.id.parent);
        tvNamaBank = rootView.findViewById(R.id.tvNamaBankSampah);
        tvAlamatBank = rootView.findViewById(R.id.tvAlamatBankSampah);
        imgBankSampah = rootView.findViewById(R.id.imgBankSampah);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        session = new PrefManager(getContext());
        //session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        strNama = user.get(PrefManager.KEY_NAMA);
        strFoto = user.get(PrefManager.KEY_FOTO);
        strAlamat = user.get(PrefManager.KEY_ALAMAT);

        tvNamaBank.setText(strNama);
        tvAlamatBank.setText(strAlamat);

        Picasso.get()
                .load(apiData.get("str_url_main")+ strFoto)
                .error(R.drawable.ic_navigation_profil)
                .into(imgBankSampah);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
