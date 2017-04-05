package com.mirrket.tod_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.BookDetailActivity;
import com.mirrket.tod_app.behaviour.EndlessRecyclerViewScrollListener;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.BookViewHolder;


public abstract class BookListFragment extends BaseFragment {
    private static final String TAG = "BookListFragment";
    int limitPost = 100;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Book, BookViewHolder> mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView mRecycler;
    private ProgressBar progressBar;
    private LinearLayoutManager mManager;

    public BookListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.book_discover_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(Book.class, R.layout.item_discover,
                BookViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final BookViewHolder viewHolder, final Book model, final int position) {
                final DatabaseReference bookRef = getRef(position);

                // Set click listener for the whole post view
                final String bookKey = bookRef.getKey();

                populateItemDiscover(viewHolder,model,bookKey);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}

