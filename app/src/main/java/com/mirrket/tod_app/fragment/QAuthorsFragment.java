package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 17.04.2017.
 */

public class QAuthorsFragment extends BookListFragment {
    String search;
    public QAuthorsFragment() {}

    public QAuthorsFragment(String newSearch) {
        search = newSearch;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query searchListsQuery = databaseReference
                .child("authors")
                .orderByChild("searchRef")
                .startAt(search);

        return searchListsQuery;
    }

}
