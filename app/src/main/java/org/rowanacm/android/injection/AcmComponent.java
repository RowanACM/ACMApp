package org.rowanacm.android.injection;

import org.rowanacm.android.AdminFragment;
import org.rowanacm.android.App;
import org.rowanacm.android.AppModule;
import org.rowanacm.android.InfoFragment;
import org.rowanacm.android.MainTabActivity;
import org.rowanacm.android.MeFragment;
import org.rowanacm.android.announcement.AnnouncementListFragment;
import org.rowanacm.android.announcement.CreateAnnouncementDialog;
import org.rowanacm.android.settings.SettingsActivity;
import org.rowanacm.android.settings.SettingsFragment;
import org.rowanacm.android.authentication.UserManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, AcmModule.class})
public interface AcmComponent {
    void inject(App application);

    void inject(MainTabActivity mainActivity);

    void inject(SettingsActivity settingsActivity);
    void inject(SettingsFragment settingsFragment);

    void inject(AdminFragment adminFragment);
    void inject(InfoFragment infoFragment);
    void inject(MeFragment meFragment);
    void inject(AnnouncementListFragment announcementListFragment);

    void inject(CreateAnnouncementDialog createAnnouncementDialog);

    void inject(UserManager userManager);

}