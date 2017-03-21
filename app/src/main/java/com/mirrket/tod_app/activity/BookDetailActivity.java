package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.adapter.CommentAdapter;
import com.mirrket.tod_app.behaviour.AppBarLayoutBehavior;
import com.mirrket.tod_app.models.Comment;
import com.mirrket.tod_app.models.Post;
import com.mirrket.tod_app.models.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BookDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BookDetailActivity";
    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String bookTitle;
    private String authorTitle;
    private CommentAdapter mAdapter;

    private ImageView mPhoto;
    private TextView mBookView;
    private TextView mBookAuthorView;
    private TextView mPublisherView;
    private TextView mPageView;
    private TextView mBodyView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCollapsingToolbar();

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(mPostKey);

        // Initialize Views
        mPhoto = (ImageView) findViewById(R.id.default_photo);
        mBookView = (TextView) findViewById(R.id.book_name);
        mBookAuthorView = (TextView) findViewById(R.id.author_name);
        mPublisherView = (TextView) findViewById(R.id.field_publisher);
        mPageView = (TextView) findViewById(R.id.field_page);
        mBodyView = (TextView) findViewById(R.id.post_body);

        //COMMENT AREA
        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);

        //Button setOnClickListeners
        mCommentButton.setOnClickListener(this);
        mPhoto.setOnClickListener(this);

        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for scroll post body when it is to long
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                Picasso.with(getApplicationContext())
                        .load(post.fileUri)
                        .placeholder(R.drawable.ic_no_image)
                        .fit()
                        .into(mPhoto);
                mBookView.setText(post.title);
                mBookAuthorView.setText(post.title_author);
                mPublisherView.setText(post.publisher);
                mPageView.setText(post.page);
                mBodyView.setText(post.body);

                bookTitle = post.title;
                authorTitle = post.title_author;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                String snackText = getString(R.string.failed_load_post);
                showSnack(snackText);
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //fullScreenCall();

        mBodyView.setMovementMethod(new ScrollingMovementMethod());

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

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        appBarLayout.setExpanded(true);
        // hiding & showing the title when include_toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarTitle);
                    collapsingToolbar.setTitleEnabled(false);
                    mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.ToolbarTitle);
                    mToolbar.setSubtitleTextAppearance(getApplicationContext(),R.style.ToolbarSubtitle);
                    mToolbar.setTitle(bookTitle);
                    mToolbar.setSubtitle(authorTitle);
                    //collapsingToolbar.setTitle(collBarTitle);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitleEnabled(false);
                    mToolbar.setTitle("");
                    mToolbar.setSubtitle("");
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });
    }

}
