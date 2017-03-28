package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Author;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.AuthorViewHolder;
import com.mirrket.tod_app.viewholder.BookViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by yy on 25.03.2017.
 */

public class AuthorDetailActivity extends BaseActivity{

    private static final String TAG = "AuthorDetailActivity";
    public static final String EXTRA_POST_KEY = "author_key";

    private DatabaseReference mDatabase;
    private DatabaseReference mAuthorReference;
    private ValueEventListener mBookListener;
    private Query bookQuery;
    private String mAuthorKey;
    private String authorRef;

    private FirebaseRecyclerAdapter<Book, BookViewHolder> mAdapter;
    private ImageView mPhoto;
    private TextView mName;
    private TextView mInfo;
    private RecyclerView mBooksRecycler;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get book key from intent
        authorRef = getIntent().getStringExtra("authorRef");
        mAuthorKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mAuthorKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthorReference = FirebaseDatabase.getInstance().getReference().child("authors").child(mAuthorKey);

        // Initialize Views
        mPhoto = (ImageView) findViewById(R.id.author_photo);
        mName = (TextView) findViewById(R.id.author_name);
        mInfo = (TextView) findViewById(R.id.author_info);

        mBooksRecycler = (RecyclerView) findViewById(R.id.recycler_author_books);
        mBooksRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for scroll post book_info when it is to long
                mInfo.setMovementMethod(new ScrollingMovementMethod());
                // Get Book object and use the values to update the UI
                Author author = dataSnapshot.getValue(Author.class);
                Picasso.with(getApplicationContext())
                        .load(author.photoUrl)
                        .placeholder(R.drawable.ic_no_image)
                        .fit()
                        .into(mPhoto);
                mName.setText(author.name);
                mInfo.setText(author.info);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Book failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                String snackText = getString(R.string.failed_load_post);
                showSnack(snackText);
                // [END_EXCLUDE]
            }
        };
        mAuthorReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
        // Keep copy of post listener so we can remove it when app stops
        mBookListener = postListener;
    }

    @Override
    protected void onResume() {
        super.onResume();

        bookQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(Book.class, R.layout.item_discover,
                BookViewHolder.class, bookQuery) {
            @Override
            protected void populateViewHolder(final BookViewHolder viewHolder, final Book model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String bookKey = postRef.getKey();

                populateItemDiscover(viewHolder,model,bookKey);

            }
        };

        mBooksRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mBookListener != null) {
            mAuthorReference.removeEventListener(mBookListener);
        }
        mAdapter.cleanup();
    }

    Query getQuery(DatabaseReference databaseReference) {

        Query searchListsQuery = databaseReference
                .child("books")
                .orderByChild("author")
                .equalTo(authorRef);

        return searchListsQuery;
    }
}
