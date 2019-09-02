package com.demo.user.banksampah.MainFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.demo.user.banksampah.Activities.QRScanActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.TrackGPS;
import com.demo.user.banksampah.Activities.TrashDetail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static android.content.Context.MODE_PRIVATE;

public class SalesFragment extends Fragment implements View.OnClickListener {

    //Inflate for Home Fragment
    public static void newInstance() {
        new SalesFragment();
    }

    /*API process and dialog*/
    private RestProcess rest_class;
    protected HashMap<String, String> apiData;

    //ListView for Jenis Sampah and Adapter
    private ListView lvJenisSampah;
    private LazyAdapter adapter;
    ArrayList<String> JenisSampah_Arr;

    //ImageView Initiation
    private ImageView imageView, dropdownList;

    //LinearLayout Initiation
    protected LinearLayout linear1, linearLayout_daftarSampah;
    protected LinearLayout linearLayout_Barcode;

    protected CardView cd_noData;

    //TextView Initiation
    protected TextView edttrash, tvPoints, tvReload;
    protected CardView cdBarcode;

    //LazyAdapter adapter;
    ArrayList<HashMap<String, String>> barcodeStatus = new ArrayList<>();
    ArrayList<HashMap<String, String>> arrCheckOrder = new ArrayList<>();
    ArrayList<HashMap<String, String>> point = new ArrayList<>();
    private HashMap<String, String> var_trash_data = new HashMap<>();

    /*FOR GPS*/
    protected TrackGPS gps;
    double longitude;
    double latitude;

    protected DecimalFormat decimalFormat;

    //Request Code
    private final static int BARCODE_REQUEST_CODE = 1;

    protected String contents, format;

    //Create Dialog
    private Dialog myDialog;
    protected ConnectivityManager conMgr;
    private ProgressDialog progressDialog;

    private CustomProgress customProgress;

    protected TextView tvTitle_Popup;
    protected Button btnOk_Popup;
    protected String strInputKg = "", strInputPcs = "", strConvertedKg = "";

    //Check If Order Exist or Not from API
    protected String getIdUser_Result = "";
    protected String getNameOrder_Result = "";
    protected String getMessage_Result = "";

    //Initiation Variable for Barcode PopUp -->
    private TextView tvTipe, tvNoBarcode, tvPerusahaan, tvProduk, tvJenis, tvCompare;
    private TextView tvTakTerdaftar;

    private EditText etInputKg, etInputPcs, etCalculate;
    protected double getTotalPoint = 0;

    //Initiation for Barcode Data
    private EditText etInputBarcode;
    Button btnCariBarcode;
    String strInputBarcode;

    String strMessage, strJenis, strTipe, strNamaPerusahaan, strPointPcs,
            strMerk, strPointKg, strBarcodeNumber, strBeratBersih;

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

    protected TextView tvTapMe;
    protected CardView cdFavorite, cdKantong, cdListOrder;

    //ExpandableLayout
    private ExpandableLayout expandableLayout_Favorite;
    private ExpandableLayout expandableLayout_Kantong;
    private ExpandableLayout expandableLayout_ListOrder;

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_sales, container, false);
        context = getContext();

        tvTapMe = rootView.findViewById(R.id.tvTapMe);
        cdFavorite = rootView.findViewById(R.id.cdFavorite);
        cdKantong = rootView.findViewById(R.id.cdKantong);
        cdListOrder = rootView.findViewById(R.id.cdListOrder);

        tvTapMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cdFavorite.getVisibility() == View.VISIBLE && cdKantong.getVisibility() == View.VISIBLE && cdKantong.getVisibility() == View.VISIBLE && cdListOrder.getVisibility() == View.VISIBLE) {
                    cdFavorite.setVisibility(View.GONE);
                    cdKantong.setVisibility(View.GONE);
                    cdListOrder.setVisibility(View.GONE);
                    v.animate().setDuration(1000);
                } else {
                    cdFavorite.setVisibility(View.VISIBLE);
                    cdKantong.setVisibility(View.VISIBLE);
                    cdListOrder.setVisibility(View.VISIBLE);
                    v.animate().setDuration(1000);
                }
            }
        });

        expandableLayout_Favorite = rootView.findViewById(R.id.expandable_layout_favorite);
        expandableLayout_Kantong = rootView.findViewById(R.id.expandable_layout_kantong);
        expandableLayout_ListOrder = rootView.findViewById(R.id.expandable_layout_listOrder);
        expandableLayout_Favorite.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.e("tag", "State Fav: " + state);
            }
        });

        expandableLayout_Kantong.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.e("tag", "State Kantong: " + state);
            }
        });

        expandableLayout_ListOrder.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.e("tag", "State List Order: " + state);
            }
        });

        rootView.findViewById(R.id.tvFavorite).setOnClickListener(this);
        rootView.findViewById(R.id.tvKantong).setOnClickListener(this);
        rootView.findViewById(R.id.tvListOrder).setOnClickListener(this);

        decimalFormat = new DecimalFormat(",###.##");
        imageView = rootView.findViewById(R.id.imageView);
        dropdownList = rootView.findViewById(R.id.dropdownboongan);
        lvJenisSampah = rootView.findViewById(R.id.lvJenisSampah);
        cd_noData = rootView.findViewById(R.id.cardView_noData);

        linear1 = rootView.findViewById(R.id.linear1);
        linearLayout_daftarSampah = rootView.findViewById(R.id.linearLayout_daftarSampah);

        linearLayout_Barcode = rootView.findViewById(R.id.LinearLayout_Scanner);

        cdBarcode = rootView.findViewById(R.id.cardView_Barcode);

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        edttrash = rootView.findViewById(R.id.edttrash);
        edttrash.setInputType(InputType.TYPE_NULL);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        //*Create Dialog Pop Up
        if (getActivity() != null) {
            myDialog = new Dialog(getActivity());
        }

        progressDialog = new ProgressDialog(getContext());
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
                } else {
                    dropdownList.setRotation(180);
                    linearLayout_daftarSampah.setVisibility(View.VISIBLE);

                    //cdBarcode.setVisibility(View.GONE);

                    //If Items Clicked.. go to Another Page
                    lvJenisSampah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            var_trash_data = (HashMap<String, String>) adapter.getItem(i);
                            edttrash.setText(var_trash_data.get("name"));
                            linearLayout_daftarSampah.setVisibility(View.GONE);

                            //Start Activity
                            String jenisSampah = edttrash.getText().toString();
                            Intent intent = new Intent(getContext(), TrashDetail.class);
                            intent.putExtra("detailSampah", jenisSampah);
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

        //Showcase..
        view_barcode = rootView.findViewById(R.id.cardView_Barcode);
        view_Favorite = rootView.findViewById(R.id.cardView_Favorite);

//        checkFirstRun();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvFavorite) {
            /*expandableLayout_Favorite.expand();
            expandableLayout_Kantong.collapse();
            expandableLayout_ListOrder.collapse();*/
            expandableLayout_Favorite.toggle();
            if (expandableLayout_Favorite.isExpanded()) {
                expandableLayout_Kantong.collapse();
                expandableLayout_ListOrder.collapse();
            }
        } else if (view.getId() == R.id.tvKantong) {
           /* expandableLayout_Kantong.expand();
            expandableLayout_Favorite.collapse();
            expandableLayout_ListOrder.collapse();*/
            expandableLayout_Kantong.toggle();
            if (expandableLayout_Kantong.isExpanded()) {
                expandableLayout_Favorite.collapse();
                expandableLayout_ListOrder.collapse();
            }
        } else {
            /*expandableLayout_Kantong.collapse();
            expandableLayout_Favorite.collapse();
            expandableLayout_ListOrder.expand();*/
            expandableLayout_ListOrder.toggle();
            if (expandableLayout_ListOrder.isExpanded()) {
                expandableLayout_Favorite.collapse();
                expandableLayout_Kantong.collapse();
            }
        }
    }

    @Override
    public void onResume() {
//        getJenisSampah();
//        getCurrentLocationRoute();
        super.onResume();
    }

    private void showPopUpBarcode() {
        myDialog.setContentView(R.layout.pop_up_select_barcode);

        Button btnCamera_Barcode = myDialog.findViewById(R.id.tvScanBarcode_PopUp);
        Button btnManual_Barcode = myDialog.findViewById(R.id.tvManualBarcode_PopUp);
        Button btnCancel_Barcode = myDialog.findViewById(R.id.btnCancel_BarcodePopUp);

        btnCamera_Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent_barcode = new Intent(getActivity(), QRScanActivity.class);
                    //Intent intent_barcode = new Intent(ACTION_SCAN);
                    intent_barcode.putExtra("SCAN_MODE", "BARCODE_MODE");
                    startActivityForResult(intent_barcode, BARCODE_REQUEST_CODE);
                } catch (ActivityNotFoundException anfe) {
                    showDialogBarcode(getActivity(), "Aplikasi Kamera Scanner Tidak Ditemukan",
                            "Apakah Anda ingin Mengunduh Barcode Scanner?", "Ya", "Tidak").show();
                }
                myDialog.dismiss();
            }
        });

        btnManual_Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputBarcode();
                //myDialog.dismiss();
            }
        });

        btnCancel_Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        //Customization for Dialog..
        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void showInputBarcode() {
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

        if (myDialog.getWindow() != null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void validateData() {
        strInputBarcode = etInputBarcode.getText().toString();
        if (strInputBarcode.isEmpty()) {
            etInputBarcode.setError("Harap Masukkan Data Barcode");
            etInputBarcode.requestFocus();
        } else {
            contents = strInputBarcode;
            checkBarcodeData(contents);
        }
    }

    public void BarcodePopUp() {
        myDialog.setContentView(R.layout.pop_up_barcode);

        //Initiation if Data Exist -->
        cdDataBarcode = myDialog.findViewById(R.id.cardView_DataBarcode);
//        cdCalculated = myDialog.findViewById(R.id.cdCalculated);
//        cdInputKg = myDialog.findViewById(R.id.cdInputKg);

        tvProduk = myDialog.findViewById(R.id.tvNamaProduk_PopUp);
//        tvTipe = myDialog.findViewById(R.id.tvTipe_PopUp);
        tvNoBarcode = myDialog.findViewById(R.id.tvNoBarcode_PopUp);
//        tvPerusahaan = myDialog.findViewById(R.id.tvPerusahaan_PopUp);
        tvJenis = myDialog.findViewById(R.id.tvJenis_PopUp);
//        tvCompare = myDialog.findViewById(R.id.tvCompare_PopUp);

//        etInputKg = myDialog.findViewById(R.id.etInputKg_PopUp);
//        etInputPcs = myDialog.findViewById(R.id.etInputPcs_PopUp);

//        etCalculate = myDialog.findViewById(R.id.etCalculate_PopUp);

        btnSimpanSampah = myDialog.findViewById(R.id.btnTerima_PopUp);
        btnOrderBaru = myDialog.findViewById(R.id.btnTolakOrder_PopUp);

        linearLayout_ProdukTerdaftar = myDialog.findViewById(R.id.LinearLayout_Accept_PopUp);
        //Initiation if Data Exist <--

        //Initiation if Data Not Exist -->
//        tvTakTerdaftar = myDialog.findViewById(R.id.tvTidakTerdaftar_PopUp);

//        btnFavorite = myDialog.findViewById(R.id.imbFavorite_PopUp);
        //linearLayout_ProdukTakTerdaftar= (LinearLayout)myDialog.findViewById(R.id.LinearLayout_ProdukTakTerdaftar_PopUp);
//        cd_ProdukTakTerdaftar_PopUp = myDialog.findViewById(R.id.cd_ProdukTakTerdaftar_PopUp);
        //Initiation if Data Not Exist -->

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
        if (myDialog.getWindow() != null) {
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
                etCalculate.setText(calculated());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etInputPcs.getText().toString().equals("")) {
                    tvCompare.setText("=");
                    cdCalculated.setVisibility(View.VISIBLE);
                    cdInputKg.setVisibility(View.GONE);
                } else {
                    cdInputKg.setVisibility(View.VISIBLE);
                    cdCalculated.setVisibility(View.GONE);
                    tvCompare.setText("Atau");
                }
            }
        });

        etInputKg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etInputKg.getText().toString().equals("")) {
                    etInputPcs.setEnabled(false);
                } else {
                    etInputPcs.setEnabled(true);
                }
            }
        });

    }

    protected void validasiData() {
        //Get Data Input from User in Pop Up
        strInputKg = etInputKg.getText().toString();
        strInputPcs = etInputPcs.getText().toString();
        strConvertedKg = etCalculate.getText().toString();

        if (!strInputPcs.equals("")) {
            strInputKg = strConvertedKg;
            strInputKg = strInputKg.replace(',', '.');
        }

        if (TextUtils.isEmpty(strInputPcs) && TextUtils.isEmpty(strInputKg)) {
            if (getContext() != null) {
                Toasty.warning(getContext(), "Harap Memasukkan Jumlah Pcs atau Kilo Sampah", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        try {
            getTotalPoint = Double.valueOf(strInputKg) * Double.valueOf(strPointKg);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        //Classified Input to DB if Order ID Already Exist or Not
        if (getMessage_Result.equals("True")) {
            SimpanLimbah();
        } else if (getMessage_Result.equals("False")) {
            OrderBaru();
        }
    }

    private void OrderBaru() {
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"id_user", "tipe_sampah", "jenis_sampah", "jumlah_pcs", "jumlah_kg", "point"};

        /*progressDialog.setMessage("Menambahkan, Harap Menunggu...");
        progressDialog.show();*/

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.incoming_order";
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
                //progressDialog.dismiss();
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
                    if (getContext() != null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //progressDialog.dismiss();
                customProgress.hideProgress();
                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });
    }

    //Save Data to Database if User Have Order
    private void SimpanLimbah() {
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"name", "tipe_sampah", "jenis_sampah", "jumlah_pcs", "jumlah_kg", "point"};

        /*progressDialog.setMessage("Menambahkan, Harap Menunggu...");
        progressDialog.show();*/

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.insert_incoming_order_line";
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
                //progressDialog.dismiss();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    ShowSuccessPopup();
                    tvTitle_Popup.setText(getString(R.string.MSG_SUCCESS_INPUT_INCOMING_ORDER));
                } catch (Throwable t) {
                    if (getContext() != null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 2 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                //progressDialog.dismiss();
                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 2: " + String.valueOf(error));
            }
        });
    }

    private String calculated() {
        double calculated_getPcs, calculated_getBeratBersih, calculated_getTotal;

        String getInputPcs = etInputPcs.getText().toString();
        String calculated_beratBersih = strBeratBersih;

        String calculated_pcs;

        if (getInputPcs.equals("")) {
            calculated_pcs = "0";
        } else {
            calculated_pcs = getInputPcs;
        }

        calculated_getPcs = Double.valueOf(calculated_pcs);
        calculated_getBeratBersih = Double.valueOf(calculated_beratBersih);

        calculated_getTotal = calculated_getPcs * calculated_getBeratBersih;

        return (String.valueOf(new DecimalFormat("##.##").format(calculated_getTotal)));
    }


    //Alert For Downloading Barcode Scanner Apps
    private static AlertDialog showDialogBarcode(final Activity act, CharSequence title, CharSequence message,
                                                 CharSequence btnYes, CharSequence btnNo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent_view = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent_view);
                } catch (ActivityNotFoundException anfe) {
                    anfe.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton(btnNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            //Do Scan Barcode
            if (requestCode == BARCODE_REQUEST_CODE) {
                //Content equals Barcode Number
                contents = intent.getStringExtra("SCAN_RESULT");
                format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if (getContext() != null) {
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
    private void checkBarcodeData(final String contents) {
        progressDialog.setMessage(getString(R.string.MSG_PROCESSING_DATA));
        progressDialog.show();

        // customProgress.showProgress(context, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String check_url;

        check_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.scan_barcode";
        params.put("no_barcode", contents);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.get(check_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.dismiss();
                //customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    //customProgress.hideProgress();
                    getBarcode(resp_content);
                } catch (Throwable t) {
                    //customProgress.hideProgress();
                    if (getContext() != null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 3 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 3: " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                //customProgress.hideProgress();

                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 3 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 3: " + String.valueOf(error));
            }
        });
    }

    private void getBarcode(String resp_content) {
        //customProgress.hideProgress();

        String[] field_name = {"message", "tipe_sampah", "jenis_sampah", "nama_perusahaan", "merk", "point_per_kg",
                "point_per_pcs", "no_barcode", "berat_bersih"};

        //Get Message from API
        try {
            barcodeStatus = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);
            strMessage = jsonPost.getString(field_name[0]);

            //If data Exist...
            if (strMessage.equals("True")) {
                BarcodePopUp();

                linearLayout_ProdukTerdaftar.setVisibility(View.VISIBLE);
                tvProduk.setVisibility(View.VISIBLE);
                cdDataBarcode.setVisibility(View.VISIBLE);

                //linearLayout_ProdukTakTerdaftar.setVisibility(View.GONE);
                cd_ProdukTakTerdaftar_PopUp.setVisibility(View.GONE);
                tvTakTerdaftar.setVisibility(View.GONE);

                strJenis = jsonPost.getString(field_name[1]);
                strTipe = jsonPost.getString(field_name[2]);
                strNamaPerusahaan = jsonPost.getString(field_name[3]);
                strMerk = jsonPost.getString(field_name[4]);
                strPointKg = jsonPost.getString(field_name[5]);
                strPointPcs = jsonPost.getString(field_name[6]);
                strBarcodeNumber = jsonPost.getString(field_name[7]);
                strBeratBersih = jsonPost.getString(field_name[8]);

                tvProduk.setText(strMerk);
                tvNoBarcode.setText(strBarcodeNumber);
                tvPerusahaan.setText(strNamaPerusahaan);
                tvTipe.setText(strJenis);
                tvJenis.setText(strTipe);

                //If data Non-exist
            } else {
                BarcodePopUp();

                linearLayout_ProdukTerdaftar.setVisibility(View.GONE);
                tvProduk.setVisibility(View.GONE);
                cdDataBarcode.setVisibility(View.GONE);

                //linearLayout_ProdukTakTerdaftar.setVisibility(View.VISIBLE);
                cd_ProdukTakTerdaftar_PopUp.setVisibility(View.VISIBLE);

                tvProduk.setText("");
                tvNoBarcode.setText("");
                tvPerusahaan.setText("");
                tvTipe.setText("");
                tvJenis.setText("");
            }

        } catch (JSONException e) {
            if (getContext() != null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 4 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 4: " + String.valueOf(e));
        }
    }

    //Get Location Customer
    private void getCurrentLocationRoute() {
        gps = new TrackGPS(getContext());
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
        } else {
            gps.showSettingsAlert();
        }
    }

    //Get Jenis Sampah From DB
    private void getJenisSampah() {
        //customProgress.showProgress(getContext(), "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        String base_url;

        base_url = apiData.get("str_url_address") + "/resource/Master%20Tipe%20Sampah?fields=[\"*\"]";

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.get(base_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //customProgress.hideProgress();
                String resp_content = null;
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    //progressDialog.dismiss();
                    displayTrash(resp_content);
                } catch (Throwable t) {
                    //progressDialog.dismiss();
                    if (getContext() != null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + " 4 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                    Log.e("tag", " 4: " + String.valueOf(t));
                    /*cd_noData.setVisibility(View.VISIBLE);
                    lvJenisSampah.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //progressDialog.dismiss();
                //customProgress.hideProgress();
                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 5 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_SHORT).show();
                }

                Log.e("Tag", " 5: " + String.valueOf(error));
                cd_noData.setVisibility(View.VISIBLE);
                lvJenisSampah.setVisibility(View.GONE);
            }
        });
    }

    private void displayTrash(String resp_content) {
        // JSON Field Names
        String TAG_KATEGORI = "name";
        String TAG_IMAGE = "image";
        try {
            JSONObject jsonPost = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allNames = new ArrayList<>();

            JSONArray cast = jsonPost.getJSONArray("data");
            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                // Post result field to string
                String kategori_result = c.getString(TAG_KATEGORI);
                String image_result = c.getString(TAG_IMAGE);

                // Make HashMap string for put string above
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_KATEGORI, kategori_result);
                map.put(TAG_IMAGE, image_result);

                allNames.add(map);
                //Log.e("tag", kategori_result);
            }

            // Call Lazy Adapter for Listview
            adapter = new LazyAdapter(getActivity(), allNames, 1);
            lvJenisSampah.setAdapter(adapter);

            cd_noData.setVisibility(View.GONE);
            lvJenisSampah.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 6 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 6: " + String.valueOf(e));
        }
    }

    private void checkOrder() {
        // customProgress.showProgress(getContext(), "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String checkOrder_url;

        checkOrder_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.check_incoming_order";
        params.put("id_user", MainActivity.strID);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(checkOrder_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    viewFromDB(resp_content);
                } catch (Throwable t) {
                    //Toast.makeText(getContext(), getString(R.string.MSG_CODE_409) + " 5 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 5: " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //customProgress.hideProgress();
                if (getContext() != null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 7 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag", " 7: " + String.valueOf(error));
            }
        });
    }

    private void viewFromDB(String resp_content) {
        String[] field_name = {"id_user", "name", "message"};

        try {
            arrCheckOrder = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);

            getMessage_Result = jsonPost.getString(field_name[2]);

            Log.e("tag", getIdUser_Result + " " + getNameOrder_Result);

            //Model Benarnya...
            if (getMessage_Result.equals("False")) {
                btnOrderBaru.setVisibility(View.VISIBLE);
                btnSimpanSampah.setVisibility(View.GONE);
            } else if (getMessage_Result.equals("True")) {
                getIdUser_Result = jsonPost.getString(field_name[0]);
                getNameOrder_Result = jsonPost.getString(field_name[1]);

                btnSimpanSampah.setVisibility(View.VISIBLE);
                btnOrderBaru.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            if (getContext() != null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + " 8 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 8: " + String.valueOf(e));
        }

    }

    public void ShowSuccessPopup() {
        myDialog.setContentView(R.layout.pop_up_success);
        //customProgress.hideProgress();

        //Init Data
        btnOk_Popup = myDialog.findViewById(R.id.btnOk_Confirmation);
        tvTitle_Popup = myDialog.findViewById(R.id.tvTitle_Confirmation);

        myDialog.setCanceledOnTouchOutside(false);

        btnOk_Popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                //newInstance();

                /*Intent intent_a = new Intent(getContext(), MainActivity.class);
                startActivity(intent_a);
                getActivity().finish();*/
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().startActivity(intent);
            }
        });

        //Customization for Dialog..
        if (myDialog.getWindow() != null) {
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
        SharedPreferences prefs_check = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs_check.getInt(PREF_VERSION_CODE, DOESNT_EXIST);

        //Check first run or upgrade
        //Normal run...
        if (currentVersionCode == savedVersionCode) {

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

        }

        prefs_check.edit().putInt(PREF_VERSION_CODE, currentVersionCode).apply();
    }
}



