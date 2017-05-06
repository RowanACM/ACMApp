package org.rowanacm.android;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private AcmApplication acmApplication;

    public AppModule(AcmApplication acmApplication) {
        this.acmApplication = acmApplication;
    }

    @Provides
    @Singleton
    Context providesAcmApplication() {
        return acmApplication;
    }

}