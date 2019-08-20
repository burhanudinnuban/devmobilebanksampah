package com.demo.user.banksampah.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.PrefManager;
import com.demo.user.banksampah.BottomNavigationViewNew;
import com.demo.user.banksampah.MainFragment.HomeFragment;
import com.demo.user.banksampah.MainFragment.MemberFragment;
import com.demo.user.banksampah.MainFragment.ReceiveFragment;
import com.demo.user.banksampah.MainFragment.SalesFragment;
import com.demo.user.banksampah.NotificationActivities.SubNotificationActivity;
import com.demo.user.banksampah.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {

    //Get Data From Login Process
    public static String strNama = "";
    public static String strFoto = "";
    public static String strID = "";
    public static String strPoints = "";
    public static String strLatLong = "";
    public static String strNoHP = "";
    public static String strAlamat = "";
    public static String strEmail = "";

    //Token
    public static String strToken = "";

    protected TextView tvNamaUser;
    protected ImageView imgSmallPicture, imgPesan, imgQRCode;
    protected String url_foto;
    protected boolean doubleBackPress = false;

    //private Boolean click = true;
    //CountDownTimer ctd;

    //Session Class
    PrefManager session;

    //New Setting in Fragment
    final Fragment fragment_beranda = new HomeFragment();
    final Fragment fragment_receiveSampah = new ReceiveFragment();
    final Fragment fragment_member = new MemberFragment();
    final Fragment fragment_jualSampah = new SalesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment_beranda;

    //private static final String TAG = MainActivity.class.getSimpleName();
    //private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static boolean isAppRunning;
    protected Dialog myDialog;

    //Create Static Notification
    private NotificationManager notificationManager;
    public static final String CHANNEL_ID = "test_notif";
    private static final String CHANNEL_NAME = "Test Notif";
    private static final String CHANNEL_DESC = "Test Notification Masuk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgSmallPicture = findViewById(R.id.imgRegisterPicture);
        imgPesan = findViewById(R.id.imgNotification);
        imgQRCode = findViewById(R.id.imgQrCode);
        tvNamaUser = findViewById(R.id.tvNamaUser);
        BottomNavigationViewNew navigation = findViewById(R.id.navigation);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myDialog = new Dialog(this);

        String channelId = "Erecycle";
        String channel2 = "Topic Erecycle";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    "Channel 1", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Erecycle Main");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel notificationChannel2 = new NotificationChannel(channel2,
                    "Channel 2", NotificationManager.IMPORTANCE_MIN);

            notificationChannel.setDescription("Erecycle Topic");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel2);
        }

        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                }
                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        };*/

        imgSmallPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent_profile);
            }
        });

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Baru Fragment
        fm.beginTransaction().add(R.id.main_container, fragment_beranda, "1").commit();
        fm.beginTransaction().add(R.id.main_container, fragment_member, "2").hide(fragment_member).commit();
        fm.beginTransaction().add(R.id.main_container, fragment_receiveSampah, "3").hide(fragment_receiveSampah).commit();
        fm.beginTransaction().add(R.id.main_container, fragment_jualSampah, "4").hide(fragment_jualSampah).commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{0, 200});
            channel.setSound(soundUri, att);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.areNotificationsEnabled() || isChannelBlocked(CHANNEL_ID)) {
                showPermissionNotificationOreo();
                Log.e("tag", "2");
                return;
            }
        }

        imgPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, SubNotificationActivity.class);
                startActivity(a);
            }
        });

        imgQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

    }

    @Override
    protected void onStart(){
        session = new PrefManager(getApplicationContext());
        //session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();
        strNama = user.get(PrefManager.KEY_NAMA);
        strFoto = user.get(PrefManager.KEY_FOTO);
        strPoints = user.get(PrefManager.KEY_POINT);
        strLatLong = user.get(PrefManager.KEY_LATLONG);
        strNoHP = user.get(PrefManager.KEY_NO_HP);
        strAlamat = user.get(PrefManager.KEY_ALAMAT);
        strEmail = user.get(PrefManager.KEY_EMAIL);
        strID = user.get(PrefManager.KEY_ID);

        HashMap<String, String> token = session.getTokenDetails();
        strToken = token.get(PrefManager.KEY_TOKEN);

        url_foto = "http://dev-erpnext.pracicointiutama.id";
        Picasso.get()
                .load(url_foto + MainActivity.strFoto)
                .error(R.drawable.ic_navigation_profil)
                .into(imgSmallPicture);

        tvNamaUser.setText(strNama);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));*/
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }

    private void generateQRCode(){
        try{
            int width = 300;
            int height = 300;
            int smallestDimension =  width < height ? width:height;

            String dataContain = "ID: " + strID + "\n," + "Nama: " + strNama + "\n," + "Alamat: " + strAlamat;
            String charset = "UTF-8";

            //Spesificies Error Correction
            Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            CreateQRCode(dataContain, charset, hints, smallestDimension, smallestDimension);
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("tag", ex.toString());
        }
    }

    private void CreateQRCode(String dataContain, String charset, Map hints, int widthQR, int heightQR){
        try{
            BitMatrix bitMatrix = new MultiFormatWriter().encode(new String (dataContain.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, widthQR, heightQR, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width*height];

            for(int i = 0; i< height; i++){
                int offset = i * width;

                for(int x = 0; x < width; x++){
                    pixels[offset + x] = bitMatrix.get(x,i) ?
                            ResourcesCompat.getColor(getResources(),R.color.colorAccent, null) :WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0,0, width, height);
            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dompet);

            showPopUpQRCode(overlay, bitmap);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("tag", e.toString());
        }
    }

    private Bitmap mergeBitmap(Bitmap overlay, Bitmap bitmap){
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth())/2;
        int cenreY = (canvasHeight - overlay.getHeight()/2);
        canvas.drawBitmap(overlay, centreX, cenreY, null);

        return combined;
    }

    private void showPopUpQRCode(Bitmap overlay, Bitmap bitmap){
        myDialog.setContentView(R.layout.pop_up_view_qrcode);
        myDialog.setCanceledOnTouchOutside(false);

        ImageView imgQRCode = myDialog.findViewById(R.id.img_QRCODE);
        //imgQRCode.setImageBitmap(mergeBitmap(overlay, bitmap));
        imgQRCode.setImageBitmap(bitmap);

        //Customization for Dialog..
        if(myDialog.getWindow()!= null) {
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myDialog.show();
        }
    }

    private void openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @RequiresApi(26)
    private boolean isChannelBlocked(String channelID) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = manager.getNotificationChannel(channelID);

        return channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE;
    }

    @RequiresApi(26)
    private void openChannelSettings(String channelID) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelID);
        startActivity(intent);
    }

    //Test Buat Notif
    /*private void displayNotif() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_pesan)
                        .setContentText("MESSAGE dari Pesan")
                        .setContentTitle("TITLE Dari Pesan")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat mNotif = NotificationManagerCompat.from(this);
        mNotif.notify(1, mBuilder.build());
    }*/



    private void showPermissionNotificationOreo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Perizinan Pengaturan Notifikasi");

        alertDialog.setMessage("Hai " + strNama + getString(R.string.MSG_PERIZINAN_NOTIFIKASI))
                .setIcon(R.drawable.ic_navigation_info_2)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (isChannelBlocked(CHANNEL_ID)) {
                                openChannelSettings(CHANNEL_ID);
                            } else if (!notificationManager.areNotificationsEnabled()) {
                                openNotificationSettings();
                            }
                        }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialogBuilder = alertDialog.create();
        alertDialogBuilder.show();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Intent merch_intent = new Intent(this, LogOutActivity.class);
        //startActivity(merch_intent);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private BottomNavigationViewNew.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationViewNew.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_beranda:
                    fm.beginTransaction().hide(active).show(fragment_beranda).commit();
                    active = fragment_beranda;
                    return true;
                case R.id.navigation_member:
                    fm.beginTransaction().hide(active).show(fragment_member).commit();
                    active = fragment_member;
                    return true;
                case R.id.navigation_sampah:
                    fm.beginTransaction().hide(active).show(fragment_receiveSampah).commit();
                    active = fragment_receiveSampah;
                    return true;
                case R.id.navigation_jual:
                    fm.beginTransaction().hide(active).show(fragment_jualSampah).commit();
                    active = fragment_jualSampah;
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onBackPressed(){
        if (doubleBackPress){
            super.onBackPressed();
            return;
        }
        this.doubleBackPress = true;
        Toasty.info(this, "Tekan Tombol 'Back' Sekali Lagi untuk Keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPress=false;
            }
        }, 2000);
    }
}
