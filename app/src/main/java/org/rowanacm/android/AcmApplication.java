package org.rowanacm.android;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


public class AcmApplication extends Application {

    private FirebaseComponent firebaseComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        firebaseComponent = DaggerFirebaseComponent.builder()
                .appModule(new AppModule(this)).build();
    }

    public FirebaseComponent getFirebaseComponent() {
        return firebaseComponent;
    }

}
