package com.mirrket.tod_app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String fileUri;
    public String title;
    public String title_author;
    public String publisher;
    public String page;
    public String body;
    public String date;
    public String searchRef;
    public int readedCount = 0;
    public int wantToReadCount = 0;
    public int readingCount = 0;
    public int ratedCount = 0;
    public float rating = 0;
    public Map<String, Boolean> readed = new HashMap<>();
    public Map<String, Boolean> wantToRead = new HashMap<>();
    public Map<String, Boolean> reading = new HashMap<>();
    public Map<String, Integer> rates = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String fileUri, String title, String title_author,
                String publisher, String page, String body, String date, String searchRef) {
        this.uid = uid;
        this.author = author;
        this.fileUri = fileUri;
        this.title = title;
        this.title_author = title_author;
        this.publisher = publisher;
        this.page = page;
        this.body = body;
        this.date = date;
        this.searchRef = searchRef;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("fileUri", fileUri);
        result.put("title", title);
        result.put("title_author", title_author);
        result.put("publisher", publisher);
        result.put("page", page);
        result.put("body", body);
        result.put("date", date);
        result.put("searchRef", searchRef);
        result.put("readedCount", readedCount);
        result.put("readed", readed);
        result.put("wantToReadCount", wantToReadCount);
        result.put("wantToRead", wantToRead);
        result.put("readingCount", readingCount);
        result.put("reading", reading);
        result.put("rating", rating);

        return result;
    }

    @Exclude
    public Map<String, Object> toProfile() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fileUri", fileUri);
        result.put("title", title);
        result.put("title_author", title_author);
        result.put("publisher", publisher);
        result.put("page", page);

        return result;
    }

}
// [END post_class]
