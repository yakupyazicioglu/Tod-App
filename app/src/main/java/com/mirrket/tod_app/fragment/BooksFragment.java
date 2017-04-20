package com.mirrket.tod_app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.NewAuthorActivity;
import com.mirrket.tod_app.activity.NewBookActivity;
import com.mirrket.tod_app.activity.NewCategoryActivity;
import com.mirrket.tod_app.activity.SearchActivity;
import com.mirrket.tod_app.behaviour.ConnectionApplication;
import com.mirrket.tod_app.behaviour.ConnectivityReceiver;
import com.mirrket.tod_app.behaviour.LockableViewPager;

public class BooksFragment extends BaseFragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = "BooksFragment";
    private FragmentPagerAdapter mPagerAdapter;
    private LockableViewPager mViewPager;

    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_book, container, false);
        mViewPager = (LockableViewPager) rootView.findViewById(R.id.container);

        // Create the adapter that will return a fragment for each section

        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private Fragment[] mFragments = new Fragment[]{
                    new QLastAddedFragment(),
                    new QTopReadedFragment(),
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
        };


        // Set up the ViewPager with the sections adapter
        mViewPager.setSwipeable(false);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new DefaultTransformer());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // register connection status listener
        ConnectionApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        isConnected(isConnected);
    }

    // Showing the status in Snackbar
    private void isConnected(boolean isConnected) {
        if (!isConnected) {
            String message = getString(R.string.no_internet_connection);
            showSnack(message);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.action_new_book:
                startActivity(new Intent(getContext(), NewBookActivity.class));
                break;
            case R.id.action_new_author:
                startActivity(new Intent(getContext(), NewAuthorActivity.class));
                break;
            case R.id.action_new_category:
                startActivity(new Intent(getContext(), NewCategoryActivity.class));
                break;
            case R.id.itemInSubMenu1:
                if (!item.isChecked()) item.setChecked(true);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.itemInSubMenu2:
                if (!item.isChecked()) item.setChecked(true);
                mViewPager.setCurrentItem(1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
