package org.rowanacm.android.injection;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.rowanacm.android.AcmClient;
import org.rowanacm.android.AdminManager;
import org.rowanacm.android.BuildConfig;
import org.rowanacm.android.R;
import org.rowanacm.android.firebase.RemoteConfig;
import org.rowanacm.android.authentication.UserManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AcmModule {

    public AcmModule() {

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
    GoogleApiClient providesGoogleApiClient(Context context, GoogleSignInOptions options) {
        return new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }

    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    DatabaseReference providesFirebaseDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Provides
    @Singleton
    FirebaseMessaging providesFirebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }


    @Provides
    @Singleton
    FirebaseDatabase providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    AdminManager providesAdminManager() {
        return new AdminManager();
    }

    @Provides
    @Singleton
    AcmClient providesAttendanceClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.rowanacm.org/prod/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AcmClient.class);
    }

    @Provides
    @Singleton
    UserManager providesUserManager(FirebaseAuth firebaseAuth) {
        return new UserManager();
    }
}