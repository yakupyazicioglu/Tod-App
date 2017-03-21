package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mirrket.tod_app.R;


public class SettingActivity extends BaseActivity {
    private Button btnChangePassword, btnRemoveUser,
             changePassword, signOut;

    private EditText password, newPassword;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changePassword = (Button) findViewById(R.id.changePass);
        signOut = (Button) findViewById(R.id.sign_out);

        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                btnChangePassword.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError(getString(R.string.too_short_pass));
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            String snackText = getString(R.string.updated_pass);
                                            showSnack(snackText);
                                            signOut();
                                        } else {
                                            String snackText = getString(R.string.failed_update_pass);
                                            showSnack(snackText);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError(getString(R.string.enter_pass));
                }
            }
        });


        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String snackText = getString(R.string.profile_deleted);
                                        showSnack(snackText);
                                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        String snackText = getString(R.string.failed_delete_acoount);
                                        showSnack(snackText);
                                    }
                                }
                            });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
