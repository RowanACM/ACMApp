package org.rowanacm.android.annoucement;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.us.acm.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by John on 12/1/2016.
 */

public class AnnouncementArrayAdapter extends BaseAdapter {

    private LayoutInflater myInflater;
    private ArrayList<Announcement> announcementArrayList = new ArrayList<>();

    public AnnouncementArrayAdapter(Context c) {
        myInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Announcement a) {
        announcementArrayList.add(a);
        Collections.sort(announcementArrayList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return announcementArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return announcementArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListViewParent...) will apply default layout parameters unless you use
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.announcement_view, parent, false);
        }

        TextView groupTextView = (TextView) convertView.findViewById(R.id.gro_View);
        TextView announcementTextView = (TextView) convertView.findViewById(R.id.ano_View);
        Announcement announcement = announcementArrayList.get(position);
        groupTextView.setText(announcement.getCommittee());
        announcementTextView.setText(announcement.getText());

        return convertView;
    }


}
