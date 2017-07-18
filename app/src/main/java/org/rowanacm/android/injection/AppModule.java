package org.rowanacm.android.injection;

import android.content.Context;

import org.rowanacm.android.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return app;
    }

    @Provides
    @Singleton
    App providesAcmApplication() {
        return app;
    }

}