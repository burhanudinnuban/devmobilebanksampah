package com.demo.user.banksampah.HereMaps;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Activities.OpenMaps;
import com.demo.user.banksampah.R;
import com.here.android.mpa.search.Address;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.Place;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.PlaceRequest;
import com.here.android.mpa.search.ResultListener;

public class ResultListActivity extends ListActivity {

    protected LinearLayout placeDetailLayout;
    protected TextView tvPlaceName, tvPlaceLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        placeDetailLayout = findViewById(R.id.placeDetailLayout);
        placeDetailLayout.setVisibility(View.GONE);

        tvPlaceName = findViewById(R.id.placeName);
        tvPlaceLocation =  findViewById(R.id.placeLocation);

        Button closePlaceDetailButton = findViewById(R.id.closeLayoutButton);
        closePlaceDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeDetailLayout.getVisibility() == View.VISIBLE) {
                    placeDetailLayout.setVisibility(View.GONE);
                }
            }
        });

        ResultListAdapter listAdapter = new ResultListAdapter(this,
                android.R.layout.simple_list_item_1, OpenMaps.s_ResultList);
        setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DiscoveryResult result = OpenMaps.s_ResultList.get(position);
        if (result.getResultType() == DiscoveryResult.ResultType.PLACE) {
            /* Fire the PlaceRequest */
            PlaceLink placeLink = (PlaceLink) result;
            PlaceRequest placeRequest = placeLink.getDetailsRequest();
            placeRequest.execute(m_placeResultListener);
        } else if (result.getResultType() == DiscoveryResult.ResultType.DISCOVERY) {
            /*
             * Another DiscoveryRequest object can be obtained by calling DiscoveryLink.getRequest()
             */
            Toast.makeText(this, "This is a DiscoveryLink result", Toast.LENGTH_SHORT).show();
        }
    }

    private ResultListener<Place> m_placeResultListener = new ResultListener<Place>() {
        @Override
        public void onCompleted(Place place, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {
                /*
                 * No error returned,let's show the name and location of the place that just being
                 * selected.Additional place details info can be retrieved at this moment as well,
                 * please refer to the HERE Android SDK API doc for details.
                 */
                placeDetailLayout.setVisibility(View.VISIBLE);
                tvPlaceName.setText(place.getName());
                //GeoCoordinate geoCoordinate = place.getLocation().getCoordinate();
                Address geoCoordinate = place.getLocation().getAddress();
                tvPlaceLocation.setText(geoCoordinate.toString());
            } else {
                Toast.makeText(getApplicationContext(),
                        "ERROR:Place request returns error: " + errorCode, Toast.LENGTH_SHORT)
                        .show();
            }

        }
    };
}
