package com.demo.user.banksampah.MemberFragment.RequestMember;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.ChangePassword;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.MemberFragment.ListMember.DetailMemberActivity;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailRequestMember extends AppCompatActivity {

    private Button btTerimaMember, btTolakMember;
    private ImageView imgPictureReq;
    private TextView tvNamaReqMember, tvIdReqMember, tvAlamatReqMember, tvTanggalCreateReqMember;
    protected LinearLayout parent_layout;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    //Session Class
    protected PrefManager session;

    //Dialog Message
    //private ProgressDialog progressDialog;
    private CustomProgress customProgress;

    protected String url_foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request_member);
        session = new PrefManager(DetailRequestMember.this);
        HashMap<String, String> user = session.getUserDetails();
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        //progressDialog = new ProgressDialog(this);
        customProgress = CustomProgress.getInstance();
        parent_layout = findViewById(R.id.parent1);
        btTerimaMember = findViewById(R.id.btTambahmember);
        btTolakMember = findViewById(R.id.btTolakMember);
        imgPictureReq = findViewById(R.id.imgPictureDetailReqMember);
        tvNamaReqMember = findViewById(R.id.tvNamaDetailReqMember);
        tvAlamatReqMember = findViewById(R.id.tvAlamatDetailReqMember);
        tvIdReqMember = findViewById(R.id.tvIdDetailReqMember);
        tvTanggalCreateReqMember = findViewById(R.id.tvTanggalBuatDetailReqMember);

        String strDetailPhotoReqmember = getIntent().getStringExtra("foto");
        String strNamaDetailReqMember = getIntent().getStringExtra("nama_member");
        String strAlamatDetailReqMember = getIntent().getStringExtra("alamat");
        final String strIdReqMember = getIntent().getStringExtra("id_member");
        String strTanggalCreateReqMember = getIntent().getStringExtra("creation");
        final String strIdBankSampah = getIntent().getStringExtra("id");
        final String strIdBankSampah1 = getIntent().getStringExtra("id_bank_sampah");

        tvNamaReqMember.setText(""+strNamaDetailReqMember);
        tvAlamatReqMember.setText(""+strAlamatDetailReqMember);
        tvIdReqMember.setText(""+strIdReqMember);
        tvTanggalCreateReqMember.setText(""+strTanggalCreateReqMember);

        url_foto = apiData.get("str_url_main");
        Picasso.get()
                .load(url_foto + strDetailPhotoReqmember)
                //.error(R.drawable.ic_navigation_profil)
                .into(imgPictureReq);

        final ImageView image = new ImageView(this);
        Picasso.get().load((strDetailPhotoReqmember)).into(image);

        btTolakMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailRequestMember.this);

                builder
                        .setTitle("Tolak Member!!")
                        .setPositiveButton("Tolak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String[] field_name = {"id_member", "id_bank_sampah"};
                                String base_url = apiData.get("str_url_address") + apiData.get("str_api_tolak_request_member");

                                StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        customProgress.hideProgress();
                                        Log.d("DEBUG", "Register Response: " + response);
                                        try {
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailRequestMember.this);
                                            builder
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent DetailReqMember = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(DetailReqMember);
                                                            finish();
                                                        }
                                                    });
                                            android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                            Log.e("tag", "sukses");
                                        } catch (Throwable t) {
                                            Snackbar snackbar = Snackbar
                                                    .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                            Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
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
                                        params.put(field_name[0], strIdReqMember);
                                        params.put(field_name[1], strIdBankSampah1);

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
                        });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

        btTerimaMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailRequestMember.this);
                builder
                        .setTitle("Terima Member")
                        .setPositiveButton("Terima", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String[] field_name = {"id_member", "id_bank_sampah"};

                                String base_url = apiData.get("str_url_address") + apiData.get("str_api_acc_request_member");

                                StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        customProgress.hideProgress();
                                        Log.d("DEBUG", "Register Response: " + response);
                                        try {
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailRequestMember.this);
                                            builder.setMessage(R.string.MSG_ACC_MEMBER)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent DetailReqMember = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(DetailReqMember);
                                                            finish();
                                                        }
                                                    });
                                            android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                            Log.e("tag", "sukses");
                                        } catch (Throwable t) {
                                            Snackbar snackbar = Snackbar
                                                    .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                            Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
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
                                        params.put(field_name[0], strIdReqMember);
                                        params.put(field_name[1], strIdBankSampah1);

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
                        });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}
