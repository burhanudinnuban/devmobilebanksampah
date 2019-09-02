package com.demo.user.banksampah.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Activities.UpdateListItem;
import com.demo.user.banksampah.DataPengurus.EditPengurus;
import com.demo.user.banksampah.MemberFragment.ListMember.DetailMemberActivity;
import com.demo.user.banksampah.MemberFragment.RequestMember.DetailRequestMember;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.TimelineOrder.TimelineOrderActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.demo.user.banksampah.Adapter.VolleyController.TAG;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    protected ArrayList<HashMap<String, String>> filter_data;
    private static LayoutInflater inflater = null;
    //public ImageLoader imageLoader;
    //private ItemFilter mFilter = new ItemFilter();
    private int fragment_position;
    //private int lv_pos = 0;
    protected int lv_pos_old, lv_pos_current;

    //private String ID_OrderLine, ID_IncomingOrder;

    /*protected StringBuilder sb = new StringBuilder();
    protected Formatter formatter = new Formatter(sb, Locale.getDefault());*/

    protected DecimalFormat decimalFormat, decimalFormat_Point;
    protected SimpleDateFormat inputDate, outputDate, getDay;

    //public float total_points = 0;

    //public String spinText;
    //public String spinOutletName, spinOutletId;

    protected String PACKAGE_NAME;
    //private final HashMap<String, String> outlet = new HashMap<String, String>();
    //protected ListView mListView;

    /*API process and dialog*/
    private RestProcess rest_class;
    protected HashMap<String, String> apiData;

    //private HashMap<String, String> sysMsg = new HashMap<>();

    //Cek Data
    protected ArrayList<HashMap<String, String>> arrDeleteOrder = new ArrayList<>();
    //private String getMessage_Result = null;

    //Untuk Cek Order Detail List
    protected TextView tvOrderID, tvStatusOrder, tvTotalKg, tvTotalPoints, tvAlamat;

    //Untuk Cek Order Get Member
    protected TextView tvOrderIDD, tvStatusOrderr, tvTotalKgg, tvTotalPointss;

    //Untuk Edit Data Get Member
    protected TextView tvJenis_sampah, tvBerat_item, tvPointsss;

    //private String strOrderID, strIDOrder_List;
    //private String strIDOrder_List;
    private int Status_PickupTime;
    private ListView lvListDetailOrder;
    private Dialog myDialog, DialogOrderHistory;
    Context ctx;
    /*private static class OutletHolder {
        public TextView outlet_name;
        public TextView outlet_phone;
    }*/
    ArrayList<HashMap<String, String>> allOrder = new ArrayList<>();
    ListView lvListDetailOrderUser;
    protected LazyAdapter adapter;
    protected TextView tvNamaOrderUser, tvBerat, tvPoint;
    protected ImageView imgCancel;
    protected String strNo_Telepon, strIdUser, strNamaBankSampah;
    protected PrefManager session;
    private ListView lvListDetailOrderr;
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d, int fragment_pos) {

        this.ctx = ctx;
        View vi;
        activity = a;
        filter_data = d;
        data = d;
        fragment_position = fragment_pos;
        inflater = (LayoutInflater) activity.getSystemService( LAYOUT_INFLATER_SERVICE );
        PACKAGE_NAME = activity.getPackageName();
        rest_class = new RestProcess();
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get( position );
    }

    public long getItemId(int position) {
        return position;
    }

    //Testing Holder
    static class ViewHolder {
        //Untuk Fragment Position 1 dan 2
        TextView tvJenisSampah;
        ImageView imgJenisSampah;

        //Untuk Fragment Position 3
        TextView tvJenisSampah_Incoming;
        TextView tvBeratSampah_Incoming;
        TextView tvPoints_Incoming;
        TextView tvTanggal_Incoming;
        ImageView imgDelete_Incoming;

        //Untuk Fragment Position 4
        TextView tvJenisSampah_Summary;
        TextView tvJumlahKg_Summary;
        TextView tvJumlahPoints_Summary;
        TextView tvParentID_Summary;

        //Untuk Fragment Position 5
        TextView tvOrderID_List;
        TextView tvStatusOrder_List;
        TextView tvTotalKg_List;
        TextView tvTotalPoints_List;
        TextView tvAlamat_List;
        ImageView imgFotoSampah_List;
        ImageView imgViewDetail;
        ImageView imgArrowDropDown;
        ImageView imgArrowUp;
        Button btnDetail_List;
        Button btnTelusuri_List;
        Button btnHide_List;
        Button btnCancelOrder_List;
        CardView cdDetail;
        CardView cdHide;
        TableRow tblAlamat_List;
        TableRow tblFoto_List;

        //Untuk Fragment Position 6
        TextView tvOrderID_Confirm;
        TextView tvHari_Confirm;
        TextView tvTanggal_Confirm;
        TextView tvJam_Confirm;
        TextView tvStatus_Confirm;
        Button btnYes_Confirm;
        Button btnNo_Confirm;
        TableRow tblButton_Confirm;

        //Untuk Fragment Position 7
        TextView tvOrderID_Picker;
        TextView tvName_Picker;
        TextView tvTipeTruck_Picker;
        TextView tvNopol_Picker;
        TextView tvDetail_Picker;

        //Untuk Fragment Position 8
        TextView tvPoints_Get;
        TextView tvIDOrder_Get;

        //Untuk Fragment Position 9
        ImageView imgPhotoMember;
        TextView tvNamaMember;
        TextView tvIdMember;
        TextView tvPointMember;
        Button btnDetailListMember;
        protected PrefManager session;
        Context ctx;

        //Untuk Fragment Position 10
        Button btnDetailReqMember;
        ImageView imgPictureReqMember;
        TextView tvTanggalReqMember, tvNamaMemberReq, tvIdMemberReq;

        //Untuk Fragment Posistion 11
        TextView tvOrderIDD_List;
        TextView tvStatusOrderr_List;
        TextView tvTotalKgg_List;
        TextView tvTotalPointss_List;
        ImageView imgViewwDetail;
        TextView tvAlamatt_List;
        ImageView imgFotoSampahh_List;
        ImageView imgViewDetaill;
        ImageView imgArrowwDropDown;
        ImageView imgArrowwUp;
        Button btnDetaill_List;
        Button btnEditData;
        //        Button btnTelusuri_List;
        Button btnHidee_List;
        Button btnCancelOrderr_List;
        Button btnAcceptOrderr_List;
        CardView cdDetaill;
        CardView cdHidee;
        TableRow tblAlamatt_List;
        TableRow tblFotoo_List;

        //Untuk Fragment Position 12
        TextView tvJenisItem, tvHargaItem, tvDetailItem;
        ImageView imgEditItem, imgHapusItem;

        //Untuk Fragment Position 13
        TextView tvNamaPengurus, tvJabatan, tvIdPengurus;
        ImageView btnHapusPengurus, btnEditPengurus;

        //Untuk Fragment Position 14
        TextView tvTanggalOrderUser, tvTotalBerat, tvTotalPoint;
        ImageView imgDetailView;
        FrameLayout flParent;


        //Untuk Fragment Position 15
        TextView tvNamaOrderUser, tvBerat, tvPoint;
        ImageView imgCancel;
        ArrayList<HashMap<String, String>> allOrder1 = new ArrayList<>();


    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        lv_pos_old = position;
        lv_pos_current = position;

        inputDate = new SimpleDateFormat( "yyyy-MM-dd", Locale.getDefault() );
        outputDate = new SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() );
        getDay = new SimpleDateFormat( "EEEE", Locale.getDefault() );

        Date convertDate;

        //decimalFormat = new DecimalFormat("0.##");
        decimalFormat = new DecimalFormat( ",###.##" );
        decimalFormat_Point = new DecimalFormat( ",###" );
        session = new PrefManager( activity );
        HashMap<String, String> user = session.getUserDetails();
        strNo_Telepon = user.get( PrefManager.KEY_NO_HP );
        strIdUser = user.get( PrefManager.KEY_ID );
        strNamaBankSampah = user.get( PrefManager.KEY_NAMA );
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        //if (1 == 1) switch (fragment_position) {
        switch (fragment_position) {
            case 1:
                //Jika ConvertView null, maka Inflate layout baru..
                //Jika ConvertView not null, maka Layout bisa dipakai ulang
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_trash_list, parent, false );

                    //Buat ViewHolder dan Simpan data Views
                    holder = new ViewHolder();
                    holder.tvJenisSampah = vi.findViewById( R.id.tvRouteName );
                    holder.imgJenisSampah = vi.findViewById( R.id.imageView_Sampah );
                    vi.setTag( holder );

                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> routeList;
                routeList = data.get( position );

                holder.tvJenisSampah.setText( routeList.get( "name" ) );
                String url = apiData.get( "str_url_main" ) + routeList.get( "image" );
                Picasso.get()
                        .load( url )
                        .error( R.drawable.ic_no_data )
                        .fit()
                        .centerInside()
                        .into( holder.imgJenisSampah );
                break;

            case 2:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.grid_adapter, parent, false );

                    holder = new ViewHolder();
                    holder.tvJenisSampah = vi.findViewById( R.id.tv_detailSampah );
                    holder.imgJenisSampah = vi.findViewById( R.id.imageView_detailSampah );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> sampahList;
                sampahList = data.get( position );

                holder.tvJenisSampah.setText( sampahList.get( "name" ) );
                String url_image = apiData.get( "str_url_main" ) + sampahList.get( "image" );
                Picasso.get()
                        .load( url_image )
                        .error( R.drawable.ic_no_data )
                        .fit()
                        .centerInside()
                        .into( holder.imgJenisSampah );
                break;

            case 3:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_incoming_order_data, parent, false );

                    myDialog = new Dialog( activity );
                    holder = new ViewHolder();

                    holder.tvJenisSampah_Incoming = vi.findViewById( R.id.tvJenisSampah_IncomingOrder );
                    holder.tvBeratSampah_Incoming = vi.findViewById( R.id.tvBeratSampah_IncomingOrder );
                    holder.tvPoints_Incoming = vi.findViewById( R.id.tvPoints_IncomingOrder );
                    holder.tvTanggal_Incoming = vi.findViewById( R.id.tvTanggal_IncomingOrder );
                    holder.imgDelete_Incoming = vi.findViewById( R.id.imgDelete_IncomingOrder );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> OrderList;
                OrderList = data.get( position );

                String strJenisSampah_Order = OrderList.get( "jenis_sampah" );
                String strJumlahKg_Order = OrderList.get( "jumlah_kg" );
                String strPoints_Order = OrderList.get( "point" );

                holder.tvJenisSampah_Incoming.setText( strJenisSampah_Order );
                try {
                    holder.tvBeratSampah_Incoming.setText( decimalFormat.format( Double.valueOf( strJumlahKg_Order ) ) + " Kg" );
                    holder.tvPoints_Incoming.setText( decimalFormat.format( Double.valueOf( strPoints_Order ) ) + " Pts" );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                holder.tvTanggal_Incoming.setText( OrderList.get( "created_date" ) );
                holder.imgDelete_Incoming.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderLineID = data.get( position );
                        String ID_OrderLine = SelectedOrderLineID.get( "name" );
                        String ID_IncomingOrder = SelectedOrderLineID.get( "parent" );
                        showPopupDeleteIncomingOrderLine( ID_IncomingOrder, ID_OrderLine, position );
                    }
                } );

                break;

            case 4:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_summary_order, parent, false );

                    holder = new ViewHolder();
                    holder.tvJenisSampah_Summary = vi.findViewById( R.id.tvJenisSampah_Summary );
                    holder.tvJumlahKg_Summary = vi.findViewById( R.id.tvBeratSampah_Summary );
                    holder.tvJumlahPoints_Summary = vi.findViewById( R.id.tvPoints_Summary );
                    holder.tvParentID_Summary = vi.findViewById( R.id.tvParentID_Summary );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> SummaryList;
                SummaryList = data.get( position );

                String strJenisSampah_Summary = SummaryList.get( "jenis_sampah" );
                String strJumlahKg_Summary = SummaryList.get( "jumlah_kg" );
                String strJumlahPoint_Summary = SummaryList.get( "point" );
                String strParentID_Summary = SummaryList.get( "parent" );

                holder.tvJenisSampah_Summary.setText( strJenisSampah_Summary );
                holder.tvParentID_Summary.setText( strParentID_Summary );
                try {
                    holder.tvJumlahKg_Summary.setText( decimalFormat.format( Double.valueOf( strJumlahKg_Summary ) ) + " Kg" );
                    holder.tvJumlahPoints_Summary.setText( decimalFormat.format( Double.valueOf( strJumlahPoint_Summary ) ) + " Pts" );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                break;

            case 5:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_member_list, parent, false );

                    myDialog = new Dialog( activity );

                    holder = new ViewHolder();
                    holder.tvTotalKg_List = vi.findViewById( R.id.tvTotalKg );
                    holder.tvTotalPoints_List = vi.findViewById( R.id.tvTotalPoints );
                    holder.tvAlamat_List = vi.findViewById( R.id.tvAlamat_List );
                    holder.imgFotoSampah_List = vi.findViewById( R.id.imgViewSampah_List );

                    holder.tblAlamat_List = vi.findViewById( R.id.tblLokasi_List );
                    holder.tblFoto_List = vi.findViewById( R.id.tblFoto_List );

                    holder.imgViewDetail = vi.findViewById( R.id.imgViewDetail_List );
                    holder.imgArrowDropDown = vi.findViewById( R.id.imgArrowDown_List );
                    holder.imgArrowUp = vi.findViewById( R.id.imgArrowUp_List );

                    holder.btnCancelOrder_List = vi.findViewById( R.id.btnCancel_Order_List );

                    holder.cdDetail = vi.findViewById( R.id.cdBtnDetail );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> ListDataOrder;
                ListDataOrder = data.get( position );

                final String strListOrderID = ListDataOrder.get( "name" );
                final String strStatusOrder = (ListDataOrder.get( "order_status" ));
                final String strTotalKg = (ListDataOrder.get( "berat_total" ));
                final String strTotalPoints = (ListDataOrder.get( "total_point" ));
                final String strImage = ListDataOrder.get( "image" );
                final String strAlamat = ListDataOrder.get( "alamat" );
                final String strLatlong = ListDataOrder.get( "latlong" );

                String strNamaPicker = null;
                String strNoKendaraan = null;
                String strFotoPicker = null;
                String strTipeKendaraan = null;

                //Data Picker
                if (strStatusOrder != null && strStatusOrder.equalsIgnoreCase( "On Process" )) {
                    strNamaPicker = ListDataOrder.get( "nama_picker" );
                    strNoKendaraan = ListDataOrder.get( "no_kendaraan" );
                    strFotoPicker = ListDataOrder.get( "foto_picker" );
                    strTipeKendaraan = ListDataOrder.get( "tipe_kendaraan" );
                }

                final String namaPicker = strNamaPicker;
                final String noKendaraan = strNoKendaraan;
                final String fotoPicker = strFotoPicker;
                final String tipeKendaraan = strTipeKendaraan;

                holder.tvOrderID_List.setText( strListOrderID );

                holder.btnTelusuri_List.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent a = new Intent( activity, TimelineOrderActivity.class );
                        Bundle bundle = new Bundle();
                        bundle.putString( "EXTRA_ORDER_ID", strListOrderID );
                        bundle.putString( "EXTRA_ORDER_STATUS", strStatusOrder );
                        bundle.putString( "EXTRA_ORDER_ALAMAT", strAlamat );
                        //Detail Picker
                        if (strStatusOrder != null && strStatusOrder.equalsIgnoreCase( "On Process" )) {
                            bundle.putString( "EXTRA_ORDER_LATLONG", strLatlong );
                            bundle.putString( "EXTRA_ORDER_NAMA_PICKER", namaPicker );
                            bundle.putString( "EXTRA_ORDER_NO_KENDARAAN", noKendaraan );
                            bundle.putString( "EXTRA_ORDER_FOTO_PICKER", fotoPicker );
                            bundle.putString( "EXTRA_ORDER_TIPE_KENDARAAN", tipeKendaraan );
                        }
                        a.putExtras( bundle );
                        activity.startActivity( a );
                    }
                } );

                holder.tvStatusOrder_List.setText( strStatusOrder );
                if (strStatusOrder != null) {
                    if (strStatusOrder.equalsIgnoreCase( "Cancel" )) {
                        holder.tvStatusOrder_List.setTextColor( Color.RED );
                    }
                }

                try {
                    holder.tvTotalKg_List.setText( decimalFormat.format( Double.valueOf( strTotalKg ) ) + " Kg" );
                    holder.tvTotalPoints_List.setText( decimalFormat_Point.format( Double.valueOf( strTotalPoints ) ) + " Pts" );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                holder.tvAlamat_List.setText( strAlamat );
                String url_foto = apiData.get( "str_url_main" ) + strImage;
                Picasso.get()
                        .load( url_foto )
                        .error( R.drawable.ic_no_data )
                        .into( holder.imgFotoSampah_List );

                holder.imgViewDetail.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderID = data.get( position );
                        String strListOrderID = SelectedOrderID.get( "name" );
                        showPopUpDetail( strListOrderID );
                    }
                } );

                holder.btnDetail_List.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cdDetail.setVisibility( View.GONE );
                        holder.cdHide.setVisibility( View.VISIBLE );

                        holder.tblAlamat_List.setVisibility( View.VISIBLE );
                        holder.tblFoto_List.setVisibility( View.VISIBLE );

                        if (strStatusOrder != null) {
                            if (strStatusOrder.equalsIgnoreCase( "Ready To Pickup" )) {
                                holder.btnCancelOrder_List.setVisibility( View.VISIBLE );
                            } else {
                                holder.btnCancelOrder_List.setVisibility( View.GONE );
                            }
                        }
                    }
                } );

                holder.btnHide_List.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cdDetail.setVisibility( View.VISIBLE );
                        holder.imgArrowDropDown.setVisibility( View.VISIBLE );
                        holder.btnCancelOrder_List.setVisibility( View.GONE );

                        holder.cdHide.setVisibility( View.GONE );
                        holder.tblAlamat_List.setVisibility( View.GONE );
                        holder.tblFoto_List.setVisibility( View.GONE );
                        holder.imgFotoSampah_List.setVisibility( View.GONE );
                        holder.imgArrowUp.setVisibility( View.GONE );
                    }
                } );

                holder.btnCancelOrder_List.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderID = data.get( position );
                        String strOrderID = SelectedOrderID.get( "name" );
                        showPopupCancelOrder( strOrderID, position );
                    }
                } );

                holder.imgArrowDropDown.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgFotoSampah_List.setVisibility( View.VISIBLE );
                        holder.imgArrowUp.setVisibility( View.VISIBLE );
                        holder.imgArrowDropDown.setVisibility( View.GONE );
                    }
                } );

                holder.imgArrowUp.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgFotoSampah_List.setVisibility( View.GONE );
                        holder.imgArrowUp.setVisibility( View.GONE );
                        holder.imgArrowDropDown.setVisibility( View.VISIBLE );
                    }
                } );

                break;

            case 6:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_konfirmasi_penjemputan, parent, false );

                    myDialog = new Dialog( activity );
                    holder = new ViewHolder();

                    holder.tvOrderID_Confirm = vi.findViewById( R.id.tvIDOrder_Penjemputan );
                    holder.tvHari_Confirm = vi.findViewById( R.id.tvHari_Penjemputan );
                    holder.tvTanggal_Confirm = vi.findViewById( R.id.tvTanggal_Penjemputan );
                    holder.tvJam_Confirm = vi.findViewById( R.id.tvJam_Penjemputan );
                    holder.tvStatus_Confirm = vi.findViewById( R.id.tvStatus );
                    holder.btnYes_Confirm = vi.findViewById( R.id.btnYes );
                    holder.btnNo_Confirm = vi.findViewById( R.id.btnNo );
                    holder.tblButton_Confirm = vi.findViewById( R.id.tbl_Button );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> PickUpTimeList;
                PickUpTimeList = data.get( position );

                String strIDOrder_List = PickUpTimeList.get( "id_order" );
                final String strTglJemput_List = PickUpTimeList.get( "tanggal_penjemputan" );
                final String strWaktuJemput_List = PickUpTimeList.get( "waktu_penjemputan" );
                final String strOrderStatus_List = PickUpTimeList.get( "order_status" );
                //final String strIDUser_List = PickUpTimeList.get("id_user");
                //final String strIDAssignemnt_List = PickUpTimeList.get("name");

                holder.tvOrderID_Confirm.setText( strIDOrder_List );
                try {
                    convertDate = inputDate.parse( strTglJemput_List );
                    String convertOutputDate = outputDate.format( convertDate );
                    String dateOfWeek = getDay.format( convertDate );

                    holder.tvTanggal_Confirm.setText( convertOutputDate );
                    holder.tvHari_Confirm.setText( dateOfWeek );
                } catch (ParseException e) {
                    Log.e( "tag", String.valueOf( e ) );
                }

                holder.tvJam_Confirm.setText( strWaktuJemput_List );

                if (strOrderStatus_List != null) {
                    if (strOrderStatus_List.equalsIgnoreCase( "On Process" )) {
                        holder.tblButton_Confirm.setVisibility( View.GONE );
                        holder.tvStatus_Confirm.setText( activity.getString( R.string.MSG_PICKER_COMING ) );
                        holder.tvStatus_Confirm.setVisibility( View.VISIBLE );
                    } else if (strOrderStatus_List.equalsIgnoreCase( "Cancel" )) {
                        holder.tblButton_Confirm.setVisibility( View.GONE );
                        holder.tvStatus_Confirm.setVisibility( View.VISIBLE );
                        holder.tvStatus_Confirm.setText( activity.getString( R.string.MSG_PICKER_RESET ) );
                    } else if (strOrderStatus_List.equalsIgnoreCase( "Ready To Pickup" )) {
                        holder.tblButton_Confirm.setVisibility( View.VISIBLE );
                        holder.tvStatus_Confirm.setVisibility( View.GONE );
                    }
                }

                holder.btnYes_Confirm.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedID = data.get( position );
                        String strIDOrder_List = SelectedID.get( "id_order" );
                        Status_PickupTime = 1;
                        showDialog( strIDOrder_List, Status_PickupTime );
                    }
                } );

                holder.btnNo_Confirm.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedID = data.get( position );
                        String strIDOrder_List = SelectedID.get( "id_order" );
                        Status_PickupTime = 0;
                        showDialog( strIDOrder_List, Status_PickupTime );
                    }
                } );


                break;

            case 7:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_status_penjemputan, parent, false );

                    myDialog = new Dialog( activity );

                    holder = new ViewHolder();
                    holder.tvOrderID_Picker = vi.findViewById( R.id.tvIDOrder_Penjemputan );
                    holder.tvName_Picker = vi.findViewById( R.id.tvNamaDriver_Detail );
                    holder.tvTipeTruck_Picker = vi.findViewById( R.id.tvTipeTruck_Detail );
                    holder.tvNopol_Picker = vi.findViewById( R.id.tvKendaraan_Penjemputan );
                    holder.tvDetail_Picker = vi.findViewById( R.id.tvCekDetail_Penjemputan );
                    vi.setTag( holder );

                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> PenjemputanList;
                PenjemputanList = data.get( position );

                final String idOrder = PenjemputanList.get( "name" );
                final String nopol = PenjemputanList.get( "no_kendaraan" );
                final String tgl_penjemputan = PenjemputanList.get( "tanggal_penjemputan" );
                final String waktu_penjemputan = PenjemputanList.get( "waktu_penjemputan" );
                final String nama_picker = PenjemputanList.get( "nama_picker" );
                final String tipe_kendaraan = PenjemputanList.get( "tipe_kendaraan" );
                final String foto_diri = PenjemputanList.get( "foto_diri" );

                holder.tvOrderID_Picker.setText( idOrder );
                holder.tvName_Picker.setText( nama_picker );
                holder.tvTipeTruck_Picker.setText( tipe_kendaraan );
                holder.tvNopol_Picker.setText( nopol );

                holder.tvDetail_Picker.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUpDriver( nopol, tgl_penjemputan, waktu_penjemputan, nama_picker, tipe_kendaraan,
                                foto_diri, idOrder, "7" );
                    }
                } );

                break;

            case 8:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_point_order, parent, false );

                    holder = new ViewHolder();
                    holder.tvPoints_Get = vi.findViewById( R.id.tvPointsGet );
                    holder.tvIDOrder_Get = vi.findViewById( R.id.tvIDOrder_Penjemputan );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> StatusPoint_List;
                StatusPoint_List = data.get( position );

                String strPoint = StatusPoint_List.get( "total_point" );
                String strOrder = StatusPoint_List.get( "name" );

                try {
                    holder.tvPoints_Get.setText( "Point: " + decimalFormat.format( Double.valueOf( strPoint ) ) );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e( "tag", e.toString() );
                }
                holder.tvIDOrder_Get.setText( strOrder );

                break;

            case 9:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_member_list, parent, false );

                    holder = new ViewHolder();

                    holder.tvNamaMember = vi.findViewById( R.id.tvNamaMember_ListMember );
                    holder.tvIdMember = vi.findViewById( R.id.tvIDMember_ListMember );
                    holder.tvPointMember = vi.findViewById( R.id.tvPointMember_ListMember );
                    holder.btnDetailListMember = vi.findViewById( R.id.btnDetailListMember );
                    holder.imgPhotoMember = vi.findViewById( R.id.imgPicture_Member );

                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> StatusPoint_List1;
                StatusPoint_List1 = data.get( position );

                final String strNamaMember = StatusPoint_List1.get( "nama_member" );
                final String strIdMember = StatusPoint_List1.get( "id_member" );
                final String strPointMember = StatusPoint_List1.get( "point" );
                final String strFotoMember = StatusPoint_List1.get( "foto" );
                final String strId = StatusPoint_List1.get( "id" );
                final String strNoTelepon = StatusPoint_List1.get( "no_telepon" );
                final String strAlamatMember = StatusPoint_List1.get( "alamat" );
                final String strEmailMember = StatusPoint_List1.get( "email" );
                holder.btnDetailListMember.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_detail = new Intent( activity, DetailMemberActivity.class );
                        intent_detail.putExtra( "foto", strFotoMember );
                        intent_detail.putExtra( "point", strPointMember );
                        intent_detail.putExtra( "id_member", strIdMember );
                        intent_detail.putExtra( "nama_member", strNamaMember );
                        intent_detail.putExtra( "id", strId );
                        intent_detail.putExtra( "no_telepon", strNoTelepon );
                        intent_detail.putExtra( "alamat", strAlamatMember );
                        intent_detail.putExtra( "email", strEmailMember );
                        activity.startActivity( intent_detail );
                    }
                } );

                try {
                    holder.tvPointMember.setText( "Point: " + decimalFormat.format( Double.valueOf( strPointMember ) ) );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e( "tag", e.toString() );
                }

                url_foto = apiData.get( "str_url_main" );
                Picasso.get()
                        .load( url_foto + strFotoMember )
                        .error( R.drawable.ic_navigation_profil )
                        .into( holder.imgPhotoMember );
                holder.tvIdMember.setText( strIdMember );
                holder.tvNamaMember.setText( strNamaMember );
                holder.tvPointMember.setText( strPointMember );

                break;

            case 10:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_member_request, parent, false );

                    holder = new ViewHolder();
                    holder.tvTanggalReqMember = vi.findViewById( R.id.tvTanggal_RequestMember );
                    holder.tvIdMemberReq = vi.findViewById( R.id.tvIDMember_RequestMember );
                    holder.tvNamaMemberReq = vi.findViewById( R.id.tvNamaMember_RequestMember );
                    holder.btnDetailReqMember = vi.findViewById( R.id.btnDetailReqMember );
                    holder.imgPictureReqMember = vi.findViewById( R.id.imgPicture_RequestMember );

                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> reqMember;
                reqMember = data.get( position );

                final String strNamaReqMember = reqMember.get( "nama_member" );
                final String strIdReqMember = reqMember.get( "id_member" );
                final String strTanggalReqMember = reqMember.get( "creation" );
                final String strIdBankSampah = reqMember.get( "id" );
                final String strAlamatReqMember = reqMember.get( "alamat" );
                final String strImggPictureReq = reqMember.get( "photo" );
                final String strIdbankSampah1 = reqMember.get( "id_bank_sampah" );

                holder.btnDetailReqMember.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_detail = new Intent( activity, DetailRequestMember.class );
                        intent_detail.putExtra( "foto", strImggPictureReq );
                        intent_detail.putExtra( "nama_member", strNamaReqMember );
                        intent_detail.putExtra( "id_member", strIdReqMember );
                        intent_detail.putExtra( "alamat", strAlamatReqMember );
                        intent_detail.putExtra( "creation", strTanggalReqMember );
                        intent_detail.putExtra( "id", strIdBankSampah );
                        intent_detail.putExtra( "id_bank_sampah", strIdbankSampah1 );
                        activity.startActivity( intent_detail );
                    }
                } );
                url_foto = apiData.get( "str_url_main" );
                Picasso.get()
                        .load( url_foto + strImggPictureReq )
                        .error( R.drawable.ic_navigation_profil )
                        .into( holder.imgPictureReqMember );
                holder.tvIdMemberReq.setText( strIdReqMember );
                holder.tvNamaMemberReq.setText( strNamaReqMember );
                holder.tvTanggalReqMember.setText( strTanggalReqMember );

                break;

            case 11:
                if (convertView == null) {
                    vi = inflater.inflate(R.layout.lv_list_order, parent, false);

                    myDialog = new Dialog(activity);

                    holder = new ViewHolder();
                    holder.tvOrderIDD_List = vi.findViewById(R.id.tvOrderID_List);
                    holder.tvStatusOrderr_List = vi.findViewById(R.id.tvStatusOrder_List);
                    holder.tvTotalKgg_List = vi.findViewById(R.id.tvTotalKg);
                    holder.tvTotalPointss_List = vi.findViewById(R.id.tvTotalPoints);
                    holder.tvAlamatt_List = vi.findViewById(R.id.tvAlamat_List);
                    holder.imgFotoSampahh_List = vi.findViewById(R.id.imgViewSampah_List);
                    holder.imgViewwDetail = vi.findViewById(R.id.imgViewDetail_List);
                    holder.imgArrowwDropDown = vi.findViewById(R.id.imgArrowDown_List);
                    holder.imgArrowwUp = vi.findViewById(R.id.imgArrowUp_List);
                    holder.btnDetaill_List = vi.findViewById(R.id.btnDetail_ListOrder);
                    holder.btnEditData = vi.findViewById(R.id.btnEditData);
                    holder.btnHidee_List = vi.findViewById(R.id.btnHide_ListOrder);
                    holder.btnCancelOrderr_List = vi.findViewById(R.id.btnCancel_Order_List);
                    holder.btnAcceptOrderr_List = vi.findViewById(R.id.btnAccept_Order_List);

                    holder.cdDetaill = vi.findViewById(R.id.cdBtnDetail);
                    holder.cdHidee = vi.findViewById(R.id.cdBtnHide);
                    vi.setTag(holder);
                }else{
                    holder = (ViewHolder)vi.getTag();
                }

                HashMap<String, String> ListDataOrderr;
                ListDataOrderr = data.get(position);

                final String strListOrderIDD = ListDataOrderr.get("nama");
                final String strNamaOrderr = (ListDataOrderr.get("name"));
                final String strStatusOrderr = (ListDataOrderr.get("status_order"));
                final String strTotalKgg = (ListDataOrderr.get("berat_total"));
                final String strTotalPointss = (ListDataOrderr.get("point_total"));
                final String strCreation = (ListDataOrderr.get("creation"));
                final String strImagee = ListDataOrderr.get("image");
                final String strAlamatt = ListDataOrderr.get("alamat");
                final String strLatlongg = ListDataOrderr.get("latlong");
                final String kecamatan = ListDataOrderr.get("kecamatan");

                holder.tvOrderIDD_List.setText(strListOrderIDD);
                holder.tvStatusOrderr_List.setText(strStatusOrderr);
                if(strStatusOrderr != null){
                    if(strStatusOrderr.equalsIgnoreCase("Cancel")){
                        holder.tvStatusOrderr_List.setTextColor(Color.RED);
                    }
                }


                try {
                    holder.tvTotalKgg_List.setText(decimalFormat.format(Double.valueOf(strTotalKgg)) + " Kg");
                    holder.tvTotalPointss_List.setText(decimalFormat_Point.format(Double.valueOf(strTotalPointss)) + " Pts");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                holder.imgViewwDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderIDD = data.get(position);
                        String strListOrderIDD = SelectedOrderIDD.get("name");
                        showPopUpDetaill(strListOrderIDD);
                    }
                });

                holder.btnEditData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderIDD = data.get(position);
                        String strOrderIDD = SelectedOrderIDD.get("name");
                        showPopupAcceptOrderr(strOrderIDD, position);
                    }
                });

                holder.btnDetaill_List.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cdDetaill.setVisibility(View.GONE);
                        holder.cdHidee.setVisibility(View.VISIBLE);

                        if (strStatusOrderr != null) {
                            if (strStatusOrderr.equalsIgnoreCase("On Process")) {
                                holder.btnAcceptOrderr_List.setVisibility(View.VISIBLE);
                                holder.btnCancelOrderr_List.setVisibility(View.VISIBLE);
                            } else {
                                holder.btnAcceptOrderr_List.setVisibility(View.GONE);
                                holder.btnCancelOrderr_List.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                holder.btnHidee_List.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cdDetaill.setVisibility(View.VISIBLE);
                        holder.imgArrowwDropDown.setVisibility(View.VISIBLE);
                        holder.btnAcceptOrderr_List.setVisibility(View.GONE);
                        holder.btnCancelOrderr_List.setVisibility(View.GONE);
                        holder.cdHidee.setVisibility(View.GONE);
                        holder.imgFotoSampahh_List.setVisibility(View.GONE);
                        holder.btnAcceptOrderr_List.setVisibility(View.GONE);
                        holder.btnCancelOrderr_List.setVisibility(View.GONE);
                        holder.imgArrowwUp.setVisibility(View.GONE);
                    }
                });

                holder.btnAcceptOrderr_List.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderIDD = data.get(position);
                        String strOrderIDD = SelectedOrderIDD.get("name");
                        showPopupAcceptOrderr(strOrderIDD, position);
                    }
                });

                holder.btnCancelOrderr_List.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> SelectedOrderIDD = data.get(position);
                        String strOrderIDD = SelectedOrderIDD.get("name");
                        showPopupCancelOrderr(strOrderIDD, position);
                    }
                });
                holder.imgArrowwDropDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgFotoSampahh_List.setVisibility(View.VISIBLE);
                        holder.imgArrowwUp.setVisibility(View.VISIBLE);
                        holder.imgArrowwDropDown.setVisibility(View.GONE);
                    }
                });

                holder.imgArrowwUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgFotoSampahh_List.setVisibility(View.GONE);
                        holder.imgArrowwUp.setVisibility(View.GONE);
                        holder.imgArrowwDropDown.setVisibility(View.VISIBLE);
                    }
                });


                break;

            case 12:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_list_harga_item, parent, false );
                    holder = new ViewHolder();
                    holder.imgEditItem = vi.findViewById( R.id.btnEditItem );
                    holder.imgHapusItem = vi.findViewById( R.id.btnHapusItem );
                    holder.tvHargaItem = vi.findViewById( R.id.tvHargaItem );
                    holder.tvJenisItem = vi.findViewById( R.id.tvJenisItem );

                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> ItemList;
                ItemList = data.get( position );

                final String strJenisitem = ItemList.get( "jenis_item" );
                final String strHargaitem = ItemList.get( "harga_per_kilo" );
                final String strBankSampah = ItemList.get( "id_bank_sampah" );
                final String strIdItem = ItemList.get( "id_item" );

                try {
                    holder.tvHargaItem.setText( "Rp " + decimalFormat.format( Double.valueOf( strHargaitem ) ) );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e( "tag", e.toString() );
                }
                holder.imgHapusItem.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hapusItem( strIdItem );
                    }
                } );

                holder.imgEditItem.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_detail = new Intent( activity, UpdateListItem.class );
                        intent_detail.putExtra( "id_item", strIdItem );
                        intent_detail.putExtra( "id_bank_sampah", strBankSampah );
                        intent_detail.putExtra( "jenis_item", strJenisitem );
                        intent_detail.putExtra( "harga_per_kilo", strHargaitem );
                        activity.startActivity( intent_detail );
                    }
                } );
                holder.tvJenisItem.setText( strJenisitem );
                break;

            case 13:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_pengurus, parent, false );
                    holder = new ViewHolder();
                    holder.tvNamaPengurus = vi.findViewById( R.id.tvNamaPengurus );
                    holder.tvJabatan = vi.findViewById( R.id.tvJabatan );
                    holder.btnHapusPengurus = vi.findViewById( R.id.btnHapusPengurus );
                    holder.btnEditPengurus = vi.findViewById( R.id.btnEditPengurus );
                    holder.tvIdPengurus = vi.findViewById( R.id.id_pengurus );

                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }
                HashMap<String, String> ListPengurus;
                ListPengurus = data.get( position );

                final String strNamaPengurus = ListPengurus.get( "nama_pengurus" );
                final String strJabatan = ListPengurus.get( "jabatan" );
                final String strBanksampah = ListPengurus.get( "id_bank_sampah" );
                final String strIdPengurus = ListPengurus.get( "name" );

                holder.btnEditPengurus.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editPengurus = new Intent( activity, EditPengurus.class );
                        editPengurus.putExtra( "nama_pengurus", strNamaPengurus );
                        editPengurus.putExtra( "jabatan", strJabatan );
                        editPengurus.putExtra( "id_bank_sampah", strBanksampah );
                        editPengurus.putExtra( "name", strIdPengurus );
                        activity.startActivity( editPengurus );
                    }
                } );

                holder.btnHapusPengurus.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hapusPengurus( strIdPengurus );
                    }
                } );

                holder.tvNamaPengurus.setText( strNamaPengurus );
                holder.tvJabatan.setText( strJabatan );
                holder.tvIdPengurus.setText( strIdPengurus );

                break;

            case 14:
                if (convertView == null) {
                    vi = inflater.inflate( R.layout.lv_list_order_user, parent, false );
                    myDialog = new Dialog( activity );
                    holder = new ViewHolder();
                    holder.tvTanggalOrderUser = vi.findViewById( R.id.tvOrderTanggal_List );
                    holder.tvTotalBerat = vi.findViewById( R.id.tvTotalKg );
                    holder.tvTotalPoint = vi.findViewById( R.id.tvTotalPoints );
                    holder.imgDetailView = vi.findViewById( R.id.imgViewDetail_List );
                    holder.flParent = vi.findViewById( R.id.flParent );
                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }
                HashMap<String, String> ListOrderUser;
                ListOrderUser = data.get( position );

                final String strTanggal = ListOrderUser.get( "creation" );
                final String strTotalBerat = ListOrderUser.get( "berat_total" );
                final String strTotalPoint = ListOrderUser.get( "point_total" );

                holder.imgDetailView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> ListOrderUser;
                        ListOrderUser = data.get( position );
                        String id_user = ListOrderUser.get( "id_user" );
                        allOrder.clear();
                        showDetailOrder( id_user );
                    }
                } );

                holder.tvTanggalOrderUser.setText( strTanggal );
                holder.tvTotalPoint.setText( strTotalPoint );
                holder.tvTotalBerat.setText( strTotalBerat );

                break;

            case 15:
                if (convertView == null) {

                    vi = inflater.inflate( R.layout.lv_detail_order_user, parent, false );
                    holder = new ViewHolder();
                    holder.tvNamaOrderUser = vi.findViewById( R.id.tvNamaItem );
                    holder.tvBerat = vi.findViewById( R.id.tvKg );
                    holder.tvPoint = vi.findViewById( R.id.tvPoints );

                    vi.setTag( holder );
                } else {
                    holder = (ViewHolder) vi.getTag();
                }

                HashMap<String, String> ListDetailListUser;
                ListDetailListUser = data.get( position );

                final String strjenisItemOrderUser = ListDetailListUser.get( "jenis_item" );
                final String strBerat = ListDetailListUser.get( "berat_item" );
                final String strPointItem = ListDetailListUser.get( "point" );


                holder.tvNamaOrderUser.setText( strjenisItemOrderUser );
                holder.tvBerat.setText( strBerat );
                holder.tvPoint.setText( strPointItem );


            default:
                break;
        }
        return vi;
    }

    //<---------------------- Fragment Position 3 - Incoming Order ---------------------->
    private void showPopupDeleteIncomingOrderLine(final String ID_IncomingOrder, final String ID_OrderLine, final int position) {
        myDialog.setContentView( R.layout.pop_up_delete_incoming_order_line );

        Button btnDelete_Confirmation = myDialog.findViewById( R.id.btnDelete_Confirmation );
        Button btnCancel_Confirmation = myDialog.findViewById( R.id.btnCancel_Confirmation );

        btnDelete_Confirmation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete_IncomingOrderLine( ID_IncomingOrder, ID_OrderLine, position );
            }
        } );

        btnCancel_Confirmation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        } );

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            myDialog.show();
        }
    }

    private void Delete_IncomingOrderLine(final String ID_IncomingOrder, final String ID_OrderLine, final int position) {
        String[] field_name = {"name", "inc_order", "message"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory( MySSLSocketFactory.getFixedSocketFactory() );
        RequestParams params = new RequestParams();
        String delete_url;

        final ProgressDialog dialog = new ProgressDialog( activity );
        dialog.setCanceledOnTouchOutside( false );
        dialog.setCancelable( true );
        dialog.setMessage( "Menghapus Data Terpilih, Harap Menunggu.." );
        dialog.show();

        delete_url = apiData.get( "str_url_address" ) + "/method/digitalwaste.digital_waste.custom_api.delete_item_order_line";
        params.put( field_name[0], ID_OrderLine );
        params.put( field_name[1], ID_IncomingOrder );

        client.addHeader( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
        client.post( delete_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String resp_content = "";
                try {
                    resp_content = new String( responseBody, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    viewFromDB( resp_content, position );
                    Log.e( "tag", "sukses" );
                } catch (Throwable t) {
                    Toasty.error( activity, activity.getString( R.string.MSG_CODE_409 ) + "1 : " + activity.getString( R.string.MSG_CHECK_DATA ), Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toasty.error( activity, activity.getString( R.string.MSG_CODE_500 ) + " 1 : " + activity.getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
            }
        } );
    }

    private void viewFromDB(String resp_content, final int position) {
        String[] field_name = {"id_user", "name", "message"};

        try {
            arrDeleteOrder = rest_class.getJsonData( field_name, resp_content );
            JSONObject jsonPost = new JSONObject( resp_content );

            String getMessage_Result = jsonPost.getString( field_name[2] );
            if (getMessage_Result.equals( "False" )) {
                Toasty.error( activity, "Gagal Menghapus Data Terpilih..", Toast.LENGTH_SHORT ).show();

            } else if (getMessage_Result.equals( "True" )) {
                AlertDialog.Builder builder = new AlertDialog.Builder( activity );
                builder.setMessage( "Berhasil Menghapus Data Terpilih!" )
                        .setCancelable( false )
                        .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                myDialog.dismiss();
                                /*activity.recreate();
                                activity.finish();*/
                                Intent a = new Intent( activity, MainActivity.class );
                                activity.startActivity( a );
                                activity.finish();
                                /*data.remove(position);
                                LazyAdapter.this.notifyDataSetChanged();*/
                            }
                        } );
                AlertDialog alert = builder.create();
                alert.show();
            }

        } catch (JSONException e) {
            Toasty.error( activity, activity.getString( R.string.MSG_CODE_500 ) + " 2 : " + activity.getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
        }
    }
    //<---------------------- Fragment Position 3 - Incoming Order ---------------------->


    //<---------------------- Fragment Position 5 - List Order---------------------->
    private void showPopUpDetail(final String strListOrderID) {
        myDialog.setContentView( R.layout.form_list_order_detail );

        tvOrderID = myDialog.findViewById( R.id.tvOrderID_Detail );
        tvStatusOrder = myDialog.findViewById( R.id.tvStatusOrder_Detail );
        tvTotalKg = myDialog.findViewById( R.id.tvTotalKg_Detail );
        tvTotalPoints = myDialog.findViewById( R.id.tvTotalPoints_Detail );
        tvAlamat = myDialog.findViewById( R.id.tvAlamat_Detail );
        lvListDetailOrder = myDialog.findViewById( R.id.listView_OrderDetails );

        getDetailOrderLine( strListOrderID );

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            myDialog.show();
        }
    }

    private void getDetailOrderLine(final String strListOrderID) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory( MySSLSocketFactory.getFixedSocketFactory() );
        RequestParams params = new RequestParams();
        String list_detail_order_url;

        list_detail_order_url = apiData.get( "str_url_address" ) + "/method/digitalwaste.digital_waste.custom_api.get_order_line";
        params.put( "id_order", strListOrderID );

        client.addHeader( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
        client.post( list_detail_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp_content = null;

                try {
                    resp_content = new String( responseBody, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displayOrderDetail( resp_content );
                } catch (Throwable t) {
                    Toasty.error( activity, "Gagal Mengambil Data", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toasty.error( activity, "Periksa Koneksi Anda 1", Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void displayOrderDetail(String resp_content) {
        String[] field_name = {"message", "name", "parent", "point", "creation", "tipe_sampah",
                "jenis_sampah", "jumlah_kg"};

        try {
            JSONObject jsonObject = new JSONObject( resp_content );
            ArrayList<HashMap<String, String>> allOrderDetail = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray( field_name[0] );
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject( i );

                String idOrderLine_Result = c.getString( field_name[1] );
                String idOrder_Result = c.getString( field_name[2] );
                String point_Result = c.getString( field_name[3] );
                String dateCreation_Result = c.getString( field_name[4] );
                String tipeSampah_Result = c.getString( field_name[5] );
                String jenisSampah_Result = c.getString( field_name[6] );
                String jumlahKg_Result = c.getString( field_name[7] );

                HashMap<String, String> map = new HashMap<>();

                map.put( field_name[1], idOrderLine_Result );
                map.put( field_name[2], idOrder_Result );
                map.put( field_name[3], point_Result );
                map.put( field_name[4], dateCreation_Result );
                map.put( field_name[5], tipeSampah_Result );
                map.put( field_name[6], jenisSampah_Result );
                map.put( field_name[7], jumlahKg_Result );

                allOrderDetail.add( map );

            }

            SimpleAdapter adapter3 = new SimpleAdapter( activity, allOrderDetail, R.layout.lv_detail_order_list,
                    new String[]{"point", "jenis_sampah", "jumlah_kg"}, new int[]{R.id.tvPoints_OrderList, R.id.tvJenisSampah_OrderList, R.id.tvBeratSampah_OrderList} );
            adapter3.setViewBinder( new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.tvPoints_OrderList) {
                        TextView tv = (TextView) view;
                        try {
                            tv.setText( decimalFormat_Point.format( Double.valueOf( textRepresentation ) ) + " Pts" );
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (view.getId() == R.id.tvBeratSampah_OrderList) {
                        TextView tv = (TextView) view;
                        try {
                            tv.setText( decimalFormat.format( Double.valueOf( textRepresentation ) ) + " Kg" );
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            } );
            lvListDetailOrder.setAdapter( adapter3 );

        } catch (JSONException e) {
            Toasty.error( activity, "Terjadi Kesalahan 1", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }

    private void showPopupCancelOrder(final String strOrderID, final int position) {
        myDialog.setContentView( R.layout.pop_up_delete_incoming_order_line );

        Button btnDelete_Confirmation = myDialog.findViewById( R.id.btnDelete_Confirmation );
        Button btnCancel_Confirmation = myDialog.findViewById( R.id.btnCancel_Confirmation );

        TextView tvTitle_Delete = myDialog.findViewById( R.id.tvTitle_Confirmation );
        tvTitle_Delete.setText( "Apakah Anda Yakin Membatalkan Order Limbah Sampah Tersebut?" );

        btnDelete_Confirmation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelOrder( strOrderID, position );
                myDialog.dismiss();
            }
        } );

        btnCancel_Confirmation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        } );

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            myDialog.show();
        }
    }

    private void CancelOrder(final String strOrderID, final int position) {
        String[] field_name = {"id_order", "message"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory( MySSLSocketFactory.getFixedSocketFactory() );
        RequestParams params = new RequestParams();
        String cancel_order_url;

        final ProgressDialog dialog = new ProgressDialog( activity );
        dialog.setMessage( "Membatalkan Order, Harap Menunggu..." );
        dialog.show();

        cancel_order_url = apiData.get( "str_url_address" ) + "/method/digitalwaste.digital_waste.custom_api.cancel_order";
        params.put( field_name[0], strOrderID );

        client.addHeader( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
        client.post( cancel_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String resp_content = "";
                try {
                    resp_content = new String( responseBody, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder( activity );
                    builder.setMessage( "Berhasil Membatalkan Order Terpilih!" )
                            .setCancelable( false )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    myDialog.dismiss();
                                    data.remove( position );
                                    LazyAdapter.this.notifyDataSetChanged();
                                    /*Intent a = new Intent(activity, MainActivity.class);
                                    activity.startActivity(a);
                                    activity.finish();*/
                                }
                            } );
                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (Throwable t) {
                    Toast.makeText( activity, "Terjadi Kesalahan, Mohon Periksa Data Kembali 2", Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText( activity, activity.getString( R.string.MSG_REQ_FAILED ), Toast.LENGTH_LONG ).show();
            }
        } );
    }
    //<---------------------- Fragment Position 5 - List Order---------------------->


    //<---------------------- Fragment Position 6 - Konfirmasi Penjemputan ---------------------->
    private void showDialog(final String strIDOrder_List, final int Status_PickupTime) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( activity );
        alertDialogBuilder.setTitle( "Konfirmasi Penjemputan" );
        alertDialogBuilder
                .setMessage( "Jika Anda Yakin, Tekan Tombol 'OK'" )
                .setIcon( R.drawable.ic_logo )
                .setCancelable( false )
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Update( strIDOrder_List, Status_PickupTime );
                    }
                } )
                .setNegativeButton( "Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                } );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Update(final String strIDOrder_List, final int Status_PickupTime) {
        String[] field_name = {"id_order", "data_int", "message"};

        final ProgressDialog dialog = new ProgressDialog( activity );
        dialog.setMessage( "Memproses Konfirmasi Penjemputan, Harap Menunggu.." );
        dialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory( MySSLSocketFactory.getFixedSocketFactory() );
        RequestParams params = new RequestParams();
        String cancel_order_url;

        cancel_order_url = apiData.get( "str_url_address" ) + "/method/digitalwaste.digital_waste.custom_api.confirmation_order";
        params.put( field_name[0], strIDOrder_List );
        params.put( field_name[1], Status_PickupTime );

        client.addHeader( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
        client.post( cancel_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                String resp_content = "";
                try {
                    resp_content = new String( responseBody, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder( activity );
                    if (Status_PickupTime == 1) {
                        showPickerDetail( strIDOrder_List );
                        //status_text = "Data driver kami akan tertampil di menu Detail Penjemputan";
                    } else {
                        builder.setTitle( "Berhasil Melakukan Konfirmasi Penjemputan" )
                                .setMessage( "Kami akan mengatur ulang waktu penjemputan Order Limbah Anda" )
                                .setCancelable( false )
                                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = activity.getIntent();
                                        activity.overridePendingTransition( 0, 0 );
                                        intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
                                        activity.finish();
                                        activity.overridePendingTransition( 0, 0 );
                                        activity.startActivity( intent );
                                    }
                                } );
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (Throwable t) {
                    Toasty.error( activity, activity.getString( R.string.MSG_CODE_409 ) + "1 : " + activity.getString( R.string.MSG_CHECK_DATA ), Toast.LENGTH_LONG ).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toasty.error( activity, activity.getString( R.string.MSG_CODE_500 ) + "1 : " + activity.getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
                Log.e( "tag", error.toString() );
            }
        } );
    }

    private void showPickerDetail(final String strIDOrder_List) {
        final String[] field_name = {"id_order", "message", "no_kendaraan", "tanggal_penjemputan", "waktu_penjemputan",
                "nama_picker", "tipe_kendaraan", "foto_picker", "name"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory( MySSLSocketFactory.getFixedSocketFactory() );
        RequestParams params = new RequestParams();
        String cancel_order_url;

        final ProgressDialog dialog = new ProgressDialog( activity );
        dialog.setMessage( "Memproses, Harap Menunggu..." );
        dialog.show();

        cancel_order_url = apiData.get( "str_url_address" ) + "/method/digitalwaste.digital_waste.custom_api.get_picker_information";
        params.put( field_name[0], strIDOrder_List );

        client.addHeader( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
        client.post( cancel_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp_content = "";
                try {
                    resp_content = new String( responseBody, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject( resp_content );
                    JSONArray jsonArray = jsonObject.getJSONArray( field_name[1] );

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject driver = jsonArray.getJSONObject( i );

                        String no_kendaraan = driver.getString( field_name[2] );
                        String tgl_jemput = driver.getString( field_name[3] );
                        String waktu_penjemputan = driver.getString( field_name[4] );
                        String nama_picker = driver.getString( field_name[5] );
                        String tipe_kendaraan = driver.getString( field_name[6] );
                        String foto_picker = driver.getString( field_name[7] );
                        String id_order = driver.getString( field_name[8] );

                        showPopUpDriver( no_kendaraan, tgl_jemput, waktu_penjemputan, nama_picker, tipe_kendaraan,
                                foto_picker, id_order, "6" );
                    }
                } catch (Throwable t) {
                    Toasty.error( activity, activity.getString( R.string.MSG_CODE_409 ) + "1 : " + activity.getString( R.string.MSG_CHECK_DATA ), Toast.LENGTH_LONG ).show();
                    Log.e( "tag", t.toString() );
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toasty.error( activity, activity.getString( R.string.MSG_CODE_500 ) + "1 : " + activity.getString( R.string.MSG_CHECK_CONN ), Toast.LENGTH_LONG ).show();
            }
        } );
    }
    //<---------------------- Fragment Position 6 - Konfirmasi Penjemputan ---------------------->


    //<---------------------- Fragment Position 7 & 8 - Driver Details ---------------------->
    private void showPopUpDriver(final String no_kendaraan, final String tgl_jemput, final String waktu_penjemputan,
                                 final String nama_picker, final String tipe_kendaraan, final String foto_picker,
                                 final String id_order, final String cases) {

        myDialog.setContentView( R.layout.pop_up_driver_details );
        myDialog.setCanceledOnTouchOutside( false );

        TextView tvOrderID = myDialog.findViewById( R.id.tvOrderID_PopUp );
        TextView tvDatetime = myDialog.findViewById( R.id.tvDatetime_PopUp );
        TextView tvDriverName = myDialog.findViewById( R.id.tvName_PopUp );
        TextView tvTipeKendaraan = myDialog.findViewById( R.id.tvTipeKendaraan_PopUp );
        TextView tvNopol = myDialog.findViewById( R.id.tvNopol_PopUp );

        ImageView imgDriverPhoto = myDialog.findViewById( R.id.imgPictureDriver_PopUp );
        ImageView imgClose = myDialog.findViewById( R.id.imgClose );

        inputDate = new SimpleDateFormat( "yyyy-MM-dd", Locale.getDefault() );
        outputDate = new SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() );
        getDay = new SimpleDateFormat( "EEEE", Locale.getDefault() );

        Date convertDate;

        try {
            convertDate = inputDate.parse( tgl_jemput );
            String convertOutputDate = outputDate.format( convertDate );
            String dateOfWeek = getDay.format( convertDate );

            tvDatetime.setText( dateOfWeek + ", " + convertOutputDate + " | " + waktu_penjemputan );
        } catch (ParseException e) {
            Log.e( "tag", String.valueOf( e ) );
        }

        String url = apiData.get( "str_url_main" ) + foto_picker;
        Picasso.get()
                .load( url )
                .error( R.drawable.ic_no_data )
                .fit()
                .centerInside()
                .into( imgDriverPhoto );

        // tvTime.setText(waktu_penjemputan);
        tvOrderID.setText( "Jadwal Penjemputan " + id_order );
        tvDriverName.setText( "Nama : " + nama_picker );
        tvTipeKendaraan.setText( "Tipe Kendaraan : " + tipe_kendaraan );
        tvNopol.setText( "No Kendaraan : " + no_kendaraan );

        if (cases.equals( "6" )) {
            imgClose.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                    Intent intent = activity.getIntent();
                    activity.overridePendingTransition( 0, 0 );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
                    activity.finish();
                    activity.overridePendingTransition( 0, 0 );
                    activity.startActivity( intent );
                }
            } );

            myDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    myDialog.dismiss();
                    Intent intent = activity.getIntent();
                    activity.overridePendingTransition( 0, 0 );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
                    activity.finish();
                    activity.overridePendingTransition( 0, 0 );
                    activity.startActivity( intent );
                }
            } );
        } else if (cases.equals( "7" )) {
            imgClose.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            } );

            myDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    myDialog.dismiss();
                }
            } );
        }

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            myDialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            myDialog.show();
        }
    }
    //<---------------------- Fragment Position 7 & 8 - Driver Details ---------------------->

    //<---------------------- Fragment Position 13 - Incoming Order ---------------------->
    private void hapusPengurus(final String str_id_pengurus) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( activity );
        builder.setMessage( R.string.MS_HAPUS_SUCCESS )
                .setCancelable( false )
                .setTitle( "Konfirmasi Hapus" )
                .setMessage( "Apakah Anda yakin?" )
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String[] field_name = {"id_pengurus"};
                        String base_url = apiData.get( "str_url_address" ) + (".delete_pengurus");
                        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d( "DEBUG", "Register Response: " + response );
                                try {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( activity );
                                    builder.setMessage( R.string.MS_HAPUS_SUCCESS )
                                            .setCancelable( false )
                                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent hapusPengurus = new Intent( activity, MainActivity.class );
                                                    activity.startActivity( hapusPengurus );
                                                    activity.finish();
                                                }
                                            } );
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                    Log.e( "tag", "sukses" );
                                } catch (Throwable t) {
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        } ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put( field_name[0], str_id_pengurus );
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                                return params;
                            }
                        };
                        // Adding request to request queue
                        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
                    }
                } )
                .setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } );
        android.app.AlertDialog alert = builder.create();
        alert.show();
        Log.e( "tag", "sukses" );
    }
    //<---------------------- Fragment Position 13 - Incoming Order ---------------------->

    //<---------------------- Fragment Position 12 - Incoming Order ---------------------->

    private void hapusItem(final String str_id_item) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( activity );
        builder.setMessage( "Item Berhasil Dihapus." )
                .setCancelable( false )
                .setTitle( "Konfirmasi Hapus" )
                .setMessage( "Apakah Anda yakin?" )
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String[] field_name = {"id_item"};
                        String base_url = apiData.get( "str_url_address" ) + (".delete_item");
                        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d( "DEBUG", "Register Response: " + response );
                                try {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( activity );
                                    builder.setMessage( R.string.MS_HAPUS_SUCCESS )
                                            .setCancelable( false )
                                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent hapusPengurus = new Intent( activity, MainActivity.class );
                                                    activity.startActivity( hapusPengurus );
                                                    activity.finish();
                                                }
                                            } );
                                    android.app.AlertDialog alert = builder.create();
                                    alert.show();
                                    Log.e( "tag", "sukses" );
                                } catch (Throwable t) {
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        } ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put( field_name[0], str_id_item );
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                                return params;
                            }
                        };
                        // Adding request to request queue
                        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
                    }
                } )
                .setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } );
        android.app.AlertDialog alert = builder.create();
        alert.show();
        Log.e( "tag", "sukses" );
    }
    //<---------------------- Fragment Position 12 - Incoming Order ---------------------->

    //<---------------------- Fragment Position 15 - Incoming Order ---------------------->

    public void getDetailOrderuser(String id_user) {
        String base_url = apiData.get( "str_url_address" ) + (".get_history_user");
        StringRequest strReq = new StringRequest( Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d( "debug", "Check Order User: " + response );
                try {
                    viewDataDetailOrderUser( response );
                } catch (Throwable t) {
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        } ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put( "id_bank_sampah", strNamaBankSampah );
                params.put( "id_user", id_user );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put( apiData.get( "str_header" ), apiData.get( "str_token_value" ) );
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue( strReq, apiData.get( "str_json_obj" ) );
    }

    public void viewDataDetailOrderUser(String resp_content) {

        String[] field_name = {"item_order_history_line", "jenis_item", "berat_item", "point", "message"};

        try {
            JSONObject jsonObject = new JSONObject( resp_content );
            JSONArray cast1 = jsonObject.getJSONArray( field_name[4] );
            for (int i = 0; i < cast1.length(); i++) {
                JSONObject d = cast1.getJSONObject( i );
                JSONArray line = d.getJSONArray( field_name[0] );

                Log.e( "tg", line.toString() );

                for (int x = 0; x < line.length(); x++) {
                    JSONObject c = line.getJSONObject( x );

                    String JenisItem = c.getString( field_name[1] );
                    String BeratItem = c.getString( field_name[2] );
                    String PointItem = c.getString( field_name[3] );

                    HashMap<String, String> map = new HashMap<>();

                    map.put( field_name[1], JenisItem );
                    map.put( field_name[2], BeratItem );
                    map.put( field_name[3], PointItem );
                    allOrder.add( map );
                }

                Log.d( "tag_allorder", allOrder.toString() );

                SimpleAdapter adapter4 = new SimpleAdapter( activity, allOrder, R.layout.lv_detail_order_user,
                        new String[]{"jenis_item", "berat_item", "point"}, new int[]{R.id.tvNamaItem, R.id.tvKg, R.id.tvPoints} );
                adapter4.setViewBinder( new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view.getId() == R.id.tvKg) {
                            TextView tv = (TextView) view;
                            try {
                                tv.setText( decimalFormat.format( Double.valueOf( textRepresentation ) ) + " Kg" );
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        if (view.getId() == R.id.tvPoints) {
                            TextView tv = (TextView) view;
                            try {
                                tv.setText( decimalFormat.format( Double.valueOf( textRepresentation ) ) + " Kg" );
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            return true;

                        }
                        return false;
                    }
                } );
                lvListDetailOrderUser.setAdapter( adapter4 );
            }


        } catch (JSONException e) {
            e.printStackTrace();
            /*include_FormOrderList.setVisibility(View.GONE);
            linear_NoData.setVisibility(View.VISIBLE);
            if(getContext()!=null) {
                Toasty.info(getContext(), getString(R.string.MSG_NO_LIMBAH) + "\n" + getString(R.string.MSG_PURSUE_LIMBAH), Toast.LENGTH_LONG).show();
            }*/
        }
    }

    public void showDetailOrder(String id_user) {
        myDialog.setContentView( R.layout.list_order_detail );

        lvListDetailOrderUser = myDialog.findViewById( R.id.listView_OrderUserDetail );
        getDetailOrderuser( id_user );
        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }
    //<---------------------- Fragment Position 15 - Incoming Order ---------------------->

    //<---------------------- Fragment Position 11 - Incoming Order ---------------------->

    private void showPopUpDetaill(final String strListOrderIDD) {
        myDialog.setContentView(R.layout.form_list_order_detail);

        tvOrderIDD = myDialog.findViewById(R.id.tvOrderID_Detail);
        tvStatusOrderr = myDialog.findViewById(R.id.tvStatusOrder_Detail);
        tvTotalKgg = myDialog.findViewById(R.id.tvTotalKg_Detail);
        tvTotalPointss = myDialog.findViewById(R.id.tvTotalPoints_Detail);
        lvListDetailOrderr = myDialog.findViewById(R.id.listView_OrderDetails);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        getDetailOrderLinee(strListOrderIDD);

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }
    private void EditData(final String strListOrderIDD) {
        myDialog.setContentView(R.layout.lv_edit_data);

//        tvOrderIDD = myDialog.findViewById(R.id.tvOrderID_Detail);
//        tvStatusOrderr = myDialog.findViewById(R.id.tvStatusOrder_Detail);
        tvJenis_sampah = myDialog.findViewById(R.id.etJenis_item);
        tvBerat_item = myDialog.findViewById(R.id.etBerat_item);
        tvPointsss = myDialog.findViewById(R.id.etPointss);
//        tvTotalPointss = myDialog.findViewById(R.id.tvTotalPoints_Detail);
//        tvAlamat = myDialog.findViewById(R.id.tvAlamat_Detail);
//        lvListDetailOrderr = myDialog.findViewById(R.id.listView_OrderDetails);

        getDetailOrderLinee(strListOrderIDD);

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void getEditorder(final String strListOrderIDD) {
        String base_url = apiData.get("str_url_address") + (".update_item_order");
        final String[] field_name = {"id_order"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                customProgress.hideProgress();
                Log.d(TAG, "Get List Order Response: " + response);
                try {
                    displayOrderDetaill(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_409) + " 1: " + activity.getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get List Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_500) + " 1: " + activity.getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strListOrderIDD);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }



    private void getDetailOrderLinee(final String strListOrderIDD) {
        String base_url = apiData.get("str_url_address") + (".get_detail_order");
        final String[] field_name = {"id_order"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                customProgress.hideProgress();
                Log.d(TAG, "Get List Order Response: " + response);
                try {
                    displayOrderDetaill(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_409) + " 1: " + activity.getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get List Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_500) + " 1: " + activity.getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strListOrderIDD);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void displayEditData(String resp_content) {
        String[] field_name = {"message", "id_order", "jenis_item", "berat_total", "harga_per_kilo", "creation", "berat_item"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allOrderDetail = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String id_order = c.getString(field_name[1]);
                String jenis_item = c.getString(field_name[2]);
                String berat_total = c.getString(field_name[3]);
                String harga_per_kilo = c.getString(field_name[4]);
                String creation = c.getString(field_name[5]);
                String berat_item = c.getString(field_name[6]);
                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], id_order);
                map.put(field_name[2], jenis_item);
                map.put(field_name[3], berat_total);
                map.put(field_name[4], harga_per_kilo);
                map.put(field_name[5], creation);
                map.put(field_name[6], berat_item);


                allOrderDetail.add(map);

            }

            SimpleAdapter adapter74 = new SimpleAdapter(activity, allOrderDetail, R.layout.lv_edit_data,
                    new String[]{"harga_per_kilo", "jenis_item", "berat_item"}, new int[]{R.id.etPointss, R.id.etJenis_item, R.id.etBerat_item});
            adapter74.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.tvPoints_OrderList) {
                        TextView tv = (TextView) view;
                        try {
                            tv.setText(decimalFormat_Point.format(Double.valueOf(textRepresentation)) + " Pts");
                        } catch (NumberFormatException e) {
                            Toast.makeText(activity,
                                    "Pts point", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (view.getId() == R.id.etBerat_item) {
                        TextView tv = (EditText) view;
                        try {
                            tv.setText(decimalFormat.format(Double.valueOf(textRepresentation)) + " Kg");
                        } catch (NumberFormatException e) {
                            Toast.makeText(activity,
                                    "kg", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            });
            lvListDetailOrderr.setAdapter(adapter74);

        } catch (JSONException e) {
            Toasty.error(activity, "Terjadi Kesalahan 1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void displayOrderDetaill(String resp_content) {
        String[] field_name = {"message", "id_order", "jenis_item", "berat_total", "harga_per_kilo", "creation", "berat_item"};

        try {
            JSONObject jsonObject = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allOrderDetail = new ArrayList<>();

            JSONArray cast = jsonObject.getJSONArray(field_name[0]);
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                String id_order = c.getString(field_name[1]);
                String jenis_item = c.getString(field_name[2]);
                String berat_total = c.getString(field_name[3]);
                String harga_per_kilo = c.getString(field_name[4]);
                String creation = c.getString(field_name[5]);
                String berat_item = c.getString(field_name[6]);
                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[2], jenis_item);
                map.put(field_name[3], berat_total);
                map.put(field_name[4], harga_per_kilo);
                map.put(field_name[6], berat_item);


                allOrderDetail.add(map);

            }

            SimpleAdapter adapter77 = new SimpleAdapter(activity, allOrderDetail, R.layout.lv_detail_order_list,
                    new String[]{"harga_per_kilo", "jenis_item", "berat_item"}, new int[]{R.id.tvPoints_OrderList, R.id.tvJenisSampah_OrderList, R.id.tvBeratSampah_OrderList});
            adapter77.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.tvPoints_OrderList) {
                        TextView tv = (TextView) view;
                        try {
                            tv.setText(decimalFormat_Point.format(Double.valueOf(textRepresentation)) + " Pts");
                        } catch (NumberFormatException e) {
                            Toast.makeText(activity,
                                    "Pts point", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (view.getId() == R.id.tvBeratSampah_OrderList) {
                        TextView tv = (TextView) view;
                        try {
                            tv.setText(decimalFormat.format(Double.valueOf(textRepresentation)) + " Kg");
                        } catch (NumberFormatException e) {
                            Toast.makeText(activity,
                                    "kg", Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            });
            lvListDetailOrderr.setAdapter(adapter77);

        } catch (JSONException e) {
            Toasty.error(activity, "Terjadi Kesalahan 1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void showPopupAcceptOrderr(final String strOrderIDD, final int position) {
        myDialog.setContentView(R.layout.pop_up_delete_incoming_order_line);

        Button btnDelete_Confirmation = myDialog.findViewById(R.id.btnDelete_Confirmation);
        Button btnCancel_Confirmation = myDialog.findViewById(R.id.btnCancel_Confirmation);

        TextView tvTitle_Delete = myDialog.findViewById(R.id.tvTitle_Confirmation);
        tvTitle_Delete.setText("Apakah Ingin Menyelesaikan Order?");

        btnDelete_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptOrderr(strOrderIDD, position);
                myDialog.dismiss();
            }
        });

        btnCancel_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }
    private void AcceptOrderr(final String strOrderIDD, final int position) {
        String base_url = apiData.get("str_url_address") + (".finish_order");
        final String[] field_name = {"id_order"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                customProgress.hideProgress();
                Log.d(TAG, "Get List Order Response: " + response);
                try {
                    displayOrderDetaill(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_409) + " 1: " + activity.getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get List Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_500) + " 1: " + activity.getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strOrderIDD);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void showPopupCancelOrderr(final String strOrderIDD, final int position) {
        myDialog.setContentView(R.layout.pop_up_delete_incoming_order_line);

        Button btnDelete_Confirmation = myDialog.findViewById(R.id.btnDelete_Confirmation);
        Button btnCancel_Confirmation = myDialog.findViewById(R.id.btnCancel_Confirmation);

        TextView tvTitle_Delete = myDialog.findViewById(R.id.tvTitle_Confirmation);
        tvTitle_Delete.setText("Apakah Anda Yakin Membatalkan Order Limbah Sampah Tersebut?");

        btnDelete_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelOrderr(strOrderIDD, position);
                myDialog.dismiss();
            }
        });

        btnCancel_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void CancelOrderr(final String strOrderIDD, final int position) {
        String base_url = apiData.get("str_url_address") + (".cancel_member_order");
        final String[] field_name = {"id_order"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                customProgress.hideProgress();
                Log.d(TAG, "Get List Order Response: " + response);
                try {
                    displayOrderDetaill(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_409) + " 1: " + activity.getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get List Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
//                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.parent), activity.getString(R.string.MSG_CODE_500) + " 1: " + activity.getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strOrderIDD);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }
    //<---------------------- Fragment Position 11 - Incoming Order ---------------------->

}

