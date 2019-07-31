package com.demo.user.banksampah.Firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public void onasd(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()){
                    Log.w(TAG, "getInstanceId Failed", task.getException());
                    return;
                }

                String token = task.getResult().getToken();

                storeRegIdInPref(token);
                sendRegistrationToServer(token);

                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", token);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
            }
        });

        /*super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Save ke Shared Pref
        storeRegIdInPref(refreshedToken);

        //Send Id to Server
        sendRegistrationToServer(refreshedToken);

        //Notify UI that registration has completed..
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/

        //Log.d("TOKEN", refreshedToken);
    }

    private void sendRegistrationToServer(final String token){
        Log.e(TAG, "sendRegistrationToServer : " + token);
    }

    private void storeRegIdInPref(String token){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("regId", token);
        editor.commit();
    }

}
