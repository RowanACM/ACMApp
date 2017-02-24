package org.rowanacm.android.annoucement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.us.acm.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AnnouncementListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementListFragment extends Fragment {
    RecyclerView recyclerView;

    private AnnouncementArrayAdapter adapter;

    public AnnouncementListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AnnouncementListFragment.
     */
    public static AnnouncementListFragment newInstance() {
        return new AnnouncementListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_announcement_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.announcement_recycler_view);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        announcementsListener();
    }

    public ChildEventListener announcementsListener() {
        try {
            return FirebaseDatabase.getInstance().getReference().child("announcements").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    addAnnouncement(announcement);
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
        } catch (Exception e) {
            Log.d("Firebase job list Error", e.getMessage());
            return null;
        }
    }
    public void addAnnouncement(Announcement announcement) {
        Log.d(TAG, "addAnnouncement() called with: announcement = [" + announcement + "]");
        adapter.addItem(announcement);
    }

    /**
     * Setup the RecyclerView and link it to the NewsAdapter
     */
    private void setupRecyclerView() {
        if(recyclerView != null) {
            adapter = new AnnouncementArrayAdapter(new ArrayList<Announcement>(), AnnouncementListFragment.this, 10);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
    }



    private void switchActivity(Class newActivity) {
        Intent intent = new Intent(getContext(), newActivity);
        startActivity(intent);
    }
}
