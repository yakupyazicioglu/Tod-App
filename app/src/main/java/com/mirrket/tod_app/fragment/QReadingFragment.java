package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 05.03.2017.
 */

public class QReadingFragment extends ReadListFragment {

    private String id;

    public QReadingFragment() {}

    public QReadingFragment(String userId) {
        id = userId;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query readingList = databaseReference
                .child("books")
                .orderByChild("reading"+"/"+id)
                .equalTo(true);

        return readingList;
    }
}
