package org.rowanacm.android;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A RecyclerView that allows you to search items
 * @param <K> View holder
 * @param <T> Item that the list contains
 */
public abstract class SearchableAdapter<K extends RecyclerView.ViewHolder, T extends Searchable & Comparable<T>> extends RecyclerView.Adapter<K> {

    public static final int DISPLAY_DEFAULT = 10;

    protected List<T> list;
    protected List<T> listAll;

    protected int numToDisplay;

    public SearchableAdapter(List<T> list) {
        this(list, DISPLAY_DEFAULT);
    }

    public SearchableAdapter(List<T> initList, int numToDisplay) {
        this.numToDisplay = numToDisplay;
        this.listAll = initList;
        this.list = new ArrayList<>();

        for (int i = 0; i < initList.size() && i < numToDisplay; i++) {
            this.list.add(initList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String text) {
        if (text != null) {
            text = text.toLowerCase().trim();
        }

        ArrayList<T> result = new ArrayList<>();
        for (T item : listAll) {
            if (item.search(text)) {
                result.add(item);
            }
        }

        list.clear();
        for (int i = 0; i < numToDisplay && i < result.size(); i++) {
            list.add(result.get(i));
        }
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (!listAll.contains(item)) {
            listAll.add(item);
            Collections.sort(listAll);

            list.clear();
            for (int i = 0; i < listAll.size() && i < numToDisplay; i++) {
                this.list.add(listAll.get(i));
            }

            notifyDataSetChanged();
        }
    }

    public void clear() {
        list.clear();
        listAll.clear();

        notifyDataSetChanged();
    }


}