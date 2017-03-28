package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 05.03.2017.
 */

public class WTReadFragment extends ReadListFragment {

    public WTReadFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query wantToReadList = databaseReference
                .child("books")
                .orderByChild("wantToRead"+"/"+getUid())
                .equalTo(true);

        return wantToReadList;
    }
}
