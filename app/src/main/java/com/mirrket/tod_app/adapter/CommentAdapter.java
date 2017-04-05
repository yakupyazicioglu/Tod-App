package com.mirrket.tod_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.mirrket.tod_app.R;
import com.mirrket.tod_app.activity.BookDetailActivity;
import com.mirrket.tod_app.activity.UserProfileActivity;
import com.mirrket.tod_app.models.Comment;
import com.mirrket.tod_app.util.CircleTransform;
import com.mirrket.tod_app.viewholder.CommentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mCommentReference;
    private ChildEventListener mChildEventListener;

    private List<String> mCommentIds = new ArrayList<>();
    private List<Comment> mComments = new ArrayList<>();

    public CommentAdapter(Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Comment comment = dataSnapshot.getValue(Comment.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mCommentIds.add(dataSnapshot.getKey());
                mComments.add(comment);
                notifyItemInserted(mComments.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Replace with the new data
                    mComments.set(commentIndex, newComment);

                    // Update the RecyclerView
                    notifyItemChanged(commentIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int commentIndex = mCommentIds.indexOf(commentKey);
                if (commentIndex > -1) {
                    // Remove data from the list
                    mCommentIds.remove(commentIndex);
                    mComments.remove(commentIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(commentIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final CommentViewHolder holder, final int position) {
        final Comment comment = mComments.get(position);
        Picasso.with(mContext)
                .load(comment.userPhoto)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_action_account_circle_40)
                .fit()
                .into(holder.photo);
        holder.authorView.setText(comment.author);
        holder.bodyView.setText(comment.comment);
        holder.dateView.setText(comment.date);
        holder.numLike.setText(String.valueOf(comment.likeCount));
        holder.numDislike.setText(String.valueOf(comment.dislikeCount));


        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLikeClicked(mDatabaseReference.child(mCommentIds.get(position)));
            }
        });

        holder.btn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDislikeClicked(mDatabaseReference.child(mCommentIds.get(position)));
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("userId", comment.uid);
                mContext.startActivity(intent);
            }
        });

        holder.authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("userId", comment.uid);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

    private void onLikeClicked(final DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }
                if (c.likes.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    c.likeCount = c.likeCount - 1;
                    c.likes.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    c.likeCount = c.likeCount + 1;
                    c.likes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void onDislikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment c = mutableData.getValue(Comment.class);
                if (c == null) {
                    return Transaction.success(mutableData);
                }

                if (c.dislikes.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    c.dislikeCount = c.dislikeCount - 1;
                    c.dislikes.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    c.dislikeCount = c.dislikeCount + 1;
                    c.dislikes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void gotoUser(DatabaseReference userRef){

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
