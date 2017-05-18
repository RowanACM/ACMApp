package org.rowanacm.android;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


public class AcmApplication extends Application {

    private AcmComponent acmComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        acmComponent = DaggerAcmComponent.builder()
                .appModule(new AppModule(this)).build();
    }

    public AcmComponent getAcmComponent() {
        return acmComponent;
    }

}
