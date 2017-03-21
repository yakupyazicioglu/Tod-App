package com.mirrket.tod_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mirrket.tod_app.models.Post;
import com.mirrket.tod_app.viewholder.PostViewHolder;


public abstract class BookListFragment extends BaseFragment {
    private static final String TAG = "PostListFragment";
    int limitPost = 100;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
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

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_discover,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.authorTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        int userRate = 0;
                        if(model.rates.get(getUid()) != null){
                            userRate = model.rates.get(getUid());
                        }

                        rateBookClicked(postKey, userRate);
                        return false;
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly

                if (model.readed.containsKey(getUid())) {
                    viewHolder.readView.setImageResource(R.drawable.ic_checkedo);
                } else {
                    viewHolder.readView.setImageResource(R.drawable.ic_checkmarko);
                }

                if (model.wantToRead.containsKey(getUid())) {
                    viewHolder.wtReadView.setImageResource(R.drawable.ic_checkedo);
                } else {
                    viewHolder.wtReadView.setImageResource(R.drawable.ic_checkmarko);
                }

                if (model.reading.containsKey(getUid())) {
                    viewHolder.readingView.setImageResource(R.drawable.ic_checkedo);
                } else {
                    viewHolder.readingView.setImageResource(R.drawable.ic_checkmarko);
                }


                viewHolder.bindToPost(model);

                viewHolder.bindToReaded(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View readView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postKey);
                        DatabaseReference userRef = mDatabase.child("users").child(getUid());

                        // Run two transactions
                        readClicked(globalPostRef);
                        userReadeds(globalPostRef, userRef, postKey);
                        //moveOthers(globalPostRef, userRef, postKey);
                    }
                });

                viewHolder.bindToWantToRead(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View wtReadView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postKey);
                        DatabaseReference userRef = mDatabase.child("users").child(getUid());

                        // Run two transactions
                        wtReadClicked(globalPostRef);
                        userWantToReads(globalPostRef, userRef, postKey);
                        //moveOthers(globalPostRef, userRef, postKey);
                    }
                });

                viewHolder.bindToReading(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View readingView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postKey);
                        DatabaseReference userRef = mDatabase.child("users").child(getUid());

                        // Run two transactions
                        readingClicked(globalPostRef);
                        userReadings(globalPostRef, userRef, postKey);
                        //moveOthers(globalPostRef, userRef, postKey);
                    }
                });

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

