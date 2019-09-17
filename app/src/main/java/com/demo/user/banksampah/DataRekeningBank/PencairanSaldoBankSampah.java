package com.demo.user.banksampah.DataRekeningBank;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PencairanSaldoBankSampah extends AppCompatActivity {
    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected CustomProgress customProgress;
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
    protected LinearLayout parent_layout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected ConnectivityManager conMgr;
    Context ctx;
    protected CardView cd_NoData, cd_NoConnection;
    protected FloatingActionButton addRekBank;
    protected RelativeLayout parentLayout;
    protected TextView tvBankAccount, tvNoRek, tvIdBankSampahDefault;
    protected ImageView imgBank;
    protected EditText etJumlahWithdraw;
    protected Button btnCairkan;

    //Get Data From Login Process
    protected static String getNama = "", getId = "";
    private ShimmerFrameLayout mShimmerViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pencairan_saldo_bank_sampah );

        ctx = PencairanSaldoBankSampah.this;
        session = new PrefManager( this );
        final HashMap<String, String> user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getId = user.get( PrefManager.KEY_ID );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        mShimmerViewContainer = findViewById( R.id.shimmer_view_container );
        cd_NoData = findViewById( R.id.cd_noData );
        cd_NoConnection = findViewById( R.id.cd_noInternet );
        parent_layout = findViewById( R.id.parent );
        customProgress = CustomProgress.getInstance();
        addRekBank = findViewById( R.id.fbaddRekBank );
        parentLayout = findViewById( R.id.parentLayout );
        mSwipeRefreshLayout = findViewById( R.id.swipeToRefresh );
        btnCairkan = findViewById( R.id.btnCairkan );
        imgBank = findViewById( R.id.imgBank );
        etJumlahWithdraw = findViewById( R.id.etJumlahWithdraw );
        tvBankAccount = findViewById( R.id.tvBankAccount );
        tvIdBankSampahDefault = findViewById( R.id.tvIdBankSampahDefault );
        tvNoRek = findViewById( R.id.tvNoRek );

        String strWithdraw = etJumlahWithdraw.getText().toString();
        getDataRekening();

        btnCairkan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pencairanSaldoBank();
            }
        } );
    }
    private void getDataRekening(){
        String base_url = apiData.get( "str_url_address" ) + (".check_rekening");
        StringRequest stringRequest = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    viewDataRekening( response );
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar.make( parentLayout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make( parentLayout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                snackbar.show();
            }
        } ){
            @Override
            protected Map<String, String > getParams(){
                Map<String, String> params = new HashMap<>(  );
                params.put( "id_user", getId);
                return params;
            }
            @Override
            public  Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<>(  );
                params.put( apiData.get( "str_header" ), apiData.get("str_token_value") );
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue( stringRequest, apiData.get( "str_json_obj" ) );
    }

    protected void viewDataRekening(String response){
        String[] field_name = {"message", "data", "name", "no_rekening", "bank_account", "logo"};
        try {
            JSONObject jsonObject = new JSONObject( response );
            String message = jsonObject.getString( field_name[0] );
            JSONObject jsonObject1 = jsonObject.getJSONObject( field_name[1] );
            String idBankSampah = jsonObject1.getString( field_name[2] );
            String noRekeningBank = jsonObject1.getString( field_name[3] );
            String bankAccount = jsonObject1.getString( field_name[4] );
            String logoBank = jsonObject1.getString( field_name[5] );

            if (message.equals( "True" )){
                tvNoRek.setText( noRekeningBank );
                tvIdBankSampahDefault.setText( "Lestari" );
                tvBankAccount.setText( bankAccount );
                Picasso.get()
                        .load(apiData.get("str_url_main")+ logoBank)
                        .error(R.drawable.ic_navigation_profil)
                        .into(imgBank);
            }
            else {
                Intent intent = new Intent( PencairanSaldoBankSampah.this, DataRekeningBank.class );
                startActivity( intent );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pencairanSaldoBank(){
        String base_url = apiData.get( "str_url_address" ) + (".withdraw_request_default");
        StringRequest stringRequest = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    viewDataRekening( response );
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar.make( parentLayout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar snackbar = Snackbar.make( parentLayout, "Mohon Periksa Data Kembali", Snackbar.LENGTH_SHORT );
                snackbar.show();
            }
        } ){
            @Override
            protected Map<String, String > getParams(){
                Map<String, String> params = new HashMap<>(  );
                params.put( "id_user", getId);
                params.put( "id_bank_sampah", getId);
                params.put( "id_bank_account", getId);
                params.put( "jumlah_withdraw", getId);
                return params;
            }
            @Override
            public  Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<>(  );
                params.put( apiData.get( "str_header" ), apiData.get("str_token_value") );
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue( stringRequest, apiData.get( "str_json_obj" ) );
    }
}
