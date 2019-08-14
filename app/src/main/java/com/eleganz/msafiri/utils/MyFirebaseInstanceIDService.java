package com.eleganz.msafiri.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.eleganz.msafiri.PaymentActivity.SHARED_PREFERENCES;

/**
 * Created by eleganz on 29/1/19.
 */

public class MyFirebaseInstanceIDService
        extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("InstanceID", refreshedToken);
        editor.commit();

        Log.d("mytokenrefreshed",refreshedToken);

    }
}
