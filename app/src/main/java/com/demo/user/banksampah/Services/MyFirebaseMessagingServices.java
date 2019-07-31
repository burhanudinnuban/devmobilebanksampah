package com.demo.user.banksampah.Services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.demo.user.banksampah.Firebase.Config;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    protected String title, body;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null){
            return;
        }

        if(remoteMessage.getData().size() > 0){
            try {
                Log.e("tag", "via data");
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().toString());
                //Dapat dikostum sesuai kebutuhan...
                JSONObject data_fcm = jsonObject.getJSONObject("data");

                String title = data_fcm.getString("title");
                String body = data_fcm.getString("message");
                String imageUrl = data_fcm.getString("image");
                String timeStamp = data_fcm.getString("timestamp");
                boolean isBackground = data_fcm.getBoolean("is_background");

                Intent resultIntent = new Intent("com.demo.user.erecycle.notification");
                resultIntent.putExtra("message", body);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag 2", String.valueOf(e));
            }

        }

        if(remoteMessage.getNotification() != null){
            Log.e("tagss", remoteMessage.getData().toString());

            //Lebih Baik pakai getData... Jika di Background (HARUS CUSTOM LAGI JSON NYA)

            /*
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            */

            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();

            //Untuk Cek jika App sedang di Background atau Foreground (PANGGIL TOAST)
            handleNotification(body);

            //Tampilin Push Notifikasi FCM Jika via Google Console... (MODEL STANDAR)
            if (!NotificationHelper.isAppIsInBackground(getApplicationContext())) {
                Log.e("tag", "test notif di foreground");
                NotificationHelper.displayNotification(getApplicationContext(), title, body);
            }else{
                showNotificationMessage(getApplicationContext(), title, body, null, null);
                Log.e("tag", "test notif di background");
            }

        }else {
            //UNTUK CEK DATA JSON ADA DATA LAINNYA ATAU TIDAK....
            if (remoteMessage.getData().size() > 0) {
                Log.e("tag", "Data Payload: " + remoteMessage.getData().toString());
                try {
                    JSONObject jsonObject = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("tag 2", String.valueOf(e));
                }
            }
        }
    }

    private void handleNotification(String message){
        if(!NotificationHelper.isAppIsInBackground(getApplicationContext())){

            //Jika Aplikasi ada di Foreground, Tampilin Toast Message
            Log.e("tag", "via Not Backgroud");
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            //Play sound..
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.playNotificationSound();
        }else{
            //Jika aplikasi di Background...
            Log.e("tag", "Background");
        }
    }

    private void handleDataMessage(JSONObject jsonObject){
        try{
            //Dapat dikostum sesuai kebutuhan...
            JSONObject data_fcm = jsonObject.getJSONObject("data");

            String title = data_fcm.getString("title");
            String body = data_fcm.getString("message");
            String imageUrl = data_fcm.getString("image");
            String timeStamp = data_fcm.getString("timestamp");
            boolean isBackground = data_fcm.getBoolean("is_background");
            JSONObject dataPayload = data_fcm.getJSONObject("payload");

            Log.e("tag", "title: " + title);
            Log.e("tag", "message: " + body);
            Log.e("tag", "isBackground: " + isBackground);
            Log.e("tag", "payload: " + dataPayload.toString());
            Log.e("tag", "imageUrl: " + imageUrl);
            Log.e("tag", "timestamp: " + timeStamp);


            if (!NotificationHelper.isAppIsInBackground(getApplicationContext())) {

                //Jika apps ada di foreground, tampilin messagenya..
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", body);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                NotificationHelper.displayNotification(getApplicationContext(), title, body);

                //Play sound..
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.playNotificationSound();

                Log.e("tag" , "not in back");

            }else{
                Intent resultIntent = new Intent("com.demo.user.erecycle.notification");
                resultIntent.putExtra("message", body);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

                if (TextUtils.isEmpty(imageUrl)){
                    showNotificationMessage(getApplicationContext(), title, body, timeStamp, resultIntent);
                }else{
                    showNotificationMessageWithImage(getApplicationContext(), title, body, timeStamp, resultIntent, imageUrl);
                }

                Log.e("tag" , "dibelakang");

            }
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("tag", "JSON Exception: " + String.valueOf(e));
        }catch (Exception e){
            e.printStackTrace();
            Log.e("tag", "Exception: " + String.valueOf(e));
        }
    }

    private void showNotificationMessage(Context context, String title, String body, String timeStamp, Intent intent){
        NotificationHelper notificationHelper = new NotificationHelper(context);
        //intent = new Intent("com.demo.user.erecycle.notification");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, body, timeStamp, intent);
    }

    private void showNotificationMessageWithImage(Context context, String title, String body, String timeStamp,
                                                  Intent intent, String imageUrl){
        NotificationHelper notificationHelper = new NotificationHelper(context);
        //intent = new Intent("com.demo.user.erecycle.notification");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationHelper.showNotificationMessage(title, body, timeStamp, intent, imageUrl);
    }

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
        Log.e("tag", s);
    }
}
