package org.rowanacm.android;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, FirebaseModule.class})
public interface FirebaseComponent {
    void inject(AcmApplication application);

    void inject(MainTabActivity mainActivity);

    void inject(InfoFragment mainFragment);

    /*
    void inject(PTSDTestFragment ptsdTestFragment);
    void inject(ResourcesFragment resourcesFragment);
    void inject(NewsFragment newsFragment);
    void inject(FacilitiesFragment facilitiesFragment);
    void inject(SettingsFragment settingsFragment);

    void inject(RatingDialog ratingDialog);

    void inject(FacilityLoader facilityLoader);
    */
}