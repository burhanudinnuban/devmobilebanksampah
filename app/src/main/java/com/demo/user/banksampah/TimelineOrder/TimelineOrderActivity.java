package com.demo.user.banksampah.TimelineOrder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TimelineOrderActivity extends AppCompatActivity {

    protected ListView lvTimeline_Order;
    protected TextView tvTimeline_OrderID;
    protected ArrayList<TimelineRow> timelineRowArrayList;
    protected TimelineRow timelineRow;
    protected ArrayAdapter<TimelineRow> myAdapter;

    protected String getOrderID, getStatus, getAlamat, getLatlong, getNamaPicker,
            getNoKendaraan, getFotoPicker, getTipeKendaraan;

    protected Dialog myDialog;
    protected HashMap<String, String> apiData;
    private RestProcess rest_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_order);

        Intent intent = getIntent();
        if (intent != null){
            getOrderID = intent.getStringExtra("EXTRA_ORDER_ID");
            getStatus = intent.getStringExtra("EXTRA_ORDER_STATUS");
            getAlamat = intent.getStringExtra("EXTRA_ORDER_ALAMAT");
            //Detail Picker
            getLatlong = intent.getStringExtra("EXTRA_ORDER_LATLONG");
            getNamaPicker = intent.getStringExtra("EXTRA_ORDER_NAMA_PICKER");
            getNoKendaraan = intent.getStringExtra("EXTRA_ORDER_NO_KENDARAAN");
            getFotoPicker = intent.getStringExtra("EXTRA_ORDER_FOTO_PICKER");
            getTipeKendaraan = intent.getStringExtra("EXTRA_ORDER_TIPE_KENDARAAN");
        }

        myDialog = new Dialog(TimelineOrderActivity.this);
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        lvTimeline_Order = findViewById(R.id.lvTimeline_Order);
        tvTimeline_OrderID = findViewById(R.id.tvTimeline_OrderID);
        tvTimeline_OrderID.setText(getOrderID);

        timelineRowArrayList = new ArrayList<>();
        for(int i =0; i<=3; i++){
            timelineRowArrayList.add(createTimelineRow(i));
        }

        myAdapter = new TimelineViewAdapter(this, 0, timelineRowArrayList, true){
            /*@Override
            public boolean isEnabled(int position){
                try{
                    return true;
                }catch (ArrayIndexOutOfBoundsException e){
                    Log.e("tag", e.toString());
                }
                return true;
            }*/
        };
        lvTimeline_Order.setAdapter(myAdapter);

        lvTimeline_Order.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout layoutBelow;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted(){
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                    && this.currentScrollState == SCROLL_STATE_IDLE){

                    /*for(int i = 0; i < 4; i++){
                        myAdapter.add(createTimelineRow(i));
                    }*/

                }
            }
        });

        AdapterView.OnItemClickListener adapterListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimelineRow row = timelineRowArrayList.get(position);
                //Toast.makeText(getApplicationContext(), row.getTitle() + "," + id, Toast.LENGTH_SHORT).show();

                if(getStatus.equalsIgnoreCase("On Process") && position == 2){
                    Toast.makeText(TimelineOrderActivity.this, "Tampil Dialog", Toast.LENGTH_SHORT).show();
                    showPopUpPickerDetail();
                }
            }
        };

        lvTimeline_Order.setOnItemClickListener(adapterListener);
    }

    private TimelineRow createTimelineRow(int id){
        timelineRow = new TimelineRow(id);

        if (id == 3) {
            timelineRow.setDate(new Date());
            timelineRow.setTitle("Order Masuk");
            timelineRow.setDescription("Kami telah menerima pesanan Anda");
            timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_order_insert));
            timelineRow.setBellowLineColor(Color.BLUE);
            timelineRow.setBellowLineSize(3);
            timelineRow.setImageSize(50);

            if (getStatus.equalsIgnoreCase("Ready To Pickup")) {
                timelineRow.setTitleColor(Color.BLUE);
                timelineRow.setDescriptionColor(Color.BLUE);
            }

        } else if (id == 2) {
            timelineRow.setDate(new Date());
            timelineRow.setTitle("Order Terkonfirmasi");
            timelineRow.setDescription("Pesanan Anda sedang diproses");
            timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_order_list));
            timelineRow.setBellowLineColor(Color.BLUE);
            timelineRow.setBellowLineSize(3);
            timelineRow.setImageSize(50);

            if (getStatus.equalsIgnoreCase("On Process")){
                timelineRow.setTitleColor(Color.BLUE);
                timelineRow.setDescriptionColor(Color.BLUE);
            }

        } else if(id == 1){
            timelineRow.setDate(new Date());
            timelineRow.setTitle("Jemput Order");
            timelineRow.setDescription("Pesanan Anda sedang dijemput oleh Petugas Kami");
            timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_order_pickup));
            timelineRow.setBellowLineColor(Color.BLUE);
            timelineRow.setBellowLineSize(3);
            timelineRow.setImageSize(50);
        } else if(id == 0){
            timelineRow.setDate(new Date());
            timelineRow.setTitle("Order Selesai");
            timelineRow.setDescription("Pesanan Anda telah diambil oleh Petugas Kami");
            timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ic_order_finish));
            timelineRow.setBellowLineColor(Color.BLUE);
            timelineRow.setBellowLineSize(3);
            timelineRow.setImageSize(50);
        }

        return timelineRow;
    }

    private void showPopUpPickerDetail(){
        myDialog.setContentView(R.layout.pop_up_driver_details);
        myDialog.setCanceledOnTouchOutside(false);

        //TextView tvOrderID = myDialog.findViewById(R.id.tvOrderID_PopUp);
        //TextView tvDatetime = myDialog.findViewById(R.id.tvDatetime_PopUp);
        TextView tvDriverName = myDialog.findViewById(R.id.tvName_PopUp);
        TextView tvTipeKendaraan = myDialog.findViewById(R.id.tvTipeKendaraan_PopUp);
        TextView tvNopol = myDialog.findViewById(R.id.tvNopol_PopUp);

        ImageView imgDriverPhoto = myDialog.findViewById(R.id.imgPictureDriver_PopUp);
        ImageView imgClose = myDialog.findViewById(R.id.imgClose);

        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat getDay = new SimpleDateFormat("EEEE", Locale.getDefault());

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        String url = apiData.get("str_url_main") + getFotoPicker;
        Picasso.get()
                .load(url)
                .error(R.drawable.ic_no_data)
                .fit()
                .centerInside()
                .into(imgDriverPhoto);

        // tvTime.setText(waktu_penjemputan);
        //tvOrderID.setText("Jadwal Penjemputan " + id_order);
        tvDriverName.setText("Nama : " + getNamaPicker);
        tvTipeKendaraan.setText("Tipe Kendaraan : " + getTipeKendaraan);
        tvNopol.setText("No Kendaraan : " + getNoKendaraan);

        if(myDialog.getWindow()!= null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }

    }
}
