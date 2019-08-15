package com.demo.user.banksampah.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.TrackGPS;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class UpdateProfileActivity extends AppCompatActivity {

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String,String> apiData;

    //Untuk DatePicker
    protected DatePickerDialog.OnDateSetListener mDateSetListener;

    //Upload Image
    protected String cameraFilePath, imageFileName, ConvertImageToBase64;
    protected String StrImageUploadToDB = "data:image/jpeg:base64,";
    protected Bitmap getBitmapPicture;
    protected ByteArrayOutputStream byteArrayOutputStream;

    //Linear Satu

    protected CustomProgress customProgress;

    //Session Class
    protected PrefManager session;
    protected HashMap<String,String> user;

    //Get Data From Login Process
    protected static String getNama = "";
    protected static String getFoto = "";
    protected static String getID = "";
    protected static String getTglLahir = "";
    protected static String getNoHP = "";
    protected static String getAlamat = "";
    protected static String getEmail = "";

    @SuppressLint("StaticFieldLeak")
    protected static ImageView imgProfil;

    protected String url_foto;

    protected RelativeLayout parent_layout;
    //Deklarasi Ke Layout
    protected ImageView imgPinCircle, imgRegister;
    protected EditText etNamaBankSampah, etNoHpBankSampah, etAlamat,etPassword, etConfirmPassword,etNamaPengurus, etNoHpPengurus,
            etNamaPengurusDua, etNoHpPengurusDua, etNamaDetailBank, etNoRekeningBank, etNamaRekeningBank, etEmailBankSampah, etJabatanPengurus, etJabatanPengurus2;
    protected TextView tvMaps , tvDataPengurus, tvStepOne, tvStepTwo, tvStepThree, tvStatusNoHp, tvDataRekeningBankSampah;
    protected Button btDaftarkan, btnNext, btnNext2, btnPrev2, btnPrev3;
    protected LinearLayout registerBankSampah, registerPengurus,registerDetailBank;


    protected String strNamaLengkap_Update, strLatLong_Update, strNoHpBankSampah,
              strAlamat_Update, strEmail_Update, strNamaPengurus, strNoHpPengurus, strNamaPengurusDua, strNoHpPengurusDua,
            strNamaDetailBank, strNoRekeningBank, strNamaRekeningBank, strEmailBankSampah, strJabatanPengurus, strJabatanPengurus2;

    /*FOR GPS*/
    protected TrackGPS gps;
    double longitude;
    double latitude;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatter_ToDB;
    protected DatePickerDialog datePickerDialog;

    protected DateFormat inputDateFormat, outputDateFormat;
    protected String dateFixed;

    protected CountDownTimer ctd;
    protected boolean click = true;

    //Request Code
    private final static int ADDRESS_REQUEST_CODE = 2;
    private final static int GALLERY_REQUEST_CODE = 3;
    private final static int CAMERA_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        customProgress = CustomProgress.getInstance();
        byteArrayOutputStream = new ByteArrayOutputStream();

        inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        etAlamat = findViewById(R.id.etAlamat);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etNamaBankSampah = findViewById(R.id.etNamaBankSampah_Register);
        etNamaDetailBank = findViewById(R.id.etNamaDetailBank_Register);
        etNamaPengurus = findViewById(R.id.etNamaPengurus_Register);
        etNamaPengurusDua = findViewById(R.id.etNamaPengurusDua_Register);
        etNamaRekeningBank = findViewById(R.id.etNamaRekeningBank_Register);
        etNoHpBankSampah = findViewById(R.id.etNoHpBankSampah_Register);
        etNoHpPengurus = findViewById(R.id.etNoHpPengurus_Register);
        etNoHpPengurusDua = findViewById(R.id.etNoHpPengurusDua_Register);
        etNoRekeningBank = findViewById(R.id.etNoRekeningBank_Register);
        etPassword = findViewById(R.id.etPassword);
        etEmailBankSampah = findViewById(R.id.etEmailBankSampah_Register);
        etJabatanPengurus = findViewById(R.id.etJabatanPengurus_Register);
        etJabatanPengurus2 = findViewById(R.id.etJabatanPengurus2_Register);
        imgPinCircle = findViewById(R.id.imgPinCircle);
        imgRegister = findViewById(R.id.imgRegisterPicture);
        tvDataPengurus = findViewById(R.id.tvDataPengurus);
        tvDataRekeningBankSampah = findViewById(R.id.tvDataRekeningBankSampah);
        tvMaps = findViewById(R.id.tvMaps);
        tvStatusNoHp= findViewById(R.id.tvStatusNoHP);
        tvStepOne= findViewById(R.id.tvStepOne);
        tvStepThree = findViewById(R.id.tvStepThree);
        tvStepTwo = findViewById(R.id.tvStepTwo);
        registerPengurus = findViewById(R.id.linear_RegisterPengurus);
        registerBankSampah = findViewById(R.id.linear_RegisterBankSampah);
        registerDetailBank = findViewById(R.id.linear_RegisterDetailBank);
        btDaftarkan = findViewById(R.id.btDaftarkan);
        parent_layout = findViewById(R.id.ParentUpdateProfile);
        btnNext = findViewById(R.id.btnNext1);
        btnNext2 = findViewById(R.id.btnNext2);
        btnPrev2 = findViewById(R.id.btnPrevious2);
        btnPrev3 = findViewById(R.id.btnPrevious3);



        //Session Instance
        session = new PrefManager(getApplicationContext());
        user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);
        getFoto = user.get(PrefManager.KEY_FOTO);
        getNoHP = user.get(PrefManager.KEY_NO_HP);
        getAlamat = user.get(PrefManager.KEY_ALAMAT);
        getEmail = user.get(PrefManager.KEY_EMAIL);
        getID = user.get(PrefManager.KEY_ID);


        etNamaBankSampah.setText(getNama);
        etEmailBankSampah.setText(getEmail);
        etNoHpBankSampah.setText(getNoHP);
        etAlamat.setText(getAlamat);

        ctd = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                click = true;
            }
        };

        url_foto = apiData.get("str_url_main");
        Picasso.get()
                .load(url_foto + getFoto)
                //.error(R.drawable.ic_navigation_profil)
                .into(imgPinCircle);

        //Get Current Location GPS
        getCurrentLocation();

        //DateFormat
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormatter_ToDB = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());



//        etTglLahir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDateDialog();
//            }
//        });

//        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                month = month + 1;
//                String date = dayOfMonth + "/" + month + "/" + year;
//                etTglLahir.setText(date);
//            }
//        };

        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMaps = new Intent(getApplicationContext(), OpenMaps.class);
                startActivityForResult(openMaps, ADDRESS_REQUEST_CODE);
            }
        });

        imgRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btDaftarkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        //Steps Hiden Update
        tvStepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBankSampah.setVisibility(View.VISIBLE);
                registerDetailBank.setVisibility(View.GONE);
                registerPengurus.setVisibility(View.GONE);
                tvStepOne.setBackgroundColor(Color.BLUE);
                tvStepOne.setTextColor(Color.WHITE);
                tvStepThree.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                tvStepTwo.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                tvStepOne.setBackgroundResource(R.drawable.rectangle_aktif);
                tvStepTwo.setBackgroundResource(R.drawable.rectangle_non);
                tvStepThree.setBackgroundResource(R.drawable.rectangle_non);
                btDaftarkan.setVisibility(View.INVISIBLE);
            }
        });

        tvStepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etNamaBankSampah.getText().toString().length()==0)
                {
                    etNamaBankSampah.setError("Nama Bank Sampah Diperlukan");
                    etNamaBankSampah.requestFocus();
                }
                else if(etEmailBankSampah.getText().toString().length()==0)
                {
                    etEmailBankSampah.setError("Email Bank Sampah Diperlukan");
                    etEmailBankSampah.requestFocus();
                }
                else if(etNoHpBankSampah.getText().toString().length()==0)
                {
                    etNoHpBankSampah.setError("No Hp Bank Sampah Diperlukan");
                    etNoHpBankSampah.requestFocus();
                }
                else if(etAlamat.getText().toString().length()==0)
                {
                    etAlamat.setError("Alamat Bank Sampah Diperlukan");
                    etAlamat.requestFocus();
                }
                else
                {
                    registerBankSampah.setVisibility(View.GONE);
                    registerDetailBank.setVisibility(View.GONE);
                    registerPengurus.setVisibility(View.VISIBLE);
                    tvStepTwo.setBackgroundColor(Color.BLUE);
                    tvStepTwo.setTextColor(Color.WHITE);
                    tvStepThree.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                    tvStepOne.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                    tvStepOne.setBackgroundResource(R.drawable.rectangle_non);
                    tvStepTwo.setBackgroundResource(R.drawable.rectangle_aktif);
                    tvStepThree.setBackgroundResource(R.drawable.rectangle_non);
                    btDaftarkan.setVisibility(View.INVISIBLE);
                    tvStepTwo.setClickable(true);
                }

            }
        });

        tvStepThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etNamaPengurus.getText().toString().length()==0)
                {
                    etNamaPengurus.setError("Nama Pengurus Diperlukan");
                    etNamaPengurus.requestFocus();
                }
                else if(etNoHpPengurus.getText().toString().length()==0)
                {
                    etNoHpPengurus.setError("No Hp Pengurus Diperlukan");
                    etNoHpPengurus.requestFocus();
                }
                else if(etJabatanPengurus.getText().toString().length()==0)
                {
                    etJabatanPengurus.setError("Jabatan Pengurus Diperlukan");
                    etJabatanPengurus.requestFocus();
                }
                else
                    {
                    registerBankSampah.setVisibility(View.GONE);
                    registerDetailBank.setVisibility(View.VISIBLE);
                    btDaftarkan.setVisibility(View.VISIBLE);
                    registerPengurus.setVisibility(View.GONE);
                    tvStepThree.setBackgroundColor(Color.BLUE);
                    tvStepThree.setTextColor(Color.WHITE);
                    tvStepTwo.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                    tvStepOne.setTextColor(getApplication().getResources().getColor(R.color.colorPrimary));
                    tvStepOne.setBackgroundResource(R.drawable.rectangle_non);
                    tvStepTwo.setBackgroundResource(R.drawable.rectangle_non);
                    tvStepThree.setBackgroundResource(R.drawable.rectangle_aktif);
                    tvStepThree.setClickable(true);
                    }
            }
        });
    }

        //Get Current Location
        private void getCurrentLocation(){
            gps = new TrackGPS(getApplicationContext());
            if(gps.canGetLocation()){
                longitude = gps.getLongitude();
                latitude = gps.getLatitude();
                strLatLong_Update =  latitude + "," + longitude;
            }else{
                gps.showSettingsAlert();
                strLatLong_Update = "0,0";
            }
        }

//        private void showDateDialog(){
//            Calendar newCalendar = Calendar.getInstance();
//            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                    Calendar newDate = Calendar.getInstance();
//                    newDate.set(year, month,dayOfMonth);
//                    etTglLahir.setText(dateFormatter.format(newDate.getTime()));
//                    getTglLahir = dateFormatter_ToDB.format(newDate.getTime());
//                }
//            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//            datePickerDialog.show();
//        }

    private void selectImage(){
        final CharSequence[] options = {"Ambil Foto", "Pilih dari Galeri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setTitle("Tambah Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Ambil Foto")){
                    captureFromCamera();
                }else if(options[which].equals("Pilih dari Galeri")){
                    pickFromGallery();
                } else if(options[which].equals("Batal")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void captureFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                ex.printStackTrace();
            }

            if (photoFile != null){
                Uri photoFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID
                        + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void pickFromGallery(){
        Intent intent_gallery = new Intent(Intent.ACTION_PICK);
        intent_gallery.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent_gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent_gallery, GALLERY_REQUEST_CODE);
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADDRESS_REQUEST_CODE:
                    String lokasi = data.getStringExtra("Alamat_Lokasi");
                    etAlamat.setText(lokasi);

                    strLatLong_Update = data.getStringExtra("LatLong_Lokasi");
                    Log.e("tag updt latlong", data.getStringExtra("LatLong_Lokasi"));
                    break;

                case GALLERY_REQUEST_CODE:
                    try {
                        Uri imageURI = data.getData();
                        if (imageURI!=null) {
                            InputStream imageStream = getContentResolver().openInputStream(imageURI);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            selectedImage = Bitmap.createScaledBitmap(selectedImage, 300, 400, false);
                            selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
                            imgRegister.setImageBitmap(selectedImage);

                            String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
                            String name = etNamaBankSampah.getText().toString();
                            imageFileName = "JPEG_" + name + "_" + timeStamp;
                        }
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    File imageFile = new File(cameraFilePath);
                    if (imageFile.exists()){
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap images = BitmapFactory.decodeFile(cameraFilePath, options);
                        images = Bitmap.createScaledBitmap(images, 300, 400, false);
                        images = getResizedBitmap(images, 400);

                        imgRegister.setImageBitmap(images);

                        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
                        String name = etNamaBankSampah.getText().toString();
                        imageFileName = "JPEG_" + name + "_" + timeStamp;
                    }

                    break;
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void validate()
    {
        strNamaLengkap_Update = etNamaBankSampah.getText().toString();
        strEmail_Update = etEmailBankSampah.getText().toString();
        strNoHpBankSampah = etNoHpBankSampah.getText().toString();
        strAlamat_Update = etAlamat.getText().toString();
        strNamaPengurus = etNamaPengurus.getText().toString();
        strNoHpPengurus = etNoHpPengurus.getText().toString();
        strJabatanPengurus = etJabatanPengurus.getText().toString();
        strNamaPengurusDua = etNamaPengurusDua.getText().toString();
        strNoHpPengurusDua = etNoHpPengurusDua.getText().toString();
        strJabatanPengurus2 = etJabatanPengurus2.getText().toString();
        strNamaDetailBank = etNamaDetailBank.getText().toString();
        strNoRekeningBank = etNoRekeningBank.getText().toString();
        strNamaRekeningBank = etNamaRekeningBank.getText().toString();

        if (etNamaDetailBank.getText().toString().length()==0)
        {
            etNamaDetailBank.setError("Nama Bank Diperlukan");
            etNamaDetailBank.requestFocus();
        }
        else if(etNoRekeningBank.getText().toString().length()==0)
        {
            etNoRekeningBank.setError("No Rekening Bank Diperlukan");
            etNoRekeningBank.requestFocus();
        }
        else if(etNamaRekeningBank.getText().toString().length()==0)
        {
            etNamaRekeningBank.setError("Nama Rekening Bank Sampah");
            etNamaRekeningBank.requestFocus();
        }
        else if (imgRegister.getDrawable()!= null && imageFileName != null){
            UpdateProfile();
            AddPengurus();
            AddRekeningBank();
            uploadImage();
        }
        else
            {
            UpdateProfile();
            AddPengurus();
            AddRekeningBank();
            }
    }

    private void UpdateProfile(){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"id_bank_sampah", "email", "alamat","jam_operasional","latlong"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_update_profile");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(changePassword);
                                    finish();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], "");
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

    private void AddPengurus(){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon", "old_pass", "new_pass","message"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_change_password");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(changePassword);
                                    finish();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], "");
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

    private void AddRekeningBank(){
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon", "old_pass", "new_pass","message"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_change_password");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d("DEBUG", "Register Response: " + response);
                try {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateProfileActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent changePassword = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(changePassword);
                                    finish();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d("DEBUG", "Error Validate Change Password Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG", "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], "");
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

    private void uploadImage(){
        String[] field_name = {"doctype", "docname", "filename", "isprivate", "filedata", "from_form", "docfield"};

        getBitmapPicture = ((BitmapDrawable)UpdateProfileActivity.imgProfil.getDrawable()).getBitmap();
        getBitmapPicture.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

        byte[]imageInByte = byteArrayOutputStream.toByteArray();
        ConvertImageToBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        StrImageUploadToDB += ConvertImageToBase64;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String upload_image_url;

        Log.e("tag", "id user : " + getID);
        Log.e("tag", "image file : " + imageFileName);
        //Log.e("tag", "image code : " + StrImageUploadToDB);
        Log.e("tag", "image base64 : " + ConvertImageToBase64);

        upload_image_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.upload_image_token";
        params.put(field_name[0], "Master Customer");
        params.put(field_name[1], getID);
        params.put(field_name[2], imageFileName+".jpg");
        params.put(field_name[3], "0");
        params.put(field_name[4], StrImageUploadToDB);
        params.put(field_name[6], "image");

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(upload_image_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp_content;
                try {
                    resp_content = new String(responseBody, "UTF-8");
                    Log.e("tag", resp_content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("tag", String.valueOf(e));
                }
                try {
                    //Cek Lagi Nanti Disini
                    session.updatePictureProfil("/files/" + imageFileName + ".jpg");
                    Toasty.success(getApplicationContext(), "Berhasil Memperbarui Profil Picture", Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    //Toast.makeText(getContext(), "Terjadi Kesalahan, Mohon Periksa Data Kembali 2", Toast.LENGTH_LONG).show();
                    Log.e("tag", "Gagal1" + ": " +String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refreshActivity(){
        Intent a = new Intent(UpdateProfileActivity.this, MainActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        finish();
        super.onBackPressed();
    }

}
