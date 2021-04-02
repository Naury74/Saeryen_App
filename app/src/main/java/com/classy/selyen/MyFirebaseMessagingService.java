package com.classy.selyen;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.e(TAG, "onNewToken 호출됨: "+ token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d(TAG, "onMessageReceived 호출됨. ");
    }

}
