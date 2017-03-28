package com.mirrket.tod_app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Author;
import com.mirrket.tod_app.viewholder.AuthorViewHolder;

public class AuthorsActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "AuthorActivity";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Author, AuthorViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private GridLayoutManager mManager;
    private SearchView searchView;
    private Query bookQuery;
    private ProgressBar progressBar;
    private int itemCount;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecycler = (RecyclerView) findViewById(R.id.author_list);
        searchView = (SearchView) findViewById(R.id.search_author);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mManager = new GridLayoutManager(this,4);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);

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
    protected void onResume() {
        super.onResume();

        itemCount = mRecycler.getChildCount();

       /* new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (itemCount > 0){
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, 2222);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_authors, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.itemAz:
                if (!item.isChecked()) item.setChecked(true);
                mManager.setReverseLayout(false);
                break;
            case R.id.itemZa:
                if (!item.isChecked()) item.setChecked(true);
                mManager.setReverseLayout(true);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    Query getQuery(DatabaseReference databaseReference, String search) {

        Query searchListsQuery = databaseReference
                .child("authors")
                .orderByChild("searchRef")
                .startAt(search);

        return searchListsQuery;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        bookQuery = getQuery(mDatabase, newText);

        mAdapter = new FirebaseRecyclerAdapter<Author, AuthorViewHolder>(Author.class, R.layout.item_author,
                AuthorViewHolder.class, bookQuery) {
            @Override
            protected void populateViewHolder(final AuthorViewHolder viewHolder, final Author model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String authorKey = postRef.getKey();
                final String authorRef = model.name;

                viewHolder.photoUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getApplicationContext(), AuthorDetailActivity.class);
                        intent.putExtra(AuthorDetailActivity.EXTRA_POST_KEY, authorKey);
                        intent.putExtra("authorRef",authorRef);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToPost(model);

            }
        };

        mRecycler.setAdapter(mAdapter);
        return true;
    }
}
