package com.demo.user.banksampah.MemberFragment.ListMember;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.Pin.CheckSetPin;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Services.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailMemberActivity extends AppCompatActivity {
    //Session Class
    protected PrefManager session;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    protected String strIDUser;
    protected CustomProgress customProgress;

    protected LinearLayout parent_layout;
    protected ConnectivityManager conMgr;

    protected View rootView;
    protected LazyAdapter adapter;

    protected CardView cd_NoData, cvSaldo, cvTelepon, cvRiwayat, cvHapus;

    protected ImageView imgDetailMember, imgPencairan, imgHubungiMember, imgRiwayatOrder, imgHapusMember, arrowBack;
    protected TextView tvNamaMemberDetail, tvPointMemberDetail, tvIdMemberDetail, tvAlamatMemberDetail, tvNoHpMemberDetail, tvStatusMemberDetail, tvEmailMemberDetail;
    protected String url_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_member );
        session = new PrefManager( DetailMemberActivity.this );
        HashMap<String, String> user = session.getUserDetails();
        strIDUser = user.get( PrefManager.KEY_NAMA );

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        //        Deklarasi dari layout ke aktivity
        imgDetailMember = findViewById( R.id.imgPicture_DetailMember );
        imgPencairan = findViewById( R.id.imgPencairan );
        imgHubungiMember = findViewById( R.id.imgHubungiMember );
        imgHapusMember = findViewById( R.id.imgHapusMember );
        imgRiwayatOrder = findViewById( R.id.imgRiwayatOrder );
        tvNamaMemberDetail = findViewById( R.id.tvNamaMember_DetailMember );
        tvPointMemberDetail = findViewById( R.id.tvPointMember_DetailMember );
        tvIdMemberDetail = findViewById( R.id.tvIDMember_DetailMember );
        tvAlamatMemberDetail = findViewById( R.id.tvAlamatMember_DetailMember );
        tvNoHpMemberDetail = findViewById( R.id.tvNoHPMember_DetailMember );
        tvStatusMemberDetail = findViewById( R.id.tvStatusMember_DetailMember );
        tvEmailMemberDetail = findViewById( R.id.tvEmailMemberDetail );
        cvHapus = findViewById( R.id.cvHapus );
        cvRiwayat = findViewById( R.id.cvRiwayat );
        cvSaldo = findViewById( R.id.cvSaldo );
        cvTelepon = findViewById( R.id.cvTelepon );
        parent_layout = findViewById( R.id.parent );
//        arrowBack = findViewById( R.id.arrowBack );
//        arrowBack.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        } );
//        Deklarasi String ke Rest
        String imgDetailMember1 = getIntent().getStringExtra( "foto" );
        String tvNamaMemberDetail1 = getIntent().getStringExtra( "nama_member" );
        final String tvPointMemberDetail1 = getIntent().getStringExtra( "point" );
        final String tvIdMemberDetail1 = getIntent().getStringExtra( "id_member" );
        String tvAlamatMemberDetail1 = getIntent().getStringExtra( "alamat" );
        String tvNoHpMemberDetail1 = getIntent().getStringExtra( "no_telepon" );
        String tvStatusMemberDetail1 = getIntent().getStringExtra( "id" );
        String tvEmailMemberDetail1 = getIntent().getStringExtra( "email" );

        session.DetailMember( tvPointMemberDetail1, tvIdMemberDetail1 );

        tvNamaMemberDetail.setText( "" + tvNamaMemberDetail1 );
        tvPointMemberDetail.setText( "" + tvPointMemberDetail1 );
        tvIdMemberDetail.setText( "" + tvIdMemberDetail1 );
        tvAlamatMemberDetail.setText( "" + tvAlamatMemberDetail1 );
        tvNoHpMemberDetail.setText( "" + tvNoHpMemberDetail1 );
        tvStatusMemberDetail.setText( "" + tvStatusMemberDetail1 );
        tvEmailMemberDetail.setText( "" + tvEmailMemberDetail1 );

        Picasso.get().load( (imgDetailMember1) ).into( imgDetailMember );

        final ImageView image = new ImageView( this );

        url_foto = apiData.get( "str_url_main" );
        Picasso.get()
                .load( url_foto + imgDetailMember1 )
                .error( R.drawable.ic_navigation_profil )
                .into( imgDetailMember );

        cvRiwayat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listOrderUser = new Intent( DetailMemberActivity.this, ListOrderUser.class );
                listOrderUser.putExtra( "id_member", tvIdMemberDetail1 );
                startActivity( listOrderUser );
            }
        } );

        cvTelepon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlertDialogWithRadioButtonGroup();
            }
        } );

        cvSaldo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkpin = new Intent( DetailMemberActivity.this, CheckSetPin.class );
                startActivity( checkpin );
            }
        } );

        cvHapus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strIdMemberDetail = tvIdMemberDetail.getText().toString();
                hapusMember( strIdMemberDetail );
            }
        } );
    }

    public void hapusMember(String strIdMemberDetail) {
        String tvPointMemberDetail1 = getIntent().getStringExtra( "point" );
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( DetailMemberActivity.this );
        builder.setMessage( R.string.MS_HAPUS_SUCCESS )
                .setCancelable( false )
                .setTitle( "Konfirmasi Hapus" )
                .setMessage( "Apakah Anda yakin?" )
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (tvPointMemberDetail1.equals( "0.0" )) {
                            final String[] field_name = {"id_member"};

                            String base_url = apiData.get( "str_url_address" ) + (".delete_member");
                            StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d( "DEBUG", "Register Response: " + response );
                                    try {
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( DetailMemberActivity.this );
                                        builder.setMessage( R.string.MS_HAPUS_SUCCESS )
                                                .setCancelable( false )
                                                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent( DetailMemberActivity.this, MainActivity.class );
                                                        startActivity( intent );
                                                        finish();
                                                    }
                                                } );
                                        android.app.AlertDialog alert = builder.create();
                                        alert.show();
                                        Log.e( "tag", "sukses" );

                                    } catch (Throwable t) {
                                        Snackbar snackbar = Snackbar
                                                .make( parent_layout, getString( R.string.MSG_CODE_409 ) + " 1: " + getString( R.string.MSG_CHECK_DATA ), Snackbar.LENGTH_SHORT );
                                        snackbar.show();
                                        Log.d( "DEBUG", "Error Validate Change Password Response: " + t.toString() );
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d( "DEBUG", "Volley Error: " + error.getMessage() );
                                    Snackbar snackbar = Snackbar
                                            .make( parent_layout, getString( R.string.MSG_CODE_500 ) + " 1: " + getString( R.string.MSG_CHECK_CONN ), Snackbar.LENGTH_SHORT );
                                    snackbar.show();
                                }
                            } ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put( field_name[0], strIdMemberDetail );
                                    params.put( "id_bank_sampah", strIDUser );
                                    Log.d( "123", "onClick: " + strIdMemberDetail );
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
                        } else {
                            Toast.makeText( DetailMemberActivity.this, "Nominal Saldo Masih tersedia.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } )
                .setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } );
        android.app.AlertDialog alert = builder.create();
        alert.show();
        Log.e( "tag", "sukses" );
    }

    public void CreateAlertDialogWithRadioButtonGroup() {
        Dialog mydialog = new Dialog( DetailMemberActivity.this );
        CardView cvMessage, cvCall;
        TextView imgExit;
        mydialog.setContentView( R.layout.alert_call_n_message );
        cvMessage = mydialog.findViewById( R.id.cvMessage );
        cvCall = mydialog.findViewById( R.id.cvCall );
        imgExit = mydialog.findViewById( R.id.imgExit );

        cvCall.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = tvNoHpMemberDetail.getText().toString();
                if (!TextUtils.isEmpty( phoneNo )) {
                    String dial = "tel:" + phoneNo;
                    startActivity( new Intent( Intent.ACTION_DIAL, Uri.parse( dial ) ) );
                } else {
                    Toast.makeText( DetailMemberActivity.this, "Enter a phone number", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        cvMessage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = tvNoHpMemberDetail.getText().toString();
                String message = tvNoHpMemberDetail.getText().toString();
                if (!TextUtils.isEmpty( message ) && !TextUtils.isEmpty( phoneNo )) {
                    Intent smsIntent = new Intent( Intent.ACTION_SENDTO, Uri.parse( "smsto:" + phoneNo ) );
                    smsIntent.putExtra( "sms_body", message );
                    startActivity( smsIntent );
                }
            }
        } );

        imgExit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialog.dismiss();
            }
        } );

        if (mydialog.getWindow() != null) {
            mydialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            mydialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            mydialog.show();
        }
    }
}
