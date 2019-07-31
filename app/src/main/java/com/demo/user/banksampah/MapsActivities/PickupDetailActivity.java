package com.demo.user.banksampah.MapsActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Firebase.Config;
import com.demo.user.banksampah.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class PickupDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RestProcess rest_class;
    private HashMap<String,String> data_pickup = new HashMap<>();

    private String order_id, latlong_penjemputan;
    protected BroadcastReceiver broadcastReceiver;

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout linear_sheet;
    private Button btnTest;

    protected GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_detail);

        linear_sheet = findViewById(R.id.bottom_sheet_picker);
        sheetBehavior = BottomSheetBehavior.from(linear_sheet);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(PickupDetailActivity.this);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            order_id = null;
            latlong_penjemputan = null;
        }else{
            order_id = extras.getString("EXTRA_ORDER_ID");
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

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("tag", "HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("tag", "EXPAND");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("tag", "COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("tag", "DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("tag", "SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap mMap){
        googleMap = mMap;

        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
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
