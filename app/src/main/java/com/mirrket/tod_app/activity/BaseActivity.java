package com.mirrket.tod_app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
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
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.viewholder.BookViewHolder;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public static final Integer SELECT_PICTURE = 101;
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public ProgressDialog mProgressDialog;
    public int userRate = 0;

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

    public void populateItemDiscover(final BookViewHolder viewHolder, final Book model, final String bookKey){

        if(model.rates.get(getUid()) != null){
            userRate = model.rates.get(getUid());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userRate;
                if (model.rates.get(getUid()) != null) {
                    userRate = model.rates.get(getUid());
                }
                else
                    userRate = 0;

                // Launch BookDetailActivity
                Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_POST_KEY, bookKey);
                intent.putExtra("userRate", userRate);
                startActivity(intent);
            }
        });

        /*viewHolder.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int userRate = 0;
                if (model.rates.get(getUid()) != null) {
                    userRate = model.rates.get(getUid());
                }
                rateBookClicked(bookKey, userRate);
                return false;
            }
        });*/

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
                // Need to write to both places the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);
                DatabaseReference userRef = mDatabase.child("users").child(getUid());

                // Run two transactions
                readedClicked(globalBookRef);
                //userReadeds(globalBookRef, userRef, postKey);
                //moveOthers(globalBookRef, userRef, postKey);
            }
        });

        viewHolder.bindToWantToRead(model, new View.OnClickListener() {
            @Override
            public void onClick(View wtReadView) {
                // Need to write to both places the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);
                DatabaseReference userRef = mDatabase.child("users").child(getUid());

                // Run two transactions
                wtReadClicked(globalBookRef);
                //userWantToReads(globalBookRef, userRef, postKey);
                //moveOthers(globalPostRef, userRef, postKey);
            }
        });

        viewHolder.bindToReading(model, new View.OnClickListener() {
            @Override
            public void onClick(View readingView) {
                // Need to write to both places the post is stored
                DatabaseReference globalBookRef = mDatabase.child("books").child(bookKey);
                DatabaseReference userRef = mDatabase.child("users").child(getUid());

                // Run two transactions
                readingClicked(globalBookRef);
                //userReadings(globalBookRef, userRef, postKey);
                //moveOthers(globalPostRef, userRef, postKey);
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

    public void rateBookClicked(final String postKey, final Integer userRate){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference globalBookRef = mDatabase.child("books").child(postKey);
        final Dialog rankDialog;
        final SimpleRatingBar ratingBar;

        rankDialog = new Dialog(this);
        rankDialog.setContentView(R.layout.ratingbar_dialog);
        rankDialog.setCancelable(true);
        ratingBar = (SimpleRatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(userRate);

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int newRate = (int) ratingBar.getRating();

                ratingClicked(globalBookRef, userRate, newRate);

                rankDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
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

    public void takeNoteClicked(final String postKey, final String userNote){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference globalBookRef = mDatabase.child("books").child(postKey);
        final Dialog noteDailog;
        final EditText editText;
        final Button saveButton;

        noteDailog = new Dialog(this);
        noteDailog.setContentView(R.layout.take_note_dialog);
        noteDailog.setCancelable(true);
        editText = (EditText) noteDailog.findViewById(R.id.userNote);
        editText.setText(userNote);

        saveButton = (Button) noteDailog.findViewById(R.id.save_note_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newUserNote;
                newUserNote = editText.getText().toString().trim();
                userNoteClicked(globalBookRef, newUserNote);

                noteDailog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        noteDailog.show();
    }

    public void userNoteClicked(DatabaseReference postRef, final String newNote) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Book p = mutableData.getValue(Book.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                else {
                    p.userNotes.put(getUid(), newNote);
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

}
