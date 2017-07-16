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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import org.rowanacm.android.announcement.CreateAnnouncementDialog;
import org.rowanacm.android.settings.SettingsActivity;
import org.rowanacm.android.authentication.UserInfo;
import org.rowanacm.android.authentication.UserListener;
import org.rowanacm.android.authentication.UserManager;
import org.rowanacm.android.utils.ViewUtils;

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

    @Inject DatabaseReference database;
    @Inject FirebaseAuth firebaseAuth;
    @Inject GoogleApiClient googleApiClient;
    @Inject
    UserManager userManager;

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.google_sign_in_button) SignInButton googleSignInButton;

    private UserListener userListener;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private boolean showingAdminFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setupTabLayout();

        userListener = new UserListener() {
            @Override
            public void onUserChanged(UserInfo currentUser) {
                if (currentUser != null && currentUser.getIsAdmin()) {
                    showAdminFragment();
                    //fab.show();
                } else {
                    //fab.hide();
                }
            }
        };


        userManager.addUserListener(userListener);
    }

    private void setupTabLayout() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new EmptyTabChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ViewUtils.setVisibility(fab, position == 1 && userManager.getCurrentUser() != null && userManager.getCurrentUser().getIsAdmin());
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    public void showAdminFragment() {
        if (!showingAdminFragment) {
            showingAdminFragment = true;
            sectionsPagerAdapter.addFragment(AdminFragment.newInstance());
            sectionsPagerAdapter.notifyDataSetChanged();
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
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        userManager.removeUserListener(userListener);
        googleApiClient.disconnect();
    }

    @OnClick(R.id.google_sign_in_button)
    public void signInGoogle() {
        Toast.makeText(this, R.string.google_sign_in_prompt, Toast.LENGTH_LONG).show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RequestCode.GOOGLE_SIGN_IN);
    }

    public void showGoogleSignInButton() {
        googleSignInButton.setVisibility(View.VISIBLE);
    }

    /**
     * Update the views related to Google sign in
     * @param currentlySignedIn Whether the user is currently signed in
     */
    public void updateGoogleSignInButtons(boolean currentlySignedIn) {
        ViewUtils.setVisibility(googleSignInButton, !currentlySignedIn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google sign in was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(MainTabActivity.this, "Unable to sign in. Try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Sign the user into Firebase after they have signed into their google account
     * @param account The user's GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();
        if (!isRowanEmailAddress(account.getEmail())) {
            Toast.makeText(this, "Invalid email. Sign in with your Rowan email address.", Toast.LENGTH_LONG).show();
            Auth.GoogleSignInApi.revokeAccess(googleApiClient);
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainTabActivity.this, "Unable to sign in. Try again later.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(MainTabActivity.this, "Unable to sign in. Try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean isRowanEmailAddress(String email) {
        return email.endsWith("rowan.edu");
    }

    /**
     * Show the dialog to create an announcement
     */
    @OnClick(R.id.fab)
    protected void showCreateAnnouncementDialog() {
        new CreateAnnouncementDialog(this).create().show();
    }

    /**
     * Switch to a new activity
     * @param newActivity The class of the activity to switch to. Use ActivityName.class
     */
    private void switchActivity(Class newActivity) {
        startActivity(new Intent(this, newActivity));
    }

}
