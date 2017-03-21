package com.mirrket.tod_app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mirrket.tod_app.R;

public class ResetPassActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ResetPassActivity";

    private FirebaseAuth mAuth;
    private EditText inputEmail;
    private Button btnReset, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mAuth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_pass);
        btnBack = (Button) findViewById(R.id.btn_back);

        btnReset.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void resetPass(){
        Log.d(TAG, "resetPass");
        if (!validateForm()) {
            return;
        }
        String email = inputEmail.getText().toString().trim();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String snackText = getString(R.string.send_reset_pass);
                            showSnack(snackText);
                            inputEmail.setText(null);
                        } else {
                            String snackText = getString(R.string.failed_reset_email);
                            showSnack(snackText);
                        }
                    }
                });

    }

    private boolean validateForm() {
        String required = getString(R.string.required);
        boolean result = true;
        if (TextUtils.isEmpty(inputEmail.getText().toString())) {
            inputEmail.setError(required);
            result = false;
        }
        else {
            inputEmail.setError(null);
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            finish();
        }
        if (i == R.id.btn_reset_pass) {
            resetPass();
        }
    }
}
