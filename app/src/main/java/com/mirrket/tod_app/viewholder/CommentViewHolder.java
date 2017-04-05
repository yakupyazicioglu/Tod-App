package com.mirrket.tod_app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirrket.tod_app.R;
import com.mirrket.tod_app.models.Comment;


/**
 * Created by yy on 09.02.2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public String cid;
    public ImageView photo;
    public TextView authorView;
    public TextView bodyView;
    public TextView dateView;
    public TextView numLike;
    public TextView numDislike;
    public ImageButton btn_like,btn_dislike;

    public CommentViewHolder(View itemView) {
        super(itemView);

        photo = (ImageView) itemView.findViewById(R.id.comment_photo);
        authorView = (TextView) itemView.findViewById(R.id.comment_author);
        bodyView = (TextView) itemView.findViewById(R.id.comment_body);
        dateView = (TextView) itemView.findViewById(R.id.comment_date);
        numLike = (TextView) itemView.findViewById(R.id.comment_num_likes);
        numDislike = (TextView) itemView.findViewById(R.id.comment_num_dislikes);
        btn_like = (ImageButton) itemView.findViewById(R.id.like_comment);
        btn_dislike = (ImageButton) itemView.findViewById(R.id.dislike_comment);
    }

}

