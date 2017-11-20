package org.rowanacm.android.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * ChildEventListener has 5 abstract methods that need to be implemented, even if they aren't used
 * This class is used to simplify its usage
 */
public class ChildListener implements ChildEventListener {

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {}

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
