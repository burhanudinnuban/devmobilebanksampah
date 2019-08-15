package com.demo.user.banksampah.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.demo.user.banksampah.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;
    public ExpandableListAdapter(Context context, List<String> listHeader,
                                 HashMap<String, List<String>> listChildData){
        this._context = context;
        this._listDataHeader = listHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent){

        final String childText = (String)getChild(groupPosition, childPosition);
        DecimalFormat decimalFormat = new DecimalFormat(",###.##");

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_child_nama_harga_sampah, parent, false);
        }

        TextView tvJenisSampah = convertView.findViewById(R.id.child_nama_sampah);
        TextView tvHargaSampah = convertView.findViewById(R.id.child_harga_sampah);

        String[]parseData = childText.split(",");
        tvJenisSampah.setText(parseData[1]);
        try {
            tvHargaSampah.setText("Rp. " + decimalFormat.format(Double.valueOf(parseData[0])));
        }catch (NumberFormatException e){
            e.printStackTrace();
            Log.e("tag", e.toString());
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition){
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        String headerTitle = (String)getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_header_tipe_sampah, parent, false);
        }

        TextView tvTipeSampah = convertView.findViewById(R.id.header_tipe_sampah);
        tvTipeSampah.setTypeface(null, Typeface.BOLD);
        tvTipeSampah.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }
}
