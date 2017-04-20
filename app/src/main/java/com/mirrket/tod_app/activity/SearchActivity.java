package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Author;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.AuthorViewHolder;
import com.mirrket.tod_app.viewholder.BookViewHolder;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = "SearchActivity";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Book, BookViewHolder> mAdapterBook;
    private RecyclerView mRecyclerBook, mRecyclerAuthor;
    private LinearLayoutManager mManagerLinear;
    private Query bookQuery;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerBook = (RecyclerView) findViewById(R.id.book_search_list);
        searchView = (SearchView) findViewById(R.id.search_book);

        mManagerLinear = new LinearLayoutManager(this);
        mRecyclerBook.setHasFixedSize(true);
        mRecyclerBook.setLayoutManager(mManagerLinear);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        searchView.setOnQueryTextListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        onQueryTextChange("");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapterBook != null) {
            mAdapterBook.cleanup();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        bookQuery = getBook(mDatabase, newText);

        mAdapterBook = new FirebaseRecyclerAdapter<Book, BookViewHolder>(Book.class, R.layout.item_discover,
                BookViewHolder.class, bookQuery) {
            @Override
            protected void populateViewHolder(final BookViewHolder viewHolder, final Book model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                populateItemDiscover(viewHolder,model,postKey);

            }
        };

        mRecyclerBook.setAdapter(mAdapterBook);
        return true;
    }

    Query getBook(DatabaseReference databaseReference, String search) {

        Query searchListsQuery = databaseReference
                .child("books")
                .orderByChild("searchRef")
                .startAt(search)
                .limitToFirst(100);

        return searchListsQuery;
    }
}
