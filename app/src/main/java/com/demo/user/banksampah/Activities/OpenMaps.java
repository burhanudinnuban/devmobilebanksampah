package com.demo.user.banksampah.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.TrackGPS;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;

import com.demo.user.banksampah.R;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.DiscoveryRequest;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.GeocodeResult;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here.android.mpa.search.SearchRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenMaps extends AppCompatActivity {
    private final static int REQUEST_PERMISSION = 1;
    private static final String[] RUNTIME_PERMISSION ={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Here Maps
    private Map map = null;
    protected SupportMapFragment mapFragment = null;
    protected MapMarker mapMarker;
    protected com.here.android.mpa.common.Image myImage;
    protected List<String> schemes;

    public static List<DiscoveryResult> s_ResultList;
    private List<MapObject> m_mapObjectList = new ArrayList<>();

    //Get Current Location
    protected String latlong_koordinat = "0,0";
    protected String nama_lokasi;
    protected TrackGPS gps;
    double longitude;
    double latitude;

    protected ImageView ivmarker;
    protected TextView tvSearchLocation;
    protected TextView etSearchLocation;
    protected Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_maps);

        ivmarker = findViewById(R.id.ivmarker);
        btnSave = findViewById(R.id.btnSaveLocation);
        etSearchLocation = findViewById(R.id.etSearchLocation);

        etSearchLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchAddress(etSearchLocation.getText().toString());
                    return true;
                }
                return false;
            }
        });

        if (hasPermissions(this, RUNTIME_PERMISSION)){
            //setupMapFragmentView();
            getCurrentLocation();
            initMapFragment();
        }else{
            ActivityCompat.requestPermissions(this, RUNTIME_PERMISSION, REQUEST_PERMISSION);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getLocation = getIntent();
                getLocation.putExtra("LatLong_Lokasi", latitude + "," + longitude);
                getLocation.putExtra("Alamat_Lokasi", nama_lokasi);
                setResult(RESULT_OK, getLocation);
                finish();
            }
        });

        ivmarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanMap();
                SearchRequest searchRequest = new SearchRequest(etSearchLocation.getText().toString());
                searchRequest.setSearchCenter(map.getCenter());
                searchRequest.execute(discoveryResultPageListener);
            }
        });

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                initMapFragment();
                getCurrentLocation();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private SupportMapFragment getMapFragment(){
        return (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void initMapFragment(){
        mapFragment = getMapFragment();

        if(mapFragment != null){
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        map = mapFragment.getMap();
                        schemes = map.getMapSchemes();
                        map.setMapScheme(schemes.get(0));
                        map.setCenter(new GeoCoordinate(latitude, longitude, 0.5f), Map.Animation.LINEAR);
                        map.setZoomLevel(16, Map.Animation.LINEAR);

                        //Add Marker Icon
                        addMarker_Now();
                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }

                    markerOnTouch();
                }


            });

        }
    }

    private ResultListener<DiscoveryResultPage> discoveryResultPageListener = new ResultListener<DiscoveryResultPage>() {
        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {
                s_ResultList = discoveryResultPage.getItems();
                for (DiscoveryResult item : s_ResultList) {
                    /*if(item.getResultType() == DiscoveryResult.ResultType.PLACE){
                        PlaceLink placeLink = (PlaceLink)s_ResultList;
                        PlaceRequest placeRequest = placeLink.getDetailsRequest();
                        placeRequest.execute()
                    }*/

                    if (item.getResultType() == DiscoveryResult.ResultType.DISCOVERY) {
                        PlaceLink placeLink = (PlaceLink) item;
                        addMarker(placeLink);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "ERROR:Discovery search request returned return error code+ " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void addMarker_Now(){
        myImage = new com.here.android.mpa.common.Image();
        try{
            myImage.setImageResource(R.drawable.ic_marker_maps);
        }catch (IOException e){
            e.printStackTrace();
        }

        mapMarker = new MapMarker();
        //mapMarker.setIcon(myImage);
        mapMarker.setCoordinate(new GeoCoordinate(latitude, longitude, 0.5f));

        map.addMapObject(mapMarker);
    }

    private void addMarker(PlaceLink placeLink){
        myImage = new com.here.android.mpa.common.Image();
        try{
            myImage.setImageResource(R.drawable.ic_marker_maps);
        }catch (IOException e){
            e.printStackTrace();
        }
        mapMarker = new MapMarker();
        //mapMarker.setIcon(myImage);
        if (placeLink!=null){
            mapMarker.setCoordinate(new GeoCoordinate(placeLink.getPosition()));
        }else {
            mapMarker.setCoordinate(new GeoCoordinate(latitude, longitude, 0.5f));
        }
        map.addMapObject(mapMarker);
    }

    public void searchAddress(String address){
        if (mapMarker != null){
            map.removeMapObject(mapMarker);
        }
        GeoCoordinate geoCoordinate = new GeoCoordinate(latitude, longitude);
        GeocodeRequest request = new GeocodeRequest(address).setSearchArea(geoCoordinate, 5000);
        request.execute(new ResultListener<List<GeocodeResult>>() {
            @Override
            public void onCompleted(List<GeocodeResult> geocodeResults, ErrorCode errorCode) {
                if(errorCode != ErrorCode.NONE){
                    Log.e("tag", errorCode.toString());
                }else{
                    for (GeocodeResult result : geocodeResults){
                        mapMarker.setCoordinate(new GeoCoordinate(result.getLocation().getCoordinate().getLatitude(),
                                result.getLocation().getCoordinate().getLongitude(), 3.5f));
                        map.addMapObject(mapMarker);
                        map.setCenter(new GeoCoordinate(result.getLocation().getCoordinate().getLatitude(),
                                result.getLocation().getCoordinate().getLongitude(), 0.5f), Map.Animation.LINEAR);
                        map.setZoomLevel(16, Map.Animation.LINEAR);
                    }
                }
            }
        });
    }

    public void markerOnTouch(){
        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
            @Override
            public boolean onTapEvent(PointF pointF) {
                etSearchLocation.setText("");

                /*ArrayList<ViewObject> viewObjectArrayList = (ArrayList<ViewObject>)map.getSelectedObjects(pointF);
                for (ViewObject viewObject : viewObjectArrayList){
                    if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT){
                        MapObject mapObject = (MapObject)viewObject;
                        if (mapObject.getType() == MapObject.Type.MARKER){
                            MapMarker selectedMarker = ((MapMarker) mapObject);
                            dropMarker(selectedMarker.getCoordinate());
                            reverseGeocode(selectedMarker.getCoordinate());
                            Log.e("tag", selectedMarker.getCoordinate().toString());
                        }
                    }
                }*/

                GeoCoordinate position = map.pixelToGeo(pointF);
                dropMarker(position);
                reverseGeocode(position);
                return false;
            }

            @Override
            public boolean onLongPressEvent(PointF pointF) {
                GeoCoordinate position = map.pixelToGeo(pointF);
                dropMarker(position);
                reverseGeocode(position);

                return false;
            }
        });
    }

    public void reverseGeocode(final GeoCoordinate point){
        ReverseGeocodeRequest request = new ReverseGeocodeRequest(point);
        request.execute(new ResultListener<Address>() {
            @Override
            public void onCompleted(Address address, ErrorCode errorCode) {
                if (errorCode != ErrorCode.NONE){
                    Log.e("tag", errorCode.toString());
                }else{
                    Toast.makeText(OpenMaps.this, address.getText() + "\n" + point.toString(), Toast.LENGTH_SHORT).show();
                    nama_lokasi = address.getText();
                    etSearchLocation.setText(nama_lokasi);
                    latitude = point.getLatitude();
                    longitude = point.getLongitude();
                }
            }
        });
    }

    public void dropMarker(GeoCoordinate position){
        if (mapMarker!=null){
            map.removeMapObject(mapMarker);
        }
        mapMarker = new MapMarker();
       // mapMarker.setIcon(myImage);
        mapMarker.setCoordinate(position);
        mapMarker.setTitle("Selected Location");
        mapMarker.showInfoBubble();
        map.addMapObject(mapMarker);
    }

    public void Search() {
        class SearchRequestListener implements ResultListener<DiscoveryResultPage>{
            @Override
            public void onCompleted(DiscoveryResultPage data, ErrorCode errorCode){
                if (errorCode != ErrorCode.NONE){
                    Toast.makeText(OpenMaps.this, "Terjadi Masalah", Toast.LENGTH_SHORT).show();
                }else{

                }
            }
        }

        try{
            GeoCoordinate findSearch = new GeoCoordinate(latitude, longitude);
            DiscoveryRequest request = new SearchRequest("restaurant").setSearchCenter(findSearch);
            request.setCollectionSize(10);

            ErrorCode errorCode = request.execute(new SearchRequestListener());
            if (errorCode != ErrorCode.NONE){
                Toast.makeText(OpenMaps.this, "Terjadi Masalah 2", Toast.LENGTH_SHORT).show();
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void cleanMap(){
        if(!m_mapObjectList.isEmpty()){
            map.removeMapObjects(m_mapObjectList);
            m_mapObjectList.clear();
        }
    }

    public void getCurrentLocation(){
        gps = new TrackGPS(getApplicationContext());
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps .getLatitude();
            latlong_koordinat = latitude + "," + longitude;
        }else{
            gps.showSettingsAlert();
        }
    }
}
