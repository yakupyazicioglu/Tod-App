package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 05.03.2017.
 */

public class ReadingFragment extends ReadListFragment {

    public ReadingFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query readingList = databaseReference
                .child("books")
                .orderByChild("reading"+"/"+getUid())
                .equalTo(true);

        return readingList;
    }
}
