package com.mirrket.tod_app.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yy on 24.03.2017.
 */

public class Author {
    public String photoUrl;
    public String name;
    public String info;
    public String searchRef;

    public Author() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Author(String photoUrl, String name, String info, String searchRef) {
        this.photoUrl = photoUrl;
        this.name = name;
        this.info = info;
        this.searchRef = searchRef;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("photoUrl", photoUrl);
        result.put("name", name);
        result.put("info", info);
        result.put("searchRef", searchRef);
        return result;
    }
}
