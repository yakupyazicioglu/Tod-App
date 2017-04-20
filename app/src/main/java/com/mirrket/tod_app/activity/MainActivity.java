package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.fragment.AuthorsFragment;
import com.mirrket.tod_app.fragment.BooksFragment;
import com.mirrket.tod_app.fragment.CategoriesFragment;
import com.mirrket.tod_app.fragment.SettingsFragment;
import com.mirrket.tod_app.models.User;
import com.mirrket.tod_app.util.CircleTransform;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private DatabaseReference mUserRef;
    private StorageReference userImgRef;
    private ValueEventListener mUserListener;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView profileImg;
    private TextView mUserName;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_BOOKS = "Books";
    private static final String TAG_AUTHORS = "Authors";
    private static final String TAG_CATEGORIES = "Categories";
    private static final String TAG_SETTINGS = "Settings";
    public static String CURRENT_TAG = TAG_BOOKS;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private String userId,snackText;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        userId = getUid();

        // [START initialize_database_ref]
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userImgRef = FirebaseStorage.getInstance().getReference().child("USER_PROFILE");

        // Navigation view header
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        mUserName = (TextView) navHeader.findViewById(R.id.user_name);
        profileImg = (ImageView) navHeader.findViewById(R.id.user_img);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BOOKS;
            loadHomeFragment();
        }

        mUserName.setOnClickListener(this);
        profileImg.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for scroll post book_info when it is to long
                // Get Book object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                Picasso.with(getApplicationContext())
                        .load(user.photoUrl)
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.ic_action_account_circle_40)
                        .into(profileImg);
                mUserName.setText(user.username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                snackText = getString(R.string.failed_load_post);
                showSnack(snackText);
                // [END_EXCLUDE]
            }
        };
        mUserRef.addValueEventListener(userListener);
        mUserListener = userListener;
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new BooksFragment();
            case 1:
                return new AuthorsFragment();
            case 2:
                return new CategoriesFragment();
            case 3:
                return new SettingsFragment();
            default:
                return new BooksFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BOOKS;
                        break;
                    case R.id.nav_authors:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_AUTHORS;
                        break;
                    case R.id.nav_categories:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CATEGORIES;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_donate:
                        startActivity(new Intent(MainActivity.this, DonateActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BOOKS;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        // checking if user is on other navigation menu
        // rather than home
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BOOKS;
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.user_name)
            startActivity(new Intent(this, UserProfileActivity.class));
        drawer.closeDrawers();
        if(id == R.id.user_img)
            startActivity(new Intent(this, UserProfileActivity.class));
        drawer.closeDrawers();
    }
}