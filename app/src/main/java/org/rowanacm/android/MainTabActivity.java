package org.rowanacm.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.rowanacm.android.annoucement.Announcement;
import org.rowanacm.android.annoucement.AnnouncementListFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import static org.rowanacm.android.UserData.mDatabase;

public class MainTabActivity extends AppCompatActivity {

    private static final String TAG = "MainTabActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if(position == 0) {
                    fab.hide();
                }
                else if(position == 1) {
                    if(admin) {
                        fab.show();
                    }
                    else {
                        fab.hide();
                    }
                }
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

                    FirebaseDatabase.getInstance().getReference("slack").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            if(currentUser != null) {
                                String email = currentUser.getEmail();


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (((String) snapshot.getValue()).equalsIgnoreCase(email)) {
                                        updateSlackViews(true);
                                        return;
                                    }
                                }
                                updateSlackViews(false);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    //fab.show();
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainTabActivity.this);
                alertDialogBuilder.setTitle("Create announcement");
                final View dialogView = getLayoutInflater().inflate(R.layout.create_announcement_view, null);
                alertDialogBuilder.setView(dialogView);

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String author = ((EditText)dialogView.findViewById(R.id.author_edit_text)).getText().toString();
                        String subject = ((EditText)dialogView.findViewById(R.id.subject_edit_text)).getText().toString();
                        String message = ((EditText)dialogView.findViewById(R.id.message_edit_text)).getText().toString();
                        String committee = ((Spinner)dialogView.findViewById(R.id.committee_spinner)).getSelectedItem().toString();

                        long timestamp = new Date().getTime() / 1000;

                        String date = DateFormat.getDateTimeInstance().format(new Date());

                        Announcement announcement = new Announcement(author, committee, date, subject, message, subject, timestamp);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference newRef = database.child("announcements").push();
                        newRef.setValue(announcement);
                    }
                });

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser != null) {
                    EditText nameEditText = (EditText) dialogView.findViewById(R.id.author_edit_text);
                    nameEditText.setText(currentUser.getDisplayName());
                }


                Spinner spinner = (Spinner) dialogView.findViewById(R.id.committee_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainTabActivity.this,
                        R.array.committee_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                alertDialogBuilder.create().show();
            }
        });

    }

    public void adminListener(final String userid) {
        mDatabase.child("admins").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(((String)dataSnapshot.getValue()).equalsIgnoreCase(userid)){
                    admin = true;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

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

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ATTENDANCE";
                case 1:
                    return "ANNOUNCEMENTS";
            }
            return null;
        }
    }
    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(this, newActivity);
        startActivity(intent);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(String... urls) {
            String fullString = "";
            URL url = null;
            try {
                //TODO Find a way to commit the token
                url = new URL("https://slack.com/api/users.list?token=abc123");

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                fullString += line;
            }
            reader.close();

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser == null || currentUser.getEmail() == null)
                    return false;
                return fullString.contains(currentUser.getEmail());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            updateSlackViews(result);
        }
    }


    private void updateSlackViews(boolean onSlack) {
        TextView slackTextView = (TextView) findViewById(R.id.slack_textview);
        Button slackSignUpButton = (Button) findViewById(R.id.slack_sign_up_button);
        slackSignUpButton.setVisibility(View.VISIBLE);
        slackTextView.setVisibility(View.VISIBLE);

        if(onSlack) {
            slackTextView.setText("You are on slack ✓");
            slackSignUpButton.setText("Open Slack");

            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("slack://open");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }
        else {
            slackTextView.setText("You are not on slack");
            slackSignUpButton.setText("Sign Up");
            slackSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.openUrl(MainTabActivity.this, "https://rowanacm.slack.com/signup");
                }
            });
        }
    }

}
