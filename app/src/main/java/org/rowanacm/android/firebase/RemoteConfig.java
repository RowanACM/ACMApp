package org.rowanacm.android.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.us.acm.BuildConfig;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * A wrapper for FirebaseRemoteConfig
 */
public class RemoteConfig {

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private Context context;

    public RemoteConfig(@NonNull FirebaseRemoteConfig firebaseRemoteConfig, Context context) {
        this.firebaseRemoteConfig = firebaseRemoteConfig;
        this.context = context;
        fetch();
    }

    public void fetch() {
        fetch(BuildConfig.DEBUG ? 0 : 12*60*60);
    }

    public void fetch(int cacheSeconds) {
        firebaseRemoteConfig.fetch(cacheSeconds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseRemoteConfig.activateFetched();
                    }
                });
    }

    public FirebaseRemoteConfig getFirebaseRemoteConfig() {
        return firebaseRemoteConfig;
    }

    public boolean getBoolean(int resId) {
        return firebaseRemoteConfig.getBoolean(context.getString(resId));
    }

    public int getInt(int resId) {
        return (int) firebaseRemoteConfig.getDouble(context.getString(resId));
    }

    public double getDouble(int resId) {
        return firebaseRemoteConfig.getDouble(context.getString(resId));
    }

    public String getString(int resId) {
        return firebaseRemoteConfig.getString(context.getString(resId));
    }
}