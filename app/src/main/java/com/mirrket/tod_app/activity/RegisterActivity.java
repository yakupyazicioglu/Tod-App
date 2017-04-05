package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.User;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mUnameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPassField;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mUnameField = (EditText) findViewById(R.id.field_username);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mConfirmPassField = (EditText) findViewById(R.id.field_confirm_pass);
        mRegisterButton = (Button) findViewById(R.id.button_register);

        mRegisterButton.setOnClickListener(this);
    }

    private void register() {
        Log.d(TAG, "register");

        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        /*if(password != con_pass){
            Toast.makeText(getApplicationContext(), "Passwords are different!!", Toast.LENGTH_SHORT).show();
            mConfirmPassField.setText(null);
            return;
        }*/

        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            String snackText = getString(R.string.failed_sign_in);
                            showSnack(snackText);
                        }
                    }
                });
    }

    private boolean validateForm() {
        String required = getString(R.string.required);
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(required);
            result = false;
        }
        else {
            mEmailField.setError(null);
        }

        if(mPasswordField.getText().toString().length() < 6) {
            String snackText = getString(R.string.too_short_pass);
            showSnack(snackText);
            return false;
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(required);
            result = false;
        }

        else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mConfirmPassField.getText().toString())) {
            mConfirmPassField.setError(required);
            result = false;
        }
        else {
            mConfirmPassField.setError(null);
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = mUnameField.getText().toString().trim();
        String photoUrl = null;

        // Write new user
        writeNewUser(user.getUid(), photoUrl, username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void writeNewUser(String userId, String photoUrl, String username, String email) {
        //User user = new User(photoUrl, username, email);

        //mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_register) {
            register();
        }
    }
}
