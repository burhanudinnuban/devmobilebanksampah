package com.demo.user.banksampah.MainFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.QRScanActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.DataItem.ListHargaItem;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    protected RelativeLayout parent_layout;
    protected TextView tvNamaBank, tvAlamatBank, tvSaldo;
    protected ImageView imgBankSampah;
    protected String contents, format;
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    private Dialog ScanDialog;
    protected ConnectivityManager conMgr;
    private final static int BARCODE_REQUEST_CODE = 1;
    protected LazyAdapter adapter;
    protected View rootView;
    protected CardView cvRekBank, cvDataItem, cvPencairanSaldo, cvDataPengurus;
    protected String strNamaBankSampah, strMessage;
    protected CustomProgress customProgress;
    //Session Class
    PrefManager session;
    protected String strNama, strAlamat, strFoto;
    ArrayList<HashMap<String, String>> ScanStatus = new ArrayList<>();
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout scanQR = rootView.findViewById( R.id.layoutScanQR );
        parent_layout = rootView.findViewById(R.id.parent);
        tvNamaBank = rootView.findViewById(R.id.tvNamaBankSampah);
        tvAlamatBank = rootView.findViewById(R.id.tvAlamatBankSampah);
        imgBankSampah = rootView.findViewById(R.id.imgBankSampah);
        cvDataItem = rootView.findViewById(R.id.cvDataItem);
        cvPencairanSaldo = rootView.findViewById( R.id.cvPencairanSaldo );
        customProgress = CustomProgress.getInstance();
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        tvSaldo = rootView.findViewById( R.id.saldo_bank );
        DecimalFormat decimalFormat_Point = new DecimalFormat( ",###" );
        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        session = new PrefManager(getContext());
        //session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        strNama = user.get(PrefManager.KEY_NAMA);
        strFoto = user.get(PrefManager.KEY_FOTO);
        strAlamat = user.get(PrefManager.KEY_ALAMAT);
        String saldo = user.get( PrefManager.KEY_SALDO_BANK_SAMPAH );

        tvSaldo.setText( "Rp." + decimalFormat_Point.format(Double.valueOf(saldo))  );
        tvNamaBank.setText(strNama);
        tvAlamatBank.setText(strAlamat);

        cvDataItem.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent list_harga = new Intent( getContext(), ListHargaItem.class );
                startActivity( list_harga );
            }
        } );

        scanQR.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanmember = new Intent( getContext(), QRScanActivity.class );
                scanmember.putExtra( "SCAN_MODE", "BARCODE_MODE" );
                startActivityForResult( scanmember, BARCODE_REQUEST_CODE );
            }
        } );

        cvPencairanSaldo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "Maaf Sedang Dalam Proses", Toast.LENGTH_LONG ).show();
            }
        } );

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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            //Do Scan Barcode
            if (requestCode == BARCODE_REQUEST_CODE) {
                //Content equals Barcode Number
                contents = intent.getStringExtra( "SCAN_RESULT" );
//                format = intent.getStringExtra( "SCAN_RESULT_FORMAT" );
                Log.d( "tag", contents );

                if (getContext() != null) {
                    Toasty.info( getContext(), "Kode Barcode Produk: " + contents, Toast.LENGTH_LONG ).show();
                }

                //Check Barcode data From DB
                checkScanData( contents );
            }
        }
    }

    private void checkScanData(final String contents) {
        ScanDialog = new Dialog(getContext());
        String base_url = apiData.get( "str_url_address" ) + ( ".scan_add_member" );
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d( "DEBUG", "Check Barcode Response: " + response );
                try {
                    getMember( response );
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make( parent_layout, getString( R.string.MSG_CODE_409 ) + " 1: " + getString( R.string.MSG_CHECK_DATA ), Snackbar.LENGTH_SHORT );
                    snackbar.show();
                    Log.d( "DEBUG", "Error Login Response: " + t.toString() );
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d( "DEBUG", "Volley Error: " + error.getMessage() );
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                snackbar.show();
            }
        } ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_user", contents );
                params.put( "id_bank_sampah", strNama );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
    }

    private void getMember(String resp_content) {
        String[] field_name = {"message", "data", "status", "id_bank_sampah", "id_member", "nama_member", "alert", "point"};
        try {
            ScanStatus = rest_class.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );
            strMessage = jsonPost.getString( field_name[0] );
            JSONObject cast = jsonPost.getJSONObject( field_name[1] );
            Log.d("tag", String.valueOf( cast ) );
            String strAlert = jsonPost.getString( field_name[6] );

            if (strMessage.equals( "True" )) {
                String strStatus = cast.getString( field_name[2] );
                String strIdMember = cast.getString( field_name[4] );
                String sttBankSampah= cast.getString( field_name[3] );
                String strNamaMember = cast.getString( field_name[5] );
                String strPoint = cast.getString( field_name[7]);
                ScanDataMemberPopUp(strStatus, strIdMember, strNamaMember, sttBankSampah, strPoint);
            } else {
                Toast.makeText( getContext(), strAlert,Toast.LENGTH_SHORT ).show();
            }

        } catch (JSONException e) {
            Snackbar snackbar = Snackbar
                    .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 2: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
            snackbar.show();
            Log.e( "DEBUG", "JSON Exception Error: " + e.toString() );
        }
    }
    private void ScanDataMemberPopUp(String strStatus,String strIdMember,String strNamaMember,String sttBankSampah, String strPoint){
        ScanDialog.setContentView( R.layout.activity_scan_data_member );
        TextView tvNama, tvStatus, tvBankSampah, tvIdMember, tvPoint;

        tvNama = ScanDialog.findViewById( R.id.tvNamaMember );
        tvStatus = ScanDialog.findViewById( R.id.tvStatusMember );
        tvBankSampah = ScanDialog.findViewById( R.id.tvNamaBankSampah );
        tvIdMember = ScanDialog.findViewById( R.id.tvIdMember );
        tvPoint = ScanDialog.findViewById( R.id.tvPoint );

        Button btnApprove = ScanDialog.findViewById( R.id.btnApprove );

        btnApprove.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanDialog.dismiss();
            }
        } );
        tvNama.setText( strNamaMember );
        tvStatus.setText( strStatus );
        tvBankSampah.setText( sttBankSampah );
        tvIdMember.setText( strIdMember );
        tvPoint.setText( strPoint );

        if (ScanDialog.getWindow()!= null){
            ScanDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            ScanDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            ScanDialog.show();
        }
    }
}
