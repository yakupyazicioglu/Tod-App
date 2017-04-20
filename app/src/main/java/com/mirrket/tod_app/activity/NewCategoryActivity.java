package com.mirrket.tod_app.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.mirrket.tod_app.models.Category;

import java.util.HashMap;
import java.util.Map;

public class NewCategoryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewCategoryActivity";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleCategory;
    private FloatingActionButton mSubmitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        //views
        mTitleCategory = (EditText) findViewById(R.id.field_category_name);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(this);
    }

    private void submitPost() {

        final String category_name = mTitleCategory.getText().toString().trim();

        // Disable button so there are no multi-posts

        String snackText = getString(R.string.posting);
        showSnack(snackText);

        mDatabase.child("authors").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Author author = dataSnapshot.getValue(Author.class);

                        writeNewCategory(category_name);
                        // Finish this Activity, back to the stream
                        mTitleCategory.setText("");
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
    private void writeNewCategory(String category_name) {

        String categoryKey = mDatabase.child("categories").push().getKey();
        Category category = new Category(category_name);
        Map<String, Object> postValues = category.toMap();

        String categoryId =  categoryKey + "-" + category_name;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/categories/" + categoryId, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmitButton) {
            submitPost();
        }
    }
}
