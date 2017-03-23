package com.mirrket.tod_app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
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
import com.mirrket.tod_app.activity.LoginActivity;
import com.mirrket.tod_app.models.Post;
import com.mirrket.tod_app.models.User;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by yy on 14.03.2017.
 */

public class BaseFragment extends Fragment {

    // [START define_database_reference]
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    // [END define_database_reference]

    public void showSnack(String snacktext) {
        int color;
        String message = snacktext;
        color = Color.WHITE;

        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.container), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

        textView.setTextColor(color);
        snackbar.show();

    }

    public void readedClicked(DatabaseReference postRef) {
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
        final DatabaseReference globalPostRef = mDatabase.child("posts").child(postKey);
        final Dialog rankDialog;
        final RatingBar ratingBar;

        rankDialog = new Dialog(getContext());
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

    public void moveToReaded(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                postRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData1) {
                        Post post = mutableData1.getValue(Post.class);
                        Map<String, Object> readValues = post.toProfile();
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Boolean> postValues = new HashMap<>();
                        postValues.put(getUid(), true);

                        childUpdates.put("/posts/" + key + "/readed/", postValues);
                        childUpdates.put("/posts/" + key + "/readedCount/", post.readedCount + 1);
                        childUpdates.put("/users/" + getUid() + "/readed/" + key, readValues);
                        childUpdates.put("/users/" + getUid() + "/readeds/", u.readeds + 1);

                        mDatabase.updateChildren(childUpdates);
                        return Transaction.success(mutableData1);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
                });

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

    public void moveToWTRead(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                postRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData1) {
                        Post post = mutableData1.getValue(Post.class);
                        Map<String, Object> readValues = post.toProfile();
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Boolean> postValues = new HashMap<>();
                        postValues.put(getUid(), true);

                        childUpdates.put("/posts/" + key + "/wantToRead/", postValues);
                        childUpdates.put("/posts/" + key + "/wantToReadCount/", post.wantToReadCount + 1);
                        childUpdates.put("/users/" + getUid() + "/wantToRead/" + key, readValues);
                        childUpdates.put("/users/" + getUid() + "/wanttoreads/", u.wanttoreads + 1);

                        mDatabase.updateChildren(childUpdates);
                        return Transaction.success(mutableData1);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

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

    public void moveToReading(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final User u = mutableData.getValue(User.class);

                if (u == null) {
                    return Transaction.success(mutableData);
                }

                postRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData1) {
                        Post post = mutableData1.getValue(Post.class);
                        Map<String, Object> readValues = post.toProfile();
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Boolean> postValues = new HashMap<>();
                        postValues.put(getUid(), true);

                        childUpdates.put("/posts/" + key + "/reading/", postValues);
                        childUpdates.put("/posts/" + key + "/readingCount/", post.readingCount + 1);
                        childUpdates.put("/users/" + getUid() + "/reading/" + key, readValues);
                        childUpdates.put("/users/" + getUid() + "/readings/", u.readings+ 1);

                        mDatabase.updateChildren(childUpdates);
                        return Transaction.success(mutableData1);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });


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

    public void moveOthers(final DatabaseReference postRef, final DatabaseReference userRef, final String key) {
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
                }
                if (p.readed.containsKey(getUid())) {
                    p.readedCount = p.readedCount - 1;
                    p.readed.remove(getUid());
                }
                if (p.reading.containsKey(getUid())) {
                    p.readingCount = p.readingCount - 1;
                    p.reading.remove(getUid());
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);

                if (u == null) {
                    return Transaction.success(mutableData);
                }
                if (u.wantToRead.containsKey(key)) {
                    u.wantToRead.remove(key);
                    u.wanttoreads = u.wanttoreads - 1;
                }
                if (u.readed.containsKey(key)) {
                    u.readed.remove(key);
                    u.readeds = u.readeds - 1;
                }
                if (u.reading.containsKey(key)) {
                    u.reading.remove(key);
                    u.readings = u.readings - 1;
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

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null)
            startActivity(new Intent(getActivity(), LoginActivity.class));
        else {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
