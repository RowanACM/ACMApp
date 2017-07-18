package org.rowanacm.android.announcement;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.rowanacm.android.AcmClient;
import org.rowanacm.android.App;
import org.rowanacm.android.BaseFragment;
import org.rowanacm.android.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementListFragment extends BaseFragment {

    private AnnouncementAdapter adapter;

    SearchView searchView;

    @Inject AcmClient acmClient;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        App.get().getAcmComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        Call<List<Announcement>> announcements = acmClient.getAnnouncements();
        announcements.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                for (Announcement announcement : response.body()) {
                    addAnnouncement(announcement);
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {

            }
        });
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
        return App.get().getString(R.string.title_activity_announcement);
    }


    public void addAnnouncement(Announcement announcement) {
        adapter.addItem(announcement);
        if (announcement.getIcon() != null && announcement.getIcon().length() > 0) {
            preloadAnnouncementImage(announcement);
        }
    }

    private void preloadAnnouncementImage(Announcement announcement) {
        Picasso.with(getActivity()).load(announcement.getIcon()).fetch();
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
