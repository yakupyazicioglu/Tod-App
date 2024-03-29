package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 05.03.2017.
 */

public class QWTReadFragment extends ReadListFragment {

    private String id;

    public QWTReadFragment() {}

    public QWTReadFragment(String userId) {
        id = userId;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query wantToReadList = databaseReference
                .child("books")
                .orderByChild("wantToRead"+"/"+id)
                .equalTo(true);

        return wantToReadList;
    }
}
