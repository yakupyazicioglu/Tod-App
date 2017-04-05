package com.mirrket.tod_app.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by yy on 05.03.2017.
 */

public class ReadedFragment extends ReadListFragment {

    private String id;

    public ReadedFragment() {}

    public ReadedFragment(String userId) {
        id = userId;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query readedList = databaseReference
                .child("books")
                .orderByChild("readed"+"/"+id)
                .equalTo(true);

        return readedList;
    }

}
