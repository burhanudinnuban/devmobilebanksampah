package com.demo.user.banksampah.Firebase;

import android.app.NotificationManager;
import android.content.Context;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    private NotificationManager notificationManager;
//    = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);


    /*public NotificationUtils(Context mContext){
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timestamp,
                                        Intent intent){
        showNotificationMessage(title, message, timestamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp,
                                        Intent intent, String imageUrl){

        //Cek untuk push message empty..
        if (TextUtils.isEmpty(message)){
            return;
        }

        //Notif Icon...
        final int icon = R.mipmap.ic_launcher_round;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                mContext,0,intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
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
        String channelId = mContext.getString(R.string.default_notification_channel_id);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(soundUri)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timestamp))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp,
                                PendingIntent resultPendingIntent, Uri soundUri){
        String channelId = mContext.getString(R.string.default_notification_channel_id);
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
            List<ActivityManager.RunningAppProcessInfo>runningProcess = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo : runningProcess){
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    for (String activeProcess : processInfo.pkgList){
                        if (activeProcess.equalsIgnoreCase(context.getPackageName())){
                            isInBackground = false;
                        }
                    }
                }
            }
        }else{
            List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
            ComponentName componentName = taskInfos.get(0).topActivity;
            if(componentName.getPackageName().equalsIgnoreCase(context.getPackageName())){
                isInBackground = false;
            }
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
    }*/
}
