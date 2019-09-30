package com.demo.user.banksampah.DataItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class TambahkanDaftar extends AppCompatActivity {

    //Deklarasi Layout Global
    private static ImageView imgBahan;
    private EditText etNamaBahan, etHargaPerKG;
    private Spinner sBahan;
    private Button btnTambahkan;
    private RelativeLayout parent_layout;
    protected ImageView arrowBack;

    //An ArrayList for Spinner Items
    private ArrayList<String> Items;

    //Proccess API
    protected RestProcess restClass;
    protected HashMap<String, String> apiData;

    //Upload Image
    protected String cameraFilePath, imageFileName, ConvertImageToBase64;
    protected String StrImageUploadToDB = "data:image/jpeg:base64,";
    protected Bitmap getBitmapPicture;
    protected ByteArrayOutputStream byteArrayOutputStream;

    //Request Code
    private final static int ADDRESS_REQUEST_CODE = 2;
    private final static int GALLERY_REQUEST_CODE = 3;
    private final static int CAMERA_REQUEST_CODE = 4;

    protected String getPhone_Extra = "", getIdUser_Extra = "";

    //API dialog progress loading
    protected CustomProgress customProgress;


    //Session Class
    protected PrefManager session;
    protected HashMap<String,String> user;

    //Get Data From Login Process
    protected static String getNama = "";

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahkan_daftar);

        this.ctx = ctx;

        //Define Id Layout
        etNamaBahan = findViewById(R.id.etNamaDaftar);
        etHargaPerKG = findViewById(R.id.etHarga);
        sBahan = findViewById(R.id.sBahan);
        btnTambahkan = findViewById(R.id.btnTambahkanDaftar);
        parent_layout = findViewById(R.id.parentDaftar);
        arrowBack = findViewById( R.id.arrowBack );


        restClass = new RestProcess();
        apiData = restClass.apiErecycle();
        customProgress = CustomProgress.getInstance();

        //Session Instance
        session = new PrefManager(getApplicationContext());
        user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);

        //Initializing the ArrayList
        Items = new ArrayList<String>();
        etNamaBahan.setText(getNama);
        etNamaBahan.setEnabled( false );

        sBahan.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) ctx);
        btnTambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        getData();
    }

    protected void SaveToDB(final String strNamaBahan, final String strHargaBahan, final String strSpinner) {
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"id_bank_sampah", "jenis_item", "harga"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_add_item");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TambahkanDaftar.this);
                    builder.setMessage("Item Berhasil Ditambahkan.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent add_item = new Intent( TambahkanDaftar.this, ListHargaItem.class );
                                    startActivity( add_item );
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Register Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strNamaBahan);
                params.put(field_name[1], strSpinner);
                params.put(field_name[2], strHargaBahan);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }
    private void validateData(){

        final String strNamaBahan = etNamaBahan.getText().toString().trim();
        final String strJenis_item = sBahan.getSelectedItem().toString().trim();
        final String strHargaItem = etHargaPerKG.getText().toString().trim();

        if (strNamaBahan.isEmpty()){
            etNamaBahan.setError("Harap Masukkan Nama Bank Sampah");
            etNamaBahan.requestFocus();
        }else if (strJenis_item.isEmpty()) {
            etNamaBahan.setError("Harap Pilih Item");
            etNamaBahan.requestFocus();
        }else if (strHargaItem.isEmpty()) {
            etHargaPerKG.setError("Harap Masukkan Harga Item");
            etHargaPerKG.requestFocus();
        }else{
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TambahkanDaftar.this);
            builder.setMessage("Apakah Anda Yakin Ingin Menambah Harga Item?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveToDB(strNamaBahan, strHargaItem, strJenis_item);
                        }
                    })
                    .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void getData(){
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST,apiData.get("str_url_address")+apiData.get("str_api_list_item"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tag", response);
                        getItem(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getItem(String response){
        String[]field_name = {"message", "jenis_item"};

        try {
            JSONObject jsonObject = new JSONObject(response);
            ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            Log.e("tag", String.valueOf(cast.length()));

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String jenis_item= c.getString(field_name[1]);

                Items.add(jenis_item);
            }
        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 2 :" + String.valueOf(e));
            e.printStackTrace();
        }
        //Setting adapter to show the items in the spinner
        sBahan.setAdapter(new ArrayAdapter<String>(TambahkanDaftar.this, android.R.layout.simple_spinner_dropdown_item, Items));
    }

}