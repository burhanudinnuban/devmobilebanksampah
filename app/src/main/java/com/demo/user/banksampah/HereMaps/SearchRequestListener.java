/*
package com.demo.user.erecycle_v2.HereMaps;

import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.search.DiscoveryLink;
import com.here.android.mpa.search.DiscoveryRequest;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;

public class SearchRequestListener implements ResultListener<DiscoveryResultPage> {

    @Override
    public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode){
        if (errorCode != ErrorCode.NONE){
            Log.e("tag class", errorCode.toString());
        }
        else{

        }
    }

    try{
        GeoCoordinate geoCoordinate = new GeoCoordinate();
        DiscoveryRequest discoveryRequest = new SearchRequest().setSearchCenter(geoCoordinate);
        discoveryRequest.setCollectionSize(10);

        ErrorCode code = discoveryRequest.execute(new SearchRequestListener());
        if (code != ErrorCode.NONE){

        }
    }catch(IllegalArgumentException e){
        e.printStackTrace();
    }
}
*/
