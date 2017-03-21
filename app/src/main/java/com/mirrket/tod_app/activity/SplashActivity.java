package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mirrket.tod_app.R;

public class SplashActivity extends BaseActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        fullScreenCall();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null)
                    onAuthSuccess(mAuth.getCurrentUser());
                else
                    onAuthFailed();
            }
        }, SPLASH_TIME_OUT);
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void onAuthFailed() {
        // Go to MainActivity
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

}



