package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class QTopReadedFragment extends BookListFragment {

    public QTopReadedFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String myUserId = getUid();
        Query myPostsQuery = databaseReference
                .child("books")
                .orderByChild("readedCount")
                .limitToLast(100);

        return myPostsQuery;
    }

}
