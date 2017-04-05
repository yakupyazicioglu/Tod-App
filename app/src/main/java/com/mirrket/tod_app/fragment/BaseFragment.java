package com.mirrket.tod_app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.AuthorDetailActivity;
import com.mirrket.tod_app.activity.BookDetailActivity;
import com.mirrket.tod_app.activity.LoginActivity;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.models.User;
import com.mirrket.tod_app.viewholder.BookViewHolder;

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

    public void populateItemDiscover(final BookViewHolder viewHolder, final Book model, final String bookKey) {

        final String book = model.book;
        final String author = model.author;

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch BookDetailActivity
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, bookKey);
                intent.putExtra("book", book);
                intent.putExtra("author", author);
                startActivity(intent);
            }
        });

        // Determine if the current user has liked this post and set UI accordingly

        if (model.readed.containsKey(getUid())) {
            viewHolder.readView.setImageResource(R.drawable.ic_checkedo);
        } else {
            viewHolder.readView.setImageResource(R.drawable.ic_checkmarko);
        }

        if (model.wantToRead.containsKey(getUid())) {
            viewHolder.wtReadView.setImageResource(R.drawable.ic_checkedo);
        } else {
            viewHolder.wtReadView.setImageResource(R.drawable.ic_checkmarko);
        }

        if (model.reading.containsKey(getUid())) {
            viewHolder.readingView.setImageResource(R.drawable.ic_checkedo);
        } else {
            viewHolder.readingView.setImageResource(R.drawable.ic_checkmarko);
        }


        viewHolder.bindToPost(model);

        viewHolder.bindToReaded(model, new View.OnClickListener() {
            @Override
            public void onClick(View readView) {
                // Need to write to place the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);

                // Run transactions
                readedClicked(globalBookRef);
            }
        });

        viewHolder.bindToWantToRead(model, new View.OnClickListener() {
            @Override
            public void onClick(View wtReadView) {
                // Need to write to place the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);

                // Run transactions
                wtReadClicked(globalBookRef);
            }
        });

        viewHolder.bindToReading(model, new View.OnClickListener() {
            @Override
            public void onClick(View readingView) {
                // Need to write to place the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);

                // Run transactions
                readingClicked(globalBookRef);
            }
        });
    }

    public void readedClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Book p = mutableData.getValue(Book.class);
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
                Book p = mutableData.getValue(Book.class);
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
                Book p = mutableData.getValue(Book.class);
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

    public void ratingClicked(DatabaseReference postRef, final int userRate, final int newRate) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Book p = mutableData.getValue(Book.class);
                float rateSum = p.ratedCount * p.rating;

                if (p == null) {
                    return Transaction.success(mutableData);
                }
                if (p.rates.containsKey(getUid())) {
                    p.rating = (rateSum - userRate + newRate) / p.ratedCount;
                    p.rates.put(getUid(), newRate);
                } else {
                    p.ratedCount = p.ratedCount + 1;
                    p.rating = (rateSum + newRate) / p.ratedCount;
                    p.rates.put(getUid(), newRate);
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

    public void moveToReaded(final DatabaseReference postRef, final String key) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData1) {
                Book post = mutableData1.getValue(Book.class);
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Boolean> postValues = new HashMap<>();
                postValues.put(getUid(), true);

                if(!post.readed.containsKey(getUid())){
                    childUpdates.put("/books/" + key + "/readed/", postValues);
                    childUpdates.put("/books/" + key + "/readedCount/", post.readedCount + 1);
                }

                mDatabase.updateChildren(childUpdates);
                return Transaction.success(mutableData1);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });

    }

    public void moveToWTRead(final DatabaseReference postRef, final String key) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData1) {
                Book post = mutableData1.getValue(Book.class);
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Boolean> postValues = new HashMap<>();
                postValues.put(getUid(), true);

                if(!post.wantToRead.containsKey(getUid())){
                    childUpdates.put("/books/" + key + "/wantToRead/", postValues);
                    childUpdates.put("/books/" + key + "/wantToReadCount/", post.wantToReadCount + 1);
                }

                mDatabase.updateChildren(childUpdates);
                return Transaction.success(mutableData1);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public void moveToReading(final DatabaseReference postRef, final String key) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData1) {
                Book post = mutableData1.getValue(Book.class);
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Boolean> postValues = new HashMap<>();
                postValues.put(getUid(), true);

                if(!post.reading.containsKey(getUid())){
                    childUpdates.put("/books/" + key + "/reading/", postValues);
                    childUpdates.put("/books/" + key + "/readingCount/", post.readingCount + 1);
                }

                mDatabase.updateChildren(childUpdates);
                return Transaction.success(mutableData1);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    public void moveOthers(final DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Book p = mutableData.getValue(Book.class);

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
