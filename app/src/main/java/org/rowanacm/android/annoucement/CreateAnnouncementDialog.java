package org.rowanacm.android.annoucement;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.us.acm.R;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import static butterknife.ButterKnife.findById;

public class CreateAnnouncementDialog extends AlertDialog.Builder {

    public CreateAnnouncementDialog(Activity activity) {
        super(activity);
        setTitle(R.string.create_announcement);
        final View dialogView = activity.getLayoutInflater().inflate(R.layout.create_announcement_view, null);
        setView(dialogView);

        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            // When Cancel is pressed, close the dialog and do nothing
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String author = ((EditText)dialogView.findViewById(R.id.author_edit_text)).getText().toString();
                String subject = ((EditText)dialogView.findViewById(R.id.subject_edit_text)).getText().toString();
                String message = ((EditText)dialogView.findViewById(R.id.message_edit_text)).getText().toString();
                String committee = ((Spinner)dialogView.findViewById(R.id.committee_spinner)).getSelectedItem().toString();

                // Date uses a timestamp with milliseconds. Dividing makes it match the system
                long timestamp = new Date().getTime() / 1000;
                String date = DateFormat.getDateTimeInstance().format(new Date());

                Announcement announcement = new Announcement(author, committee, date, subject, message, subject, timestamp, null, null);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference newRef = database.child("announcements").push();
                newRef.setValue(announcement);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            EditText nameEditText = findById(dialogView, R.id.author_edit_text);
            nameEditText.setText(currentUser.getDisplayName());
        }

        Spinner spinner = (Spinner) dialogView.findViewById(R.id.committee_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.committee_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
