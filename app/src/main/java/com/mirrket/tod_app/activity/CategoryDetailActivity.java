package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.fragment.BaseFragment;
import com.mirrket.tod_app.fragment.CategoriesFragment;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.BookViewHolder;

public class CategoryDetailActivity extends BaseActivity {
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Book, BookViewHolder> mAdapterBook;
    private RecyclerView mRecyclerBook;
    private ProgressBar progressBar;
    private LinearLayoutManager mManagerLinear;
    private Query bookQuery;
    private String categoryRef,categoryName;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        categoryRef = getIntent().getStringExtra("categoryRef");
        categoryName = getIntent().getStringExtra("categoryName");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(categoryName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerBook = (RecyclerView) findViewById(R.id.book_search_list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mManagerLinear = new LinearLayoutManager(this);
        mRecyclerBook.setHasFixedSize(true);
        mRecyclerBook.setLayoutManager(mManagerLinear);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        bookQuery = getBook(mDatabase, categoryRef);

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
    }

    public int getItemCount() {
        return mRecyclerBook.getChildCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getItemCount() == 0)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    Query getBook(DatabaseReference databaseReference, String search) {

        Query searchListsQuery = databaseReference
                .child("books")
                .orderByChild("category")
                .equalTo(search);

        return searchListsQuery;
    }
}
