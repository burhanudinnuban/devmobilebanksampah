package com.demo.user.banksampah.RegisterActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demo.user.banksampah.Services.LoginActivity;
import com.demo.user.banksampah.Activities.OpenMaps;
import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.Adapter.VolleyController;
import com.demo.user.banksampah.BuildConfig;
import com.demo.user.banksampah.R;
import com.demo.user.banksampah.TrackGPS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = RegisterActivity.class.getSimpleName();
    protected ScrollView parent_layout;

    Activity activity;
    protected View rootView;
    protected Context context;

    /*API process and dialog*/
    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;
    protected CustomProgress customProgress;

    TextView tvStatusNoHP;
    EditText etNamaLengkap, etEmail, etNoHP, etAlamat, etPassword, etConfirmPassword;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imgRegisterPicture;
    ImageView imgPinCircle;
    TextInputLayout DoB;
    Button btnDaftar;

    protected String strNamaLengkap, strEmail, strNoHP, strAlamat, strPassword, strConfirmPassword;
    private String strLatLong;

    /*FOR GPS*/
    protected TrackGPS gps;
    double longitude;
    double latitude;

    //Upload Image
    protected String cameraFilePath, imageFileName, ConvertImageToBase64;
    protected String StrImageUploadToDB = "data:image/jpeg:base64,";
    protected Bitmap getBitmapPicture;
    protected ByteArrayOutputStream byteArrayOutputStream;

    //Request Code
    private final static int ADDRESS_REQUEST_CODE = 2;
    private final static int GALLERY_REQUEST_CODE = 3;
    private final static int CAMERA_REQUEST_CODE = 4;

    protected String getPhone_Extra = "", getIdUser_Extra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        parent_layout = findViewById(R.id.parent);

        Intent intent_getPhone = getIntent();
        getPhone_Extra = intent_getPhone.getStringExtra("no_telepon");
        getIdUser_Extra = intent_getPhone.getStringExtra("id_user");

        Log.e("tag", getIdUser_Extra);

        byteArrayOutputStream = new ByteArrayOutputStream();

        context = this;
        activity = (Activity) context;
        rootView = getWindow().getDecorView().getRootView();

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();
        customProgress = CustomProgress.getInstance();

        tvStatusNoHP = findViewById(R.id.tvStatusNoHP);

        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etEmail = findViewById(R.id.etEmail);
        etNoHP = findViewById(R.id.etNoHP);
//        DoB = findViewById(R.id.DateOfBirth);
        etAlamat = findViewById(R.id.etAlamat);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgRegisterPicture = findViewById(R.id.imgRegisterPicture);

        etNoHP.setText(getPhone_Extra);
        etNoHP.setCursorVisible(false);
        etNoHP.setFocusableInTouchMode(false);

        //dialog = new ProgressDialog(this);

        imgPinCircle = findViewById(R.id.imgPinCircle);

        btnDaftar = findViewById(R.id.btDaftarkan);

        //Get Current Location GPS
        getCurrentLocation();

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterProcess();
            }
        });

        imgPinCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMaps = new Intent(getApplicationContext(), OpenMaps.class);
                startActivityForResult(openMaps, ADDRESS_REQUEST_CODE);
            }
        });

        imgRegisterPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == RESULT_OK && requestCode == 0){
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADDRESS_REQUEST_CODE:

                    String lokasi = data.getStringExtra("Alamat_Lokasi");
                    etAlamat.setText(lokasi);

                    strLatLong = data.getStringExtra("LatLong_Lokasi");
                    Log.e("tag", strLatLong);
                    break;

                case GALLERY_REQUEST_CODE:
                    try {
                        Uri imageURI = data.getData();
                        if (imageURI != null) {
                            InputStream imageStream = getContentResolver().openInputStream(imageURI);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            selectedImage = Bitmap.createScaledBitmap(selectedImage, 300, 400, false);
                            selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
                            imgRegisterPicture.setImageBitmap(selectedImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;

                case CAMERA_REQUEST_CODE:
                    File imageFile = new File(cameraFilePath);
                    if (imageFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap images = BitmapFactory.decodeFile(cameraFilePath, options);
                        images = Bitmap.createScaledBitmap(images, 300, 400, false);
                        images = getResizedBitmap(images, 400);

                        imgRegisterPicture.setImageBitmap(images);
                    }
                    break;
            }

        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void selectImage() {
        final CharSequence[] options = {"Ambil Foto", "Pilih dari Galeri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Tambah Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Ambil Foto")) {
                    captureFromCamera();
                } else if (options[which].equals("Pilih dari Galeri")) {
                    pickFromGallery();
                } else if (options[which].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //Get Current Location
    private void getCurrentLocation() {
        gps = new TrackGPS(getApplicationContext());
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            strLatLong = latitude + "," + longitude;
        } else {
            gps.showSettingsAlert();
            strLatLong = "0,0";
        }
    }

    protected void RegisterProcess() {
        strNamaLengkap = etNamaLengkap.getText().toString();
        strEmail = etEmail.getText().toString();
        strNoHP = etNoHP.getText().toString();
        strAlamat = etAlamat.getText().toString();
        strPassword = etPassword.getText().toString();
        strConfirmPassword = etConfirmPassword.getText().toString();

        if (strNamaLengkap.length() == 0) {
            etNamaLengkap.setError(getString(R.string.MSG_FULLNAME_EMPTY));
            etNamaLengkap.requestFocus();
        } else if (strNoHP.length() == 0) {
            etNoHP.setError(getString(R.string.MSG_CELLPHONE_EMPTY));
            etNoHP.requestFocus();
        } else if (strNoHP.length() <= 9) {
            etNoHP.setError(getString(R.string.MSG_NO_HP_EMPTY));
            etNoHP.requestFocus();
        } else if (strAlamat.length() == 0) {
            etAlamat.setError(getString(R.string.MSG_ALAMAT_EMPTY));
            etAlamat.requestFocus();
        } else if (strPassword.length() == 0) {
            etPassword.setError(getString(R.string.MSG_PASSWORD_EMPTY));
            etPassword.requestFocus();
        } else if (strConfirmPassword.length() == 0) {
            etConfirmPassword.setError(getString(R.string.MSG_CONFIRM_EMPTY));
            etConfirmPassword.requestFocus();
        } else if (strConfirmPassword.length() < 7) {
            etConfirmPassword.setError("Minimal Kata Sandi 8 Karakter");
            etConfirmPassword.requestFocus();
        } else if (!strConfirmPassword.equals(strPassword)) {
            Toasty.warning(getApplicationContext(), getString(R.string.MSG_PASSWORD_CHECK), Toast.LENGTH_LONG).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Konfirmasi Registrasi")
                    .setIcon(R.drawable.ic_info_outline_white_24dp)
                    .setMessage("Nama Unit dan Nomor Ponsel Bank Sampah Tidak Dapat Diubah Setelahnya.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveToDB();
                            if (imgRegisterPicture.getDrawable() != null)
                                uploadImage();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void pickFromGallery() {
        Intent intent_gallery = new Intent(Intent.ACTION_PICK);
        intent_gallery.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent_gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent_gallery, GALLERY_REQUEST_CODE);
    }

    private void captureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                Log.e("tag", "photo file : " + photoFile.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
        String name = etNamaLengkap.getText().toString();

        imageFileName = "JPEG_" + name + "_" + timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg",  /* suffix */
                storageDir     /* directory */
        );
        cameraFilePath = image.getAbsolutePath();
        return image;
    }

    protected void SaveToDB() {
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"no_telepon", "nama", "email", "password", "alamat", "latlong"};

        String base_url = apiData.get("str_url_address") + apiData.get("str_api_register");

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Register Response: " + response);
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage(R.string.MSG_REGIST_SUCCESS)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(login);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.e("tag", "sukses");
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Log.d(TAG, "Error Validate Register Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], strNoHP);
                params.put(field_name[1], strNamaLengkap);
                params.put(field_name[2], strEmail);
                params.put(field_name[3], strPassword);
                params.put(field_name[4], strAlamat);
                params.put(field_name[5], strLatLong);
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

    private void uploadImage() {
        customProgress.showProgress(this, "", false);
        final String[] field_name = {"doctype", "docname", "filename", "isprivate", "filedata", "docfield"};
        String base_url = apiData.get("str_url_address") + apiData.get("str_api_upload_image");

        //Get Image and Convert to Base64
        getBitmapPicture = ((BitmapDrawable) RegisterActivity.imgRegisterPicture.getDrawable()).getBitmap();
        getBitmapPicture.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        ConvertImageToBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        StrImageUploadToDB += ConvertImageToBase64;

        String timeStamp = new SimpleDateFormat("dd-MM-yy_hh.mm", Locale.getDefault()).format(new Date());
        String name = etNamaLengkap.getText().toString();
        imageFileName = "JPEG_" + name + "_" + timeStamp;

        StringRequest strReq = new StringRequest(Request.Method.POST, base_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customProgress.hideProgress();
                Log.d(TAG, "Validate Response: " + response);
                try {
                    Toasty.success(getApplicationContext(), "Berhasil Mengupload Gambar", Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, getString(R.string.MSG_CODE_409) + " 1: " + getString(R.string.MSG_CHECK_DATA), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + "1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error Upload Image Response: " + t.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley Error: " + error.getMessage());
                customProgress.hideProgress();
                Snackbar snackbar = Snackbar
                        .make(parent_layout, getString(R.string.MSG_CODE_500) + " 1: " + getString(R.string.MSG_CHECK_CONN), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(field_name[0], "Master Customer");
                params.put(field_name[1], getIdUser_Extra);
                params.put(field_name[2], imageFileName + ".jpg");
                params.put(field_name[3], "0");
                params.put(field_name[4], StrImageUploadToDB);
                params.put(field_name[5], "image");
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
}
