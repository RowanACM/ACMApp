package org.rowanacm.android.user;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.rowanacm.android.AcmClient;
import org.rowanacm.android.App;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManager {

    @Inject
    AcmClient acmClient;
    @Inject FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;
    private UserInfo currentUser;
    private String googleLoginToken;

    private List<UserListener> userListeners = new ArrayList<>();

    public UserManager() {
        App.get().getAcmComponent().inject(this);

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                setFirebaseUser(firebaseAuth.getCurrentUser());
            }
        });

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                refreshCurrentUser();
            }
        });

        firebaseAuth.addIdTokenListener(new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    return;
                }

                firebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        setGoogleLoginToken(task.getResult().getToken());

                        if (getCurrentUser() == null) {
                            refreshCurrentUser();
                        }
                    }
                });
            }
        });
    }

    public void addUserListener(UserListener userListener) {
        userListeners.add(userListener);
        if (currentUser != null) {
            userListener.onUserChanged(currentUser);
        }
    }

    public void removeUserListener(UserListener userListener) {
        userListeners.remove(userListener);
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
        for (UserListener userListener : userListeners) {
            userListener.onUserChanged(currentUser);
        }
    }

    public String getGoogleLoginToken() {
        return googleLoginToken;
    }

    public void setGoogleLoginToken(String googleLoginToken) {
        this.googleLoginToken = googleLoginToken;
    }

    public void refreshCurrentUser() {
        acmClient.getUserInfo(googleLoginToken).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                setCurrentUser(response.body());
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }
}
