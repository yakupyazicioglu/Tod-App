package com.mirrket.tod_app.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String photoUrl;
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String photoUrl, String username, String email) {
        this.photoUrl = photoUrl;
        this.username = username;
        this.email = email;
    }

}
// [END blog_user_class]
