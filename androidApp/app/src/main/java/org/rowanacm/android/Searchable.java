package org.rowanacm.android;


public interface Searchable {

    /**
     * @return true if searching for a string contains this item
     */
    boolean search(String search);
}