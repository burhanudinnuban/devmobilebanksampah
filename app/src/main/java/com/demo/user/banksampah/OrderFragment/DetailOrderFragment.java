package com.demo.user.banksampah.OrderFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Activities.MainActivity;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Activities.TrashDetail;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class DetailOrderFragment extends Fragment {

    //Get for Activities
    protected String getNama_Sampah, getBeratBersih_Sampah, getDeskripsi_Sampah, getHargaPcs_Sampah,
            getPointKilo_Sampah, getPointPcs_Sampah, getHargaKilo_Sampah;

    protected ImageView imgSimpanLimbah, imgOrderBaru;
    protected EditText etNamaDeskripsi, etDeskripsi, etJumlahKilo;
    protected TextView tvHarga;

    protected String getInputKilo;
    protected float getTotalPoint = 0;

    protected String getIdUser_Result = null, getNameOrder_Result = null, getMessage_Result = null;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String,String> apiData;
    protected CustomProgress customProgress;

    //Cek Data
    ArrayList<HashMap<String, String>> arrCheckOrder = new ArrayList<>();

    //Pop Up
    Dialog myDialog;
    private TextView tvTitle_Popup;
    protected Button btnOk_Popup;

    private String tipe_sampah = TrashDetail.getSampah;

    protected View rootView;
    protected ConnectivityManager conMgr;

    //private ProgressDialog dialog;

    protected DecimalFormat decimalFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        if (getActivity() != null) {
            conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detail_order, container, false);

       /* dialog = new ProgressDialog(getContext());
        dialog.setMessage("Menambahkan, Harap Menunggu...");*/

        decimalFormat = new DecimalFormat(",###");

        // *Get Data From Previous Activity
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        getNama_Sampah = extras.getString("name_Sampah");
        getBeratBersih_Sampah = extras.getString("beratBersih_Sampah");
        getDeskripsi_Sampah = extras.getString("deskripsi_Sampah");
        getHargaPcs_Sampah = extras.getString("hargaPcs_Sampah");
        getPointKilo_Sampah = extras.getString("pointKilo_Sampah");
        getPointPcs_Sampah = extras.getString("pointPcs_Sampah");
        getHargaKilo_Sampah = extras.getString("hargaKilo_Sampah");

        Log.e("tag",getPointKilo_Sampah);

        //Initiation Data Layout
        imgSimpanLimbah = rootView.findViewById(R.id.imgSimpanLimbah);
        imgOrderBaru = rootView.findViewById(R.id.imgOrderBaru);

        etNamaDeskripsi = rootView.findViewById(R.id.etNamaDetil);
        etDeskripsi = rootView.findViewById(R.id.etDeskripsi);
        tvHarga = rootView.findViewById(R.id.tvHarga);

        etJumlahKilo = rootView.findViewById(R.id.etKilo);

        //*Set Text from GetString Intent
        etNamaDeskripsi.setText(getNama_Sampah);
        etDeskripsi.setText(getDeskripsi_Sampah);
        if(getPointKilo_Sampah.equals("") || getPointKilo_Sampah.equals("null")){
            tvHarga.setText("0");
        }else {
            try {
                tvHarga.setText(decimalFormat.format(Double.valueOf(getPointKilo_Sampah)) + "/Kg");
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }

        //*If Simpan Limbah Clicked, Show Pop Up
        imgSimpanLimbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();
            }
        });

        //*If Order Baru Clicked, Show Pop Up
        imgOrderBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();
            }
        });

        //Create dialog popup
        myDialog = new Dialog(getActivity());

        return rootView;
    }

    @Override
    public void onResume(){
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            CheckOrder();
        } else {
            Toast.makeText(getContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    //Show Pop Up
    public void validasiData() {
        getInputKilo = etJumlahKilo.getText().toString();

        if (TextUtils.isEmpty(getInputKilo)) {
            etJumlahKilo.setError("Harap Masukkan Data");
            etJumlahKilo.requestFocus();
            return;
        }

        getTotalPoint = Float.valueOf(getInputKilo) * Float.valueOf(getPointKilo_Sampah);

        //Model Benarnya..
        if (getMessage_Result.equals("True")){
            SimpanLimbah();
        } else if (getMessage_Result.equals("False")) {
            OrderBaru();
        }
    }

    private void CheckOrder(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.check_incoming_order";
        params.put("id_user", MainActivity.strID);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp_content ="";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    viewFromDB(resp_content);
                } catch (Throwable t) {
                    if(getContext()!=null) {
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + "1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(getContext()!=null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + "1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag","1: " + String.valueOf(error));
            }
        });
    }

    private void viewFromDB(String resp_content){
        String[] field_name = {"id_user","name","message"};

        try{
            arrCheckOrder = rest_class.getJsonData(field_name, resp_content);
            JSONObject jsonPost = new JSONObject(resp_content);

            getMessage_Result = jsonPost.getString(field_name[2]);

            Log.e("tag", getIdUser_Result + " " + getNameOrder_Result);

            //Model Benarnya...
            if(getMessage_Result.equals("False")) {
                imgOrderBaru.setVisibility(View.VISIBLE);
                imgSimpanLimbah.setVisibility(View.GONE);
            }
            else if(getMessage_Result.equals("True")){
                getIdUser_Result = jsonPost.getString(field_name[0]);
                getNameOrder_Result = jsonPost.getString(field_name[1]);

                imgSimpanLimbah.setVisibility(View.VISIBLE);
                imgOrderBaru.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            if(getContext()!=null) {
                Toasty.error(getContext(), getString(R.string.MSG_CODE_501) + "1 : " + getString(R.string.MSG_TRY_AGAIN), Toast.LENGTH_LONG).show();
            }
            Log.e("tag", " 1: " + String.valueOf(e));
        }

    }

    //Save Data to Database if User Dont Have Order
    private void OrderBaru(){
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"id_user", "tipe_sampah", "jenis_sampah", "jumlah_pcs", "jumlah_kg", "point"};
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.incoming_order";
        params.put(field_name[0], MainActivity.strID);
        params.put(field_name[1], tipe_sampah);
        params.put(field_name[2], getNama_Sampah);
        params.put(field_name[3], "0");
        params.put(field_name[4], getInputKilo);
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
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + "2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if(getContext()!=null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + "2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag","2: " + String.valueOf(error));
            }
        });
    }

    //Save Data to Database if User Have Order
    private void SimpanLimbah(){
        customProgress.showProgress(getContext(), "", false);

        String[] field_name = {"name", "tipe_sampah", "jenis_sampah", "jumlah_pcs", "jumlah_kg", "point"};
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.insert_incoming_order_line";
        params.put(field_name[0], getNameOrder_Result);
        params.put(field_name[1], tipe_sampah);
        params.put(field_name[2], getNama_Sampah);
        params.put(field_name[3], "0");
        params.put(field_name[4], getInputKilo);
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
                        Toasty.error(getContext(), getString(R.string.MSG_CODE_409) + "3 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                if(getContext()!=null) {
                    Toasty.error(getContext(), getString(R.string.MSG_CODE_500) + "3 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                }
                Log.e("Tag","3: " + String.valueOf(error));
            }
        });
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
                    Intent intent_a = new Intent(getContext(), MainActivity.class);
                    intent_a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().finish();
                    startActivity(intent_a);
                }
            }
        });

        //Customization for Dialog..
        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }
}
