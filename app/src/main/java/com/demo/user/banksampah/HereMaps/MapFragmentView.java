/*
package com.demo.user.erecycle_v2.HereMaps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.demo.user.erecycle_v2.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.search.Category;
import com.here.android.mpa.search.CategoryFilter;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ExploreRequest;
import com.here.android.mpa.search.HereRequest;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MapFragmentView {

    public static List<DiscoveryResult> s_ResultList;
    private SupportMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    private Button m_placeDetailButton;
    private List<MapObject> m_mapObjectList = new ArrayList<>();

    public MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        */
/*
         * The map fragment is not required for executing search requests. However in this example,
         * we will put some markers on the map to visualize the location of the search results.
         *//*

        initMapFragment();
        initSearchControlButtons();
        */
/* We use a list view to present the search results *//*

        initResultListButton();
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void initMapFragment() {
        */
/* Locate the mapFragment UI element *//*

        m_mapFragment = getMapFragment();

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (m_mapFragment != null) {
                */
/* Initialize the SupportMapFragment, results will be given via the called back. *//*

                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                        if (error == Error.NONE) {
                            m_map = m_mapFragment.getMap();
                            m_map.setCenter(new GeoCoordinate(49.259149, -123.008555),
                                    Map.Animation.NONE);
                            m_map.setZoomLevel(13.2);
                        } else {
                            Toast.makeText(m_activity,
                                    "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


}
*/
