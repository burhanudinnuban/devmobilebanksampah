package com.demo.user.banksampah.HereMaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.demo.user.banksampah.R;
import com.here.android.mpa.search.DiscoveryResult;

import java.util.List;

public class ResultListAdapter extends ArrayAdapter<DiscoveryResult> {

    private List<DiscoveryResult> m_discoveryResultList;

    public ResultListAdapter(Context context, int resource, List<DiscoveryResult> results) {
        super(context, resource, results);
        m_discoveryResultList = results;
    }

    @Override
    public int getCount() {
        return m_discoveryResultList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DiscoveryResult discoveryResult = m_discoveryResultList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_list_item,
                    parent, false);
        }

        /*
         * Display title and vicinity information of each result.Please refer to HERE Android SDK
         * API doc for all supported APIs.
         */
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        tv.setText(discoveryResult.getTitle());

        tv = (TextView) convertView.findViewById(R.id.vicinity);
        tv.setText(String.format("Vicinity: %s", discoveryResult.getVicinity()));
        return convertView;
    }
}
