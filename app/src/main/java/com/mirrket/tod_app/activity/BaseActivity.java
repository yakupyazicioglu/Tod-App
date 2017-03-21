package com.mirrket.tod_app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Post;
import com.mirrket.tod_app.models.User;

import java.util.HashMap;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public static final Integer SELECT_PICTURE = 101;
    public DatabaseReference mDatabase;
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void fullScreenCall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions =
                      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void showSystemUI() {
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void showSnack(final String snacktext) {
        int color;
        String message = snacktext;
        color = Color.WHITE;

        Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        textView.setTextColor(color);
        snackbar.show();

    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void readClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.readed.containsKey(getUid())) {
                    p.readedCount = p.readedCount - 1;
                    p.readed.remove(getUid());
                } else {
                    p.readed.put(getUid(), true);
                    p.readedCount = p.readedCount + 1;
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void wtReadClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.wantToRead.containsKey(getUid())) {
                    p.wantToReadCount = p.wantToReadCount - 1;
                    p.wantToRead.remove(getUid());
                } else {
                    p.wantToRead.put(getUid(), true);
                    p.wantToReadCount = p.wantToReadCount + 1;
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void readingClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.reading.containsKey(getUid())) {
                    p.readingCount = p.readingCount - 1;
                    p.reading.remove(getUid());
                } else {
                    p.reading.put(getUid(), true);
                    p.readingCount = p.readingCount + 1;
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void ratingClicked(DatabaseReference postRef, final int rate) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                final float rateSum = p.ratedCount * p.rating;

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.rates.containsKey(getUid())) {
                    p.rating = (rateSum - p.rating + rate)/p.ratedCount;
                    p.rates.put(getUid(), rate);
                }
                else {
                    p.ratedCount = p.ratedCount + 1;
                    if(p.rating == 0)
                        p.rating = rate;
                    else {
                        p.rating = (rateSum + rate)/p.ratedCount;
                    }
                    p.rates.put(getUid(), rate);
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void rateBookClicked(final String postKey, Integer userRate){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference globalPostRef = mDatabase.child("posts").child(postKey);
        final Dialog rankDialog;
        final RatingBar ratingBar;

        rankDialog = new Dialog(this);
        rankDialog.setContentView(R.layout.ratingbar_dialog);
        rankDialog.setCancelable(true);
        ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(userRate);

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int rated = (int) ratingBar.getRating();

                ratingClicked(globalPostRef, rated);

                rankDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
    }

    public void userReadeds(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.readed.containsKey(key)) {
                    u.readeds = u.readeds - 1;
                    u.readed.remove(key);
                } else {
                    postRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData1) {
                            Post post = mutableData1.getValue(Post.class);
                            Map<String, Object> postValues = post.toProfile();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put("/users/" + getUid() + "/readed/" + key, postValues);
                            childUpdates.put("/users/" + getUid() + "/readeds/", u.readeds + 1);

                            mDatabase.updateChildren(childUpdates);
                            return Transaction.success(mutableData1);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }

                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void userReadings(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.reading.containsKey(key)) {
                    u.readings = u.readings - 1;
                    u.reading.remove(key);
                } else {
                    postRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Post post = mutableData.getValue(Post.class);
                            Map<String, Object> postValues = post.toProfile();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put("/users/" + getUid() + "/reading/" + key, postValues);
                            childUpdates.put("/users/" + getUid() + "/readings/", u.readings+ 1);

                            mDatabase.updateChildren(childUpdates);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
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

    public void userWantToReads(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.wantToRead.containsKey(key)) {
                    u.wanttoreads = u.wanttoreads - 1;
                    u.wantToRead.remove(key);
                }
                else {
                    postRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Post post = mutableData.getValue(Post.class);
                            Map<String, Object> postValues = post.toProfile();
                            Map<String, Object> childUpdates = new HashMap<>();

                            childUpdates.put("/users/" + getUid() + "/wantToRead/" + key, postValues);
                            childUpdates.put("/users/" + getUid() + "/wanttoreads/", u.wanttoreads + 1);

                            mDatabase.updateChildren(childUpdates);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
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

}
