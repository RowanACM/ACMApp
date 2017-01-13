package org.rowanacm.android.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.us.acm.R;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.rowanacm.android.Utils;
import org.rowanacm.android.nfc.NfcManager;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Sign a member in to the meeting
 */
public class AttendanceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AttendanceActivity";

    private final static String defaultAttendanceFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLScgL5EttHTj4HblJrkIoSRo560gseCQFoypADL7qEd5UdJlnA/viewform";
    private static final int RC_SIGN_IN = 4;

    // Handles reading and writing from NFC
    NfcManager nfcManager;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Create the NFC Manager
        createNfcManager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText nameEditText = (EditText) findViewById(R.id.name_exit_text);
        nameEditText.setText(Utils.readFromSharedPreferenceString(this, "name"));
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences prefs = AttendanceActivity.this.getSharedPreferences(
                        "org.rowanacm.android", Context.MODE_PRIVATE);

                prefs.edit().putString("name", charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        emailEditText.setText(Utils.readFromSharedPreferenceString(this, "email"));
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences prefs = AttendanceActivity.this.getSharedPreferences(
                        "org.rowanacm.android", Context.MODE_PRIVATE);

                prefs.edit().putString("email", charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @OnClick(R.id.attendance_button)
    public void signInAttendance() {
        User user = getUser();
        String attendanceUrl = generateAttendanceUrl(user);
        Utils.openUrl(AttendanceActivity.this, attendanceUrl);
    }

    @OnClick(R.id.sign_in_google_button)
    public void signInGoogle() {
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
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Toast.makeText(this, acct.getEmail(), Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(AttendanceActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private User getUser() {
        String name = ((EditText) findViewById(R.id.name_exit_text)).getText().toString();
        String email = ((EditText) findViewById(R.id.email_edit_text)).getText().toString();

        User user = new User(name, email);

        return user;
    }

    @Override
    public void onNewIntent(Intent intent) {
        nfcManager.onActivityNewIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcManager.onActivityResume();
    }

    @Override
    protected void onPause() {
        nfcManager.onActivityPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Read the saved scan data
     * @throws IOException The file was unable to be read
     * @throws ClassNotFoundException The save file was unable to be converted to saveData
     */
    private void readScanData() throws IOException, ClassNotFoundException {
        //saveData = ReadWrite.readIn(getApplicationContext(), "Scans.ser");
    }

    /**
     * Save the scan data to a file
     * @throws IOException Unable to write to a file
     */
    private void writeScanData() throws IOException {
        //ReadWrite.writeOut(saveData, getApplicationContext());
    }

    /**
     * Create the NFC Manager and the NFC listener
     */
    private void createNfcManager() {
        nfcManager = new NfcManager(this);
        nfcManager.onActivityCreate();
        nfcManager.setOnTagReadListener(new NfcManager.TagReadListener() {
            @Override
            public void onTagRead(String tagId) {
                tagWasScanned(tagId);
            }
        });
    }

    /**
     * When a tag was scanned by the app
     * @param tagId The tag that was read
     */
    private void tagWasScanned(String tagId) {
        Toast.makeText(this, tagId, Toast.LENGTH_LONG).show();

        showCheckmarkToast();
        vibratePhone(200);
        //addScan(tagId);
        sendTagsToServer();
    }

    /**
     * Vibrate the phone
     * @param milliseconds The number of milliseconds to vibrate the phone
     */
    private void vibratePhone(int milliseconds) {
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(milliseconds);
    }

    private void sendTagsToServer() {
        //if(SaveData.getServerPassword().equals("-1")) {
        //    Toast.makeText(getBaseContext(), "Please Insert Pass Key to Send Data", Toast.LENGTH_LONG).show();
       // }
       // SendToServer.send();
    }

    /**
     * Display a checkmark toast to notify the user of a successful scan
     */
    private void showCheckmarkToast(){
        /*
        // Inflate the toast layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_checkmark,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(R.string.scan_successful);

        // Create the toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        // Toast.makeText(this, "Scan Successful", Toast.LENGTH_SHORT).show();
        */
    }

    private String generateAttendanceUrl(User user) {
        if(!user.isValid())
            return defaultAttendanceFormUrl;

        String formattedName = user.getName().replace(" ", "+");

        return "https://docs.google.com/forms/d/e/1FAIpQLScgL5EttHTj4HblJrkIoSRo560gseCQFoypADL7qEd5UdJlnA/viewform?entry.319595206=" + formattedName + "&entry.1988864937=" + user.getRowanEmailAddress() + "&entry.1997712893&entry.717459855&entry.405789413&entry.856944836";
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
