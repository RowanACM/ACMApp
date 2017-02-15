package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.us.acm.R;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.rowanacm.android.annoucement.AnnouncementListFragment;

import butterknife.ButterKnife;

/**
 * The main activity of the app. Contains a view pager with
 * two fragments, MainFragment and AnnouncementFragment
 */
public class MainTabActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // The log tag
    private static final String TAG = "MainTabActivity";

    // Handles firebase authentication
    private FirebaseAuth mAuth;
    // Handles google authentication
    private GoogleApiClient mGoogleApiClient;
    // Get called when the user signs in/out
    private FirebaseAuth.AuthStateListener mAuthListener;
    // Gets passed to onActivityResult after signing in
    private static final int RC_SIGN_IN = 4;

    // The PagerAdapter that will provide fragments for each of the sections
    private SectionsPagerAdapter mSectionsPagerAdapter;
    // The ViewPager that will host the section contents
    private ViewPager mViewPager;

    // Whether the current user is an admin and is able to create annoucements
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // Only show the fab on the AnnouncementFragment and if the user is an admin
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if(position == 1 && admin)
                    fab.show();
                else
                    fab.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    adminListener(user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    fab.hide();
                }

            }
        };
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateAnnouncementDialog();
            }
        });
    }


    /**
     * Create the menu bar on the top of Main Activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
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
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        Log.d(TAG, "onStart: " + FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signInGoogle() {
        Toast.makeText(this, "Select your Rowan account", Toast.LENGTH_LONG).show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: rcsignin");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "onActivityResult: success");

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG, "onActivityResult: failed else");
                // Google Sign In failed, update UI appropriately
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
        mAuth.signInWithCredential(credential)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainTabActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Show the dialog to create an announcement
     */
    private void showCreateAnnouncementDialog() {
        AlertDialog.Builder alertDialogBuilder = new CreateAnnouncementDialog(this);
        alertDialogBuilder.create().show();
    }

    private void adminListener(final String userid) {
        FirebaseDatabase.getInstance().getReference().child("members").child(userid).child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && ((boolean)dataSnapshot.getValue())){
                    admin = true;
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
        Intent intent = new Intent(this, newActivity);
        startActivity(intent);
    }

    /**
     * Get the google api client
     * @return The google api client
     */
    protected GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MainFragment.newInstance();
                case 1:
                    return AnnouncementListFragment.newInstance();
                case 2:
                    return CommitteeFragment.newInstance();
                case 3:
                    return MeFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            // Number of pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INFO";
                case 1:
                    return "ANNOUNCEMENTS";
                case 2:
                    return "COMMITTEE";
                case 3:
                    return "ME";

            }
            return null;
        }
    }




}
