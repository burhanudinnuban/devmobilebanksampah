package com.demo.user.banksampah.KantongFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Activities.QRScanActivity;
import com.demo.user.banksampah.Activities.TrashDetail;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static android.content.Context.MODE_PRIVATE;

public class SimpanFragment extends Fragment {

    private final String TAG = SimpanFragment.class.getSimpleName();
    protected LinearLayout parent_layout;

    //Session Class
    protected PrefManager session;
    protected String strIdBankSampah, strIdUser;

    //Inflate for Home Fragment
    public static void newInstance() {
        new SimpanFragment();
    }

    /*API process and dialog*/
    private RestProcess rest_class;
    protected HashMap<String,String> apiData;

    //ListView for Jenis Sampah and Adapter
    private ListView lvJenisSampah;
    private LazyAdapter adapter;
    ArrayList<String> JenisSampah_Arr;

    //ImageView Initiation
    protected ImageView imageView, dropdownList;

    //LinearLayout Initiation
    protected LinearLayout linear1, linearLayout_daftarSampah;
    protected LinearLayout linearLayout_Barcode;

    protected CardView cd_noData;

    //TextView Initiation
    protected TextView edttrash,tvPoints, tvReload;
    protected CardView cdBarcode;

    //LazyAdapter adapter;
    ArrayList<HashMap<String, String>> barcodeStatus = new ArrayList<>();
    ArrayList<HashMap<String, String>> arrCheckOrder = new ArrayList<>();
    ArrayList<HashMap<String, String>> point = new ArrayList<>();
    private HashMap<String, String> var_trash_data = new HashMap<>();

    protected DecimalFormat decimalFormat;
    private final static int BARCODE_REQUEST_CODE = 1;

    protected String contents, format;

    //Create Dialog
    private Dialog myDialog;
    protected ConnectivityManager conMgr;

    private CustomProgress customProgress;

    protected TextView tvTitle_Popup;
    protected Button btnOk_Popup;
    protected String strInputKg = "", strInputPcs = "", strConvertedKg = "";

    //Check If Order Exist or Not from API
    protected String getIdUser_Result = "";
    protected String getNameOrder_Result = "";
    protected String getMessage_Result = "";

    //Initiation Variable for Barcode PopUp -->
    private TextView tvTipe, tvNoBarcode, tvPerusahaan, tvProduk, tvHarga, tvCompare, tvBeratBersih;
    private TextView tvTakTerdaftar;

    private EditText etInputKg, etInputPcs, etCalculate;
    protected double getTotalPoint = 0;

    //Initiation for Barcode Data
    private EditText etInputBarcode;
    Button btnCariBarcode;
    String strInputBarcode;

    String strMessage, strJenis, strTipe, strNamaPerusahaan, strMerk, strPointKg, strBarcodeNumber, strBeratBersih;

    private LinearLayout linearLayout_ProdukTerdaftar;
    private CardView cd_ProdukTakTerdaftar_PopUp;

    private Button btnSimpanSampah, btnOrderBaru;
    protected Button btnFavorite;
    private CardView cdDataBarcode, cdInputKg, cdCalculated;
    //Initiation Variable for Barcode PopUp <--

    protected Context context;

    //Showcase Testing
    protected View view_barcode, view_Favorite;
    private GuideView mGuideView;
    private GuideView.Builder builder;

    @SuppressLint({"WrongViewCast", "SetTextI18n", "CutPasteId"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_simpan, container, false);
        context = getContext();

        parent_layout = rootView.findViewById(R.id.parent);

        //*SharedPref User
        session = new PrefManager(getContext());

        //**Get ID User
        HashMap<String,String> user = session.getUserDetails();
        strIdUser = user.get(PrefManager.KEY_ID);

        //**Get Bank Sampah User
        HashMap<String,String> id_bank_sampah = session.getUserDetails();
        strIdBankSampah = id_bank_sampah.get(PrefManager.KEY_NAMA);

        decimalFormat = new DecimalFormat(",###.##");

        //Rest API and Custom Dialog
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();
        tvPoints = rootView.findViewById(R.id.tvPoints);
        tvReload = rootView.findViewById(R.id.tvReload);
        imageView =  rootView.findViewById(R.id.imageView);
        dropdownList =  rootView.findViewById(R.id.dropdownboongan);
        lvJenisSampah =  rootView.findViewById(R.id.lvJenisSampah);
        cd_noData = rootView.findViewById(R.id.cardView_noData);

        linear1 =  rootView.findViewById(R.id.linear1);
        linearLayout_daftarSampah = rootView.findViewById(R.id.linearLayout_daftarSampah);

        linearLayout_Barcode = rootView.findViewById(R.id.LinearLayout_Scanner);

        cdBarcode = rootView.findViewById(R.id.cardView_Barcode);

        //Showcase..
        view_barcode = rootView.findViewById(R.id.cardView_Barcode);
        view_Favorite = rootView.findViewById(R.id.cardView_Favorite);

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            myDialog = new Dialog(getActivity());
        }

        edttrash =  rootView.findViewById(R.id.edttrash);
        edttrash.setInputType(InputType.TYPE_NULL);

        JenisSampah_Arr = new ArrayList<>();

        //If Trash Clicked.. Show Custom Spinner
        edttrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                //If ListView visible, then set to Gone..
                if (linearLayout_daftarSampah.getVisibility() == View.VISIBLE) {
                    linearLayout_daftarSampah.setVisibility(View.GONE);
                    dropdownList.setRotation(0);

                    //cdBarcode.setVisibility(View.VISIBLE);
                }else {
                    dropdownList.setRotation(180);
                    linearLayout_daftarSampah.setVisibility(View.VISIBLE);

                    //cdBarcode.setVisibility(View.GONE);

                    //If Items Clicked.. go to Another Page
                    lvJenisSampah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            var_trash_data = (HashMap<String, String>)adapter.getItem(i);
                            edttrash.setText(var_trash_data.get("name"));
                            linearLayout_daftarSampah.setVisibility(View.GONE);

                            //Start Activity
                            String jenisSampah = edttrash.getText().toString();
                            Intent intent = new Intent(getContext(), TrashDetail.class);
                            intent.putExtra("detailSampah",jenisSampah);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        //If Barcode Icon Clicked... Then do Something
        linearLayout_Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpBarcode();
            }
        });

        cd_noData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tag", "view Refresh");
                getJenisSampah();
            }
        });

        tvReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                    getPointUser();
                } else {
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
//        checkFirstRun();
        super.onStart();
    }

//    @Override
//    public void onResume(){
////        getJenisSampah();
//        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
////            getPointUser();
//        } else {
//            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//            tvReload.setVisibility(View.VISIBLE);
//        }
//        super.onResume();
//    }

    private void showPopUpBarcode(){
        myDialog.setContentView(R.layout.pop_up_select_barcode);

        TextView tvScanBarcode = myDialog.findViewById(R.id.tvScanBarcode_PopUp);
        TextView tvScanManual = myDialog.findViewById(R.id.tvManualBarcode_PopUp);

        tvScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent_barcode = new Intent (getActivity(), QRScanActivity.class);
                    intent_barcode.putExtra("SCAN_MODE", "BARCODE_MODE");
                    startActivityForResult(intent_barcode, BARCODE_REQUEST_CODE);
                } catch (ActivityNotFoundException anfe){
                    showDialogBarcode(getActivity()
                    ).show();
                }
                myDialog.dismiss();
            }
        });

        tvScanManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputBarcode();
                //myDialog.dismiss();
            }
        });

        //Customization for Dialog..
        if(myDialog.getWindow()!= null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void showInputBarcode(){
        myDialog.setContentView(R.layout.pop_up_input_barcode);

        //Initiation Variable
        etInputBarcode = myDialog.findViewById(R.id.etInputBarcode_PopUp);
        btnCariBarcode = myDialog.findViewById(R.id.btnCariBarcode_PopUp);

        btnCariBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        if(myDialog.getWindow()!= null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void validateData(){
        strInputBarcode = etInputBarcode.getText().toString();
        if (strInputBarcode.isEmpty()){
            etInputBarcode.setError("Harap Masukkan Data Barcode");
            etInputBarcode.requestFocus();
        }else{
            contents = strInputBarcode;
            checkBarcodeData(contents);
        }
    }

    public void BarcodePopUp(){
        myDialog.setContentView(R.layout.pop_up_barcode);

        cdDataBarcode =  myDialog.findViewById(R.id.cardView_DataBarcode);
        tvProduk = myDialog.findViewById(R.id.tvNamaProduk_PopUp);
        tvTipe =  myDialog.findViewById(R.id.tvjenissampah_PopUp);
        tvNoBarcode =  myDialog.findViewById(R.id.tvNoBarcode_PopUp);
        tvPerusahaan =  myDialog.findViewById(R.id.tvharga_per_kilo_PopUp);
        tvHarga =  myDialog.findViewById(R.id.tvPoints);
        btnSimpanSampah = myDialog.findViewById(R.id.btnTerima_PopUp);
        btnOrderBaru = myDialog.findViewById(R.id.btnTolakOrder_PopUp);

        //Check If User Already Have Order or Not
        checkOrder();

        btnSimpanSampah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();
            }
        });

        btnOrderBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();
            }
        });

        //If Favorite clicked.. Dialog Dismiss
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        //Customization for Dialog..
        if(myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }

        etInputPcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                etCalculate.setText(calculated());
//            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etInputPcs.getText().toString().equals("")){
                    tvCompare.setText("=");
                    cdCalculated.setVisibility(View.VISIBLE);
                    cdInputKg.setVisibility(View.GONE);
                }else{
                    cdInputKg.setVisibility(View.VISIBLE);
                    cdCalculated.setVisibility(View.GONE);
                    tvCompare.setText("Atau");
                }
            }
        });

        etInputKg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!etInputKg.getText().toString().equals("")){
                    etInputPcs.setEnabled(false);
                }else{
                    etInputPcs.setEnabled(true);
                }
            }
        });

    }

    protected void validasiData(){
        //Get Data Input from User in Pop Up
        strInputKg = etInputKg.getText().toString();
        strInputPcs = etInputPcs.getText().toString();
        strConvertedKg = etCalculate.getText().toString();

        if (!strInputPcs.equals("")){
            strInputKg = strConvertedKg;
            strInputKg = strInputKg.replace(',','.');
        }

        if (TextUtils.isEmpty(strInputPcs) && TextUtils.isEmpty(strInputKg)){
            if(getContext()!=null) {
                Toasty.warning(getContext(), "Harap Memasukkan Jumlah Pcs atau Kilo Sampah", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        try {
            getTotalPoint = Double.valueOf(strInputKg) * Double.valueOf(strPointKg);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        //Classified Input to DB if Order ID Already Exist or Not
        if (getMessage_Result.equals("True")){
            SimpanOrder();
        } else if (getMessage_Result.equals("False")){
            OrderBaru();
        }
    }

    private void OrderBaru(){
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"message", "name", "berat_total", "id_user", "nama", "point_total"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "str_api_scan_order";
        params.put(field_name[0], MainActivity.strID);
        params.put(field_name[1], strJenis);
        params.put(field_name[2], strTipe);
        params.put(field_name[3], "0");
        params.put(field_name[4], strInputKg);
        params.put(field_name[5], getTotalPoint);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    ShowSuccessPopup();
                    tvTitle_Popup.setText(getString(R.string.MSG_SUCCESS_INPUT_FIRST_INCOMING_ORDER));
                } catch (Throwable t) {
                    if(getContext()!=null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if(getContext()!=null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag"," 1: " + String.valueOf(error));
            }
        });
    }

    //Save Data to Database if User Have Order
    private void SimpanOrder(){
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"message"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + ".scan_order";
        params.put(field_name[0], getNameOrder_Result);
        params.put(field_name[1], strJenis);
        params.put(field_name[2], strTipe);
        params.put(field_name[3], "0");
        params.put(field_name[4], strInputKg);
        params.put(field_name[5], getTotalPoint);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    ShowSuccessPopup();
                    tvTitle_Popup.setText(getString(R.string.MSG_SUCCESS_INPUT_INCOMING_ORDER));
                } catch (Throwable t) {
                    if(getContext()!=null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 2 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if(getContext()!=null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag"," 2: " + String.valueOf(error));
            }
        });
    }

//    private String calculated(){
//        double calculated_getPcs, calculated_getBeratBersih, calculated_getTotal;
//
//        String getInputPcs = etInputPcs.getText().toString();
//        String calculated_beratBersih = strBeratBersih;
//
//        String calculated_pcs;
//
//        if (getInputPcs.equals("")){
//            calculated_pcs = "0";
//        } else {
//            calculated_pcs = getInputPcs;
//        }
//
//        calculated_getPcs = Double.valueOf(calculated_pcs);
//        calculated_getBeratBersih = Double.valueOf(calculated_beratBersih);
//
//        calculated_getTotal = calculated_getPcs * calculated_getBeratBersih;
//
//        return (String.valueOf(new DecimalFormat("##.##").format(calculated_getTotal)));
//    }


    //Alert For Downloading Barcode Scanner Apps
    private static AlertDialog showDialogBarcode(final FragmentActivity act){
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle("Aplikasi Kamera Scanner Tidak Ditemukan");
        dialog.setMessage("Apakah Anda ingin Mengunduh Barcode Scanner?");
        dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent_view = new Intent(Intent.ACTION_VIEW, uri);
                try{
                    act.startActivity(intent_view);
                }catch (ActivityNotFoundException anfe){
                    anfe.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode == Activity.RESULT_OK){
            //Do Scan Barcode
            if (requestCode == BARCODE_REQUEST_CODE){
                //Content equals Barcode Number
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if(getContext()!=null) {
                    Toasty.info(getContext(), "Kode Barcode Produk: " + contents, Toast.LENGTH_LONG).show();
                }

                //Check Barcode data From DB
                checkBarcodeData(contents);

                //Check If User Already Have Order or Not
                checkOrder();
            }
        }
    }

    //Check Barcode data From DB
    private void checkBarcodeData(final String contents){
        customProgress.showProgress(getContext(), "", false);

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_get_barcode");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Barcode Response: " + response);
                try {
                    getBarcode(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Login Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: "+ error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("no_barcode", contents);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void getBarcode(String resp_content){
        String[] field_name = {"message", "berat_bersih", "nama_perusahaan", "merk", "point_per_kilo", "no_barcode", "jenis_item"};

        //Get Message from API
        try{
            barcodeStatus = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);
            strMessage = jsonPost.getString(field_name[0]);

            //If data Exist...
            if(strMessage.equals("True")){
                BarcodePopUp();

                linearLayout_ProdukTerdaftar.setVisibility(View.VISIBLE);
                tvProduk.setVisibility(View.VISIBLE);
                cdDataBarcode.setVisibility(View.VISIBLE);

                cd_ProdukTakTerdaftar_PopUp.setVisibility(View.GONE);
                tvTakTerdaftar.setVisibility(View.GONE);

                strBeratBersih = jsonPost.getString(field_name[1]);
                strNamaPerusahaan = jsonPost.getString(field_name[2]);
                strMerk = jsonPost.getString(field_name[3]);
                strPointKg = jsonPost.getString(field_name[4]);
                strBarcodeNumber = jsonPost.getString(field_name[5]);
                strJenis = jsonPost.getString(field_name[6]);

                tvPerusahaan.setText(strNamaPerusahaan);
                tvProduk.setText(strMerk);
                tvHarga.setText("Rp. " + strPointKg);
                tvNoBarcode.setText(strBarcodeNumber);
                tvTipe.setText(strJenis);
                tvBeratBersih.setText(strBeratBersih + " gram");

                //If data Non-exist
            } else{
                BarcodePopUp();

                linearLayout_ProdukTerdaftar.setVisibility(View.GONE);
                tvProduk.setVisibility(View.GONE);
                cdDataBarcode.setVisibility(View.GONE);

                cd_ProdukTakTerdaftar_PopUp.setVisibility(View.VISIBLE);

                tvProduk.setText("");
                tvNoBarcode.setText("");
                tvPerusahaan.setText("");
                tvTipe.setText("");
                tvHarga.setText("");
            }

        } catch (JSONException e){
            Snackbar snackbar = Snackbar
                    .make(parent_layout, getString(R.string.MSG_CODE_500) + " 2: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.e(TAG, "JSON Exception Error: " + e.toString());
        }
    }

    //Get Jenis Sampah From DB
    private void getJenisSampah(){
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_get_jenis_sampah");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Get Jenis Sampah Response: " + response);
                try {
                    displayTrash(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Get Jenis Sampah Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: "+ error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();

                cd_noData.setVisibility(View.VISIBLE);
                lvJenisSampah.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void displayTrash(String resp_content) {
        // JSON Field Names
        String[]field_name = {"message", "name", "image"};

        try {
            JSONObject jsonPost = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allNames = new ArrayList<>();

            JSONArray cast = jsonPost.getJSONArray(field_name[0]);
            for (int i=0; i<cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                // Post result field to string
                String kategori_result = c.getString(field_name[1]);
                //String image_result = c.getString(field_name[2]);

                // Make HashMap string for put string above
                HashMap<String, String> map = new HashMap<>();
                map.put(field_name[1], kategori_result);

                allNames.add(map);
            }

            // Call Lazy Adapter for Listview
            adapter = new LazyAdapter(getActivity(), allNames, 1);
            lvJenisSampah.setAdapter(adapter);

            cd_noData.setVisibility(View.GONE);
            lvJenisSampah.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(parent_layout, getString(R.string.MSG_CODE_500) + " 2: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.d(TAG, "JSON Exception Error: " + e.toString());
        }
    }

    private void checkOrder(){
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_check_incoming_order");
        final String[]field_name = {"id_incoming_order", "id_bank_sampah"};

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Check Order Response: " + response);
                try {
                    viewFromDB(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Check Order Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: "+ error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strIdUser);
                params.put(field_name[1], strIdBankSampah);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void viewFromDB(String resp_content){
        String[] field_name = {"id_user", "name", "message"};

        try{
            arrCheckOrder = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);
            getMessage_Result = jsonPost.getString(field_name[2]);

            //Model Benarnya...
            if(getMessage_Result.equals("False")) {
                btnOrderBaru.setVisibility(View.VISIBLE);
                btnSimpanSampah.setVisibility(View.GONE);
            }
            else if(getMessage_Result.equals("True")){
                getIdUser_Result = jsonPost.getString(field_name[0]);
                getNameOrder_Result = jsonPost.getString(field_name[1]);

                btnSimpanSampah.setVisibility(View.VISIBLE);
                btnOrderBaru.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.d(TAG,"Error JSONException in Check Order: " + e.toString());
        }

    }

    private void getPointUser(){
        final String[]field_name = {"id_user"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_get_point");
        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Point User Response: " + response);
                try {
                    viewPoint(response);
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Point User Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: "+ error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strIdUser);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(apiData.get("str_header"), apiData.get("str_token_value"));
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, apiData.get("str_json_obj"));
    }

    private void viewPoint(String resp_content){
        String[]field_name = {"message"};
        //Get Message from API
        try{
            point = rest_class.getJsonData(field_name, resp_content);

            JSONObject jsonPost = new JSONObject(resp_content);
            strMessage = jsonPost.getString(field_name[0]);

            //If data Exist...
            if(!strMessage.isEmpty()){
                String getPoint = jsonPost.getString(field_name[0]);
                try {
                    tvPoints.setText("Rp. " + decimalFormat.format(Double.valueOf(getPoint)));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                tvReload.setVisibility(View.GONE);
            }
            //If data Non-exist
            else{
                tvReload.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e){
            tvReload.setVisibility(View.VISIBLE);
            Log.d(TAG,"Error JSONException GetPoint User: " + e.toString());
        }
    }

    public void ShowSuccessPopup(){
        myDialog.setContentView(R.layout.pop_up_success);

        //Init Data
        btnOk_Popup = myDialog.findViewById(R.id.btnOk_Confirmation);
        tvTitle_Popup = myDialog.findViewById(R.id.tvTitle_Confirmation);

        myDialog.setCanceledOnTouchOutside(false);

        btnOk_Popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

                if(getActivity()!=null) {
                    Intent intent = getActivity().getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        //Customization for Dialog..
        if(myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void checkFirstRun() {
        final String PREFS_NAME = "myPrefsFile";
        final String PREF_VERSION_CODE = "version_code";
        final int DOESNT_EXIST = -1;

        //Get Current Version
        int currentVersionCode = BuildConfig.VERSION_CODE;

        //Get Saved version code
        if (getActivity() != null) {
            SharedPreferences prefs_check = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int savedVersionCode = prefs_check.getInt(PREF_VERSION_CODE, DOESNT_EXIST);

            //Check first run or upgrade
            //Normal run...
            if (currentVersionCode == savedVersionCode) {
                //Jika...
            }
            //Jika baru install ulang atau session dihapus
            else if (savedVersionCode == DOESNT_EXIST) {
                builder = new GuideView.Builder(getActivity())
                        .setTitle("Pindai Barcode")
                        .setContentText("Pindai Limbah Anda dengan Barcode yang Tertera di Limbah Anda")
                        .setGravity(Gravity.center)
                        .setDismissType(DismissType.outside)
                        .setTargetView(view_barcode)
                        .setGuideListener(new GuideListener() {
                            @Override
                            public void onDismiss(View view) {
                                switch (view.getId()) {
                                    case R.id.cardView_Barcode:
                                        builder.setTargetView(view_Favorite)
                                                .setTitle("Limbah Favorite")
                                                .setContentText("Cari Jenis Limbah Anda di Favorite dan Pilih " +
                                                        "Sesuai dengan Limbah yang Anda Miliki")
                                                .build();
                                        break;

                                    case R.id.cardView_Favorite:
                                        return;
                                }
                                mGuideView = builder.build();
                                mGuideView.show();
                            }
                        });
                mGuideView = builder.build();
                mGuideView.show();
            }
            //Jika diupdate
            else if (currentVersionCode > savedVersionCode) {
                //Jika...
            }
            ((SharedPreferences) prefs_check).edit().putInt(PREF_VERSION_CODE, currentVersionCode).apply();
        }
    }
}