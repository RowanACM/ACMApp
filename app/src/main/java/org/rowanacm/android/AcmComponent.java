package org.rowanacm.android;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, AcmModule.class})
public interface AcmComponent {
    void inject(AcmApplication application);

    void inject(MainTabActivity mainActivity);
    void inject(SettingsActivity settingsActivity);
    void inject(InfoFragment mainFragment);
}