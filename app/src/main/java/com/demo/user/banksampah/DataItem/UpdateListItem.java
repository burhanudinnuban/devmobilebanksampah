package com.demo.user.banksampah.DataItem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateListItem extends AppCompatActivity {
    protected String strIDUser;

    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    private RestProcess rest_class;
    private HashMap<String, String> apiData;
    protected CustomProgress customProgress;

    //Deklarasi layout ke Aktivity
    private EditText etHargaitemUpdate;
    private TextView tvJenisItemUpdate, tvIDItemUpdate;
    private ImageView imgPictureItemUpdate;
    private Button btnUpdateItem;
    private LinearLayout parent_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list_item);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        session = new PrefManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        //progressDialog = new ProgressDialog(this);
        customProgress = CustomProgress.getInstance();

        //Deklarasi Ke Id Layout
        etHargaitemUpdate = findViewById(R.id.etHargaItemUpdate);
        tvJenisItemUpdate = findViewById(R.id.tvJenisItemUpdate);
        btnUpdateItem = findViewById(R.id.btnRubahItem);
        parent_layout = findViewById(R.id.ParentRubah);
        tvIDItemUpdate = findViewById( R.id.tvIdItem );

        //        Deklarasi String ke Rest

        String strIdBankSampah = getIntent().getStringExtra("id_bank_sampah");
        String strJenisItem = getIntent().getStringExtra("jenis_item");
        String strHargaPerKg = getIntent().getStringExtra("harga_per_kilo");
        String strIdItem = getIntent().getStringExtra("id_item");

        NumberFormat format = NumberFormat.getCurrencyInstance( Locale.ENGLISH);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        DecimalFormat decimalFormat = new DecimalFormat( "###" );
        etHargaitemUpdate.setText(decimalFormat.format( Double.valueOf( strHargaPerKg )));
        tvJenisItemUpdate.setText(strJenisItem);
        tvIDItemUpdate.setText( strIdItem );

        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    protected void updateItem(final String IdItem, final String HargaItem) {
        final String[] field_name = {"id_item_bs", "harga_per_kilo"};
        String base_url = apiData.get("str_url_address") + (".edit_harga_item");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG 1", "Update Item Response: " + response);
                try {
                    finish();
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.e("DEBUG 2", "Error Validate Change Password Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DEBUG", "Volley Error: " + error.getMessage());
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], IdItem);
                params.put(field_name[1], HargaItem);
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

    private void validateData() {
        DecimalFormat decimalFormat = new DecimalFormat(",###.##");
        String strHargaItem = etHargaitemUpdate.getText().toString();
        String strIdItem = tvIDItemUpdate.getText().toString();
        if (strHargaItem.isEmpty()) {
            etHargaitemUpdate.setError("Harap Masukkan Harga Item");
            etHargaitemUpdate.requestFocus();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateListItem.this);
            builder.setMessage("Apakah Anda Yakin Memperbarui Harga Item?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateItem(strIdItem,strHargaItem);
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
