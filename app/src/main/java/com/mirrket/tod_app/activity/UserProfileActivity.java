package com.mirrket.tod_app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.fragment.QReadedFragment;
import com.mirrket.tod_app.fragment.QReadingFragment;
import com.mirrket.tod_app.fragment.QWTReadFragment;
import com.mirrket.tod_app.models.User;
import com.mirrket.tod_app.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.UUID;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener,AppBarLayout.OnOffsetChangedListener{
    private static final String TAG = "UserProfileActivity";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ImageView profileImg;
    private FragmentPagerAdapter mPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private String photoUrl;
    private String userId;
    private String snackText;
    private String newUserName;
    private TextView mUserName;
    private Uri fileUri;
    private Uri filePath;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private DatabaseReference mUserRef;
    private StorageReference userImg;
    private FirebaseStorage mStorage;
    private StorageReference userImgRef;
    private ValueEventListener mUserListener;
    private UploadTask uploadTask;
    // [END declare_database_ref]

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = getUid();
        }

        //views
        mToolbar        = (Toolbar) findViewById(R.id.toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.appbar);
        mUserName = (TextView) findViewById(R.id.user_name);
        profileImg = (ImageView) findViewById(R.id.user_profile_photo);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userImgRef = FirebaseStorage.getInstance().getReference().child("USER_PROFILE");
        // [END initialize_database_ref]

        //mTitle.setTextAppearance(R.style.ToolbarTitle);
        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

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
                mTitle.setText(user.username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                snackText = getString(R.string.failed_load_post);
                showSnack(snackText);
                mTitle.setText("");
                // [END_EXCLUDE]
            }
        };
        mUserRef.addValueEventListener(userListener);
        mUserListener = userListener;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new QReadingFragment(userId),
                    new QReadedFragment(userId),
                    new QWTReadFragment(userId),
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_reading),
                    getString(R.string.heading_readed),
                    getString(R.string.heading_wanttoread),
            };
            @Override
            public Fragment getItem(int position) {return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                Locale l = Locale.getDefault();
                switch (position) {
                    case 0:
                        return getString(R.string.heading_reading) + "".toUpperCase(l);
                    case 1:
                        return getString(R.string.heading_readed).toUpperCase(l);
                    case 2:
                        return getString(R.string.heading_wanttoread).toUpperCase(l);
                }
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(userId != getUid()) {
            mViewPager.setVisibility(View.INVISIBLE);
            profileImg.setVisibility(View.INVISIBLE);
        }

        profileImg.setOnClickListener(this);
        mUserName.setOnClickListener(this);
    }

    //updated profile picture calling onResume
    @Override
    protected void onResume() {
        super.onResume();
        //fullScreenCall();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for scroll post book_info when it is to long
                // Get Book object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                Picasso.with(getApplicationContext())
                        .load(user.photoUrl)
                        .transform(new CircleTransform())
                        .fit()
                        .placeholder(R.drawable.ic_action_account_circle_40)
                        .into(profileImg);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mUserRef.addValueEventListener(userListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mUserListener != null) {
            mUserRef.removeEventListener(mUserListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE &&  resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            long maxSize = 2097152;// 2 mb

            Uri fileUri = data.getData();
            Cursor cursor = getApplicationContext().getContentResolver().query(fileUri, null, null, null, null);
            cursor.moveToFirst();
            long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            cursor.close();

            if(size > maxSize){
                String snackText = getString(R.string.failed_update_image);
                showSnack(snackText);
            }
            else {
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            String  uniqueID = UUID.randomUUID().toString();
            progressDialog.setTitle(getString(R.string.profile_picture));
            progressDialog.show();

            StorageReference riversRef = userImgRef.child(uniqueID+filePath.getLastPathSegment());
            uploadTask = riversRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileUri = taskSnapshot.getMetadata().getDownloadUrl();
                    photoUrl = String.valueOf(fileUri);
                    writeUserImg(photoUrl);
                    progressDialog.dismiss();

                    onResume();
                    snackText = getString(R.string.updated_profile_image);
                    showSnack(snackText);
                    Log.v("on upload fıle", "download url " + fileUri + " comıng lınk here " + photoUrl);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            snackText = getString(R.string.failed_update_img);
                            showSnack(snackText);
                            FirebaseCrash.report(new Exception("User profile picture problem!"));
                            Log.v("on upload fıle", "fail");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //displaying percentage in progress dialog
                            progressDialog.setMessage(getString(R.string.uploading) + ((int) progress) + "%");

                            Log.v("on upload file", "on progress");
                        }
                    });
        }
    }

    private void showNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.input_user_name);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(input);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newUserName = input.getText().toString().trim();
                if(TextUtils.isEmpty(newUserName)){
                    snackText = getString(R.string.null_user_name);
                    showSnack(snackText);
                }
                else
                writeUserName(newUserName);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void writeUserImg(final String photoUrl) {
        final Query commentQuery = getQuery(mDatabase);

        mUserRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                else {
                    u.photoUrl = photoUrl;
                }
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void deleteOldImg(final String photoUrl) {
        StorageReference storageRef = mStorage.getReferenceFromUrl(photoUrl);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });
    }

    private void writeUserName(final String newName) {
        mUserRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                else {
                    u.username = newName;
                }
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == profileImg){
            if(userId != getUid()){
                snackText = getString(R.string.no_permission);
                showSnack(snackText);
            }
            else
                showFileChooser();
        }
        else if(v == mUserName){
            if(userId != getUid()){
                snackText = getString(R.string.no_permission);
                showSnack(snackText);
            }
            else
               showNameDialog();
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    public void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mToolbar.setBackgroundResource(R.color.colorPrimaryDark);
                mIsTheTitleVisible = true;
            }

        }
        else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mToolbar.setBackgroundResource(0);
                mIsTheTitleVisible = false;
            }
        }
    }

    public void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {

        Query userComments = databaseReference
                .child("book-comments")
                .orderByChild("uid")
                .equalTo(getUid());

        return userComments;
    }

}

