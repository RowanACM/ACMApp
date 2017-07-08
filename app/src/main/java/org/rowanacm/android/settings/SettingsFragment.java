package org.rowanacm.android.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import org.rowanacm.android.App;
import org.rowanacm.android.R;
import org.rowanacm.android.utils.ExternalAppUtils;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    @Inject FirebaseAuth firebaseAuth;
    @Inject GoogleApiClient googleApiClient;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference appInfo = findPreference("app_info");
        appInfo.setSummary("Version: " + ExternalAppUtils.getVersionName(getActivity()));
        appInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (Math.random() < 0.1) {
                    Toast.makeText(getActivity(), "Easter \uD83E\uDD5A", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        final Preference google_sign_out = findPreference("google_sign_out");

        if (firebaseAuth.getCurrentUser() == null) {
            google_sign_out.setTitle("Sign in");
        } else {
            google_sign_out.setTitle("Sign out");
        }


        google_sign_out.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                google_sign_out.setTitle("Sign in");
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void signOut() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        firebaseAuth.signOut();
                        Toast.makeText(getActivity(), R.string.signed_out, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
