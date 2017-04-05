package com.mirrket.tod_app.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mirrket.tod_app.R;

public class AboutAppActivity extends AppCompatActivity {

    TextView linkMirrket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        linkMirrket = (TextView) findViewById(R.id.more_app);
        linkMirrket.setMovementMethod(LinkMovementMethod.getInstance());

        linkMirrket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://play.google.com/store/search?q=mirrket");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }
}
