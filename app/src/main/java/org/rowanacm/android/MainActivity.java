package org.rowanacm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.us.acm.R;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import org.rowanacm.android.attendance.AttendanceActivity;

import butterknife.ButterKnife;


/**
 * The main activity of the app
 * @author Rowan University Mobile Application Development
 */
public class MainActivity extends AppCompatActivity {

    private final static String ACM_ATTENDANCE_URL = "https://acm-attendance.firebaseapp.com/";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String section = getIntent().getStringExtra("section");
        if(section != null && section.equals("attendance")) {
            switchActivity(AttendanceActivity.class);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
     * Gets called when an item in the menu bar is tapped
     * @param item The menu item that was selected
     * @return true to finish processing of selection, or false to perform the normal menu handling
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                switchActivity(SettingsActivity.class);
                return true;
            //case R.id.help:
            //    startActivity(new Intent(this, Help.class));
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onStart() {
        super.onStart();
        //FirebaseMessaging.getInstance().subscribeToTopic("test_notifications");
    }

    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(this, newActivity);
        startActivity(intent);
    }
    
}
