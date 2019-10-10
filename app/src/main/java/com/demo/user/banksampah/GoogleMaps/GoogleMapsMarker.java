package com.demo.user.banksampah.GoogleMaps;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.demo.user.banksampah.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GoogleMapsMarker extends AppCompatActivity implements OnMapReadyCallback {
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    GoogleMap googleMap1;
    PlacesClient placesClient;
    ImageView imgPlace;
    Button btnPilihLokasi;
    AutocompleteSupportFragment autocompleteFragment;
    LinearLayout auto;
    String nama_lokasi = null;
    String kecamatan = null;
    String kota = null;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_google_maps_marker );

        auto = findViewById( R.id.auto );
        imgPlace = findViewById( R.id.imgPlace );
        btnPilihLokasi = findViewById( R.id.btnPilihLokasi );

        btnPilihLokasi.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getLocation = getIntent();
                getLocation.putExtra("LatLong_Lokasi", latitude + "," + longitude);
                getLocation.putExtra("Alamat_Lokasi", nama_lokasi);
                setResult(RESULT_OK, getLocation);
                finish();
            }
        } );

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        String strActionLokasi = getIntent().getStringExtra( "lihat_lokasi" );

        if (strActionLokasi.equals( "lihat_lokasi" )){
            btnPilihLokasi.setVisibility( View.GONE );
            auto.setVisibility( View.GONE );
            imgPlace.setVisibility( View.GONE );
        }else if (strActionLokasi.equals( "pilih_lokasi" )){
            btnPilihLokasi.setVisibility( View.VISIBLE );
            auto.setVisibility( View.VISIBLE );
        }

        if (autocompleteFragment!=null){
            autocompleteFragment.setCountry( "ID" );
            autocompleteFragment.setTypeFilter( TypeFilter.GEOCODE );
            autocompleteFragment.setPlaceFields( Arrays.asList( Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG ) );
            autocompleteFragment.setOnPlaceSelectedListener( new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    googleMap1.clear();
                    googleMap1.animateCamera( CameraUpdateFactory.newLatLngZoom( place.getLatLng(),15 ) );
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText( GoogleMapsMarker.this, "error in autocomplete", Toast.LENGTH_LONG ).show();
                }
            } );
        }

        String apiKey = getString( R.string.google_maps_key );
        if (!Places.isInitialized()){
            Places.initialize( getApplicationContext(),apiKey );
            placesClient = Places.createClient( this );
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(  this );
    }

    public void onMapReady(GoogleMap googleMap) {
        googleMap1=googleMap;
        String strLatlong = getIntent().getStringExtra( "latlong" );
        Log.d( "tag", strLatlong );
        String[] arrStrLatlong = strLatlong.split( "," );
        double[] arrDbLatlong= new double[2];
        for (int i =0; i<arrStrLatlong.length;i++){
            arrDbLatlong[i]= Double.parseDouble( arrStrLatlong[i] );
        }
        LatLng latlng = new LatLng( arrDbLatlong[0],arrDbLatlong[1] );
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(latlng)
                .title("Marker in Sydney"));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 15  ), 2000, null );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));

        googleMap.setOnCameraIdleListener( new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                double lat = cameraPosition.target.latitude;
                double longth = cameraPosition.target.longitude;

                String latlong_koordinat = lat + "," +longth;
                String address_result;

                address_result = convertLocation(lat, longth);
                autocompleteFragment.setText( address_result );
            }
        } );
    }

    private String convertLocation(double lat,double longth){


        Geocoder geocoder = new Geocoder( GoogleMapsMarker.this, Locale.getDefault() );

        // note //
        //getSubLocality --> Get Data kelurahan
        //getSubAdminArea --> Get Data Kota
        //getLocality --> Get Data kecamatan

        try{
            List<Address>list = geocoder.getFromLocation( lat, longth,1 );
            if (list != null){
                if (list.size() > 0 ){
                    nama_lokasi = list.get( 0 ).getAddressLine( 0 );
                    kecamatan = list.get( 0 ).getLocality();
                    kota = list.get( 0 ).getSubAdminArea();
                    kota = kota.replaceAll( "kota", "" );
                }
            }else {
                Toast.makeText( GoogleMapsMarker.this, "Maaf alamat tidak ditemukan", Toast.LENGTH_LONG ).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nama_lokasi                                                                                                                                                                                                              ;
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i("TAG", status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }
}
