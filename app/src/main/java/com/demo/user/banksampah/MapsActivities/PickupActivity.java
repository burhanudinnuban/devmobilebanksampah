package com.demo.user.banksampah.MapsActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Firebase.Config;
import com.demo.user.banksampah.R;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class PickupActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RestProcess rest_class;
    private HashMap<String,String> data_pickup = new HashMap<>();

    private String order_id, latlong_penjemputan;

    protected GoogleMap googleMap;
    protected Button btnCancel_Pickup;
    protected BroadcastReceiver broadcastReceiver;

    protected RipplePulseLayout mRipplePulseLayout;
    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        btnCancel_Pickup = findViewById(R.id.btnCancel_Pickup);
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(PickupActivity.this);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            order_id = null;
            latlong_penjemputan = null;
        }else{
            order_id = extras.getString("EXTRA_ID_ORDER");
            latlong_penjemputan = extras.getString("EXTRA_LATLONG");

            Toast.makeText(this, order_id + "," + latlong_penjemputan, Toast.LENGTH_LONG).show();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getStringExtra("title");
                String message = intent.getStringExtra("message");

                if(intent.getAction().equalsIgnoreCase(Config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                }else if(intent.getAction().equalsIgnoreCase(Config.PUSH_NOTIFICATION)){
                    //Cek Jika Driver Accept Atau Cancel Orderan User....
                    //Jika di Accept maka akan Jalanin Intent.
                }else if(message != null && title != null){
                    //Taruh Log untuk Cek Data saja...
                }else{
                    //Taruh Log kalo FCM tidak terbentuk?
                }
            }
        };

        //Jika Cancel Jalanin Apa
        btnCancel_Pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PickupActivity.this, "TEst User Cancel", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PickupActivity.this, PickupDetailActivity.class);
                intent.putExtra("EXTRA_ORDER_ID", order_id);
                intent.putExtra("EXTRA_LATLONG", latlong_penjemputan);
                startActivity(intent);
            }
        });

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable, 1000);
    }

    private final Runnable m_Runnable = new Runnable() {
        @Override
        public void run() {
            PickupActivity.this.mHandler.postDelayed(m_Runnable, 7000);
            mRipplePulseLayout.startRippleAnimation();
        }
    };

    @Override
    public void onMapReady(GoogleMap mMap){
        googleMap = mMap;

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        String[]parse_latLong = latlong_penjemputan.split(",");
        double latitudes = Double.parseDouble(parse_latLong[0]);
        double longitudes = Double.parseDouble(parse_latLong[1]);

        LatLng fixedLocation = new LatLng(latitudes, longitudes);
        BitmapDescriptor icon_pin = BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_profil);

        googleMap.addMarker(new MarkerOptions().anchor(0.5f,0.5f).position(fixedLocation).icon(icon_pin));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 15));
    }
}
