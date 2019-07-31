package com.demo.user.banksampah.Firebase;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /*private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage message){
        Log.e(TAG, "From : " + message.getFrom());

        if(message == null){
            return;
        }

        if (message.getNotification() != null){
            Log.e(TAG, "Notification Body: " + message.getNotification().getBody());
            handleNotification(message.getNotification().getBody());
        }

        //Cek jika message ada data payload
        if(message.getData().size() > 0){
            Log.e(TAG, "Data Payload: " + message.getData().toString());

            try{
                JSONObject jsonObject = new JSONObject(message.getData().toString());
                handleDataMessage(jsonObject);
            }catch (Exception e){
                Log.e(TAG, "Exception : " + e.getMessage());
            }
        }

        //sendMyNotification (message.getNotification().getBody());
    }

    private void handleNotification(String message){
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())){
            //App di foreground, broadcast push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            //Play sound..
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{

        }
    }

    private void handleDataMessage(JSONObject jsonObject){
        Log.e(TAG, "Push JSON : " + jsonObject.toString());

        try {
            JSONObject data = jsonObject.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                //App di foreground.. broadcast messagenya
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                //Play music
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        }catch (JSONException e){
            Log.e(TAG, "JSON Exception : " + e.getMessage());
        }catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent){
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl){
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }*/

    /*private void sendMyNotification(String message){

        //Jika ada notif.. Direct ke halaman ini..

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Hey, Ada Notifikasi Baru...")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }*/
}
