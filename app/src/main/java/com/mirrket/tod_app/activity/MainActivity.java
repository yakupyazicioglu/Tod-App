package com.mirrket.tod_app.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.behaviour.ConnectionApplication;
import com.mirrket.tod_app.behaviour.ConnectivityReceiver;
import com.mirrket.tod_app.behaviour.LockableViewPager;
import com.mirrket.tod_app.fragment.LastAddedFragment;
import com.mirrket.tod_app.fragment.TopReadedFragment;
import com.mirrket.tod_app.fragment.TopReadingFragment;
import com.mirrket.tod_app.models.Author;
;import java.util.Set;


public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = "MainActivity";
    private FragmentPagerAdapter mPagerAdapter;
    private LockableViewPager mViewPager;
    private ProgressBar progressBar;
    int itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private Fragment[] mFragments = new Fragment[]{
                    new LastAddedFragment(),
                    new TopReadedFragment(),
                    new TopReadingFragment(),
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }
        };

        // Set up the ViewPager with the sections adapter
        progressBar.setVisibility(View.VISIBLE);
        mViewPager = (LockableViewPager) findViewById(R.id.container);
        mViewPager.setSwipeable(false);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new DefaultTransformer());
        itemCount = mViewPager.getAdapter().getCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_new_book:
                startActivity(new Intent(this, NewBookActivity.class));
                break;
            case R.id.action_new_author:
                startActivity(new Intent(this, NewAuthorActivity.class));
                break;
            case R.id.action_profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                break;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.action_app:
                startActivity(new Intent(this, AboutAppActivity.class));
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

    // Showing the status in Snackbar
    private void isConnected(boolean isConnected) {
        if (!isConnected) {
            String message = getString(R.string.no_internet_connection);
            showSnack(message);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //fullScreenCall();

        final boolean isConnected = ConnectivityReceiver.isConnected();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (itemCount > 0){
                    progressBar.setVisibility(View.GONE);
                    isConnected(isConnected);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, 10000);

        // register connection status listener
        ConnectionApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        isConnected(isConnected);
    }

}


