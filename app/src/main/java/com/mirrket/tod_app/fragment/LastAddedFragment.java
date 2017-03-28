package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 22.02.2017.
 */

public class LastAddedFragment extends BookListFragment {

    public LastAddedFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // My top posts by number of stars

        Query myTopPostsQuery = databaseReference
                .child("books");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }

}
