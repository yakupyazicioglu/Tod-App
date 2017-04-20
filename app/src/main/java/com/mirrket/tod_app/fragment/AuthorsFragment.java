package com.mirrket.tod_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.AuthorDetailActivity;
import com.mirrket.tod_app.models.Author;
import com.mirrket.tod_app.viewholder.AuthorViewHolder;


public class AuthorsFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "AuthorsFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Author, AuthorViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private SearchView searchView;
    private Query authorQuery;
    private ProgressBar progressBar;

    public AuthorsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_authors, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.author_list);
        searchView = (SearchView) rootView.findViewById(R.id.search_author);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        return rootView.getRootView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mManager = new LinearLayoutManager(getActivity());
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);

        searchView.setOnQueryTextListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onQueryTextChange("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_authors, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
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

        authorQuery = getQuery(mDatabase, newText);

        mAdapter = new FirebaseRecyclerAdapter<Author, AuthorViewHolder>(Author.class, R.layout.item_author,
                AuthorViewHolder.class, authorQuery) {
            @Override
            protected void populateViewHolder(final AuthorViewHolder viewHolder, final Author model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String authorKey = postRef.getKey();
                final String authorRef = model.name;

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getContext(), AuthorDetailActivity.class);
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
