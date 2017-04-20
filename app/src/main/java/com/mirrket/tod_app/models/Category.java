package com.mirrket.tod_app.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yy on 10.04.2017.
 */

public class Category {
    public String category_name;
    public String itemCount;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Category(String category_name) {
        this.category_name = category_name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("category_name", category_name);
        return result;
    }
}
