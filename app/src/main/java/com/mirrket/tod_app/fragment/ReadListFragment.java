package com.mirrket.tod_app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.BookDetailActivity;
import com.mirrket.tod_app.behaviour.EndlessRecyclerViewScrollListener;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.BookViewHolder;


public abstract class ReadListFragment extends BaseFragment {
    private static final String TAG = "ReadListFragment";
    int limitPost = 100;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Book, BookViewHolder> mAdapter;
    private AlertDialog levelDialog;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String userId;

    public ReadListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_read, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.read_list);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

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

        mAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(Book.class, R.layout.item_read,
                BookViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final BookViewHolder viewHolder, final Book model, final int position) {
                final DatabaseReference postRef = getRef(position);
                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveBookClicked(postKey);
                    }
                });

                viewHolder.bindToRead(model);
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

    private void moveBookClicked(final String postKey) {
        final DatabaseReference globalPostRef = mDatabase.child("books").child(postKey);

        Resources res = getResources();
        final String[] items = res.getStringArray(R.array.read_list);

        // Creating and Building the Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.move_book_to));
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        moveOthers(globalPostRef);
                        moveToReaded(globalPostRef, postKey);
                        String snackText1 = getString(R.string.moved_readed);
                        showSnack(snackText1);
                        break;
                    case 1:
                        moveOthers(globalPostRef);
                        moveToWTRead(globalPostRef, postKey);
                        String snackText2 = getString(R.string.moved_wtread);
                        showSnack(snackText2);
                        break;
                    case 2:
                        moveOthers(globalPostRef);
                        moveToReading(globalPostRef, postKey);
                        String snackText3 = getString(R.string.moved_reading);
                        showSnack(snackText3);
                        break;
                    case 3:
                        moveOthers(globalPostRef);
                        String snackText4 = getString(R.string.removed_from_list);
                        showSnack(snackText4);
                        break;
                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}

