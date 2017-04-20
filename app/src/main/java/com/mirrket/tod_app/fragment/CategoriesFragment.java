package com.mirrket.tod_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mirrket.tod_app.activity.CategoryDetailActivity;
import com.mirrket.tod_app.models.Category;
import com.mirrket.tod_app.viewholder.CategoryViewHolder;

public class CategoriesFragment extends BaseFragment {
    private static final String TAG = "CategoriesFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Query categoryQuery;
    private ProgressBar progressBar;

    public CategoriesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.category_list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mManager = new LinearLayoutManager(getContext());
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(mManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        categoryQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class, R.layout.item_category,
                CategoryViewHolder.class, categoryQuery) {
            @Override
            protected void populateViewHolder(final CategoryViewHolder viewHolder, final Category model, final int position) {
                // Set click listener for the whole post view
                final String categoryName = model.category_name;
                final String categoryRef = categoryName.toLowerCase();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch BookDetailActivity
                        Intent intent = new Intent(getContext(), CategoryDetailActivity.class);
                        intent.putExtra("categoryRef",categoryRef);
                        intent.putExtra("categoryName",categoryName);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToList(model);
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

    Query getQuery(DatabaseReference databaseReference) {

        Query searchListsQuery = databaseReference
                .child("categories")
                .orderByChild("category_name");

        return searchListsQuery;
    }
}
