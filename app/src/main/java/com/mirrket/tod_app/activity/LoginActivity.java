package com.mirrket.tod_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mirrket.tod_app.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private String email,password;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private CheckBox mRememberMe;
    private Button mLoginButton;
    private Button mSignUpButton;
    private Button mForgotPass;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mRememberMe = (CheckBox) findViewById(R.id.chk_remember_me);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mForgotPass = (Button) findViewById(R.id.btn_forgot_password);
        mSignUpButton = (Button) findViewById(R.id.btn_register);

        //save login preferences
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            mEmailField.setText(loginPreferences.getString("email", email));
            mPasswordField.setText(loginPreferences.getString("password", password));
            mRememberMe.setChecked(true);
        }

        // Click listeners
        mLoginButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mForgotPass.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void signInEmail(){
        if (!validateForm()) {
            return;
        }

        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailField.getWindowToken(), 0);

        if (mRememberMe.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("email", email);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
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
        boolean result = true;
        String required = getString(R.string.required);
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(required);
            result = false;
        }
        else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(required);
            result = false;
        }
        else {
            mPasswordField.setError(null);
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            signInEmail();
        }
        if (i == R.id.btn_forgot_password) {
            startActivity(new Intent(this,ResetPassActivity.class));
        }
        if (i == R.id.btn_register) {
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }

}
