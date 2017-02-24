package org.rowanacm.android;

import android.content.Context;
import android.us.acm.BuildConfig;
import android.us.acm.R;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.rowanacm.android.firebase.RemoteConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    public FirebaseModule() {
        //this.mainActivity = mainActivity;
    }

    @Provides
    @Singleton
    FirebaseRemoteConfig providesFirebaseRemoteConfig() {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        return firebaseRemoteConfig;
    }

    @Provides
    @Singleton
    RemoteConfig providesRemoteConfig(FirebaseRemoteConfig firebaseRemoteConfig, Context context) {
        return new RemoteConfig(firebaseRemoteConfig,  context);
    }

    @Provides
    @Singleton
    GoogleSignInOptions providesGoogleSignInOptions(Context context) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    DatabaseReference providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }


}