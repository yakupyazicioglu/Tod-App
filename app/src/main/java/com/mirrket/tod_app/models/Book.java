package com.mirrket.tod_app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Book {

    public String bookedBy;
    public String coverUrl;
    public String book;
    public String author;
    public String publisher;
    public String page;
    public String book_info;
    public String searchRef;
    public String category;
    public int readedCount = 0;
    public int wantToReadCount = 0;
    public int readingCount = 0;
    public int ratedCount = 0;
    public float rating = 0;
    public Map<String, Boolean> readed = new HashMap<>();
    public Map<String, Boolean> wantToRead = new HashMap<>();
    public Map<String, Boolean> reading = new HashMap<>();
    public Map<String, Integer> rates = new HashMap<>();
    public Map<String, String> userNotes = new HashMap<>();

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book(String coverUrl, String book, String author,
                String publisher, String page, String book_info,
                String category, String searchRef) {
        this.coverUrl = coverUrl;
        this.book = book;
        this.author = author;
        this.publisher = publisher;
        this.page = page;
        this.book_info = book_info;
        this.category = category;
        this.searchRef = searchRef;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("coverUrl", coverUrl);
        result.put("book", book);
        result.put("author", author);
        result.put("publisher", publisher);
        result.put("page", page);
        result.put("book_info", book_info);
        result.put("category", category);
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
        result.put("coverUrl", coverUrl);
        result.put("book", book);
        result.put("author", author);
        result.put("publisher", publisher);
        result.put("page", page);

        return result;
    }

}
// [END post_class]
