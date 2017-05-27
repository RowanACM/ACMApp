package org.rowanacm.android.announcement;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.rowanacm.android.BaseFragment;
import org.rowanacm.android.R;
import org.rowanacm.android.firebase.ChildListener;

import java.util.ArrayList;

import butterknife.BindView;

public class AnnouncementListFragment extends BaseFragment {

    private static final String LOG_TAG = AnnouncementListFragment.class.getSimpleName();

    private AnnouncementAdapter adapter;

    SearchView searchView;

    @BindView(R.id.announcement_recycler_view) RecyclerView recyclerView;

    public AnnouncementListFragment() {

    }

    public static AnnouncementListFragment newInstance() {
        return new AnnouncementListFragment();
    }

    public @LayoutRes int getLayout() {
        return R.layout.fragment_announcement_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        announcementsListener();
    }

    // TODO: There is a memory leak caused by the searchview
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.annoucement_search_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);

        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    public String getTitle() {
        return "ANNOUNCEMENTS";
    }

    public ChildEventListener announcementsListener() {
        try {
            return FirebaseDatabase.getInstance().getReference().child("announcements").addChildEventListener(new ChildListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    addAnnouncement(announcement);
                }
            });
        } catch (Exception e) {
            Log.d("Firebase job list Error", e.getMessage());
            return null;
        }
    }
    public void addAnnouncement(Announcement announcement) {
        adapter.addItem(announcement);
        if (announcement.getImageUrl() != null && announcement.getImageUrl().length() > 0) {
            preloadAnnouncementImage(announcement);
        }
    }

    private void preloadAnnouncementImage(Announcement announcement) {
        Picasso.with(getActivity()).load(announcement.getImageUrl()).fetch();
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            adapter = new AnnouncementAdapter(new ArrayList<Announcement>(), AnnouncementListFragment.this, 10);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
    }

}
