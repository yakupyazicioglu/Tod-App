package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TopReadingFragment extends BookListFragment {

    public TopReadingFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        String myUserId = getUid();
        Query myPostsQuery = databaseReference
                .child("posts")
                .orderByChild("readingCount");

        return myPostsQuery;
    }

}
