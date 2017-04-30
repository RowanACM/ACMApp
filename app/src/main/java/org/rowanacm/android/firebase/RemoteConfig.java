package org.rowanacm.android.firebase;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.us.acm.BuildConfig;
import android.us.acm.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class RemoteConfig {

    // TODO Do not place Android context classes in static fields (static reference to
    // FirebaseRemoteConfig which has field mContext pointing to Context); this is a memory
    // leak (and also breaks Instant Run) less... (⌘F1) A static field will leak contexts.
    // TODO Using Dagger 2 will fix this

    private static FirebaseRemoteConfig firebaseRemoteConfig;

    private static void setupRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    public static void fetchRemoteConfig(int cacheSeconds) {
        if(firebaseRemoteConfig == null) {
            setupRemoteConfig();
            cacheSeconds = 0;
        }

        firebaseRemoteConfig.fetch(cacheSeconds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseRemoteConfig.activateFetched();
                    }
                });
    }

    private static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        if (firebaseRemoteConfig == null) {
            setupRemoteConfig();
        }
        return firebaseRemoteConfig;
    }

    public static boolean getBoolean(@NonNull Context context, @StringRes int resId) {
        return getFirebaseRemoteConfig().getBoolean(context.getString(resId));
    }

    public static int getInt(@NonNull Context context, @StringRes int resId) {
        return (int) getFirebaseRemoteConfig().getDouble(context.getString(resId));
    }

    public static double getDouble(@NonNull Context context, @StringRes int resId) {
        return getFirebaseRemoteConfig().getDouble(context.getString(resId));
    }

    public static String getString(@NonNull Context context, @StringRes int resId) {
        return getFirebaseRemoteConfig().getString(context.getString(resId));
    }
}