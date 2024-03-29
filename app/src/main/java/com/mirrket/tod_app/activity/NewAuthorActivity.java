package com.mirrket.tod_app.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Author;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yy on 23.03.2017.
 */

public class NewAuthorActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewAuthorActivity";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleAuthor;
    private EditText mAuthorPhoto;
    private EditText mAuthorInfo;
    private FloatingActionButton mSubmitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_author);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //views
        mTitleAuthor = (EditText) findViewById(R.id.field_title_author);
        mAuthorPhoto = (EditText) findViewById(R.id.field_author_link);
        mAuthorInfo = (EditText) findViewById(R.id.field_author_info);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(this);
    }


    private void submitPost() {

        final String author_photo = mAuthorPhoto.getText().toString().trim();
        final String author_name = mTitleAuthor.getText().toString().trim();
        final String author_info = mAuthorInfo.getText().toString().trim();
        final String searchRef = mTitleAuthor.getText().toString().toLowerCase();

        // Disable button so there are no multi-posts

        String snackText = getString(R.string.posting);
        showSnack(snackText);

        mDatabase.child("authors").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Author author = dataSnapshot.getValue(Author.class);

                        writeNewAuthor(author_photo, author_name, author_info, searchRef);
                        // Finish this Activity, back to the stream
                        mTitleAuthor.setText("");
                        mAuthorInfo.setText("");
                        mAuthorPhoto.setText("");
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });


    }

    // [START write_fan_out]
    private void writeNewAuthor(String author_photo, String author_name, String author_info, String searchRef) {

        String authorKey = mDatabase.child("authors").push().getKey();
        Author author = new Author(author_photo, author_name, author_info, searchRef);
        Map<String, Object> postValues = author.toMap();

        String authorId =  authorKey + "-" + searchRef.replace(" ", "");

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/authors/" + authorId, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitButton) {
            submitPost();
        }
    }
    // [END write_fan_out]
}
