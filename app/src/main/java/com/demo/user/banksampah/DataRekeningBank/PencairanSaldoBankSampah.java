package com.demo.user.banksampah.DataRekeningBank;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Services.MainActivity;
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
    protected ConnectivityManager conMgr;
    Context ctx;
    protected CardView cd_NoData, cd_NoConnection;
    protected RelativeLayout parentLayout;
    protected TextView tvBankAccount, tvNoRek, tvIdBankSampahDefault;
    protected ImageView imgBank;
    protected EditText etJumlahWithdraw;
    protected Button btnCairkan;
    protected Dialog dialog;

    //Get Data From Login Process
    protected static String getNama = "", getId = "", getBank="", getCabangBank="", getNoRek="", getUnitDefault="", getBankAccount="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pencairan_saldo_bank_sampah );

        ctx = PencairanSaldoBankSampah.this;
        session = new PrefManager( this );
        final HashMap<String, String> user = session.getUserDetails();
        getNama = user.get( PrefManager.KEY_NAMA );
        getId = user.get( PrefManager.KEY_ID );
        getBank = user.get( PrefManager.KEY_BANK );
        getCabangBank = user.get( PrefManager.KEY_CABANG );
        getNoRek = user.get( PrefManager.KEY_NO_REKENING );
        getUnitDefault = user.get( PrefManager.KEY_UNIT_DEFAULT );
        getBankAccount = user.get( PrefManager.KEY_BANK_ACCOUNT );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        dialog = new Dialog( PencairanSaldoBankSampah.this );
        cd_NoData = findViewById( R.id.cd_noData );
        cd_NoConnection = findViewById( R.id.cd_noInternet );
        parent_layout = findViewById( R.id.parent );
        customProgress = CustomProgress.getInstance();
        parentLayout = findViewById( R.id.parentLayout );
        btnCairkan = findViewById( R.id.btnCairkan );
        imgBank = findViewById( R.id.imgBank );
        etJumlahWithdraw = findViewById( R.id.etJumlahWithdraw );
        tvBankAccount = findViewById( R.id.tvBankAccount );
        tvIdBankSampahDefault = findViewById( R.id.tvIdBankSampahDefault );
        tvNoRek = findViewById( R.id.tvNoRek );



        btnCairkan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strWithdraw = etJumlahWithdraw.getText().toString();
                pencairanSaldoBank(strWithdraw);
            }
        } );

        getDataRekening();
    }
    private void getDataRekening(){
        String base_url = apiData.get( "str_url_address" ) + (".check_rekening");
        StringRequest stringRequest = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    viewDataRekening( response );
                    Log.d( "debug", "onResponse: "+response );
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

    public void pencairanSaldoBank(String strWithdraw){
        String base_url = apiData.get( "str_url_address" ) + (".withdraw_request_default");
        StringRequest stringRequest = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d( "Withdraw", "onResponse: "+response );
                    viewDataWithdraw( response );
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
                params.put( "id_bank_sampah", getUnitDefault);
                params.put( "id_bank_account", getBankAccount);
                params.put( "jumlah_withdraw", strWithdraw);
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

    protected void viewDataWithdraw(String response){
        String[] field_name = {"message", "data"};
        try {
            JSONObject jsonObject = new JSONObject( response );
            String message = jsonObject.getString( field_name[0] );
            String data = jsonObject.getString( field_name[1] );
            if (message.equals( "Success" )){
                Toast.makeText( PencairanSaldoBankSampah.this, data, Toast.LENGTH_LONG ).show();
                finish();
            } else if (message.equals( "Failed" )){
                Toast.makeText( PencairanSaldoBankSampah.this, data, Toast.LENGTH_LONG ).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void viewDataRekening(String response){
        String[] field_name = {"message", "data", "name", "no_rekening", "bank_account", "logo", "pemilik", "cabang","nama_bank"};
        try {
            JSONObject jsonObject = new JSONObject( response );
            String message = jsonObject.getString( field_name[0] );
            if (message.equals( "True" )){
                JSONObject jsonObject1 = jsonObject.getJSONObject( field_name[1] );
                String namaBank = jsonObject1.getString( field_name[8] );
                String noRekeningBank = jsonObject1.getString( field_name[3] );
                String bankAccount = jsonObject1.getString( field_name[4] );
                String logoBank = jsonObject1.getString( field_name[5] );
                String pemilik = jsonObject1.getString( field_name[6] );
                String cabang = jsonObject1.getString( field_name[7] );
                session.checkBankAkun( namaBank, noRekeningBank, pemilik, cabang, bankAccount);
                tvNoRek.setText( noRekeningBank );
                tvIdBankSampahDefault.setText( pemilik );
                tvBankAccount.setText( bankAccount );
                Picasso.get()
                        .load(apiData.get("str_url_main")+ logoBank)
                        .error(R.drawable.ic_navigation_profil)
                        .into(imgBank);
            } else if (message.equals( "Not Found" )){
                popupYesOrNo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void popupYesOrNo(){
        dialog.setContentView( R.layout.ok_dan_tidak );
        Button ok = dialog.findViewById( R.id.btnOk );
        Button cancel = dialog.findViewById( R.id.btnCancel );

        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( PencairanSaldoBankSampah.this, DataRekeningBank.class );
                startActivity( intent );
            }
        } );

        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( PencairanSaldoBankSampah.this, MainActivity.class );
                startActivity( intent );
            }
        } );

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            dialog.show();
        }
    }
}
