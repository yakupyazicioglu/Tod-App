package com.mirrket.tod_app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String userPhoto;
    public String author;
    public String comment;
    public String date;
    public int likeCount = 0;
    public int dislikeCount = 0;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String photo, String author, String text, String date) {
        this.uid = uid;
        this.userPhoto = photo;
        this.author = author;
        this.comment = text;
        this.date = date;
    }

    // [START comment_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("bookedBy", author);
        result.put("comment", comment);
        result.put("date", date);
        result.put("likeCount", likeCount);
        result.put("likes", likes);
        result.put("dislikeCount", dislikeCount);
        result.put("dislikes", dislikes);

        return result;
    }
    // [END comment_to_map]

}
// [END comment_class]
