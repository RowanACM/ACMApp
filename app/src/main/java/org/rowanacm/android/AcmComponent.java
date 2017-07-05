package org.rowanacm.android;

import org.rowanacm.android.announcement.AnnouncementListFragment;
import org.rowanacm.android.settings.SettingsActivity;
import org.rowanacm.android.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, AcmModule.class})
public interface AcmComponent {
    void inject(AcmApplication application);

    void inject(MainTabActivity mainActivity);
    void inject(SettingsActivity settingsActivity);
    void inject(SettingsFragment settingsFragment);
    void inject(AdminFragment adminFragment);
    void inject(InfoFragment mainFragment);
    void inject(AnnouncementListFragment announcementListFragment);

}