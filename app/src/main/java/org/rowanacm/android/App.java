package org.rowanacm.android;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.leakcanary.LeakCanary;

import org.rowanacm.android.injection.AcmComponent;
import org.rowanacm.android.injection.AppModule;
import org.rowanacm.android.injection.DaggerAcmComponent;


public class App extends Application {

    private static App INSTANCE;

    private AcmComponent acmComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        INSTANCE = this;

        acmComponent = DaggerAcmComponent.builder()
                .appModule(new AppModule(this))
                .build();


        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }

    public AcmComponent getAcmComponent() {
        return acmComponent;
    }

    public static App get() {
        return INSTANCE;
    }

}
