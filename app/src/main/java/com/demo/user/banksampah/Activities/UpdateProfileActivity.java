package com.demo.user.banksampah.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.Adapter.RestProcess;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    protected ImageView imgPinMaps;
    protected EditText etNamaLengkap, etEmail, etNoHp, etTglLahir, etAlamat;
    protected TextInputLayout DoB;

    protected Button btnUpdate;

    protected String strNamaLengkap_Update, strLatLong_Update, strEmail_Update,
              strAlamat_Update;

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

        imgProfil = findViewById(R.id.imgProfil_Update);
        etNamaLengkap = findViewById(R.id.etNamaLengkap_Update);
        etEmail = findViewById(R.id.etEmail_Update);
        etNoHp = findViewById(R.id.etNoHP_Update);
        etTglLahir = findViewById(R.id.etTanggalLahir_Update);
        DoB = findViewById(R.id.DateOfBirth);
        etAlamat = findViewById(R.id.etAlamat_Update);
        imgPinMaps = findViewById(R.id.imgPinCircle);
        btnUpdate = findViewById(R.id.btnSimpan_Update);

        //Session Instance
        session = new PrefManager(getApplicationContext());
        user = session.getUserDetails();
        getNama = user.get(PrefManager.KEY_NAMA);
        getFoto = user.get(PrefManager.KEY_FOTO);
        getNoHP = user.get(PrefManager.KEY_NO_HP);
        getAlamat = user.get(PrefManager.KEY_ALAMAT);
        getEmail = user.get(PrefManager.KEY_EMAIL);
        getID = user.get(PrefManager.KEY_ID);
        getTglLahir = user.get(PrefManager.KEY_ROLE_USER);

        Log.e("tag", getFoto + "," + getTglLahir);

        try {
            Date date = inputDateFormat.parse(getTglLahir);
            dateFixed = outputDateFormat.format(date);
        }catch (ParseException e){
            Log.e("tag", String.valueOf(e));

            try {
                Date date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(getTglLahir);
                dateFixed = outputDateFormat.format(date);
            }catch (ParseException a){
                a.printStackTrace();
            }
        }

        etNamaLengkap.setText(getNama);
        etEmail.setText(getEmail);
        etNoHp.setText(getNoHP);
        etAlamat.setText(getAlamat);
        etTglLahir.setText(dateFixed);

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
                .into(imgProfil);

        //Get Current Location GPS
        getCurrentLocation();

        //DateFormat
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormatter_ToDB = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        DoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        etTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                etTglLahir.setText(date);
            }
        };

        imgPinMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMaps = new Intent(getApplicationContext(), OpenMaps.class);
                startActivityForResult(openMaps, ADDRESS_REQUEST_CODE);
            }
        });

        imgProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
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

        private void showDateDialog(){
            Calendar newCalendar = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month,dayOfMonth);
                    etTglLahir.setText(dateFormatter.format(newDate.getTime()));
                    getTglLahir = dateFormatter_ToDB.format(newDate.getTime());
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }

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
                            imgProfil.setImageBitmap(selectedImage);

                            String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
                            String name = etNamaLengkap.getText().toString();
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

                        imgProfil.setImageBitmap(images);

                        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
                        String name = etNamaLengkap.getText().toString();
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

    private void validate(){
        strNamaLengkap_Update = etNamaLengkap.getText().toString();
        strEmail_Update = etEmail.getText().toString();
        strAlamat_Update = etAlamat.getText().toString();

        if (strNamaLengkap_Update.isEmpty()){
            etNamaLengkap.setError(getString(R.string.MSG_FULLNAME_EMPTY));
            etNamaLengkap.requestFocus();
        }
        else if (strAlamat_Update.isEmpty()){
            etAlamat.setError(getString(R.string.MSG_ALAMAT_EMPTY));
            etAlamat.requestFocus();
        } else if (imgProfil.getDrawable()!= null && imageFileName != null){
            UpdateToDB(strNamaLengkap_Update, strEmail_Update, strAlamat_Update);
            uploadImage();
        }else{
            UpdateToDB(strNamaLengkap_Update, strEmail_Update, strAlamat_Update);
        }
    }

    private void UpdateToDB(final String namaLengkap, final String email, final String alamat){
        customProgress.showProgress(this, "", false);

        String[]field_name = {"id_user", "nama_lengkap", "email", "tanggal_lahir",
                "alamat", "latlong"};

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/method/digitalwaste.digital_waste.custom_api.change_profile_user";
        params.put(field_name[0], getID);
        params.put(field_name[1], namaLengkap);
        params.put(field_name[2], email);
        params.put(field_name[3], getTglLahir);
        params.put(field_name[4], alamat);
        params.put(field_name[5], strLatLong_Update);

        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.post(base_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                try {
                    String resp_content = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    session.updateProfil(strNamaLengkap_Update, strLatLong_Update, strAlamat_Update, strEmail_Update);
                    Toasty.success(getApplicationContext(), "Data Profil Berhasil Diperbarui.", Toast.LENGTH_SHORT).show();
                    refreshActivity();
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                    builder.setMessage("Selamat! Profil Berhasil Diperbarui.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    refreshActivity();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();*/
                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag"," 1: " + String.valueOf(error));
            }
        });
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