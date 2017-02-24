package org.rowanacm.android;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, FirebaseModule.class})
public interface FirebaseComponent {
    void inject(AcmApplication application);

    void inject(MainTabActivity mainActivity);
    void inject(InfoFragment mainFragment);
}