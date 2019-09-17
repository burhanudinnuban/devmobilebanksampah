package com.demo.user.banksampah.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.demo.user.banksampah.Firebase.Config;
import com.demo.user.banksampah.NotificationActivities.ConfirmPickOrder;
import com.demo.user.banksampah.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationHelper {

    private Context mContext;

    public NotificationHelper(Context mContext){
        this.mContext = mContext;
    }

    public static void displayNotification(Context context, String title, String body){
        Intent intent  = new Intent(context, ConfirmPickOrder.class);
        intent.putExtra("EXTRA_TITLE", title);
        intent.putExtra("EXTRA_BODY", body);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_app_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_app_logo))
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText(body)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 500, 400, 300, 200, 400})
                        //Agar Notifnya ga kehapus...
                        //Cari inboxstyle
                        .setPriority(NotificationCompat.FLAG_FOREGROUND_SERVICE)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat mNotif = NotificationManagerCompat.from(context);
        mNotif.notify(1, mBuilder.build());
    }

    public void showNotificationMessage(String title, String message, String timestamp, Intent intent){
        showNotificationMessage(title, message, timestamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp,
                                        Intent intent, String imageUrl){

        //Cek untuk push message empty..
        if (TextUtils.isEmpty(message)){
            return;
        }
        //Notif Icon...
        final int icon = R.drawable.ic_app_logo;
        intent  = new Intent(mContext, ConfirmPickOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                mContext,0,intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, MainActivity.CHANNEL_ID);
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, soundUri);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, soundUri);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, soundUri);
            playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timestamp,
                                       PendingIntent resultPendingIntent, Uri soundUri){

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        Notification notification;

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, MainActivity.CHANNEL_ID);
//        builder.setAutoCancel(true)
//                .setSmallIcon(R.drawable.ic_pesan)
//                .setContentIntent(resultPendingIntent)
//                .setContentTitle(title)
//                .setContentText(message);
//
//        Notification notifications = builder.build();
//        NotificationManagerCompat.from(mContext).notify(0, notifications);

        notification = mBuilder.setSmallIcon(icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(soundUri)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timestamp))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp,
                                     PendingIntent resultPendingIntent, Uri soundUri){

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(soundUri)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    public Bitmap getBitmapFromURL(String strURL){
        try{
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e){
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
            return null;
        }
    }

    public void playNotificationSound(){
        try{
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(mContext, soundUri);
            ringtone.play();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }
    }

    public static boolean isAppIsInBackground(Context context){
        boolean isInBackground = true;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
        ComponentName componentName = taskInfos.get(0).topActivity;
        if (componentName.getPackageName().equals(context.getPackageName())){
            isInBackground = false;
        }
        return isInBackground;
    }

    public static void clearNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timestamp){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try{
            Date date = format.parse(timestamp);
            return date.getTime();
        }catch (ParseException e){
            e.printStackTrace();
            Log.e("tag", String.valueOf(e));
        }
        return 0;
    }
}
