package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.adapter.CommentAdapter;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.models.Comment;
import com.mirrket.tod_app.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import at.blogc.android.views.ExpandableTextView;

public class BookDetailActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "BookDetailActivity";
    public static final String EXTRA_POST_KEY = "book_key";
    public static final int USER_RATE = 0;

    private DatabaseReference mDatabase;
    private DatabaseReference mBookReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mBookListener;
    private String mBookKey;
    private String bookTitle;
    private String authorTitle;
    private String userNote;
    private int userRate;
    private boolean isShow = false;
    private int scrollRange = -1;
    private CommentAdapter mAdapter;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private ImageView bookPhoto;
    private ImageView authorPhoto;
    private TextView mBookView;
    private TextView mBookAuthorView;
    private TextView mPublisherView;
    private TextView mPageView;
    private ExpandableTextView mBodyView;
    private EditText mCommentField;
    private Button mCommentButton;
    private EditText mTakeNote;
    private TextView mExpand;
    private RecyclerView mCommentsRecycler;
    private RatingBar mRatingBar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get post key from intent
        mBookKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        bookTitle = getIntent().getStringExtra("book");
        authorTitle = getIntent().getStringExtra("author");

        if (mBookKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");

        // Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mBookReference = FirebaseDatabase.getInstance().getReference().child("books").child(mBookKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("book-comments").child(mBookKey);

        // Initialize Views
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        bookPhoto = (ImageView) findViewById(R.id.default_photo);
        authorPhoto = (ImageView) findViewById(R.id.author_photo);
        mBookView = (TextView) findViewById(R.id.book_name);
        mBookAuthorView = (TextView) findViewById(R.id.author_name);
        mPublisherView = (TextView) findViewById(R.id.field_publisher);
        mPageView = (TextView) findViewById(R.id.field_page);
        mBodyView = (ExpandableTextView) findViewById(R.id.post_body);
        mExpand = (TextView) findViewById(R.id.expand_text);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mTakeNote = (EditText) findViewById(R.id.userNote);

        //COMMENT AREA
        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCommentsRecycler.setHasFixedSize(true);

        // set interpolators for both expanding and collapsing animations
        mBodyView.setAnimationDuration(1000L);
        mBodyView.setInterpolator(new OvershootInterpolator());
        mBodyView.setExpandInterpolator(new OvershootInterpolator());
        mBodyView.setCollapseInterpolator(new OvershootInterpolator());

        //Button setOnClickListeners
        mCommentButton.setOnClickListener(this);
        bookPhoto.setOnClickListener(this);
        mExpand.setOnClickListener(this);
        mRatingBar.setOnTouchListener(this);
        mTakeNote.setOnClickListener(this);
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
                // Get Book object and use the values to update the UI
                Book post = dataSnapshot.getValue(Book.class);
                Picasso.with(getApplicationContext())
                        .load(post.coverUrl)
                        .placeholder(R.drawable.ic_no_image)
                        .fit()
                        .into(bookPhoto);
                mBookView.setText(post.book);
                mBookAuthorView.setText(post.author);
                mPublisherView.setText(post.publisher);
                mPageView.setText(post.page);
                mBodyView.setText(post.book_info);
                mRatingBar.setRating(post.rating);

                if (post.userNotes.get(getUid()) != null) {
                    userNote = post.userNotes.get(getUid());
                }
                else userNote = "";

                mTakeNote.setText(userNote);

                if (post.rates.get(getUid()) != null) {
                    userRate = post.rates.get(getUid());
                }
                else userRate = 0;
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
        mBookReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
        // Keep copy of post listener so we can remove it when app stops
        mBookListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
        mBodyView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mBookListener != null) {
            mBookReference.removeEventListener(mBookListener);
        }
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
        if(i == R.id.expand_text){
            mBodyView.toggle();
            mExpand.setText(mBodyView.isExpanded() ? R.string.expand_text : R.string.collapse_text);
        }
        if(i == R.id.userNote){
            takeNoteClicked(mBookKey, userNote);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int i = v.getId();

        if(i == R.id.ratingBar){
            rateBookClicked(mBookKey, userRate);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //fullScreenCall();

        /*AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        final AppBarLayoutBehavior mBehavior = new AppBarLayoutBehavior();
        lp.setBehavior(mBehavior);

        mToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBehavior.setInterceptTouchEvent(true);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        mBehavior.setInterceptTouchEvent(false);
                        return true;
                }
                return false;
            }
        });*/

        mAdapter = new CommentAdapter(this, mCommentsReference);

    }

    private void postComment() {
        final String required = getString(R.string.required);
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String photo = user.photoUrl;
                        String authorName = user.username;
                        String commentText = mCommentField.getText().toString();
                        final String date = new SimpleDateFormat("d/M/yy").format(Calendar.getInstance().getTime());

                        if (TextUtils.isEmpty(commentText)) {
                            mCommentField.setError(required);
                            return;
                        }

                        Comment comment = new Comment(uid, photo, authorName, commentText, date);
                        mCommentsReference.push().setValue(comment);
                        mCommentField.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        /*if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
        {
            mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.ToolbarTitle);
            mToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.ToolbarSubtitle);
            mToolbar.setTitle(bookTitle);
            mToolbar.setSubtitle(authorTitle);
        }
        *//*else
        {
            mToolbar.setTitle("");
            mToolbar.setSubtitle("");
        }*//*

        if(verticalOffset == -mCollapsingToolbarLayout.getHeight() + mToolbar.getHeight()){
            mCollapsingToolbarLayout.setTitleEnabled(true);
            mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.ToolbarTitle);
            mToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.ToolbarSubtitle);
            mToolbar.setTitle(bookTitle);
            mToolbar.setSubtitle(authorTitle);

        }else if(!mToolbar.getTitle().equals("")){
            mCollapsingToolbarLayout.setTitleEnabled(true);
            mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.ToolbarTitle);
            mToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.ToolbarSubtitle);
            mToolbar.setTitle(bookTitle);
            mToolbar.setSubtitle(authorTitle);
            //mCollapsingToolbarLayout.setTitle(mExpandedTitle);
        }*/
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarTitle);
            mCollapsingToolbarLayout.setTitleEnabled(false);
            mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.ToolbarTitle);
            mToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.ToolbarSubtitle);
            mToolbar.setTitle(bookTitle);
            mToolbar.setSubtitle(authorTitle);
            //collapsingToolbar.setTitle(bookTitle);
            isShow = true;
        } else if (isShow) {
            mCollapsingToolbarLayout.setTitleEnabled(false);
            mToolbar.setTitle("");
            mToolbar.setSubtitle("");
            //collapsingToolbar.setTitle("");
            isShow = false;
        }
    }
}
