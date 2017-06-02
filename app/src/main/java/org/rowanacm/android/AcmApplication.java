package org.rowanacm.android;

import android.app.Application;
import android.util.Log;

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
                .appModule(new AppModule(this))
                .build();

        // Exclude firebase crash reporting fran debug builds
        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    Log.wtf("Alert", throwable.getMessage(), throwable);
                    System.exit(2); // Prevents the service/app from freezing
                }
            });
        }
    }

    public AcmComponent getAcmComponent() {
        return acmComponent;
    }

}
