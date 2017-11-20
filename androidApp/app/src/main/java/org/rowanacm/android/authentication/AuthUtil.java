package org.rowanacm.android.authentication;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

public class AuthUtil {

    public static void signOutGoogle(final GoogleApiClient googleApiClient) {
        if (googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient);
        } else {
            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    Auth.GoogleSignInApi.signOut(googleApiClient);
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            });

            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }
}
