package com.demo.user.banksampah.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.LazyAdapter;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;

/*import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;*/

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class SummaryOrderActivity extends AppCompatActivity {

    protected String getNameOrder_Result = null;

    //Session Class
    protected PrefManager session;
    protected String strAlamat, strLatLong, strName, strUserID;

    protected ConnectivityManager conMgr;
    private ListView lvSummary_Data;
    protected Button btnJemput, btnCancelIncomingOrder;

    protected TextView tvTotalPoints;
    protected String totalPoints;

    /*API process and dialog*/
    private RestProcess rest_class;
    protected HashMap<String,String> apiData;

    protected LazyAdapter adapter4;

    //Cek Data
    ArrayList<HashMap<String, String>> arrCheckSubmitted = new ArrayList<>();
    ArrayList<HashMap<String, String>> arrCheckUpdated = new ArrayList<>();

    private String getOrder_ID_Result = "";

    //Create Dialog
    private Dialog myDialog;
    private EditText etAlamat_Penjemputan;

    @SuppressLint("StaticFieldLeak")
    private static ImageView imgSelectImage;

    protected String cameraFilePath, imageFileName, ConvertImageToBase64;
    protected String StrImageUploadToDB = "data:image/jpeg;base64,";
    protected Bitmap getBitmapPictureSampah;

    protected DecimalFormat decimalFormat;

    protected TextView tvTitle_Popup;
    protected Button btnOk_Popup;

    //private ProgressDialog progressDialog;
    protected CustomProgress customProgress;

    private final static int ADDRESS_REQUEST_CODE = 2;
    private final static int CAMERA_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_order);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        session = new PrefManager(getApplicationContext());
        decimalFormat = new DecimalFormat(",###.##");

        HashMap<String, String> user = session.getUserDetails();
        strAlamat = user.get(PrefManager.KEY_ALAMAT);
        strLatLong = user.get(PrefManager.KEY_LATLONG);
        strUserID = user.get(PrefManager.KEY_ID);
        strName = user.get(PrefManager.KEY_NAMA);

        customProgress = CustomProgress.getInstance();

        //progressDialog = new ProgressDialog(this);
        //*Create Dialog Pop Up
        myDialog = new Dialog(this);
        conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        tvTotalPoints = findViewById(R.id.tvGrandPoints_Summary);
        btnJemput = findViewById(R.id.btnJemputOrder);
        btnCancelIncomingOrder = findViewById(R.id.btnCancel_IncomingOrder2);
        lvSummary_Data = findViewById(R.id.listView_Summary);

        Intent a = getIntent();
        getNameOrder_Result = a.getStringExtra("inc_order");
        totalPoints = a.getStringExtra("total_points");
        tvTotalPoints.setText(totalPoints + " " +getString(R.string.placeholder_point));

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
            getSummaryOrder(getNameOrder_Result);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

        btnJemput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });
        btnCancelIncomingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDeleteIncomingOrder();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    private void getSummaryOrder(final String getNameOrder_Result) {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String summary_order_url;

        summary_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.summary_item";
        params.put("inc_order", getNameOrder_Result);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(summary_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                    Log.e("tag", resp_content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    displaySummary(resp_content);
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });
    }

    private void displaySummary(String resp_content) {
        String[] field_name = {"summary", "point", "jenis_sampah", "parent", "jumlah_kg"};

        try {
            JSONObject jsonPost = new JSONObject(resp_content);
            ArrayList<HashMap<String, String>> allFields = new ArrayList<>();
            JSONArray cast = jsonPost.getJSONArray(field_name[0]);

            for (int i = 0; i < cast.length(); i++) {
                JSONObject c = cast.getJSONObject(i);

                // Post result field to string
                String point_summary = c.getString(field_name[1]);
                String jenis_sampah_summary = c.getString(field_name[2]);
                String parent_summary = c.getString(field_name[3]);
                String jumlah_kg_summary = c.getString(field_name[4]);

                HashMap<String, String> map = new HashMap<>();

                map.put(field_name[1], point_summary);
                map.put(field_name[2], jenis_sampah_summary);
                map.put(field_name[3], parent_summary);
                map.put(field_name[4], jumlah_kg_summary);

                allFields.add(map);
            }

            // Call Lazy Adapter for Listview
            adapter4 = new LazyAdapter(this, allFields, 4);
            lvSummary_Data.setAdapter(adapter4);

        } catch (JSONException e) {
            e.printStackTrace();
            Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            Log.e("tag", " 2: " + String.valueOf(e));
        }
    }

    private void showPopUp() {
        myDialog.setContentView(R.layout.pop_up_select_location_order);

        etAlamat_Penjemputan = myDialog.findViewById(R.id.etAlamatPenjemputan);
        etAlamat_Penjemputan.setEnabled(false);
        etAlamat_Penjemputan.setText(strAlamat);

        Log.e("tag", "before: " + strLatLong);

        final TableRow tblConfirm_Penjemputan = myDialog.findViewById(R.id.tblConfirm_Location);
        final TableRow tblEdit_Penjemputan = myDialog.findViewById(R.id.tblEdit_Location);

        Button btnEditAlamat_Penjemputan = myDialog.findViewById(R.id.btnEdit);
        Button btnKonfirmasi_Penjemputan = myDialog.findViewById(R.id.btnKonfirmasi_Penjemputan);

        Button btnPilihLokasi_Penjemputan = myDialog.findViewById(R.id.btnPilihLokasi);
        Button btnOK_Penjemputan = myDialog.findViewById(R.id.btnOK);

        imgSelectImage = myDialog.findViewById(R.id.imgSelectImage_Penjemputan);
        ImageView imgClose = myDialog.findViewById(R.id.imgClose_OrderPopUp);

        btnEditAlamat_Penjemputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tblEdit_Penjemputan.setVisibility(View.VISIBLE);
                tblConfirm_Penjemputan.setVisibility(View.GONE);
                etAlamat_Penjemputan.setEnabled(true);
            }
        });

        btnOK_Penjemputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tblEdit_Penjemputan.setVisibility(View.GONE);
                tblConfirm_Penjemputan.setVisibility(View.VISIBLE);
                etAlamat_Penjemputan.setEnabled(false);
            }
        });

        btnPilihLokasi_Penjemputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMaps = new Intent(getApplicationContext(), OpenMaps.class);
                startActivityForResult(openMaps, ADDRESS_REQUEST_CODE);
            }
        });

//        btnKonfirmasi_Penjemputan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //validate(strAlamat, getNameOrder_Result);
//                Intent intent_a = new Intent(getApplicationContext(), PickupActivity.class);
//                intent_a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent_a.putExtra("EXTRA_ID_ORDER", getOrder_ID_Result);
//                intent_a.putExtra("EXTRA_LATLONG", strLatLong);
//                startActivity(intent_a);
//                finish();
//            }
//        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        imgSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFromCamera();
            }
        });

        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void captureFromCamera() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (photoFile!=null){
                    Uri photoFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    Log.e("tag", "photo file : " + photoFile.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
        imageFileName = getNameOrder_Result + "_" + strUserID + "_" + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg",  /* suffix */
                storageDir     /* directory */
        );
        //cameraFilePath = "file://" + image.getAbsolutePath();
        cameraFilePath = image.getAbsolutePath();
        return image;
    }

    private void validate(final String Alamat, final String Order){
        String alamat_jemput = etAlamat_Penjemputan.getText().toString();
        String latLong = strLatLong;
        Log.e("tag3", latLong);

        if (alamat_jemput.isEmpty()){
            etAlamat_Penjemputan.setError("Harap Masukkan Alamat Penjemputan");
            etAlamat_Penjemputan.requestFocus();
        }else{
            submitIncomingOrder(alamat_jemput, latLong, Order);
        }
    }

    private void submitIncomingOrder(final String alamat, final String latlong, final String Order) {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String submit_incoming_url;

        submit_incoming_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.submit_incoming_order";
        params.put("inc_order", Order);

        client.addHeader("Authorization", "token dd42bf276446682:753232865403c96");
        client.post(submit_incoming_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                    Log.e("tag", resp_content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    String[] field_name = {"message", "order_id"};
                    arrCheckSubmitted = rest_class.getJsonData(field_name, resp_content);
                    JSONObject jsonObject = new JSONObject(resp_content);

                    String getMessage_Result = jsonObject.getString(field_name[0]);

                    if (getMessage_Result.equalsIgnoreCase("Submitted")) {
                        getOrder_ID_Result = jsonObject.getString(field_name[1]);

                        //Jalankan Update Order API and Send Image
                        Toasty.success(getApplicationContext(), "Sukses Menambahkan Order", Toast.LENGTH_SHORT).show();
                        updateLocationImageOrder(alamat, latlong, Order);

                    } else if (getMessage_Result.equalsIgnoreCase("Invalid")) {
                        Toasty.error(getApplicationContext(), "Gagal Menambahkan Order", Toast.LENGTH_SHORT).show();
                        Log.e("tag", "Gagal Menambahkan Order");
                    }
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 2 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 2 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 2 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 2: " + String.valueOf(error));
            }
        });
    }

    private void updateLocationImageOrder(final String strAlamat, final String strLatLong, final String getNameOrder_Result) {
        customProgress.showProgress(this, "", false);

        String[] field_name = {"inc_order", "alamat", "latlong", "docname", "filename"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String update_location_image_url;

        update_location_image_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.update_order";
        params.put(field_name[0], getNameOrder_Result);
        params.put(field_name[1], strAlamat);
        params.put(field_name[2], strLatLong);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(update_location_image_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content = "";
                try {
                    resp_content = new String(responseBody, "UTF-8");
                    Log.e("tag", resp_content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    Log.e("tag", "Sukses Menyimpan Data Alamat dan Gambar Limbah");

                    String[] status = {"message"};
                    arrCheckUpdated = rest_class.getJsonData(status, resp_content);
                    JSONObject jsonObject = new JSONObject(resp_content);

                    String getMessage_Result = jsonObject.getString(status[0]);

                    if (getMessage_Result.equalsIgnoreCase("True")) {
                        Log.e("tag", "Sukses Mengupdate Order");

                        ShowSuccessPopup();
                        tvTitle_Popup.setText(getString(R.string.MSG_SUCCESS_ORDER));

                        //Jalankan Send Image API
                        if (imgSelectImage.getDrawable() == null){
                            Log.e("tag", "no_image");
                        }
                        else{
                            uploadImage();
                        }

                    } else {
                        Toasty.error(getApplicationContext(), "Gagal Mengupdate Order", Toast.LENGTH_SHORT).show();
                        Log.e("tag", "Gagal Menambahkan Order");
                    }
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 3 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 3 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 3 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 3: " + String.valueOf(error));
            }
        });
    }

    private void uploadImage() {
    //private void uploadImage(final String getOrder_ID_Result) {
        customProgress.showProgress(this, "", false);

        String[] field_name = {"doctype", "docname", "filename", "isprivate", "filedata", "from_from", "docfield"};

        getBitmapPictureSampah = ((BitmapDrawable) SummaryOrderActivity.imgSelectImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        getBitmapPictureSampah.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        ConvertImageToBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        StrImageUploadToDB += ConvertImageToBase64;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String upload_image_url;

        upload_image_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.upload_image_token";
        params.put(field_name[0], "Order");
        params.put(field_name[1], getOrder_ID_Result);
        params.put(field_name[2], imageFileName + ".jpg");
        params.put(field_name[3], "0");
        params.put(field_name[4], StrImageUploadToDB);
        params.put(field_name[6], "image");

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(upload_image_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String resp_content;
                try {
                    resp_content = new String(responseBody, "UTF-8");
                    Log.e("tag", resp_content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("tag", String.valueOf(e));
                }
                try {
                    Log.e("tag", "Sukses Mengupload Gambar");
                    Toasty.success(getApplicationContext(), "Berhasil Mengupload Gambar", Toast.LENGTH_SHORT).show();

                } catch (Throwable t) {
                    //Toast.makeText(getContext(), "Terjadi Kesalahan, Mohon Periksa Data Kembali 2", Toast.LENGTH_LONG).show();
                    Log.e("tag", "Gagal1" + ": " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Log.e("tag foto", error.toString());
                Toasty.error(getApplicationContext(), getString(R.string.MSG_REQ_FAILED), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void ShowSuccessPopup() {
        myDialog.setContentView(R.layout.pop_up_success);

        //Init Data
        btnOk_Popup = myDialog.findViewById(R.id.btnOk_Confirmation);
        tvTitle_Popup = myDialog.findViewById(R.id.tvTitle_Confirmation);

        myDialog.setCanceledOnTouchOutside(false);

//        btnOk_Popup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent_a = new Intent(getApplicationContext(), PickupActivity.class);
//                intent_a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent_a.putExtra("EXTRA_ID_ORDER", getOrder_ID_Result);
//                intent_a.putExtra("EXTRA_LATLONG", strLatLong);
//                startActivity(intent_a);
//                finish();
//                myDialog.dismiss();
//            }
//        });

        //Customization for Dialog..
        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADDRESS_REQUEST_CODE:
                    /*Place place = PlacePicker.getPlace(SummaryOrderActivity.this, data);
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;

                    strAlamat = String.valueOf(place.getAddress());
                    etAlamat_Penjemputan.setText(strAlamat);

                    strLatLong = String.valueOf(latitude + "," + longitude);
                    Toast.makeText(getApplicationContext(), strLatLong, Toast.LENGTH_SHORT).show();

                    Log.e("tag", "after: " + strLatLong);*/
                    String lokasi = data.getStringExtra("Alamat_Lokasi");
                    etAlamat_Penjemputan.setText(lokasi);
                    strAlamat = lokasi;
                    Log.e("tag1", strAlamat);
                    Log.e("tag2", lokasi);

                    strLatLong = data.getStringExtra("LatLong_Lokasi");
                    Log.e("tag", strLatLong);
                    break;

                case CAMERA_REQUEST_CODE:
                    Log.e("tag1", cameraFilePath);

                    File imageFile = new File (cameraFilePath);
                    if (imageFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap images = BitmapFactory.decodeFile(cameraFilePath, options);
                        images = Bitmap.createScaledBitmap(images, 300, 400, false);
                        images = getResizedBitmap(images, 400);

                        imgSelectImage.setImageBitmap(images);
                    }
                    break;
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        double x;

        if (width >= height && width > maxSize){
            x = width/height;
            width = maxSize;
            height = (int)(maxSize/x);
        }else if(height >= width && height > maxSize){
            x = height/width;
            height = maxSize;
            width = (int) (maxSize/x);
        }

       /* float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }*/
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void showPopupDeleteIncomingOrder() {
        myDialog.setContentView(R.layout.pop_up_delete_incoming_order_line);

        Button btnDelete_Confirmation = myDialog.findViewById(R.id.btnDelete_Confirmation);
        Button btnCancel_Confirmation = myDialog.findViewById(R.id.btnCancel_Confirmation);
        TextView tvTitle_Delete = myDialog.findViewById(R.id.tvTitle_Confirmation);

        tvTitle_Delete.setText(getString(R.string.MSG_DELETE_INCOMING));

        btnDelete_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIncomingOrder();
            }
        });

        btnCancel_Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        if(myDialog.getWindow()!=null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void deleteIncomingOrder() {
        /*dialog.setMessage("Menghapus Data, Harap Menunggu...");
        dialog.show();*/

        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String delete_incoming_order_url;

        delete_incoming_order_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.delete_incoming_order";
        params.put("inc_order", getNameOrder_Result);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(delete_incoming_order_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryOrderActivity.this);
                        builder.setMessage("Berhasil Menghapus Kantong Limbah Sampah!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                } catch (Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 3 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 3: " + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 5 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();

                Log.e("Tag", " 5: " + String.valueOf(error));
            }
        });
    }
}
