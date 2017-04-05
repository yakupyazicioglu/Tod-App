package com.mirrket.tod_app.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Book;
import com.mirrket.tod_app.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewBookActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewBookActivity";
    public static final Integer SELECT_PICTURE = 101;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    // [END declare_database_ref]

    private Integer numberBook = 0;

    private EditText mCoverUrl;
    private EditText mTitleBook;
    private EditText mTitleAuthor;
    private EditText mPublisher;
    private EditText mPage;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        // [END initialize_database_ref]

        //views
        mCoverUrl = (EditText) findViewById(R.id.field_cover);
        mTitleBook = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        mTitleAuthor = (EditText) findViewById(R.id.field_title_author);
        mPublisher = (EditText) findViewById(R.id.field_publisher);
        mPage = (EditText) findViewById(R.id.field_page);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(this);
    }


    private void submitPost() {

        if (!validateForm()) {
            return;
        }

        final String cover = mCoverUrl.getText().toString().trim();
        final String title = mTitleBook.getText().toString().trim();
        final String title_author = mTitleAuthor.getText().toString().trim();
        final String publisher = mPublisher.getText().toString().trim();
        final String page = mPage.getText().toString();
        final String body = mBodyField.getText().toString().trim();
        final String searchRef = title.toLowerCase() + " - " + title_author.toLowerCase().trim();

        // Disable button so there are no multi-posts
        setEditingEnabled(false);

        String snackText = getString(R.string.posting);
        showSnack(snackText);

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            String snackText = getString(R.string.failed_fetch_user);
                            showSnack(snackText);
                        } else {
                            // Write new post
                            writeNewPost(user.username, cover, title, title_author, publisher, page, body, searchRef);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        mCoverUrl.setText("");
                        mBodyField.setText("");
                        mTitleBook.setText("");
                        mPage.setText("");
                        //finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });


    }


    private boolean validateForm() {
        String required = getString(R.string.required);
        boolean result = true;
        if (TextUtils.isEmpty(mTitleBook.getText().toString())) {
            mTitleBook.setError(required);
            result = false;
        } else {
            mTitleBook.setError(null);
        }

        if (TextUtils.isEmpty(mTitleAuthor.getText().toString())) {
            mTitleAuthor.setError(required);
            result = false;
        } else {
            mTitleAuthor.setError(null);
        }

        if (TextUtils.isEmpty(mBodyField.getText().toString())) {
            mBodyField.setError(required);
            result = false;
        } else {
            mBodyField.setError(null);
        }

        return result;
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleBook.setEnabled(enabled);
        mTitleAuthor.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String username, String image_link,
                              String book, String author, String publisher,
                              String page, String info,  String searchRef) {

        String bookLowerCase = book.toLowerCase();  
        String bookKey = mDatabase.child("books").push().getKey();
        Book post = new Book(username, image_link, book, author, publisher, page, info, searchRef);
        Map<String, Object> postValues = post.toMap();

        String bookId = bookKey + "-" + bookLowerCase.replace(" ","");

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/books/" + bookId, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitButton) {
            submitPost();
        }
    }

}
