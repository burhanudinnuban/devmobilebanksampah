package com.demo.user.banksampah.MemberFragment.ListMember;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.ExpandableListAdapter;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

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
    protected LazyAdapter adapter1;
    protected ExpandableListAdapter expandableListAdapter;
    protected ExpandableListAdapter expandableListAdapter1;
    protected ExpandableListView ELListOrderUser;

    protected CardView cd_NoData, cd_NoConnection;

    protected ImageView imgDetailMember, imgPencairan, imgHubungiMember, imgRiwayatOrder, imgHapusMember;
    protected TextView tvNamaMemberDetail, tvPointMemberDetail, tvIdMemberDetail, tvAlamatMemberDetail, tvNoHpMemberDetail, tvStatusMemberDetail, tvEmailMemberDetail;
    protected AlertDialog alertDialog1;
    private String m_Text = "";
    protected String url_foto;

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
        imgDetailMember = findViewById(R.id.imgPicture_DetailMember);
        imgPencairan = findViewById(R.id.imgPencairan);
        imgHubungiMember = findViewById(R.id.imgHubungiMember);
        imgHapusMember = findViewById(R.id.imgHapusMember);
        imgRiwayatOrder = findViewById(R.id.imgRiwayatOrder);
        tvNamaMemberDetail = findViewById(R.id.tvNamaMember_DetailMember);
        tvPointMemberDetail = findViewById(R.id.tvPointMember_DetailMember);
        tvIdMemberDetail = findViewById(R.id.tvIDMember_DetailMember);
        tvAlamatMemberDetail = findViewById(R.id.tvAlamatMember_DetailMember);
        tvNoHpMemberDetail = findViewById(R.id.tvNoHPMember_DetailMember);
        tvStatusMemberDetail = findViewById(R.id.tvStatusMember_DetailMember);
        tvEmailMemberDetail = findViewById(R.id.tvEmailMemberDetail);
        ELListOrderUser = findViewById(R.id.ELListOrderUser);

//        Deklarasi String ke Rest
        String imgDetailMember1 = getIntent().getStringExtra("foto");
        String tvNamaMemberDetail1 = getIntent().getStringExtra("nama_member");
        final String tvPointMemberDetail1 = getIntent().getStringExtra("point");
        final String tvIdMemberDetail1 = getIntent().getStringExtra("id_member");
        String tvAlamatMemberDetail1 = getIntent().getStringExtra("alamat");
        String tvNoHpMemberDetail1 = getIntent().getStringExtra("no_telepon");
        String tvStatusMemberDetail1 = getIntent().getStringExtra("id");
        String tvEmailMemberDetail1 = getIntent().getStringExtra("email");


        tvNamaMemberDetail.setText("" + tvNamaMemberDetail1);
        tvPointMemberDetail.setText("" + tvPointMemberDetail1);
        tvIdMemberDetail.setText("" + tvIdMemberDetail1);
        tvAlamatMemberDetail.setText("" + tvAlamatMemberDetail1);
        tvNoHpMemberDetail.setText("" + tvNoHpMemberDetail1);
        tvStatusMemberDetail.setText("" + tvStatusMemberDetail1);
        tvEmailMemberDetail.setText("" + tvEmailMemberDetail1);

        Picasso.get().load((imgDetailMember1)).into(imgDetailMember);

        final ImageView image = new ImageView(this);

        url_foto = apiData.get("str_url_main");
        Picasso.get()
                .load(url_foto + imgDetailMember1)
                .error(R.drawable.ic_navigation_profil)
                .into(imgDetailMember);

        imgRiwayatOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgress.showProgress(DetailMemberActivity.this, "", false);
                final String[] FieldName = {"id_user", "id_bank_sampah"};

                String base_url = apiData.get("str_url_address") + apiData.get("str_api_history_order_user");
                StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        customProgress.hideProgress();
                        Log.d("debug", "Check Login Response: " + response);
                        try {
                                viewOrderUser(response);
                            customProgress.hideProgress();
                        } catch (Throwable t) {
                            Snackbar snackbar = Snackbar
                                    .make(parent_layout, getString(R.string.MSG_CODE_409) + "1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            Log.d("debug", "Error Check Login Response: " + t.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        Log.d("debug", "Volley Error: " + error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(FieldName[0], tvIdMemberDetail1);
                        params.put(FieldName[1], strIDUser);
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

            protected void viewOrderUser(String resp_content) {
                String[] field_name = {"message", "nama", "creation", "berat_total", "item_order_history_line","point_total"};
                String[] field_name1 = {"jenis_item", "point"};

                try {
                    JSONObject jsonObject = new JSONObject(resp_content);
                    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
                    ArrayList<HashMap<String, String>> allOrder1 = new ArrayList<>();

                    JSONArray cast = jsonObject.getJSONArray(field_name[0]);
                    JSONArray cast1 = jsonObject.getJSONArray(field_name[4]);
                    Log.e("tag", String.valueOf(cast.length()));
                    Log.e("tag", String.valueOf(cast1.length()));
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject c = cast.getJSONObject(i);

                            String nama_user = c.getString(field_name[1]);
                            String tanggal_order = c.getString(field_name[2]);
                            String berat_total = c.getString(field_name[3]);
                            String point_total = c.getString(field_name[5]);

                            HashMap<String, String> map = new HashMap<>();

                            map.put(field_name[1], nama_user);
                            map.put(field_name[2], tanggal_order);
                            map.put(field_name[3], berat_total);
                            map.put(field_name[5], point_total);
                            allOrder.add(map);

                            for (int x = 0; x< cast1.length(); x++){
                                JSONObject D = cast1.getJSONObject(x);
                                String jenis_item = D.getString(field_name1[0]);
                                String point = D.getString(field_name1[1]);
                                HashMap<String, String> map1 = new HashMap<>();
                                map1.put(field_name1[0], jenis_item);
                                map1.put(field_name1[1],point);
                                allOrder1.add(map1);
                            }
                        }

                    Log.d("tag", allOrder.toString());
                    Log.d("tag", allOrder1.toString());

                    adapter = new LazyAdapter(DetailMemberActivity.this, allOrder,  13);
                    adapter1 = new LazyAdapter(DetailMemberActivity.this, allOrder1,  14);
//                    ExpandableListView.setAdapter(adapter);
//                    ExpandableListView.setAdapter(adapter1);


            /*} else {
                //include_FormOrderList.setVisibility(View.GONE);
                cd_NoData.setVisibility(View.VISIBLE);
                linear_listOrder.setVisibility(View.GONE);
                cd_NoConnection.setVisibility(View.GONE);
                *//*if (getContext() != null) {
                    Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
                }*//*
            }*/

                } catch (JSONException e) {
                    if (DetailMemberActivity.this != null) {
                        Toasty.error(DetailMemberActivity.this, getString(R.string.MSG_CODE_409) + " 2: " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 2 :" + String.valueOf(e));
                    e.printStackTrace();
            /*include_FormOrderList.setVisibility(View.GONE);
            linear_NoData.setVisibility(View.VISIBLE);
            if(getContext()!=null) {
                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
            }*/
                }
            }
        });

        imgHubungiMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlertDialogWithRadioButtonGroup();
            }
        });


        imgPencairan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailMemberActivity.this);
                // Set up the input
                final EditText input = new EditText(DetailMemberActivity.this);
                input.setText(m_Text);

                builder.setTitle("Masukkan Jumlah Uang Yang Ingin Dicairkan")
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the textinput.setInputType(InputType.TYPE_CLASS_TEXT);
                        .setView(input)
                        // Set up the buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();
                                final String[] field_name = {"id_member", "jumlah_withdraw", "id_bank_sampah"};
                                String base_url = apiData.get("str_url_address") + apiData.get("str_api_pencairan_saldo");
                                StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("DEBUG", "Register Response: " + response);
                                        try {
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailMemberActivity.this);
                                            builder.setMessage(R.string.MS_PENCAIRAN_SALDO)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent pencairan = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(pencairan);
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
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put(field_name[0], tvIdMemberDetail1);
                                        params.put(field_name[1], m_Text);
                                        params.put(field_name[2], strIDUser);
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
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        imgHapusMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailMemberActivity.this);
                builder.setMessage(R.string.MS_HAPUS_SUCCESS)
                        .setCancelable(false)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (tvPointMemberDetail1.equals("0.0")) {
                                    final String[] field_name = {"id_member"};
                                    String base_url = apiData.get("str_url_address") + (".delete_member");
                                    StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("DEBUG", "Register Response: " + response);
                                            try {
                                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailMemberActivity.this);
                                                builder.setMessage(R.string.MS_HAPUS_SUCCESS)
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                Intent changePassword = new Intent(getApplicationContext(), MainActivity.class);
                                                                startActivity(changePassword);
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
                                            params.put(field_name[0], tvIdMemberDetail1);

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
                                } else {
                                    Toast.makeText(DetailMemberActivity.this, "Nominal Saldo Masih tersedia.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.e("tag", "sukses");
            }
        });
    }

    public void CreateAlertDialogWithRadioButtonGroup() {
        CharSequence[] values = {" Phone Call ", " Message "};

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailMemberActivity.this);

        builder.setTitle("Select Your Choice");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                String phoneNo = tvNoHpMemberDetail.getText().toString();
                String message = tvNoHpMemberDetail.getText().toString();
                switch (item) {
                    case 0:

                        if (!TextUtils.isEmpty(phoneNo)) {
                            String dial = "tel:" + phoneNo;
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                        } else {
                            Toast.makeText(DetailMemberActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));
                            smsIntent.putExtra("sms_body", message);
                            startActivity(smsIntent);
                        }
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }
}
