package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.rowanacm.android.announcement.CreateAnnouncementDialog;
import org.rowanacm.android.utils.AcmUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The main activity of the app. Contains a view pager with
 * two fragments, InfoFragment and AnnouncementFragment
 */
public class MainTabActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainTabActivity.class.getSimpleName();

    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean showingAdminFragment;

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 4;

    @Inject FirebaseAuth firebaseAuth;
    @Inject GoogleSignInOptions gso;
    @Inject GoogleApiClient googleApiClient;

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;

    SectionsPagerAdapter sectionsPagerAdapter;

    // Whether the current user is an admin and is able to create announcements
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((AcmApplication)getApplication()).getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new EmptyTabChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                AcmUtils.setFabVisibility(fab, position == 1 && admin);
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    adminListener(user.getUid());
                } else {
                    // User is signed out
                    AcmUtils.setFabVisibility(fab, false);
                }

            }
        };
    }

    public void showAdminFragment() {
        if (!showingAdminFragment) {
            showingAdminFragment = true;
            sectionsPagerAdapter.addFragment(AdminFragment.newInstance());
            viewPager.setAdapter(sectionsPagerAdapter);
        }
    }


    /**
     * Create the menu bar on the top of Main Activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    /**
     * Called by the system when an options menu item is selected (the ... on the top right of the activity)
     * @param item The item that was selected
     * @return True if it was properly handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                switchActivity(SettingsActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
        googleApiClient.disconnect();
    }

    public void signInGoogle() {
        Toast.makeText(this, R.string.google_sign_in_prompt, Toast.LENGTH_LONG).show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(LOG_TAG, "onActivityResult: failed else");
            }
        }
    }

    /**
     * Sign the user into Firebase after they have signed into their google account
     * @param acct The user's GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Toast.makeText(this, acct.getEmail(), Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "onFailure: " + e);
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainTabActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Show the dialog to create an announcement
     */
    @OnClick(R.id.fab)
    protected void showCreateAnnouncementDialog() {
        new CreateAnnouncementDialog(this).create().show();
    }

    // TODO: Something in this method is causing a memory leak
    private void adminListener(final String userid) {
        FirebaseDatabase.getInstance().getReference().child("members").child(userid).child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && ((boolean)dataSnapshot.getValue())) {
                    admin = true;
                    showAdminFragment();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Switch to a new activity
     * @param newActivity The class of the activity to switch to. Use ActivityName.class
     */
    private void switchActivity(Class newActivity) {
        startActivity(new Intent(this, newActivity));
    }

}
