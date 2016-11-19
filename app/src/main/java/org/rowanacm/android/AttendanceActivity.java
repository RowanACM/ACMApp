package org.rowanacm.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.us.acm.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AttendanceActivity extends AppCompatActivity {

    private final static String defaultAttendanceFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLScgL5EttHTj4HblJrkIoSRo560gseCQFoypADL7qEd5UdJlnA/viewform";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button attendanceButton = (Button) findViewById(R.id.attendance_button);
        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(generateAttendanceUrl());
            }
        });

        EditText nameEditText = (EditText) findViewById(R.id.name_exit_text);
        nameEditText.setText(readFromSharedPreferenceString("name"));
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences prefs = AttendanceActivity.this.getSharedPreferences(
                        "mordor.us.acm", Context.MODE_PRIVATE);

                prefs.edit().putString("name", charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        emailEditText.setText(readFromSharedPreferenceString("email"));
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences prefs = AttendanceActivity.this.getSharedPreferences(
                        "mordor.us.acm", Context.MODE_PRIVATE);

                prefs.edit().putString("email", charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private String readFromSharedPreferenceString(String key) {
        SharedPreferences prefs = AttendanceActivity.this.getSharedPreferences(
                "mordor.us.acm", Context.MODE_PRIVATE);

        return prefs.getString(key, ""); // the default value is an empty string
    }


    private String generateAttendanceUrl() {
        String name = ((EditText) findViewById(R.id.name_exit_text)).getText().toString();
        String email = ((EditText) findViewById(R.id.email_edit_text)).getText().toString();

        if(name.length() == 0 && email.length() == 0)
            return defaultAttendanceFormUrl;

        name = name.replace(" ", "+");

        return "https://docs.google.com/forms/d/e/1FAIpQLScgL5EttHTj4HblJrkIoSRo560gseCQFoypADL7qEd5UdJlnA/viewform?entry.319595206=" + name + "&entry.1988864937=" + email + "&entry.1997712893&entry.717459855&entry.405789413&entry.856944836";
    }

    private void openUrl(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

}
